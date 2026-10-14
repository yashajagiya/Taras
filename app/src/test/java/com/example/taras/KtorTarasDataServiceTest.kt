package com.example.taras

import com.example.taras.core.common.AppError
import com.example.taras.core.common.toAppError
import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.KtorTarasDataService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorTarasDataServiceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private fun createServiceWithMock(mockEngine: MockEngine): KtorTarasDataService {
        val client = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(json)
            }
            defaultRequest {
                url(ApiConstants.BASE_URL_TARAS_GITHUB)
            }
        }
        return KtorTarasDataService(client)
    }

    @Test
    fun getCalendar_successfulResponse_deserializesCorrectly() = runBlocking {
        val responseJson = """
            [
              {
                "round": 1,
                "id": "bahrain",
                "name": "Bahrain Grand Prix"
              }
            ]
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("https://yashajagiya.github.io/TarasAPIstatic/v2/calendar.json", request.url.toString())
            respond(
                content = responseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val service = createServiceWithMock(engine)
        val calendar = service.getCalendar()

        assertEquals(1, calendar.size)
        assertEquals("Bahrain Grand Prix", calendar[0].name)
    }

    @Test
    fun getResultsByRound_formatsRoundInUrlCorrectly() = runBlocking {
        val responseJson = """
            {
              "round": 4,
              "race_name": "Japanese Grand Prix",
              "date": "2026-04-05",
              "results": []
            }
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("https://yashajagiya.github.io/TarasAPIstatic/v2/results/round_4.json", request.url.toString())
            respond(
                content = responseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val service = createServiceWithMock(engine)
        val result = service.getResultsByRound(4)

        assertEquals(4, result.round)
        assertEquals("Japanese Grand Prix", result.raceName)
    }

    @Test
    fun getLive_successfulResponse_deserializesCorrectly() = runBlocking {
        val responseJson = """
            {
              "season": 2026,
              "is_live": true,
              "session": "Live Session",
              "track_status": "GREEN"
            }
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("https://yashajagiya.github.io/TarasAPIstatic/v2/live.json", request.url.toString())
            respond(
                content = responseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val service = createServiceWithMock(engine)
        val live = service.getLive()

        assertEquals(2026, live.season)
        assertTrue(live.isLive)
        assertEquals("Live Session", live.session)
        assertEquals("GREEN", live.trackStatus)
    }

    @Test
    fun http404_mapsTo_appErrorNotFound() = runBlocking {
        val engine = MockEngine {
            respond(
                content = "Not Found",
                status = HttpStatusCode.NotFound
            )
        }

        val service = createServiceWithMock(engine)
        try {
            service.getDrivers()
            error("Should have thrown exception")
        } catch (e: Throwable) {
            val appError = e.toAppError()
            assertTrue(appError is AppError.NotFound)
        }
    }

    @Test
    fun http500_mapsTo_appErrorServer() = runBlocking {
        val engine = MockEngine {
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val service = createServiceWithMock(engine)
        try {
            service.getTeams()
            error("Should have thrown exception")
        } catch (e: Throwable) {
            val appError = e.toAppError()
            assertTrue(appError is AppError.Server)
            assertEquals(500, (appError as AppError.Server).code)
        }
    }
}

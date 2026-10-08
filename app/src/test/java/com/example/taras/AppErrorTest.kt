package com.example.taras

import com.example.taras.core.common.AppError
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AppErrorTest {

    @Test
    fun unknownHostException_mapsTo_networkError() {
        val exception = UnknownHostException("Unable to resolve host")
        val error = exception.toAppError()

        assertTrue(error is AppError.Network)
        assertEquals("No internet connection. Please check your network.", error.message)
    }

    @Test
    fun socketTimeoutException_mapsTo_networkError() {
        val exception = SocketTimeoutException("Connection timed out")
        val error = exception.toAppError()

        assertTrue(error is AppError.Network)
    }

    @Test
    fun ioException_mapsTo_networkError() {
        val exception = IOException("Stream closed")
        val error = exception.toAppError()

        assertTrue(error is AppError.Network)
    }

    @Test
    fun serializationException_mapsTo_serializationError() {
        val exception = SerializationException("Field 'id' is required")
        val error = exception.toAppError()

        assertTrue(error is AppError.Serialization)
        assertEquals("Failed to parse data from server.", error.message)
    }

    @Test
    fun genericException_mapsTo_unknownError() {
        val exception = IllegalStateException("Something unusual happened")
        val error = exception.toAppError()

        assertTrue(error is AppError.Unknown)
        assertEquals("Something unusual happened", error.message)
    }

    @Test
    fun uiStateError_acceptsAppError() {
        val networkError = AppError.Network()
        val uiState = UiState.Error(networkError)

        assertEquals("No internet connection. Please check your network.", uiState.message)
        assertEquals(networkError, uiState.appError)
    }
}

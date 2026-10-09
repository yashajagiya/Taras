package com.example.taras.core.common

import io.ktor.client.network.sockets.ConnectTimeoutException as KtorConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException as KtorSocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import okio.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface AppError {
    val message: String
    val cause: Throwable?

    data class Network(
        override val message: String = "No internet connection. Please check your network.",
        override val cause: Throwable? = null
    ) : AppError

    data class Server(
        val code: Int? = null,
        override val message: String = "Server error occurred. Please try again later.",
        override val cause: Throwable? = null
    ) : AppError

    data class Serialization(
        override val message: String = "Failed to parse data from server.",
        override val cause: Throwable? = null
    ) : AppError

    data class NotFound(
        override val message: String = "Requested data was not found.",
        override val cause: Throwable? = null
    ) : AppError

    data class Unknown(
        override val message: String = "An unexpected error occurred. Please try again.",
        override val cause: Throwable? = null
    ) : AppError
}

fun Throwable.toAppError(): AppError {
    return when (this) {
        is UnknownHostException,
        is UnresolvedAddressException,
        is SocketTimeoutException,
        is KtorSocketTimeoutException,
        is HttpRequestTimeoutException,
        is KtorConnectTimeoutException -> AppError.Network(cause = this)
        is IOException -> AppError.Network(cause = this)
        is ResponseException -> {
            when (val statusCode = response.status.value) {
                404 -> AppError.NotFound(cause = this)
                in 500..599 -> AppError.Server(code = statusCode, cause = this)
                else -> AppError.Server(
                    code = statusCode,
                    message = "Server error ($statusCode)",
                    cause = this
                )
            }
        }
        is SerializationException -> AppError.Serialization(cause = this)
        else -> AppError.Unknown(message = localizedMessage ?: "An unexpected error occurred", cause = this)
    }
}

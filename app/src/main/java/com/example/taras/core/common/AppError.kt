package com.example.taras.core.common

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
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
        is UnknownHostException, is SocketTimeoutException -> AppError.Network(cause = this)
        is IOException -> AppError.Network(cause = this)
        is HttpException -> {
            when (val statusCode = code()) {
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

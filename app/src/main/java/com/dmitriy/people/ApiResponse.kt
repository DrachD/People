package com.dmitriy.people

import retrofit2.Response

class ApiResponse<T>(
    val status: Status,
    val data: Response<T>?,
    val exception: Exception?,
    val code: Int?,
    val message: String?
) {

    companion object {

        fun <T> success(data: Response<T>): ApiResponse<T> {
            return ApiResponse(
                status = Status.Success,
                data = data,
                exception = null,
                code = data.code(),
                message = data.message()
            )
        }

        fun <T> failure(exception: Exception): ApiResponse<T> {
            return ApiResponse(
                status = Status.Failure,
                data = null,
                exception = exception,
                code = null,
                message = exception.message
            )
        }
    }
}

sealed class Status {
    object Success : Status()
    object Failure : Status()
}
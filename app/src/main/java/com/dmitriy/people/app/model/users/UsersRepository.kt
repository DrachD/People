package com.dmitriy.people.app.model.users

import com.dmitriy.people.ApiResponse
import com.dmitriy.people.sources.NetworkLayer
import com.dmitriy.people.data.Items
import com.dmitriy.people.sources.users.UsersApi
import retrofit2.Response

class UsersRepository : UsersSource {

    private val apiInterface: UsersApi = NetworkLayer.apiService

    override suspend fun getUsers(): ApiResponse<Items> {
        return safeApiCall {
            NetworkLayer.apiService.getUsers()
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): ApiResponse<T> {
        return try {
            ApiResponse.success(apiCall.invoke())
        } catch (e: Exception) {
            ApiResponse.failure(e)
        }
    }
}
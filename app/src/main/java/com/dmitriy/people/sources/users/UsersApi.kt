package com.dmitriy.people.sources.users

import androidx.lifecycle.LiveData
import com.dmitriy.people.data.Items
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface UsersApi {

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("users")
    suspend fun getUsers() : Response<Items>
}
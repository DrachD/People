package com.dmitriy.people.app.model.users

import androidx.lifecycle.LiveData
import com.dmitriy.people.ApiResponse
import com.dmitriy.people.data.Items
import retrofit2.Call
import retrofit2.Response

interface UsersSource {

    suspend fun getUsers(): ApiResponse<Items>
}
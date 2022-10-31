package com.dmitriy.people.app.model.users

import com.dmitriy.people.ApiResponse
import com.dmitriy.people.data.Items

interface UsersSource {

    suspend fun getUsers(): ApiResponse<Items>
}
package com.dmitriy.people.sources.users.entities

data class GetUserResponseEntity(
    val id: String = "",
    val avatarUrl: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val userTag: String = "",
    val department: String = "",
    val position: String = "",
    val birthday: String = "",
    val phone: String = ""
)

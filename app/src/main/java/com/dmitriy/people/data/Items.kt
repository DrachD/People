package com.dmitriy.people.data

import com.dmitriy.people.app.model.users.entities.User

data class Items(
    var items: List<User> = emptyList()
)

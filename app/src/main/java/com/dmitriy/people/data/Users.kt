package com.dmitriy.people.data

data class Users(
    val `data`: List<Data> = emptyList(),
    val page: Int = 0,
    val per_page: Int = 0,
    val support: Support = Support(),
    val total: Int = 0,
    val total_pages: Int = 0
)
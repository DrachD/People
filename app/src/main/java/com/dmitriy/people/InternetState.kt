package com.dmitriy.people

enum class ItemBirthdayType {
    ITEM_BEFORE_NEW_YEAR,
    ITEM_AFTER_NEW_YEAR,
    ITEM_NEW_YEAR
}

sealed class NetworkStatus {

    object Success : NetworkStatus()
    object Failure : NetworkStatus()

    companion object {
        var status: NetworkStatus = Failure
    }
}
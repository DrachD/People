package com.dmitriy.people

object InternetState {

    var currentState = InternetStateEnum.INTERNET_CONNECTED_ERROR

    fun currentInternetConnetedState(state: InternetStateEnum) {
        currentState = state
    }
}

enum class InternetStateEnum {
    INTERNET_CONNECTED_SUCCESS,
    INTERNET_CONNECTED_ERROR
}

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
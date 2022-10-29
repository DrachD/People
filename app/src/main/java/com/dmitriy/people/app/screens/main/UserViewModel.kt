package com.dmitriy.people.app.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriy.people.ApiResponse
import com.dmitriy.people.app.Singleton
import com.dmitriy.people.app.model.users.UsersSource
import com.dmitriy.people.data.Items
import kotlinx.coroutines.launch

class UserViewModel(
    private val usersSource: UsersSource = Singleton.usersRepository
) : ViewModel() {

    private val _users = MutableLiveData<ApiResponse<Items>>()
    val users: LiveData<ApiResponse<Items>> = _users

    fun getUsers() {
        viewModelScope.launch {
            val response = usersSource.getUsers()
            _users.postValue(response)
        }
    }
}

sealed class SortedType {
    object ByName : SortedType()
    object ByBirthday : SortedType()

    companion object {
        var type: SortedType = ByName
    }
}


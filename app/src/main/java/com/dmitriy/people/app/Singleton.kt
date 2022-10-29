package com.dmitriy.people.app

import android.content.Context
import com.dmitriy.people.NetworkLayer
import com.dmitriy.people.app.model.users.UsersRepository

object Singleton {

    private var appContext: Context? = null

    val usersRepository: UsersRepository by lazy {
        UsersRepository()
    }

    fun init(appContext: Context) {

        if (Singleton.appContext == null) {
            Singleton.appContext = appContext
        }
    }
}
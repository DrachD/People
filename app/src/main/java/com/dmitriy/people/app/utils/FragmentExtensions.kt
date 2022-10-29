package com.dmitriy.people.app.utils

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dmitriy.people.R

fun Fragment.findTopNavController(): NavController {
    val navHostFragment = requireActivity().supportFragmentManager
        .findFragmentById(R.id.fragmentContainer) as NavHostFragment
    return navHostFragment.navController
}
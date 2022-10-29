package com.dmitriy.people

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dmitriy.people.app.Singleton
import com.dmitriy.people.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Singleton.init(applicationContext)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        navController = getRootNavController()
    }

    override fun onDestroy() {
        navController = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (navController?.currentDestination?.id?.equals(R.id.homeFragment)!!) {
            finish()
        }
        navController?.popBackStack()
        //super.onBackPressed()
    }

    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        return navHost.navController
    }
}
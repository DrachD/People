package com.dmitriy.people

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dmitriy.people.databinding.FragmentCriticalErrorBinding

class CriticalErrorFragment : Fragment(R.layout.fragment_critical_error) {

    private lateinit var binding: FragmentCriticalErrorBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("logs", "onViewCreated: CriticalErrorFragment")
        binding = FragmentCriticalErrorBinding.bind(view)

        binding.tryAgainTextView.setOnClickListener { onTryAgainButtonPressed() }
    }

    private fun onTryAgainButtonPressed() {
        findNavController().navigate(R.id.homeFragment, null, navOptions {
            popUpTo(R.id.criticalErrorFragment) {
                inclusive = true
            }
        })
    }
}
package com.dmitriy.people.app.screens.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dmitriy.people.app.screens.bottomsheet.PhoneBottomSheetFragment
import com.dmitriy.people.R
import com.dmitriy.people.app.model.users.entities.User
import com.dmitriy.people.databinding.FragmentAccountDetailsBinding
import com.squareup.picasso.Picasso

class DetailsFragment : Fragment(R.layout.fragment_account_details) {

    private lateinit var binding: FragmentAccountDetailsBinding

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountDetailsBinding.bind(view)

        var user: User? = null

        args.currentUser?.let {
            user = it
        }

        user?.let {
            binding.apply {
                firstLastNameTextView.text = "${it.firstName} ${it.lastName}"
                ageTextView.text = it.birthday
                dateTextView.text = it.birthday
                phoneTextView.text = it.phone
                phoneTextView.setOnClickListener { onPhoneBottomSheetButtonPressed() }
                phoneImageView.setOnClickListener { onPhoneBottomSheetButtonPressed() }
                departmentTextView.text = it.department
                userTagTextView.text = it.userTag
                backImageView.setOnClickListener { onBackButtonPressed() }

                Picasso.get()
                    //.load(avatarUrl)
                    .load("https://reqres.in/img/faces/8-image.jpg")
                    .into(photoImageView)
            }
        }
    }

    private fun onBackButtonPressed() {
        findNavController().popBackStack()
    }

    private fun onPhoneBottomSheetButtonPressed() {
        PhoneBottomSheetFragment().show(parentFragmentManager, "PHONE_BOTTOM_SHEET_FRAGMENT")
    }

    companion object {
        const val ARG_USER = "USER"
    }
}
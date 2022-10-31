package com.dmitriy.people.app.screens.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmitriy.people.R
import com.dmitriy.people.app.screens.main.SortedType
import com.dmitriy.people.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment(
    private val callback: Callback
) : BottomSheetDialogFragment() {

    interface Callback {
        fun putSorted(sortedType: SortedType)
    }

    private lateinit var binding: FragmentBottomSheetBinding

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (SortedType.type) {
            is SortedType.ByName -> {
                binding.nameSortRadioButton.isChecked = true
                binding.birthdaySortRadioButton.isChecked = false
            }
            is SortedType.ByBirthday -> {
                binding.nameSortRadioButton.isChecked = false
                binding.birthdaySortRadioButton.isChecked = true
            }
        }

        binding.sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.nameSortRadioButton -> {
                    callback.putSorted(SortedType.ByName)
                    dismiss()
                }
                R.id.birthdaySortRadioButton -> {
                    callback.putSorted(SortedType.ByBirthday)
                    dismiss()
                }
            }
        }
    }
}
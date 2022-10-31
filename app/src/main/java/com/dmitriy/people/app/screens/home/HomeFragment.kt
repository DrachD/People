package com.dmitriy.people

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmitriy.people.app.adapter.PeopleAdapter
import com.dmitriy.people.app.model.Months
import com.dmitriy.people.app.model.users.entities.User
import com.dmitriy.people.app.screens.bottomsheet.BottomSheetFragment
import com.dmitriy.people.app.screens.main.SortedType
import com.dmitriy.people.app.screens.main.UserViewModel
import com.dmitriy.people.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel by viewModels<UserViewModel>()

    private lateinit var binding: FragmentHomeBinding

    private var actionSortMenuItem: MenuItem? = null
    private var logoIcon: View? = null

    private var items: List<User>? = null

    private var queryText: String = ""

    private val filterSearchMap = mutableMapOf<FilterSearchType, String>()

    private enum class FilterSearchType {
        All,
        Design,
        Analytics,
        Management,
        IOS,
        Android
    }

    private val queryTextListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            binding.cancelSearchTextView.visibility = View.VISIBLE
            actionSortMenuItem?.isVisible = false

            val logoDrawable = resources.getDrawable(R.drawable.ic_search, null)
            logoDrawable.setTint(resources.getColor(R.color.black))
            binding.toolbar.logo = logoDrawable
        } else {
            binding.cancelSearchTextView.visibility = View.GONE
            actionSortMenuItem?.isVisible = true

            val logoDrawable = resources.getDrawable(R.drawable.ic_search, null)
            logoDrawable.setTint(Color.parseColor("#FFC3C3C6"))
            binding.toolbar.logo = logoDrawable
        }
    }

    private var tabSelected: Int = 0
    private val tabLayoutListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {

            tab?.let {
                when (tab.position) {
                    0 -> { searchUsers(queryText, FilterSearchType.All) }
                    1 -> { searchUsers(queryText, FilterSearchType.Design) }
                    2 -> { searchUsers(queryText, FilterSearchType.Analytics) }
                    3 -> { searchUsers(queryText, FilterSearchType.Management) }
                    4 -> { searchUsers(queryText, FilterSearchType.IOS) }
                    5 -> { searchUsers(queryText, FilterSearchType.Android) }
                }

                tabSelected = tab.position
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) { }
        override fun onTabReselected(tab: TabLayout.Tab?) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("logs", "onCreate: HomeFragment")
        viewModel.getUsers()

        filterSearchMap[FilterSearchType.All] = ""
        filterSearchMap[FilterSearchType.Design] = "design"
        filterSearchMap[FilterSearchType.Analytics] = "analytics"
        filterSearchMap[FilterSearchType.Management] = "management"
        filterSearchMap[FilterSearchType.IOS] = "ios"
        filterSearchMap[FilterSearchType.Android] = "android"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("logs", "onViewCreated: HomeFragment")
        binding = FragmentHomeBinding.bind(view)

        binding.cancelSearchTextView.setOnClickListener {
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()

            Months.initSorted(SortedType.type)
            tabLayoutListener.onTabSelected(binding.tabLayout.getTabAt(tabSelected))
        }

        logoIcon = getToolbarLogoIcon(binding.toolbar)
        logoIcon?.setOnClickListener {
            binding.searchView.apply {
                if (!isFocused) {
                    isIconified = false
                    isFocusable = true
                    queryTextListener.onFocusChange(this, true)
                }
            }
        }

        binding.searchView.setOnQueryTextFocusChangeListener(queryTextListener)

        binding.toolbar.apply {
            inflateMenu(R.menu.menu_toolbar)
            actionSortMenuItem = this.menu.findItem(R.id.action_sort)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_sort -> {
                        onActionSortButtonPressed()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null) {
                    queryText = newText
                    when (tabSelected) {
                        0 -> { searchUsers(queryText, FilterSearchType.All) }
                        1 -> { searchUsers(queryText, FilterSearchType.Design) }
                        2 -> { searchUsers(queryText, FilterSearchType.Analytics) }
                        3 -> { searchUsers(queryText, FilterSearchType.Management) }
                        4 -> { searchUsers(queryText, FilterSearchType.IOS) }
                        5 -> { searchUsers(queryText, FilterSearchType.Android) }
                    }
                }

                return true
            }
        })


        binding.tabLayout.addOnTabSelectedListener(tabLayoutListener)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
        binding.recyclerView.adapter = PeopleAdapter(null) {
            onPeopleDetailsButtonPressed(it)
        }

        updateUI()
    }

    private fun searchUsers(query: String?, filterSearchType: FilterSearchType) {
        val filteredList = mutableListOf<User>()

        query?.let { text ->
            Months.sorteredItems?.let {
                for (item in it) {
                    if (item.firstName.lowercase().contains(text.lowercase()) &&
                        item.department.lowercase().contains(filterSearchMap[filterSearchType]!!)) {
                        filteredList.add(item)
                    }
                }
            }

            if (SortedType.type == SortedType.ByBirthday) {
                binding.recyclerView.adapter = PeopleAdapter(
                    Months.resortingBirthdayList(filteredList)
                ) {
                    onPeopleDetailsButtonPressed(it)
                }
            } else {
                binding.recyclerView.adapter = PeopleAdapter(filteredList) {
                    onPeopleDetailsButtonPressed(it)
                }
            }
        }
    }

    private fun updateUI() {

        viewModel.users.observe(viewLifecycleOwner) { response ->

            when (response.status) {
                is Status.Success -> {
                    NetworkStatus.status = NetworkStatus.Success

                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_LONG).show()
                    Toast.makeText(requireContext(), response.code.toString(), Toast.LENGTH_LONG).show()

                    Log.d("logs", "message: " + response.message.toString())
                    Log.d("logs", "code: " + response.code.toString())
                    Log.d("logs", "status: " + response.status.toString())
                    Log.d("logs", "exception: " + response.exception.toString())

                    val body = response.data?.body()!!

                    Months.initList(response.data.body()!!.items)
                    Months.initSorted(SortedType.ByName)
                    tabLayoutListener.onTabSelected(binding.tabLayout.getTabAt(tabSelected))
                }
                is Status.Failure -> {
                    NetworkStatus.status = NetworkStatus.Failure

                    Log.d("logs", "code: " + response.code.toString())
                    Log.d("logs", "message: " + response.message.toString())
                    Log.d("logs", "status: " + response.status.toString())
                    Log.d("logs", "exception: " + response.exception.toString())
                }
                else -> {
                    NetworkStatus.status = NetworkStatus.Failure
                }
            }
        }
    }

    private fun onActionSortButtonPressed() {

        BottomSheetFragment(
            object : BottomSheetFragment.Callback {
                override fun putSorted(sortedType: SortedType) {

                    when (sortedType) {
                        is SortedType.ByName -> {
                            SortedType.type = SortedType.ByName
                            tabLayoutListener.onTabSelected(binding.tabLayout.getTabAt(tabSelected))
                        }
                        is SortedType.ByBirthday -> {
                            SortedType.type = SortedType.ByBirthday
                            tabLayoutListener.onTabSelected(binding.tabLayout.getTabAt(tabSelected))
                        }
                    }
                }
            }
        ).show(parentFragmentManager, "BOTTOM_SHEET_FRAGMENT")
    }

    private fun onPeopleDetailsButtonPressed(user: User) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(user)
        findNavController().navigate(action)
    }

    private fun getToolbarLogoIcon(toolbar: Toolbar): View? {
        val potentialViews = arrayListOf<View>()
        toolbar.findViewsWithText(potentialViews, "logo", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)

        var icon: View? = null
        if (potentialViews.size > 0) {
            icon = potentialViews[0]
        }

        return icon
    }
}

class PersonDate(birthDay: String) : Comparable<PersonDate> {

    var birthDay: Date
    val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    init {
        this.birthDay = formatter.parse(birthDay) as Date
    }

    override fun compareTo(other: PersonDate): Int {
        val calendar1 = Calendar.getInstance().apply {
            time = birthDay
        }
        val calendar2 = Calendar.getInstance().apply {
            time = other.birthDay
        }

        val month1 = calendar1.get(Calendar.MONTH)
        val month2 = calendar2.get(Calendar.MONTH)

        if (month1 < month2)
            return -1
        else if (month1 == month2)
            return calendar1.get(Calendar.DAY_OF_MONTH) - calendar2.get(Calendar.DAY_OF_MONTH)

        else return 1
    }
}
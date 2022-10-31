package com.dmitriy.people.app.model

import com.dmitriy.people.ItemBirthdayType
import com.dmitriy.people.PersonDate
import com.dmitriy.people.app.model.users.entities.User
import com.dmitriy.people.app.screens.main.SortedType
import java.text.SimpleDateFormat
import java.util.*

object Months {

    var originalItems: List<User>? = null
    var sorteredItems: List<User>? = null

    private data class Birthday(
        val day: Int,
        val month: Int
    )

    var newYearBirthdayList = mutableListOf<Map<SortedType, ItemBirthdayType>>()

    private val monthsMap = mutableMapOf<Int, String>()
    private val birthdayMap = mutableMapOf<String, Birthday>()
    val newYearMap = mutableMapOf<SortedType, ItemBirthdayType>()
    val birthdayTextMap = mutableMapOf<String, String>()

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val calendar = GregorianCalendar()

    var currentYear: Int = 0
        private set
    private var currentMonth: Int = 0
    private var currentDay: Int = 0

    private val cal = Calendar.getInstance()

    init {
        monthsMap[0] = "янв"
        monthsMap[1] = "фев"
        monthsMap[2] = "мар"
        monthsMap[3] = "апр"
        monthsMap[4] = "мая"
        monthsMap[5] = "июня"
        monthsMap[6] = "июля"
        monthsMap[7] = "авг"
        monthsMap[8] = "сен"
        monthsMap[9] = "окт"
        monthsMap[10] = "ноя"
        monthsMap[11] = "дек"
    }

    fun initList(users: List<User>) {
        originalItems = users
    }

    fun initSorted(sortedType: SortedType) {

        SortedType.type = sortedType

        cal.time = Date()
        currentYear = cal.get(Calendar.YEAR)
        currentMonth = cal.get(Calendar.MONTH)
        currentDay = cal.get(Calendar.DAY_OF_MONTH)

        originalItems?.let { users ->
            // Sorted By: Birthday or Name Alphabet
            when (sortedType) {
                is SortedType.ByName -> sortedByName()
                is SortedType.ByBirthday -> {
                    sortedByDate()
                    resortingBirthdayList()
                }
            }

            // Parse date
            for (item in users) {
                val date = formatter.parse(item.birthday)
                date?.let { calendar.time = it }

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                birthdayMap[item.id] = Birthday(day, month)
                birthdayTextMap[item.id] = "$day ${monthsMap[month]}"
            }
        }
    }

    fun resortingBirthdayList() {

        val newList = mutableListOf<User>()
        val afterNewYearList = mutableListOf<User>()
        val beforeNewYearList = mutableListOf<User>()
        newYearBirthdayList = mutableListOf()

        var countPeopleAfterNewYear = 0
        var countPeopleBeforeNewYear = 0

        sorteredItems?.let {
            for (item in it) {
                val birthday: Birthday? = birthdayMap[item.id]
                birthday?.let { bth ->
                    if (bth.month > currentMonth ||
                        (bth.month == currentMonth && bth.day >= currentDay)) {
                        beforeNewYearList.add(item)
                        ++countPeopleBeforeNewYear
                    }

                    if (bth.month < currentMonth ||
                        (bth.month == currentMonth && bth.day < currentDay) ) {
                        afterNewYearList.add(item)
                        ++countPeopleAfterNewYear
                    }
                }
            }
        }

        for (before in 0 until countPeopleBeforeNewYear) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_BEFORE_NEW_YEAR))
        }

        if (countPeopleBeforeNewYear > 0) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_NEW_YEAR))
        }

        for (after in 0 until countPeopleAfterNewYear) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_AFTER_NEW_YEAR))
        }

        newList.addAll(beforeNewYearList)
        if (countPeopleBeforeNewYear > 0) {
            newList.add(User())
        }
        newList.addAll(afterNewYearList)

        sorteredItems = newList
    }

    fun resortingBirthdayList(users: List<User>): List<User> {

        val newList = mutableListOf<User>()
        val afterNewYearList = mutableListOf<User>()
        val beforeNewYearList = mutableListOf<User>()
        newYearBirthdayList = mutableListOf()

        var countPeopleAfterNewYear = 0
        var countPeopleBeforeNewYear = 0

        users.let {
            for (item in it) {
                val birthday: Birthday? = birthdayMap[item.id]
                birthday?.let { bth ->
                    if (bth.month > currentMonth ||
                        (bth.month == currentMonth && bth.day >= currentDay)) {
                        beforeNewYearList.add(item)
                        ++countPeopleBeforeNewYear
                    }

                    if (bth.month < currentMonth ||
                        (bth.month == currentMonth && bth.day < currentDay) ) {
                        afterNewYearList.add(item)
                        ++countPeopleAfterNewYear
                    }
                }
            }
        }

        for (before in 0 until countPeopleBeforeNewYear) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_BEFORE_NEW_YEAR))
        }

        if (countPeopleBeforeNewYear > 0) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_NEW_YEAR))
        }

        for (after in 0 until countPeopleAfterNewYear) {
            newYearBirthdayList.add(mapOf(SortedType.ByBirthday to ItemBirthdayType.ITEM_AFTER_NEW_YEAR))
        }

        newList.addAll(beforeNewYearList)
        if (countPeopleBeforeNewYear > 0) {
            newList.add(User())
        }
        newList.addAll(afterNewYearList)

        return newList
    }

    // -----------------------------------------------------------
    // Sorted by name and date
    // -----------------------------------------------------------

    private fun sortedByName() {

        val userMap = mutableMapOf<String, User>()
        val nameMap = mutableMapOf<String, String>()

        originalItems?.let { users ->
            for (item in users) {
                userMap[item.id] = item
                nameMap[item.id] = item.firstName
            }
        }

        val nameMapSorted = nameMap.toList().sortedBy { (_, value) -> value }.toMap()
        val userList = mutableListOf<User>()

        for (item in nameMapSorted) {
            userList.add(userMap[item.key]!!)
        }

        sorteredItems = userList
    }

    private fun sortedByDate() {

        val personDateMap = mutableMapOf<PersonDate, String>()

        originalItems?.let { users ->
            for (item in users) {
                personDateMap[PersonDate(item.birthday)] = item.id
            }
        }

        val sortedMap = personDateMap.toSortedMap(compareBy { it })

        for (item in sortedMap) {
            item.key.formatter.format(item.key.birthDay)
        }

        val userMap = mutableMapOf<String, User>()

        originalItems?.let { users ->
            for (item in users) {
                userMap[item.id] = item
            }
        }

        val userList = mutableListOf<User>()

        for (item in sortedMap) {
            userList.add(userMap[item.value]!!)
        }

        sorteredItems = userList
    }
}
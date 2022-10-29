package com.dmitriy.people.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmitriy.people.*
import com.dmitriy.people.app.model.users.entities.User
import com.dmitriy.people.app.screens.main.SortedType
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class PeopleAdapter(
    private var list: List<User>?,
    private var onPeopleDetails: (User) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.AbstractPeopleHolder>() {

    abstract class AbstractPeopleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class PeopleHolder(itemView: View) : AbstractPeopleHolder(itemView) {

        private val firstLastNameTextView: TextView
        private val userTagTextView: TextView
        private val departmentTextView: TextView
        private val photoImageView: ImageView
        private val birthdayTextView: TextView
        private val itemLinearLayout: LinearLayout
        private var user: User? = null

        init {
            firstLastNameTextView = itemView.findViewById(R.id.firstLastNameTextView)
            userTagTextView = itemView.findViewById(R.id.userTagTextView)
            departmentTextView = itemView.findViewById(R.id.departmentTextView)
            photoImageView = itemView.findViewById(R.id.photoImageView)
            birthdayTextView = itemView.findViewById(R.id.birthdayTextView)
            itemLinearLayout = itemView.findViewById(R.id.itemLinearLayout)
        }

        fun onBind(user: User?) {
            this.user = user

            firstLastNameTextView.text = "${this.user?.firstName} ${this.user?.lastName}"
            userTagTextView.text = user?.userTag
            departmentTextView.text = user?.department
            birthdayTextView.text = Months.birthdayTextMap[user?.id]

            Picasso.get()
                //.load(avatarUrl)
                .load("https://reqres.in/img/faces/8-image.jpg")
                .into(photoImageView)

            itemLinearLayout.setOnClickListener { onPeopleDetailsButtonPressed() }

//            val urlImage: URL = URL(avatarUrl)
//
//            val result: Deferred<Bitmap?> = GlobalScope.async {
//                BitmapFactory.decodeStream(urlImage.openStream())
//            }
//
//            GlobalScope.launch(Dispatchers.Main) {
//                val bitmap: Bitmap? = result.await()
//
//                photoImageView.setImageBitmap(bitmap)
//            }
        }

        private fun onPeopleDetailsButtonPressed() {
            onPeopleDetails.invoke(this.user!!)
        }
    }

    inner class HiddenPeopleHolder(itemView: View) : AbstractPeopleHolder(itemView)

    inner class BirthdayBorderHolder(itemView: View) : AbstractPeopleHolder(itemView) {

        private val currentYear: TextView

        init {
            currentYear = itemView.findViewById(R.id.currentYearTextView)
        }

        fun onBind() {
            val nextYear = Months.currentYear + 1
            currentYear.text = nextYear.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractPeopleHolder {

        val lInflater = LayoutInflater.from(parent.context)
        val view = lInflater.inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.item_list_people -> PeopleHolder(view)
            R.layout.item_list_hide_people -> HiddenPeopleHolder(view)
            R.layout.item_list_birthday_border -> BirthdayBorderHolder(view)
            else -> PeopleHolder(view)
        }
    }

    override fun onBindViewHolder(holder: AbstractPeopleHolder, position: Int) {
        val item = list?.get(position)

        when (holder) {
            is PeopleHolder -> {
                holder.onBind(item)
            }
            is BirthdayBorderHolder -> {
                holder.onBind()
            }
        }
    }

    override fun getItemCount(): Int = list?.size ?: 7

    override fun getItemViewType(position: Int): Int {

        return when(NetworkStatus.status) {
            is NetworkStatus.Success -> {
                if (SortedType.type == SortedType.ByName) {
                    R.layout.item_list_people
                } else {
                    val itemBirthdayType = Months.newYearBirthdayList[position]

                    if (itemBirthdayType[SortedType.ByBirthday] == ItemBirthdayType.ITEM_BEFORE_NEW_YEAR ||
                        itemBirthdayType[SortedType.ByBirthday] == ItemBirthdayType.ITEM_AFTER_NEW_YEAR) {
                        R.layout.item_list_people
                    } else {
                        R.layout.item_list_birthday_border
                    }
                }
            }
            is NetworkStatus.Failure -> {
                R.layout.item_list_hide_people
            }
        }
    }
}

class Months {

    companion object {

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
            this.originalItems = users
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
}
package com.dmitriy.people.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmitriy.people.*
import com.dmitriy.people.app.model.Months
import com.dmitriy.people.app.model.users.entities.User
import com.dmitriy.people.app.screens.main.SortedType
import com.squareup.picasso.Picasso

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
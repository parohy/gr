package com.parohy.goodrequestusers.ui.home.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parohy.goodrequestusers.R
import com.parohy.goodrequestusers.api.model.User
import com.parohy.goodrequestusers.widget.RowWithLabel
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val avatarView: AppCompatImageView = itemView.itemUserAvatar
    private val nameView: RowWithLabel = itemView.itemUserName
    private val idView: RowWithLabel = itemView.itemUserId

    fun updateData(user: User) {
        Glide.with(avatarView)
            .load(user.avatar)
            .into(avatarView)

        nameView.text = nameView.resources.getString(
            R.string.user_name_format,
            user.name,
            user.surname
        )
        idView.text = user.id.toString()
    }
}
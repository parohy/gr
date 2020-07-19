package com.parohy.goodrequestusers.ui.home.adapter

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parohy.goodrequestusers.R
import com.parohy.goodrequestusers.api.model.User
import java.util.*

class UserListAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener<User>? = null
): RecyclerView.Adapter<UserViewHolder>() {
    companion object {
        private const val TAG = "UserListAdapter"
    }

    private val mutableList: MutableList<User> = Collections.synchronizedList(mutableListOf())
    private val handlerThread: HandlerThread by lazy {
        HandlerThread("UserListAdapter")
            .apply {
                start()
            }
    }
    private val handler: Handler by lazy { Handler(handlerThread.looper) }
    private val mainHandler: Handler by lazy { Handler(context.mainLooper) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        LayoutInflater.from(context)
            .inflate(R.layout.item_user, parent, false)
            .let { UserViewHolder(it) }

    override fun getItemCount(): Int = mutableList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.updateData(mutableList[position])
        holder.itemView.setOnClickListener { onItemClickListener?.onClick(mutableList[position]) }
    }

    fun insertData(data: List<User>) {
        if (data.isNotEmpty()) {
            handler.post {
                val toInsert = data.filter { !mutableList.contains(it) }
                Log.d(TAG, "To insert: $toInsert")
                insert(toInsert)
            }
        }
    }

    private fun insert(data: List<User>) {
        mainHandler.post {
            val from = mutableList.lastIndex + 1
            mutableList.addAll(data)
            notifyItemRangeInserted(from, mutableList.lastIndex)
        }
    }

    fun updateData(data: List<User>) {
        if (data.isNotEmpty()) {
            handler.post {
                val update = mutableMapOf<Int, User>()
                val toInsert = mutableListOf<User>()
                data.forEachIndexed { index, new ->
                    val original = mutableList.find { it.id == new.id }
                    if (original == null) toInsert.add(new)
                    else if (new != original) update[index] = new
                }

                if (update.isNotEmpty() || toInsert.isNotEmpty()) {
                    insert(toInsert)
                    mainHandler.post {
                        update.keys.forEach { i ->
                            Log.d(TAG, "To update: ${update[i]}")
                            mutableList.removeAt(i)
                            mutableList.add(i, update[i]!!)
                            notifyItemChanged(i)
                        }
                    }
                }
            }
        }
    }
}
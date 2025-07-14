package com.devsyncit.instagramclone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter (val context: Context, val messageList: MutableList<String>,
    val senderList: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var RIGHT_VIEW: Int = 0
    var LEFT_VIEW: Int = 1

    inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewRightMessage: TextView =
            itemView.findViewById<TextView>(R.id.textViewRightMessage)
    }

    inner class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewLeftMessage: TextView =
            itemView.findViewById<TextView>(R.id.textViewLeftMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)

        if (viewType == RIGHT_VIEW) {
            val rightView: View = inflater.inflate(R.layout.item_right_message, parent, false)
            return RightViewHolder(rightView)
        } else {
            val leftView: View = inflater.inflate(R.layout.item_left_message, parent, false)
            return LeftViewHolder(leftView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == RIGHT_VIEW) {
            val rightViewHolder = holder as RightViewHolder

            val chat_message: String = messageList.get(position)

            rightViewHolder.textViewRightMessage.text = chat_message
        } else {
            val leftViewHolder = holder as LeftViewHolder

            val chat_message: String = messageList.get(position)

            leftViewHolder.textViewLeftMessage.text = chat_message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val myUserId = user!!.uid

        val senderId: String = senderList.get(position)

        return if (senderId == myUserId) {
            RIGHT_VIEW
        } else {
            LEFT_VIEW
        }
    }
}
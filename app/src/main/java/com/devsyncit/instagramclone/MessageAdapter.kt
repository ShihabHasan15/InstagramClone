package com.devsyncit.instagramclone

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val userList: List<HashMap<String, String>>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfile: CircleImageView = itemView.findViewById(R.id.userProfile)
        val userFullName: TextView = itemView.findViewById(R.id.userfullname)
        val userName: TextView = itemView.findViewById(R.id.username)
        val chatBody: ConstraintLayout = itemView.findViewById(R.id.chatBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val messageView = LayoutInflater.from(context).inflate(R.layout.chat_interface_design, parent, false)

        return MessageViewHolder(messageView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val messageListUser = userList[position]

        val name = messageListUser["name"]
        val userId = messageListUser["userId"]
        val profileImage = messageListUser["profileImage"]
        val username = messageListUser["username"]


        profileImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            holder.userProfile.setImageBitmap(bitmap)

        }

        holder.userFullName.text = name
        holder.userName.text = username

        holder.chatBody.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userFullName", name)
            intent.putExtra("userName", username)
            intent.putExtra("profileImage", profileImage)
            context.startActivity(intent)
        }

    }

}
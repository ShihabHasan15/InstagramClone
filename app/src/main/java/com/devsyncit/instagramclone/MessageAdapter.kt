package com.devsyncit.instagramclone

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val userList: List<HashMap<String, String>>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var filteredUserList = userList

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfile: CircleImageView = itemView.findViewById(R.id.userProfile)
        val userFullName: TextView = itemView.findViewById(R.id.userfullname)
        val userName: TextView = itemView.findViewById(R.id.username)
        val chatBody: ConstraintLayout = itemView.findViewById(R.id.chatBody)
        val activeDot: ImageView = itemView.findViewById(R.id.activeDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val messageView = LayoutInflater.from(context).inflate(R.layout.chat_interface_design, parent, false)

        return MessageViewHolder(messageView)
    }

    override fun getItemCount(): Int {
        return filteredUserList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val messageListUser = filteredUserList[position]

        val name = messageListUser["name"]?: ""
        val userId = messageListUser["userId"]?: ""
        val profileImage = messageListUser["profileImage"]?: ""
        val username = messageListUser["username"]?: ""


        //active status check

        var activeStatusRef = FirebaseDatabase.getInstance().getReference("ActiveStatus").child(userId)

        activeStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var isActive = snapshot.child("active").getValue(Boolean::class.java)

                if (isActive == true){
                    holder.activeDot.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, ""+error.message, Toast.LENGTH_SHORT).show()
            }
        })

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

    fun filter(query: String){
        filteredUserList = if (query.isEmpty()){
            userList
        }else{
            userList.filter { user ->
                val name = user["name"] ?: ""
                val username = user["username"] ?: ""
                name.contains(query, ignoreCase = true) || username.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
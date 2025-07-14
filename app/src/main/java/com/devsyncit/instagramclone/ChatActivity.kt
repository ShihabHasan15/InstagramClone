package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {

    lateinit var userImage: CircleImageView
    lateinit var userFullName: TextView
    lateinit var userIsTyping: TextView
    lateinit var audioCall: ImageButton
    lateinit var videoCall: ImageButton
    lateinit var messageRecyclerView: RecyclerView
    lateinit var messageInputLayout: EditText
    lateinit var sendBtn: ImageButton
    var messageList: MutableList<String> = mutableListOf()
    var senderList: MutableList<String> = mutableListOf()

    val uid = FirebaseAuth.getInstance().currentUser?.uid //current user id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userImage = findViewById(R.id.userImage)
        userFullName = findViewById(R.id.userFullName)
        userIsTyping = findViewById(R.id.userIsTyping)
        audioCall = findViewById(R.id.audioCall)
        videoCall = findViewById(R.id.videoCall)
        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageInputLayout = findViewById(R.id.messageInputLayout)
        sendBtn = findViewById(R.id.sendBtn)

        val userId = intent.getStringExtra("userId") //user friend id
        val userfullname = intent.getStringExtra("userFullName")
        val userName = intent.getStringExtra("userName")
        val profileImage = intent.getStringExtra("profileImage")


        profileImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            userImage.setImageBitmap(bitmap)

        }

        userFullName.text = userfullname


        messageInputLayout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollToBottom()
            }
        }

        scrollToBottom()


        //agora work


        audioCall.setOnClickListener {

        }

        videoCall.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            val channelName = if (uid!! < userId!!) "${uid}_${userId}" else "${userId}_${uid}"
            intent.putExtra("channelName", channelName)
            startActivity(intent)
        }

        //====================================

        val chatRoomId = generateChatRoomID(uid!!, userId!!)

        val messageRef = FirebaseDatabase.getInstance().getReference("messages").child(chatRoomId)




        sendBtn.setOnClickListener {

            val message = messageInputLayout.text.toString()

            if (!message.isBlank()) {
                val messageMap = HashMap<String, String>()
                messageMap.put("sender", uid!!)
                messageMap.put("message", message)
                messageRef.push().setValue(messageMap)


                scrollToBottom()

                messageInputLayout.text.clear()
                messageInputLayout.clearFocus()
            }

        }

        val adapter = ChatAdapter(this, messageList, senderList)
        messageRecyclerView.adapter = adapter
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()
                senderList.clear()

                for (messageSnap in snapshot.children) {
                    val sender = messageSnap.child("sender").getValue(String::class.java)
                    val getMessage = messageSnap.child("message").getValue(String::class.java)
                    if (!sender!!.isBlank() && !getMessage!!.isBlank()) {
                        messageList.add(getMessage)
                        senderList.add(sender)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error - $error", Toast.LENGTH_SHORT).show()
            }
        })



    }//onCreate


    fun generateChatRoomID(user1: String, user2: String): String {
        return if (user1.compareTo(user2) < 0) {
            user1 + "_" + user2
        } else {
            user2 + "_" + user1
        }
    }


    fun scrollToBottom() {
        messageRecyclerView.post {
            messageRecyclerView.scrollToPosition(messageList.size - 1)
        }
    }

}
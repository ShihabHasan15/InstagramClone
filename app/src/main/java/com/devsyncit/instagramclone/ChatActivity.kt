package com.devsyncit.instagramclone

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Timer
import java.util.TimerTask

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

    var chatRoomId = ""

    val uid = FirebaseAuth.getInstance().currentUser?.uid //current user id

    var typingRef = FirebaseDatabase.getInstance().getReference("typingStatus")

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

            val intent = Intent(this, AudioCallActivity::class.java)
            val channelName = if (uid!! < userId!!) "${uid}_${userId}" else "${userId}_${uid}"
            intent.putExtra("channelName", channelName)
            intent.putExtra("userId", userId)
            intent.putExtra("userFullName", userfullname)
            intent.putExtra("profileImage", profileImage)
            startActivity(intent)

        }

        videoCall.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            val channelName = if (uid!! < userId!!) "${uid}_${userId}" else "${userId}_${uid}"
            intent.putExtra("channelName", channelName)
            intent.putExtra("userId", userId)
            intent.putExtra("userFullName", userfullname)
            intent.putExtra("profileImage", profileImage)
            startActivity(intent)
        }

        //typing status

        messageInputLayout.addTextChangedListener(object : TextWatcher{

            private var typingTimer: Timer? = null
            private val TYPING_DELAY = 2000L

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTypingStatus(true)

                typingTimer?.cancel()
                typingTimer = Timer()
                typingTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        (messageInputLayout.context as Activity).runOnUiThread {
                            setTypingStatus(false)
                        }
                    }
                }, TYPING_DELAY)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        //typing status watch of other user

        chatRoomId = generateChatRoomID(uid!!, userId!!)

        val typingWatchRef = FirebaseDatabase.getInstance()
            .getReference("typingStatus")
            .child(chatRoomId)
            .child(userId!!)

        typingWatchRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                if (isTyping) {
                    userIsTyping.text = "${userfullname?.split(" ")?.get(0)} is typing..."
                    userIsTyping.visibility = View.VISIBLE
                } else {
                    userIsTyping.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //====================================



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

    fun setTypingStatus(isTyping: Boolean){
        if (uid != null) {
            typingRef.child(chatRoomId).child(uid).setValue(isTyping)
        }
    }

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
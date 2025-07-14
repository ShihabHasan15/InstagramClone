package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Config
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.ui.feature.channels.header.ChannelListHeaderView
import io.getstream.chat.android.ui.feature.channels.list.ChannelListView
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import io.getstream.result.then

class MessageActivity : AppCompatActivity() {

    lateinit var message_recycle: RecyclerView
    lateinit var search_bar: EditText
    lateinit var edit: ImageButton
    lateinit var back: ImageButton
    lateinit var toolbar: MaterialToolbar

    var uid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        message_recycle = findViewById(R.id.message_recycle)
        search_bar = findViewById(R.id.search_bar)
        edit = findViewById(R.id.edit)
        back = findViewById(R.id.back)
        toolbar = findViewById(R.id.toolbar)


        var dbRef = FirebaseDatabase.getInstance().getReference("followers")

        var followers = mutableListOf<HashMap<String, String>>()

        dbRef.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (child in snapshot.children){

                        var messageMap = HashMap<String, String>()
                        var name = child.child("name").getValue(String::class.java)
                        var userId = child.child("userId").getValue(String::class.java)
                        var profileImage = child.child("profileImage").getValue(String::class.java)

                        messageMap.put("name", name!!)
                        messageMap.put("userId", userId!!)
                        messageMap.put("profileImage", profileImage!!)

                        var usernameRef = FirebaseDatabase.getInstance().getReference("users")
                        usernameRef.child(userId!!).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                var username = snapshot.child("username").getValue(String::class.java)

                                messageMap.put("username", username!!)

                                followers.add(messageMap)

                                val messageAdapter = MessageAdapter(this@MessageActivity, followers)
                                message_recycle.adapter = messageAdapter
                                message_recycle.layoutManager = LinearLayoutManager(this@MessageActivity)
                                messageAdapter.notifyDataSetChanged()

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

}
package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        back.setOnClickListener {
            onBackPressed()
        }


        var dbRef = FirebaseDatabase.getInstance().getReference("followers")

        var followers = mutableListOf<HashMap<String, String>>()

//        val tempMap1 = HashMap<String, String>()
//        tempMap1.put("name", "Md Shazid Mahmud")
//        tempMap1.put("userId", "123456")
//        tempMap1.put("username", "shazid01")
//        followers.add(tempMap1)
//
//        val tempMap2 = HashMap<String, String>()
//        tempMap2.put("name", "Riaz Mahmud Saikat")
//        tempMap2.put("userId", "123456")
//        tempMap2.put("username", "singrazriaz")
//        followers.add(tempMap2)
//
//        val tempMap3 = HashMap<String, String>()
//        tempMap3.put("name", "Md Hridoy Hossain")
//        tempMap3.put("userId", "123456")
//        tempMap3.put("username", "hridoy06")
//        followers.add(tempMap3)
//
//        val tempMap4 = HashMap<String, String>()
//        tempMap4.put("name", "Julfikar Islam Hridoy")
//        tempMap4.put("userId", "123456")
//        tempMap4.put("username", "julfikar23")
//        followers.add(tempMap4)
//
//        val tempMap5 = HashMap<String, String>()
//        tempMap5.put("name", "Md Rashedul Islam Rabby")
//        tempMap5.put("userId", "123456")
//        tempMap5.put("username", "rashedul56")
//        followers.add(tempMap5)
//
//        val tempMap6 = HashMap<String, String>()
//        tempMap6.put("name", "Mehad Hossain")
//        tempMap6.put("userId", "123456")
//        tempMap6.put("username", "mehad53")
//        followers.add(tempMap6)
//
//        val tempMap7 = HashMap<String, String>()
//        tempMap7.put("name", "Nazmul Islam Nobel")
//        tempMap7.put("userId", "123456")
//        tempMap7.put("username", "nazmulnobel4")
//        followers.add(tempMap7)
//
//        val tempMap8 = HashMap<String, String>()
//        tempMap8.put("name", "Swarna Akter")
//        tempMap8.put("userId", "123456")
//        tempMap8.put("username", "swarna89")
//        followers.add(tempMap8)
//
//        val tempMap9 = HashMap<String, String>()
//        tempMap9.put("name", "Maksuda Akter Mitu")
//        tempMap9.put("userId", "123456")
//        tempMap9.put("username", "mitu49")
//        followers.add(tempMap9)
//
//        val tempMap10 = HashMap<String, String>()
//        tempMap10.put("name", "Meherunnesa Bithy")
//        tempMap10.put("userId", "123456")
//        tempMap10.put("username", "bithy01")
//        followers.add(tempMap10)
//
//        val tempMap11 = HashMap<String, String>()
//        tempMap11.put("name", "Jamiul Hasan")
//        tempMap11.put("userId", "123456")
//        tempMap11.put("username", "jamiul69")
//        followers.add(tempMap11)
//
//        val tempMap12 = HashMap<String, String>()
//        tempMap12.put("name", "Md Abu Talha Sumon")
//        tempMap12.put("userId", "123456")
//        tempMap12.put("username", "talha42")
//        followers.add(tempMap12)

        val messageAdapter = MessageAdapter(this@MessageActivity, followers)
        message_recycle.adapter = messageAdapter
        message_recycle.layoutManager = LinearLayoutManager(this@MessageActivity)

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


        search_bar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                messageAdapter.filter(p0.toString())
            }

        })

    }

}
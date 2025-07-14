package com.devsyncit.instagramclone

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.LocalTime

class SpecificPost : AppCompatActivity() {

    lateinit var love_react: ImageView
    lateinit var love_react_filled: ImageView
    lateinit var profileName: TextView
    lateinit var Name: TextView
    lateinit var PostCaption: TextView
    lateinit var reaction_count: TextView
    lateinit var postImage: ImageView
    lateinit var profileSmallImage: ImageView
    lateinit var comment_icon: ImageView
    var count: Int = 0
    lateinit var dbRef: DatabaseReference

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_post)

        love_react = findViewById(R.id.love_react)
        love_react_filled = findViewById(R.id.love_react_filled)
        profileName = findViewById(R.id.profileName)
        Name = findViewById(R.id.Name)
        reaction_count = findViewById(R.id.reaction_count)
        PostCaption = findViewById(R.id.caption)
        postImage = findViewById(R.id.postImage)
        profileSmallImage = findViewById(R.id.profileSmallImage)
        comment_icon = findViewById(R.id.comment_icon)

        //user name get and set

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var username = snapshot.child("username").getValue(String::class.java)

                Name.text = username
                profileName.text = username

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //profile pic get and set

        dbRef = FirebaseDatabase.getInstance().getReference("userProfilePics")

        dbRef.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var ProfileImage = snapshot.getValue(String::class.java)

                ProfileImage.let {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

                    profileSmallImage.setImageBitmap(bitmap)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        var postId = intent.getStringExtra("postId")

        //listeners

        dbRef = FirebaseDatabase.getInstance().getReference("postlikes")

        val postRef = dbRef.child(postId!!)

        love_react.setOnClickListener {
            love_react.visibility = View.INVISIBLE
            love_react_filled.visibility = View.VISIBLE

            count++

            val updateMap = HashMap<String, Any>()
            updateMap["like"] = count.toString() // or store as Int if your DB structure allows

            postRef.updateChildren(updateMap)
        }

        love_react_filled.setOnClickListener {
            love_react.visibility = View.VISIBLE
            love_react_filled.visibility = View.INVISIBLE

            count--

            val updateMap = HashMap<String, Any>()
            updateMap["like"] = count.toString() // or store as Int if your DB structure allows

            postRef.updateChildren(updateMap)

        }


        //post details get and set


        dbRef = FirebaseDatabase.getInstance().getReference("Posts")

        dbRef.child(uid!!).child(postId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var caption = snapshot.child("caption").getValue(String::class.java)
                var date = snapshot.child("date").getValue(String::class.java)
                var id = snapshot.child("id").getValue(String::class.java)
                var image = snapshot.child("image").getValue(String::class.java)
                var time = snapshot.child("like").getValue(String::class.java)

                PostCaption.text = caption

                image.let {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

                    postImage.setImageBitmap(bitmap)
                }

                val db = FirebaseDatabase.getInstance().getReference("postlikes")

                db.child(id!!).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var like = snapshot.child("like").getValue(String::class.java)

                        count = like?.toIntOrNull() ?: 0

                        if (count==0){
                            reaction_count.visibility = View.GONE
                        }else{
                            reaction_count.visibility = View.VISIBLE
                            reaction_count.text = count.toString()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        comment_icon.setOnClickListener {
            showCommentSheet()
        }



    }

    private fun showCommentSheet() {

        var commentSheet = LayoutInflater.from(this).inflate(R.layout.comment_sheet_design, null)


        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(commentSheet)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet?.requestLayout()
        }

        var comment_recycle = commentSheet.findViewById<RecyclerView>(R.id.comment_recycle)
        var user_photo = commentSheet.findViewById<CircleImageView>(R.id.user_photo)
        var comment_edittext = commentSheet.findViewById<EditText>(R.id.comment_edittext)
        var send_btn = commentSheet.findViewById<ImageButton>(R.id.send_button)
        var no_cmnt_txt = commentSheet.findViewById<TextView>(R.id.no_cmnt_txt)
        var start_cmnt_txt = commentSheet.findViewById<TextView>(R.id.start_cmnt_txt)


        var postId = intent.getStringExtra("postId")



        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        val profileGetDb = FirebaseDatabase.getInstance().getReference("userProfilePics")

        profileGetDb.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val profilePic = snapshot.child(currentUser!!).getValue(String::class.java)

                profilePic.let {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

                    user_photo.setImageBitmap(bitmap)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })





        val commentList = mutableListOf<HashMap<String, String>>()
        val commentLoadDb = FirebaseDatabase.getInstance().getReference("postComments")

        var commentRecycleAdapter = CommentRecycleAdapter(this, commentList)
        comment_recycle.adapter = commentRecycleAdapter
        comment_recycle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        commentLoadDb.child(postId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                commentList.clear()

                for (userSnapshot in snapshot.children){

                    for (commentSnap in userSnapshot.children){
                        val commentMap = hashMapOf<String, String>()

                        val comment = commentSnap.child("comment").getValue(String::class.java)
                        val commentId = commentSnap.child("commentId").getValue(String::class.java)
                        val commenterName = commentSnap.child("name").getValue(String::class.java)
                        val profileImage = commentSnap.child("profileImage").getValue(String::class.java)
                        val userId = commentSnap.child("userId").getValue(String::class.java)
                        val date = commentSnap.child("date").getValue(String::class.java)
                        val time = commentSnap.child("time").getValue(String::class.java)

                        commentMap["comment"] = comment?: ""
                        commentMap["commentId"] = commentId?: ""
                        commentMap["commenterName"] = commenterName?: ""
                        commentMap["profileImage"] = profileImage?: ""
                        commentMap["userId"] = userId?: ""
                        commentMap["date"] = date?: ""
                        commentMap["time"] = time?: ""

                        commentList.add(commentMap)
                    }

                }

                if (commentList.isNotEmpty()) {

                    no_cmnt_txt.visibility = View.GONE
                    start_cmnt_txt.visibility = View.GONE

                    commentRecycleAdapter.notifyDataSetChanged()

                } else {
                    no_cmnt_txt.visibility = View.VISIBLE
                    start_cmnt_txt.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        send_btn.setOnClickListener {
            var comment = comment_edittext.text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser?.uid

            val userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser!!)

            if (!comment.isEmpty()){

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").getValue(String::class.java)

                        val profilePicRef =
                            FirebaseDatabase.getInstance().getReference("userProfilePics")

                        profilePicRef.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            @SuppressLint("NewApi")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val profilePic =
                                    snapshot.child(currentUser).getValue(String::class.java)

                                val commentDb = FirebaseDatabase.getInstance()
                                    .getReference("postComments")
                                    .child(postId!!)
                                    .child(currentUser)

                                val commentId = commentDb.push().key!!

                                val commentMap = hashMapOf(
                                    "commentId" to commentId,
                                    "userId" to currentUser,
                                    "name" to username,
                                    "profileImage" to profilePic,
                                    "comment" to comment,
                                    "date" to LocalDate.now().toString(), // server‑side time
                                    "time" to LocalTime.now().toString() // server‑side time
                                )

                                commentDb.child(commentId).setValue(commentMap)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            comment_edittext.text.clear()
                                        } else {
                                            Toast.makeText(
                                                this@SpecificPost,
                                                "Failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }

        bottomSheetDialog.show()


    }
}
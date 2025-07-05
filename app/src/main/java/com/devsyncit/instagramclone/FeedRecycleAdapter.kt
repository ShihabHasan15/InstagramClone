package com.devsyncit.instagramclone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.icu.util.LocaleData
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class FeedRecycleAdapter(var context: Context, var feedList: List<HashMap<String, String>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var count: Int = 0

    // view holder classes
    class imagePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var profileSmallImage = itemView.findViewById<CircleImageView>(R.id.profileSmallImage)
        var username = itemView.findViewById<TextView>(R.id.username)
        var postImage = itemView.findViewById<ImageView>(R.id.postImage)
        var loveReact = itemView.findViewById<ImageView>(R.id.love_react)
        var comment_icon = itemView.findViewById<ImageView>(R.id.comment_icon)
        var loveReactFilled = itemView.findViewById<ImageView>(R.id.love_react_filled)
        var profilename = itemView.findViewById<TextView>(R.id.profileName)
        var caption = itemView.findViewById<TextView>(R.id.caption)
        var reaction_count = itemView.findViewById<TextView>(R.id.reaction_count)

    }

//=============================

    //implemented methods


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var postview = LayoutInflater.from(context).inflate(R.layout.post_design, parent, false)

        return imagePostViewHolder(postview)
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var myHolder = holder as imagePostViewHolder

        var feedPost = feedList.get(position)

        var profileImage = feedPost["profileImage"]
        var name = feedPost["name"]
        var followedAt = feedPost["followedAt"]
        var userId = feedPost["userId"]
        var caption = feedPost["caption"]
        var date = feedPost["date"]
        var postId = feedPost["postId"]
        var postImage = feedPost["image"]
        var like = feedPost["like"]
        var time = feedPost["time"]

        profileImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            myHolder.profileSmallImage.setImageBitmap(bitmap)

        }

        myHolder.profilename.text = name
        myHolder.caption.text = caption

        postImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            myHolder.postImage.setImageBitmap(bitmap)
        }

//        count = like!!.toInt()


        myHolder.reaction_count.visibility = View.GONE

//user name set

        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.child(userId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user_name = snapshot.child("username").getValue(String::class.java)

                myHolder.username.text = user_name

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

//likes set

        val db = FirebaseDatabase.getInstance().getReference("postlikes")

        val postRef = db.child(postId!!)

        postRef.child("like").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentCount = snapshot.getValue(String::class.java)?.toIntOrNull() ?: 0

                myHolder.loveReact.setOnClickListener {

                    val updatedCount = currentCount + 1

                    val updateMap = HashMap<String, Any>()
                    updateMap["like"] = updatedCount.toString() // or store as Int if your DB structure allows

                    postRef.updateChildren(updateMap).addOnCompleteListener {
                        if (it.isSuccessful){
                            myHolder.loveReact.visibility = View.INVISIBLE
                            myHolder.loveReactFilled.visibility = View.VISIBLE
                        }
                    }

                    currentCount = updatedCount

                }



                myHolder.loveReactFilled.setOnClickListener {

                    val updatedCount = if (currentCount > 0) currentCount - 1 else 0

                    val updateMap = HashMap<String, Any>()
                    updateMap["like"] = updatedCount.toString() // or store as Int if your DB structure allows

                    postRef.updateChildren(updateMap).addOnCompleteListener {
                        if (it.isSuccessful){
                            myHolder.loveReact.visibility = View.VISIBLE
                            myHolder.loveReactFilled.visibility = View.INVISIBLE
                        }
                    }

                    currentCount = updatedCount

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

//comments

        myHolder.comment_icon.setOnClickListener {

            val dialogView = LayoutInflater.from(context).inflate(R.layout.comment_sheet_design, null)

            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.setContentView(dialogView)

            bottomSheetDialog.setOnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                bottomSheet?.requestLayout()
            }


            //comment load

            val commentLoadDb = FirebaseDatabase.getInstance().getReference("postComments")



            var user_photo = dialogView.findViewById<CircleImageView>(R.id.user_photo)
            var comment_edittext = dialogView.findViewById<EditText>(R.id.comment_edittext)
            var send_button = dialogView.findViewById<ImageButton>(R.id.send_button)
            var no_cmnt_txt = dialogView.findViewById<TextView>(R.id.no_cmnt_txt)
            var start_cmnt_txt = dialogView.findViewById<TextView>(R.id.start_cmnt_txt)
            var comment_recycle = dialogView.findViewById<RecyclerView>(R.id.comment_recycle)

            send_button.setOnClickListener {

                var comment = comment_edittext.text.toString()

                val currentUser = FirebaseAuth.getInstance().currentUser?.uid

                val userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser!!)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").getValue(String::class.java)

                        val profilePicRef = FirebaseDatabase.getInstance().getReference("userProfilePics")

                        profilePicRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            @SuppressLint("NewApi")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val profilePic = snapshot.child(currentUser).getValue(String::class.java)

                                val commentDb  = FirebaseDatabase.getInstance()
                                    .getReference("postComments")
                                    .child(postId!!)
                                    .child(currentUser)

                                val commentId  = commentDb.push().key!!

                                val commentMap = hashMapOf(
                                    "commentId"    to commentId,
                                    "userId"       to currentUser,
                                    "name"         to username,
                                    "profileImage" to profilePic,
                                    "comment"      to comment,
                                    "date"    to LocalDate.now().toString(), // server‑side time
                                    "time"    to LocalTime.now().toString() // server‑side time
                                )

                                commentDb.child(commentId).setValue(commentMap).addOnCompleteListener {task ->
                                    if (task.isSuccessful){
                                        comment_edittext.text.clear()
                                    }else {
                                        Toast.makeText(context, "Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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


                if (!comment.isBlank()){
                    val commentDb = FirebaseDatabase.getInstance().getReference("postComments")



                }

            }//send button



            bottomSheetDialog.show()

        }



    }


}
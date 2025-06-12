package com.devsyncit.instagramclone

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Sign_up : AppCompatActivity() {

    lateinit var login_with_fb: MaterialButton
    lateinit var sign_up_btn: MaterialButton
    lateinit var email: EditText
    lateinit var user_password: EditText
    lateinit var full_name: EditText
    lateinit var username: EditText
    lateinit var login_txt: TextView
    private lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        login_with_fb = findViewById(R.id.log_in_with_fb)
        sign_up_btn = findViewById(R.id.sign_up_btn)
        email = findViewById(R.id.email)
        user_password = findViewById(R.id.user_password)
        full_name = findViewById(R.id.full_name)
        username = findViewById(R.id.username)
        login_txt = findViewById(R.id.login_txt)
        progressBar = findViewById(R.id.progressBar)

        database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("users")

        auth = FirebaseAuth.getInstance()

        sign_up_btn.setOnClickListener {

            var user_email = email.text.toString()
            var user_password = user_password.text.toString()
            var user_full_name = full_name.text.toString()
            var user_name= username.text.toString()

            if (user_email.isBlank() || user_password.isBlank() || user_full_name.isBlank() || user_name.isBlank()) {

                Toast.makeText(this, "Please fill out the blank fields", Toast.LENGTH_LONG).show()

            }else{

                if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()){

                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_LONG).show()

                }else{

                    progressBar.visibility = View.VISIBLE

                    auth.createUserWithEmailAndPassword(user_email, user_password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                val user = auth.currentUser
                                val uid = user?.uid

                                val userInfo = mapOf(
                                    "fullname" to user_full_name,
                                    "username" to user_name,
                                    "email" to user_email,
                                    "password" to user_password,
                                    "pronounce" to "",
                                    "bio" to ""
                                )

                                dbRef.child(uid!!).setValue(userInfo).addOnSuccessListener {

                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                    finish()

                                }.addOnFailureListener{

                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                                    finish()

                                    }


                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, "Task is not successfull", Toast.LENGTH_SHORT).show()
                                finish()

                            }
                        }

                }

            }

        }

    }
}
package com.devsyncit.instagramclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sign

class Sign_in : AppCompatActivity() {

    lateinit var sign_up_txt: TextView
    lateinit var login: MaterialButton
    lateinit var useremail: EditText
    lateinit var password: EditText
    lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_up_txt = findViewById(R.id.sign_up_txt)
        login = findViewById(R.id.login_btn)
        useremail = findViewById(R.id.email)
        password = findViewById(R.id.password)


        database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("users")

        auth = FirebaseAuth.getInstance()

        sign_up_txt.setOnClickListener {
            startActivity(Intent(this, Sign_up::class.java))
        }

        login.setOnClickListener {

            var email = useremail.text.toString()
            var password = password.text.toString()

            if (email.isBlank() || password.isBlank()) {

                Toast.makeText(this, "Please fill out the blank fields", Toast.LENGTH_LONG).show()

            }else{

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_LONG).show()
                }else{

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeCycleObserver())

                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                val user = auth.currentUser
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()

                            } else {

                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

                            }
                        }


                }

            }

        }
    }
}
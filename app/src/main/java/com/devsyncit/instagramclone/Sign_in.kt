package com.devsyncit.instagramclone

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sign

class Sign_in : AppCompatActivity() {

    lateinit var sign_up_txt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_up_txt = findViewById(R.id.sign_up_txt)

        sign_up_txt.setOnClickListener {
            startActivity(Intent(this, Sign_up::class.java))
        }


    }
}
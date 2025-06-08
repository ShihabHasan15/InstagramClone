package com.devsyncit.instagramclone

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
import com.google.android.material.button.MaterialButton
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

                    val apiService = RetrofitInstance.getInstance().create(ApiService::class.java)

                    apiService.signupUser(user_email, user_password, user_full_name, user_name)
                        .enqueue(object : Callback<ResponseBody>{
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                val responseBody = response.body()!!.string()

                                Log.d("responseBody", responseBody)

                                val jsonObject = JSONObject(responseBody)

                                val isSuccess = jsonObject.getBoolean("success")

                                Toast.makeText(this@Sign_up, ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                                finish()

                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("responseBody", ""+t.toString())
                            }

                        })

                }

            }

        }

    }
}
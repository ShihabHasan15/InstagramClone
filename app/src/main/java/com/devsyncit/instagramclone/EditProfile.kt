package com.devsyncit.instagramclone

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditProfile : AppCompatActivity() {

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var profileImage: CircleImageView
    lateinit var change_profile_txt: TextView
    lateinit var progressBar: ProgressBar
    private var selectedBase64Image: String? = null
    lateinit var name_edittext: TextInputEditText
    lateinit var username_edittext: TextInputEditText
    lateinit var pronounce_edittext: TextInputEditText
    lateinit var bio_edittext: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val switchBack = findViewById<MaterialCardView>(R.id.switch_back)
        val toggleLayout = findViewById<LinearLayout>(R.id.toggle_layout)
        val switchToggle = findViewById<MaterialCardView>(R.id.switch_toggle)
        profileImage = findViewById(R.id.circleImageView)
        change_profile_txt = findViewById(R.id.change_profile_txt)
        progressBar = findViewById(R.id.progressBar)
        name_edittext = findViewById(R.id.name_edittext)
        username_edittext = findViewById(R.id.username_edittext)
        pronounce_edittext = findViewById(R.id.pronounce_edittext)
        bio_edittext = findViewById(R.id.bio_edittext)

        switchBack.setCardBackgroundColor(Color.LTGRAY)

        var isSwitchOn = false

        switchBack.setOnClickListener {
            isSwitchOn = !isSwitchOn

            val parentWidth = switchBack.width
            val toggleWidth = switchToggle.width

            // Calculate destination X position
            val translationX = if (isSwitchOn) {
                (parentWidth - toggleWidth - 6).toFloat() // 6 = paddingLeft + right margin buffer
            } else {
                0f
            }

            // Animate toggle movement
            switchToggle.animate()
                .translationX(translationX)
                .setDuration(250)
                .start()

            // Animate background color
            val colorFrom = if (isSwitchOn) Color.LTGRAY else Color.BLACK
            val colorTo = if (isSwitchOn) Color.BLACK else Color.LTGRAY

            val colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo)
            colorAnimation.duration = 250
            colorAnimation.addUpdateListener { animator ->
                switchBack.setCardBackgroundColor(animator.animatedValue as Int)
                if (isSwitchOn) {
                    // Toggle ON → move right, left padding = 0
                    toggleLayout.setPadding(0, 3, 3, 3)
                } else {
                    // Toggle OFF → move left, right padding = 0
                    toggleLayout.setPadding(3, 3, 0, 3)
                }
            }
            colorAnimation.start()
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == RESULT_OK && result.data != null){
                var imageUri = result.data!!.data
                lifecycleScope.launch {
                    try {

                        val compressedBase64Image = withContext(Dispatchers.IO) {
                            val inputStream = contentResolver.openInputStream(imageUri!!)
                            val originalBitmap = BitmapFactory.decodeStream(inputStream)

                            val exif = ExifInterface(contentResolver.openInputStream(imageUri)!!)
                            val orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )

                            val rotatedBitmap = rotateBitmapIfRequired(originalBitmap, orientation)
                            val resizedBitmap = resizeBitmap(rotatedBitmap, 800)

                            val stream = ByteArrayOutputStream()
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                            val compressedBytes = stream.toByteArray()

                            Base64.encodeToString(compressedBytes, Base64.DEFAULT) to resizedBitmap
                        }

                        val (base64Image, resizedBitmap) = compressedBase64Image
                        selectedBase64Image = base64Image
                        profileImage.setImageBitmap(resizedBitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        profileImage.setOnClickListener {
            Log.d("ProfileImage", "Clicked!")
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

    }

    private fun rotateBitmapIfRequired(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun resizeBitmap(original: Bitmap, maxSize: Int): Bitmap {
        var width = original.width
        var height = original.height

        val ratio = width.toFloat() / height

        if (ratio > 1) {
            width = maxSize
            height = (width / ratio).toInt()
        } else {
            height = maxSize
            width = (height * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(original, width, height, true)
    }

    override fun onBackPressed() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        //user data
        val uid = user?.uid
        val name = name_edittext.text.toString()
        val username = username_edittext.text.toString()
        val pronounce = pronounce_edittext.text.toString()
        val bio = bio_edittext.text.toString()

        progressBar.visibility = View.VISIBLE

        if (selectedBase64Image != null) {
            val database = FirebaseDatabase.getInstance().getReference("userProfilePics").child(uid!!)
            database.setValue(selectedBase64Image).addOnCompleteListener {
            Toast.makeText(this, "Profile Picture Updated", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            progressBar.visibility = View.GONE
            super.onBackPressed()
        }

        if (!name.isBlank() || !username.isBlank() || !pronounce.isBlank() || !bio.isBlank()){

            val userInfo = mapOf(
                "fullname" to name,
                "username" to username,
                "pronounce" to pronounce,
                "bio" to bio
            )

            val database = FirebaseDatabase.getInstance().getReference("users").child(uid!!)
            database.updateChildren(userInfo).addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
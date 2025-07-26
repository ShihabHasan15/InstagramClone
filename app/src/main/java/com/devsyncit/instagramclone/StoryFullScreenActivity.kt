package com.devsyncit.instagramclone

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime

class StoryFullScreenActivity : AppCompatActivity() {

    lateinit var story_back_btn: ImageButton
    lateinit var text_btn: ImageButton
    lateinit var star_btn: ImageButton
    lateinit var three_dot_btn: ImageButton
    lateinit var imageFrame: ImageView
    lateinit var story_txt: TextView
    lateinit var post_story_btn: MaterialButton
    lateinit var rootLayout: ConstraintLayout

    private var isDoubleTap = false

    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    var scaleFactor = 1.0f
    val minScale = 0.5f
    val maxScale = 3.0f

    private lateinit var fontList: List<Typeface?>
    private lateinit var colorList: List<Int?>
    private var currentFontIndex = 0
    private var currentColorIndex = 0

    private var dX = 0f
    private var dY = 0f

    val uid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_full_screen)

        story_back_btn = findViewById(R.id.story_back_btn)
        text_btn = findViewById(R.id.text_btn)
        star_btn = findViewById(R.id.stars)
        three_dot_btn = findViewById(R.id.three_dot)
        imageFrame = findViewById(R.id.image_frame)
        story_txt = findViewById(R.id.story_txt)
        rootLayout = findViewById(R.id.main)
        post_story_btn = findViewById(R.id.post_story_btn)

        val selectedImage = intent.getStringExtra("selectedImage")
        val selectedImageUri = Uri.parse(selectedImage)

        Glide.with(this)
            .load(selectedImageUri)
            .into(imageFrame)

        story_back_btn.setOnClickListener {
            onBackPressed()
        }

        text_btn.setOnClickListener {
            showTextInputDialog()
        }

        post_story_btn.setOnClickListener {
            captureAndUploadScreen()
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onDoubleTap(e: MotionEvent): Boolean {
                isDoubleTap = true
                changeFont()
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {

                if (isDoubleTap){
                    isDoubleTap = false
                    return true
                }

                changeColor()
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(minScale, maxScale)

                val newSize = 16f * scaleFactor
                story_txt.textSize = newSize
                return true
            }
        })


        rootLayout.setOnTouchListener { view, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            true
        }


        story_txt.setOnTouchListener { v, event ->

            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    v.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            true
        }

        fontList = listOf(
            getFont(this, R.font.alata),
            getFont(this, R.font.akatab_semibold),
            getFont(this, R.font.roboto),
            getFont(this, R.font.play_write),
            getFont(this, R.font.dancing_script)
        )

        colorList = listOf(
            Color.BLACK,
            Color.BLUE,
            Color.RED,
            Color.CYAN,
            Color.YELLOW,
            Color.GREEN,
            Color.LTGRAY,
            Color.MAGENTA,
            Color.WHITE
        )



    }


    private fun showToast(message: String){

        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()

    }

    private fun showTextInputDialog() {
        val input = EditText(this)
        input.hint = "Enter your text"

        AlertDialog.Builder(this)
            .setTitle("Write Story Text")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val userText = input.text.toString()
                if (userText.isNotEmpty()) {
                    story_txt.text = userText
                    story_txt.visibility = View.VISIBLE
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changeFont(){
        currentFontIndex = (currentFontIndex + 1) % fontList.size
        story_txt.typeface = fontList[currentFontIndex]
    }

    private fun changeColor(){
        currentColorIndex = (currentColorIndex + 1) % colorList.size
        story_txt.setTextColor(colorList[currentColorIndex]!!)
    }

    @SuppressLint("NewApi")
    private fun captureAndUploadScreen(){
        story_back_btn.visibility = View.GONE
        text_btn.visibility = View.GONE
        star_btn.visibility = View.GONE
        three_dot_btn.visibility = View.GONE
        post_story_btn.visibility = View.GONE

        val bitmap = Bitmap.createBitmap(rootLayout.width, rootLayout.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        rootLayout.draw(canvas)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)


        story_back_btn.visibility = View.VISIBLE
        text_btn.visibility = View.VISIBLE
        star_btn.visibility = View.VISIBLE
        three_dot_btn.visibility = View.VISIBLE
        post_story_btn.visibility = View.VISIBLE


        val storyDb = FirebaseDatabase.getInstance().getReference("Stories").child(uid!!)

        val storyMap = HashMap<String, Any>()

        storyMap.put("id",""+storyDb.push().key)
        storyMap.put("userId", uid)
        storyMap.put("story",""+base64String)
        storyMap.put("date", ""+LocalDate.now())
        storyMap.put("time", System.currentTimeMillis())

        storyDb.push().setValue(storyMap)
            .addOnSuccessListener {
                showToast("Story Uploaded Successfully")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener{
                showToast("Story Upload Failed")
            }

    }

}
package com.devsyncit.instagramclone

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import android.os.Handler
import android.util.Base64

class AudioCallActivity : AppCompatActivity() {

    private val appId = "5a0af225d1f24b4caa7630b2c9d6d7c2"
    private var channelName = ""
    private val token = ""
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid.hashCode()

    private var isJoined = false
    private var isMuted = false
    private var isSpeakerEnabled = false
    private var agoraEngine: RtcEngine? = null

    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
        )

    private var callDurationSeconds = 0
    private var callTimerHandler: Handler? = null
    private var callTimerRunnable: Runnable? = null

    lateinit var back_button: ImageButton
    lateinit var btn_mute: ImageButton
    lateinit var btn_end_call: ImageButton
    lateinit var btn_loud_speaker: ImageButton
    lateinit var userImage: CircleImageView
    lateinit var userFullName: TextView
    lateinit var callTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)

        back_button = findViewById(R.id.back_button)
        btn_mute = findViewById(R.id.btn_mute)
        btn_end_call = findViewById(R.id.btn_end_call)
        btn_loud_speaker = findViewById(R.id.btn_loud_speaker)
        userImage = findViewById(R.id.userImage)
        userFullName = findViewById(R.id.userFullName)
        callTimer = findViewById(R.id.callTimer)

        channelName = intent.getStringExtra("channelName").toString()
        var userId = intent.getStringExtra("userId").toString()
        var userfullname = intent.getStringExtra("userFullName").toString()
        var userProfileImage = intent.getStringExtra("profileImage").toString()

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, PERMISSION_REQ_ID)
        } else {
            setupAudioCall()
        }


        userProfileImage.let {
            val decoded = Base64.decode(it, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            userImage.setImageBitmap(bitmap)
        }

        userFullName.text = userfullname

        back_button.setOnClickListener {
            leaveCall()
            finish()
            onBackPressed()
        }

        btn_end_call.setOnClickListener {
            leaveCall()
            stopCallTimer()
            finish()
        }

        btn_loud_speaker.setOnClickListener {
            isSpeakerEnabled = !isSpeakerEnabled
            agoraEngine?.setEnableSpeakerphone(isSpeakerEnabled)

            val status = if (isSpeakerEnabled) "Speaker On" else "Speaker Off"
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        btn_mute.setOnClickListener {
            toggleMute()
        }

    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAudioCall() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = rtcEventHandler
            agoraEngine = RtcEngine.create(config)

            agoraEngine?.disableVideo() // IMPORTANT
            agoraEngine?.enableAudio()
            agoraEngine?.setEnableSpeakerphone(true)

            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

            agoraEngine?.joinChannel(token, channelName, uid, option)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun leaveCall() {
        if (!isJoined) {
            Toast.makeText(this, "Join a channel first", Toast.LENGTH_SHORT).show()
        }else{
            agoraEngine?.leaveChannel()
            Toast.makeText(this, "You left the call", Toast.LENGTH_SHORT).show()
            isJoined = false
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.leaveChannel()
        RtcEngine.destroy()
        agoraEngine = null
    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                isJoined = true
                Toast.makeText(this@AudioCallActivity, "Joined Audio Channel", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            startCallTimer()
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Toast.makeText(this@AudioCallActivity, "User Left", Toast.LENGTH_SHORT).show()
            }
            stopCallTimer()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            setupAudioCall()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun startCallTimer() {

        callTimerHandler = Handler(Looper.getMainLooper())
        callTimerRunnable = object : Runnable {
            override fun run() {
                callDurationSeconds++
                val hours = callDurationSeconds / 3600
                val minutes = (callDurationSeconds % 3600) / 60
                val seconds = callDurationSeconds % 60

                callTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                callTimerHandler?.postDelayed(this, 1000)
            }
        }
        callTimerHandler?.post(callTimerRunnable!!)
    }

    private fun stopCallTimer() {
        callTimerHandler?.removeCallbacks(callTimerRunnable!!)
        callTimerHandler = null
        callTimerRunnable = null
        callDurationSeconds = 0
    }

    private fun toggleMute() {
        isMuted = !isMuted
        agoraEngine?.muteLocalAudioStream(isMuted)
        btn_mute.alpha = if (isMuted) 0.5f else 1.0f // Visual feedback
        val status = if (isMuted) "Muted" else "Unmuted"
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
    }

}
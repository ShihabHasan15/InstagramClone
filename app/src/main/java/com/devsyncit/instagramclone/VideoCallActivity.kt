package com.devsyncit.instagramclone

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VideoCallActivity : AppCompatActivity() {

    private val appId = "5a0af225d1f24b4caa7630b2c9d6d7c2"
    private var channelName = ""
    private val token = ""
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid.hashCode()

    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )

    private var isJoined = false
    private var isMuted = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null


    lateinit var video_call_root: FrameLayout
    lateinit var remote_user: FrameLayout
    lateinit var local_user: FrameLayout
    lateinit var userInfoLayout: LinearLayout
    lateinit var callControlButtons: LinearLayout
    lateinit var userProfileImage: CircleImageView
    lateinit var userNameText: TextView
    lateinit var btn_mute: ImageButton
    lateinit var btn_end_call: ImageButton
    lateinit var btn_switch_camera: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)


        video_call_root = findViewById(R.id.video_call_root)
        remote_user = findViewById(R.id.remote_user)
        local_user = findViewById(R.id.local_user)
        userInfoLayout = findViewById(R.id.userInfoLayout)
        callControlButtons = findViewById(R.id.callControlButtons)
        userProfileImage = findViewById(R.id.userProfileImage)
        userNameText = findViewById(R.id.userNameText)
        btn_mute = findViewById(R.id.btn_mute)
        btn_end_call = findViewById(R.id.btn_end_call)
        btn_switch_camera = findViewById(R.id.btn_switch_camera)

        channelName = intent.getStringExtra("channelName").toString()
        var userId = intent.getStringExtra("userId").toString()
        var userfullname = intent.getStringExtra("userFullName").toString()
        var ProfileImage = intent.getStringExtra("profileImage").toString()

        setUpVideoSdkEngine()

        // Permission check
        if (!checkSelfPermission()) {
            ActivityCompat
                .requestPermissions(
                    this, REQUESTED_PERMISSION, PERMISSION_REQ_ID
                )
        }else{
            joinCall()
        }


        ProfileImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            userProfileImage.setImageBitmap(bitmap)
        }

        userNameText.text = userfullname

        btn_mute.setOnClickListener {
            toggleMute()
        }

        btn_end_call.setOnClickListener {
            leaveCall()
            finish()
        }

        btn_switch_camera.setOnClickListener {
            switchCamera()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        leaveCall()
        finish()
    }


    private fun checkSelfPermission(): Boolean{

        return !(ContextCompat.checkSelfPermission(
            this, REQUESTED_PERMISSION[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this, REQUESTED_PERMISSION[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String){
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun joinCall() {

        if (checkSelfPermission()){
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            agoraEngine!!.startPreview()
            agoraEngine?.setEnableSpeakerphone(true)
            setUpLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            agoraEngine!!.joinChannel(token, channelName, uid, option)
        }

    }

    private fun leaveCall(){

        if (!isJoined){
            showMessage("Join a channel first")
        }else{
            agoraEngine!!.leaveChannel()
            showMessage("You left the call")
            if (remoteSurfaceView!=null) remoteSurfaceView!!.visibility = View.GONE
            if (localSurfaceView!=null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }

    }

    private fun setUpVideoSdkEngine(){
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.message.toString())
        }
    }
    
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined")

            runOnUiThread {
                setUpRemoteVideo(uid)
                local_user.bringToFront()
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("User Offline")

            runOnUiThread {
                remoteSurfaceView!!.visibility = View.GONE
            }
        }

        override fun onConnectionLost() {
            runOnUiThread {
                showMessage("Connection lost. Trying to reconnect...")
            }
        }

        override fun onConnectionInterrupted() {
            runOnUiThread {
                showMessage("Connection interrupted. Please check your network.")
            }
        }

    }

    private fun setUpRemoteVideo(uid: Int){
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        remote_user.addView(remoteSurfaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uid
            )
        )
    }

    private fun setUpLocalVideo(){
        localSurfaceView = SurfaceView(baseContext)
        localSurfaceView!!.setZOrderMediaOverlay(true)
        local_user.addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun toggleMute() {
        isMuted = !isMuted
        agoraEngine?.muteLocalAudioStream(isMuted)
        btn_mute.alpha = if (isMuted) 0.5f else 1.0f // Visual feedback
        showMessage(if (isMuted) "Muted" else "Unmuted")
    }

    private fun switchCamera() {
        agoraEngine?.switchCamera()
        showMessage("Camera switched")
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                joinCall()
            } else {
                showMessage("Permissions required for video call.")
                finish()
            }
        }
    }

}

package com.devsyncit.instagramclone

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    
    lateinit var bottomnav: BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var messengerIcon: ImageView
    var homeFragment = HomeFragment()
    var profileFragment = ProfileFragment()
    var postUploadFragment = PostUploadFragment()
    var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomnav = findViewById(R.id.bottom_nav)
        toolbar = findViewById(R.id.toolbar)
        messengerIcon = findViewById(R.id.messenger_icon)

        askNotificationPermission()

        messengerIcon.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }


        val username = intent.getStringExtra("username")

        val bundle = Bundle()
        bundle.putString("username", username)

        homeFragment.arguments = bundle
        profileFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.frame, profileFragment, "Profile").hide(profileFragment)
            .add(R.id.frame, postUploadFragment, "Post Upload").hide(postUploadFragment)
            .add(R.id.frame, homeFragment, "Home")
            .commit()


        bottomnav.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                if (item.itemId == R.id.home){

                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(homeFragment)
                        .commit()

                    activeFragment = homeFragment
                    toolbar.visibility = View.VISIBLE

                }else if (item.itemId == R.id.profile){

                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(profileFragment)
                        .commit()

                    activeFragment = profileFragment
                    toolbar.visibility = View.GONE

                }else if (item.itemId == R.id.post){

                    val anchorView = findViewById<View>(R.id.post) // menuPost is the nav item id
                    val inflater = LayoutInflater.from(this@MainActivity)
                    val popupView = inflater.inflate(R.layout.post_or_story_bottom_sheet, null)

                    val popupWindow = PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                    )

                    popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    popupWindow.isOutsideTouchable = true
                    popupWindow.elevation = 10f


                    val rootView = window.decorView as ViewGroup
                    val dimView = View(this@MainActivity)
                    dimView.setBackgroundColor(Color.parseColor("#80000000")) // 50% black
                    dimView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    rootView.addView(dimView)

                    popupWindow.setOnDismissListener {
                        rootView.removeView(dimView)
                    }


                    popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val popupHeight = popupView.measuredHeight


                    val location = IntArray(2)
                    anchorView.getLocationOnScreen(location)
                    val x = location[0] + anchorView.width / 2 - popupView.measuredWidth / 2
                    val y = location[1] - popupHeight

                    popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)


                    val postTxt = popupView.findViewById<LinearLayout>(R.id.postTxt)
                    val storyTxt = popupView.findViewById<LinearLayout>(R.id.storyTxt)

                    postTxt.setOnClickListener {
                        popupWindow.dismiss()
                         startActivity(Intent(this@MainActivity, PostUpload::class.java))
                    }

                    storyTxt.setOnClickListener {
                        popupWindow.dismiss()
                        startActivity(Intent(this@MainActivity, StoryUpload::class.java))
                    }



//                    supportFragmentManager.beginTransaction()
//                        .hide(activeFragment)
//                        .show(postUploadFragment)
//                        .commit()
//
//                    activeFragment = postUploadFragment
//                    toolbar.visibility = View.GONE
                }

                return true
            }

        })

    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

                android.app.AlertDialog.Builder(this)
                    .setTitle("Notification Permission")
                    .setMessage("Permission needed for better experience")
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    })
                    .create().show()

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onBackPressed() {
        when (activeFragment) {
            profileFragment -> {
                bottomnav.selectedItemId = R.id.home
            }
            postUploadFragment -> {
                bottomnav.selectedItemId = R.id.home
            }
            homeFragment -> {
                super.onBackPressed()
            }
        }
    }

}
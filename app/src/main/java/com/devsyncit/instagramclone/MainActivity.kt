package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    
    lateinit var bottomnav: BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    var homeFragment = HomeFragment()
    var profileFragment = ProfileFragment()
    var postUploadFragment = PostUploadFragment()
    var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomnav = findViewById(R.id.bottom_nav)
        toolbar = findViewById(R.id.toolbar)

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
}
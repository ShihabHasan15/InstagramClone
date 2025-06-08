package com.devsyncit.instagramclone

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    lateinit var bottomnav: BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    var homeFragment = HomeFragment()
    var profileFragment = ProfileFragment()
    var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomnav = findViewById(R.id.bottom_nav)
        toolbar = findViewById(R.id.toolbar)

        supportFragmentManager.beginTransaction()
            .add(R.id.frame, profileFragment, "Profile").hide(profileFragment)
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

                }

                return true
            }

        })

    }
}
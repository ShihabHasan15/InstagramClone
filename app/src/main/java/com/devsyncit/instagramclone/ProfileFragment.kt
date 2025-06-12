package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewpager: ViewPager2
    var postFragment = PostsFragment()
    lateinit var edit_profile: MaterialButton
    var activeFragment: Fragment = postFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tabLayout = view.findViewById(R.id.tablayout)
        viewpager = view.findViewById(R.id.view_pager)
        edit_profile = view.findViewById(R.id.edit_profile)



        edit_profile.setOnClickListener {
            var intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }


        val adapter = TabPagerAdapter(this)
        viewpager.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager){tab, position ->
            tab.setIcon(when (position) {
                0 -> R.drawable.grid
                1 -> R.drawable.reel
                2 -> R.drawable.user_photo_video
                else -> R.drawable.grid
            })
        }.attach()

        tabLayout.getTabAt(0)!!.icon!!.setTint(Color.BLACK)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabPosition = tab!!.position
                tab.icon!!.setTint(Color.BLACK)

                if (tabPosition==0){

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.getIcon()!!.setTint(Color.GRAY);
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


        return view
    }
}
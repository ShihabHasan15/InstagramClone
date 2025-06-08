package com.devsyncit.instagramclone

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment() {

    lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tabLayout = view.findViewById(R.id.tablayout)

        tabLayout.getTabAt(0)!!.icon!!.setTint(Color.BLACK)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabPosition = tab!!.position
                tab.icon!!.setTint(Color.BLACK)
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
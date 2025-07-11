package com.devsyncit.instagramclone

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PostsFragment()
            1 -> ProfileReelsFragment()
            2 -> ProfilePhotosAndVideosFragment()
            else -> PostsFragment()
        }
    }
}
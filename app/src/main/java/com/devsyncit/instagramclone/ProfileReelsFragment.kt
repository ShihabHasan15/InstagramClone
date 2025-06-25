package com.devsyncit.instagramclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ProfileReelsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val profileReelsView = inflater.inflate(R.layout.fragment_profile_reels, container, false)

        return profileReelsView
    }
}
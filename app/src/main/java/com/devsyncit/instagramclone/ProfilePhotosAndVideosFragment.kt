package com.devsyncit.instagramclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ProfilePhotosAndVideosFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val profilePhotosAndVideosView = inflater.inflate(R.layout.fragment_profile_photos_and_videos, container, false)

        return profilePhotosAndVideosView
    }

}
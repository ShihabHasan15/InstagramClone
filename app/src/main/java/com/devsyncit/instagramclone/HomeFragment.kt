package com.devsyncit.instagramclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    lateinit var story_list: RecyclerView
    lateinit var feed_list: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        story_list = view.findViewById(R.id.story_list)
        feed_list = view.findViewById(R.id.feed_list)


        return view
    }
}
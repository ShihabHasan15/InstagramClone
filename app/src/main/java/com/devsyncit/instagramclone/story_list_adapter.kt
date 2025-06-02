package com.devsyncit.instagramclone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class story_list_adapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var MY_STORY = 1
    var OTHER_STORY = 0

    class my_story_holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class other_story_holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun getItemViewType(position: Int): Int {

        if (position==0) return MY_STORY
        else return OTHER_STORY

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType==MY_STORY){
            var view = LayoutInflater.from(context).inflate(R.layout.my_story_design, parent, false)

            return my_story_holder(view)
        }else{
            var view = LayoutInflater.from(context).inflate(R.layout.other_story_design, parent, false)

            return other_story_holder(view)
        }

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}
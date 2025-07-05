package com.devsyncit.instagramclone

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class story_list_adapter(var context: Context, var storyList: List<Story>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var MY_STORY = 1
    var OTHER_STORY = 0

    class my_story_holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var myImage = itemView.findViewById<ImageView>(R.id.my_story)

    }

    class other_story_holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username = itemView.findViewById<TextView>(R.id.name)
        var userimage = itemView.findViewById<CircleImageView>(R.id.image)

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

        return storyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position)==MY_STORY){

            var my_story_holder = holder as my_story_holder

                var my_story = storyList.get(position)

                var content = my_story.content
                var userName = my_story.userName
                var userImage = my_story.userImage

                my_story_holder.myImage.setImageResource(userImage)

        }else{

            var other_story_holder = holder as other_story_holder


                var other_story = storyList.get(position)

                var content = other_story.content
                var userName = other_story.userName
                var userImage = other_story.userImage

            other_story_holder.username.text = userName
            other_story_holder.userimage.setImageResource(userImage)

        }




    }

}
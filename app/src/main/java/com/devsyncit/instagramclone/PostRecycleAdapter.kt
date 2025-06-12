package com.devsyncit.instagramclone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class PostRecycleAdapter(var context: Context, var imageList: List<Int>): RecyclerView.Adapter<PostRecycleAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View): ViewHolder(itemView) {

        var imageView = itemView.findViewById<ImageView>(R.id.image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.post_grid_item, parent, false)

        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {

        return imageList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        var image = imageList.get(position)

        holder.imageView.setImageResource(image)

    }

}
package com.devsyncit.instagramclone

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.FirebaseDatabase

class PostRecycleAdapter(var context: Context, var imageList: List<String>, var postList: List<String>): RecyclerView.Adapter<PostRecycleAdapter.PostViewHolder>() {

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
        var postId = postList.get(position)

        image.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            holder.imageView.setImageBitmap(bitmap)
        }

        holder.imageView.setOnClickListener {
            var intent = Intent(context, SpecificPost::class.java)
            intent.putExtra("postId", postId)
            context.startActivity(intent)
        }

    }

}
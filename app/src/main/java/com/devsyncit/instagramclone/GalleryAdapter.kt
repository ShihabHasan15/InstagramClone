package com.devsyncit.instagramclone

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

class GalleryAdapter(val imageList: List<Uri>): RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageViewThumbnail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_galery_image, parent, false)

        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {

        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .load(imageList[position])
            .centerCrop()
            .into(holder.imageView)

    }

}
package com.devsyncit.instagramclone

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class CommentRecycleAdapter(var context: Context, var commentList: List<HashMap<String, String>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class commentViewHolder(itemView: View): ViewHolder(itemView) {

        var profileImage = itemView.findViewById<CircleImageView>(R.id.profileImage)
        var name_txt = itemView.findViewById<TextView>(R.id.name_txt)
        var comment_txt = itemView.findViewById<TextView>(R.id.comment_txt)
        var reply_txt = itemView.findViewById<TextView>(R.id.reply_txt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var commentView = LayoutInflater.from(context).inflate(R.layout.comment_design, parent, false)

        return commentViewHolder(commentView)
    }

    override fun getItemCount(): Int {

        return commentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var commentHolder = holder as commentViewHolder

        val comment = commentList[position]

        val commentText = comment["comment"]
        val commentId = comment["commentId"]
        val commenterName = comment["commenterName"]
        val profileImage = comment["profileImage"]
        val userId = comment["userId"]
        val date = comment["date"]
        val time = comment["time"]



        commentHolder.comment_txt.text = commentText
        commentHolder.name_txt.text = commenterName

       profileImage.let {
            val decoded = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

            commentHolder.profileImage.setImageBitmap(bitmap)
       }

    }
}
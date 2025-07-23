package com.devsyncit.instagramclone

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StoryUpload : AppCompatActivity() {

    lateinit var text_story: LinearLayout
    lateinit var template_story: LinearLayout
    lateinit var boomerang_story: LinearLayout
    lateinit var recyclerViewGallery: RecyclerView
    lateinit var adapter: GalleryAdapter
    var imageList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_upload)

        text_story = findViewById(R.id.text_story)
        template_story = findViewById(R.id.template_story)
        boomerang_story = findViewById(R.id.boomerang_story)
        recyclerViewGallery = findViewById(R.id.recyclerViewGallery)

        adapter = GalleryAdapter(imageList)
        recyclerViewGallery.layoutManager = GridLayoutManager(this,3)
        recyclerViewGallery.adapter = adapter

        if (checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            loadGalleryImages()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                100)
        }
    }

    fun loadGalleryImages(){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(uri, projection, null, null,
            MediaStore.Images.Media.DATE_ADDED + " DESC")

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media._ID
            )

            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(uri, id)
                imageList.add(imageUri)
            }
        }
        Log.d("imageCount", ""+imageList.size)
        adapter.notifyDataSetChanged()
    }
}
package com.devsyncit.instagramclone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostsFragment : Fragment() {
    lateinit var postRecycle: RecyclerView
    lateinit var dbRef: DatabaseReference

    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser

    var uid = user?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var postsView = inflater.inflate(R.layout.fragment_posts, container, false)

        postRecycle = postsView.findViewById(R.id.postRecycle)

        var imageList = ArrayList<String>()

        dbRef = FirebaseDatabase.getInstance().getReference("Posts")

        Log.d("userId", uid.toString())

        dbRef.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                imageList.clear()

                Log.d("childCount", snapshot.childrenCount.toString())

                for (postSnap in snapshot.children){
                    val postKey = postSnap.key

                    val image = postSnap.child("image").getValue(String::class.java)
                    Log.d("images", image!!)
                    imageList.add(image!!)
                }

                Log.d("imageList", imageList.size.toString())

                var postRecycleAdapter = PostRecycleAdapter(requireContext(), imageList)

                postRecycle.adapter = postRecycleAdapter

                var gridLayoutManager = GridLayoutManager(requireContext(),3)

                postRecycle.layoutManager = gridLayoutManager

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return postsView
    }

//    fun setGridViewHeight(gridView: GridView) {
//        val adapter = gridView.adapter ?: return
//        val numColumns = gridView.numColumns
//        val totalItems = adapter.count
//        val rows = if (totalItems % numColumns == 0) {
//            totalItems / numColumns
//        } else {
//            (totalItems / numColumns) + 1
//        }
//
//        val listItem = adapter.getView(0, null, gridView)
//        listItem.measure(0, 0)
//        val itemHeight = listItem.measuredHeight
//        val totalHeight = itemHeight * rows + (gridView.verticalSpacing * (rows - 1))
//
//        val params = gridView.layoutParams
//        params.height = totalHeight
//        gridView.layoutParams = params
//    }
}
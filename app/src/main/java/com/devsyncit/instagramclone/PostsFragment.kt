package com.devsyncit.instagramclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostsFragment : Fragment() {
    lateinit var postRecycle: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var postsView = inflater.inflate(R.layout.fragment_posts, container, false)

        postRecycle = postsView.findViewById(R.id.postRecycle)

        var imageList = listOf(
            R.drawable.shihab, R.drawable.shihab,
            R.drawable.shihab, R.drawable.shihab,
            R.drawable.shihab, R.drawable.shihab,
            R.drawable.shihab, R.drawable.shihab,
            R.drawable.shihab, R.drawable.shihab
        )

        var postRecycleAdapter = PostRecycleAdapter(requireContext(), imageList)

        postRecycle.adapter = postRecycleAdapter

        var gridLayoutManager = GridLayoutManager(requireContext(),3)

        postRecycle.layoutManager = gridLayoutManager

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
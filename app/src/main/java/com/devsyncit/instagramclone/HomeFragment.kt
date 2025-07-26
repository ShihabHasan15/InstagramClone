package com.devsyncit.instagramclone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    lateinit var story_list: RecyclerView
    lateinit var feed_list: RecyclerView

    lateinit var storyMap: HashMap<String, Any>

    lateinit var dbRef: DatabaseReference

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        story_list = view.findViewById(R.id.story_list)
        feed_list = view.findViewById(R.id.feed_list)

        val followersStoryList = mutableListOf<Story>()

        var story_list_adapter = story_list_adapter(requireContext(), followersStoryList)

        story_list.adapter = story_list_adapter

        story_list.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        val userDb = FirebaseDatabase.getInstance().getReference("users")

        userDb.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child("fullname").getValue(String::class.java)

                val profileImageDb = FirebaseDatabase.getInstance().getReference("userProfilePics")

                profileImageDb.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val profileImage = snapshot.child(uid).getValue(String::class.java)

                        followersStoryList.removeAll { it.userName == name }

                        var story = Story(userImage = profileImage, userName = name)

                        followersStoryList.add(0, story)

                        story_list_adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        val followersDb = FirebaseDatabase.getInstance().getReference("followers").child(uid!!)

        followersDb.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (followSnap in snapshot.children){
                    val name = followSnap.child("name").getValue(String::class.java)
                    val profileImage = followSnap.child("profileImage").getValue(String::class.java)
                    val userId = followSnap.child("userId").getValue(String::class.java)

                    val storyDb = FirebaseDatabase.getInstance().getReference("Stories")

                    storyDb.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for(userSnapshot in snapshot.children){
                                val storyDbUserId = userSnapshot.key

                                if (userId.equals(storyDbUserId)){
                                    val story = Story(profileImage, name)
                                    followersStoryList.add(story)
                                    story_list_adapter.notifyDataSetChanged()
                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


//        var storyList = listOf(
//            Story("", R.drawable.shihab, "shi_15"),
//            Story("", R.drawable.zilani, "mdzilani"),
//            Story("", R.drawable.nobel, "nazmul_nobel"),
//            Story("", R.drawable.emon, "emon_03"),
//            Story("", R.drawable.riaz, "riaz.mahmud"),
//            Story("", R.drawable.jubayer, "jubayer.hossain"),
//            Story("", R.drawable.elon, "eLon_rev_musk"),
//            Story("", R.drawable.billgates, "billgates")
//        )




        //story setting









        //feed setting
        dbRef = FirebaseDatabase.getInstance().getReference("followers")
        val feedPosts = mutableListOf<HashMap<String, String>>()
        val addedPostIds = HashSet<String>()

//adapter set

        var feedRecycleAdapter = FeedRecycleAdapter(requireContext(), feedPosts)

        feed_list.adapter = feedRecycleAdapter

        feed_list.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        //firebase operation

        dbRef.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                for (child in snapshot.children) {

                    val userId = child.child("userId").getValue(String::class.java)
                    val name = child.child("name").getValue(String::class.java)
                    val followedAt = child.child("followedAt").getValue(String::class.java)
                    val profileImage = child.child("profileImage").getValue(String::class.java)

                    if (userId != null) {

                        val db = FirebaseDatabase.getInstance().getReference("Posts")

                        db.child(userId).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                for (post in snapshot.children) {

                                    var postId = post.child("id").getValue(String::class.java)


                                    if (postId != null && !addedPostIds.contains(postId)) {
                                        addedPostIds.add(postId)

                                        var image = post.child("image").getValue(String::class.java)
                                        var time = post.child("time").getValue(String::class.java)
                                        var caption = post.child("caption").getValue(String::class.java)
                                        var date = post.child("date").getValue(String::class.java)

                                        val postMap = HashMap<String, String>()
                                        postMap.put("userId", userId!!)
                                        postMap.put("name", name!!)
                                        postMap.put("followedAt", followedAt!!)
                                        postMap.put("profileImage", profileImage!!)
                                        postMap.put("caption", caption!!)
                                        postMap.put("date", date!!)
                                        postMap.put("postId", postId!!)
                                        postMap.put("image", image!!)
                                        postMap.put("time", time!!)

                                        val likeDb = FirebaseDatabase.getInstance().getReference("postlikes")

                                        likeDb.child(postId).addListenerForSingleValueEvent(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {

                                               // var like = snapshot.child("like").value?.toString() ?: "0"

                                                val like = snapshot.child("like").value?.toString() ?: "0"

                                                postMap.put("like", like)

                                                Log.d("like", like)
                                                feedPosts.add(postMap)


                                                feedRecycleAdapter.notifyDataSetChanged()

                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }
                                        })



                                    }//if statement ends
                                }//for loop ends

//                                var feedRecycleAdapter = FeedRecycleAdapter(requireContext(), feedPosts)
//
//                                feed_list.adapter = feedRecycleAdapter
//
//                                feed_list.layoutManager = LinearLayoutManager(
//                                    requireContext(),
//                                    LinearLayoutManager.VERTICAL,
//                                    false
//                                )
//
//                                feedRecycleAdapter.notifyDataSetChanged()



                            }//onDataChange ends

                            override fun onCancelled(error: DatabaseError) {


                            }

                        })//value event listener of post node

                    }//if statement ends
                }//for loop ends
            }//onDataChanged ends

            override fun onCancelled(error: DatabaseError) {

            }

        })//addValueEvent Listener ends


        return view
    }


}
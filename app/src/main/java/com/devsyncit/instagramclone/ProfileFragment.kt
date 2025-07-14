package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineStart

class ProfileFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewpager: ViewPager2
    var postFragment = PostsFragment()
    lateinit var edit_profile: MaterialButton
    lateinit var profile_image: CircleImageView
    lateinit var databaseRef: DatabaseReference
    lateinit var usernameTxt: TextView
    lateinit var nameTxt: TextView
    lateinit var bioTxt: TextView
    lateinit var fb_name: TextView
    var activeFragment: Fragment = postFragment

    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser

    var uid = user?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tabLayout = view.findViewById(R.id.tablayout)
        viewpager = view.findViewById(R.id.view_pager)
        edit_profile = view.findViewById(R.id.edit_profile)
        profile_image = view.findViewById(R.id.profile_image)
        usernameTxt = view.findViewById(R.id.username)
        nameTxt = view.findViewById(R.id.name)
        bioTxt = view.findViewById(R.id.bio)
        fb_name = view.findViewById(R.id.fb_name)



        edit_profile.setOnClickListener {
            var intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }


        val adapter = TabPagerAdapter(this)
        viewpager.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager){tab, position ->
            tab.setIcon(when (position) {
                0 -> R.drawable.grid
                1 -> R.drawable.reel
                2 -> R.drawable.user_photo_video
                else -> R.drawable.grid
            })
        }.attach()

        tabLayout.getTabAt(0)!!.icon!!.setTint(Color.BLACK)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabPosition = tab?.position
                tab?.icon?.setTint(Color.BLACK)

                if (tabPosition==0){

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.getIcon()?.setTint(Color.GRAY);
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


        return view
    }

    override fun onStart() {

        databaseRef = FirebaseDatabase.getInstance().getReference("userProfilePics")

        databaseRef.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var encodedImg = snapshot.getValue(String::class.java)

                encodedImg?.let {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)

                    profile_image.setImageBitmap(bitmap)

                }

            }

            override fun onCancelled(error: DatabaseError) {

                Log.d("why", error.toString())

            }
        })


        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        databaseRef.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var username = snapshot.child("username").getValue(String::class.java)

                usernameTxt.text = username

                var name = snapshot.child("fullname").getValue(String::class.java)

                nameTxt.text = name
                fb_name.text = name
                 
                var bio = snapshot.child("bio").getValue(String::class.java)

                bioTxt.text = bio

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        super.onStart()
    }

}
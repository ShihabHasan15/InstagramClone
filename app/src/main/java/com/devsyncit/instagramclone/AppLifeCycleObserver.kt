package com.devsyncit.instagramclone

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AppLifeCycleObserver: LifecycleObserver {

    private val handler = Handler(Looper.getMainLooper())

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null){
            FirebaseDatabase.getInstance().getReference("ActiveStatus")
                .child(uid)
                .child("active")
                .setValue(true)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null){
            val dbRef = FirebaseDatabase.getInstance().getReference("ActiveStatus").child(uid)

            dbRef.child("active").setValue(false)
            dbRef.child("lastSeen").setValue(System.currentTimeMillis())
        }
    }


}
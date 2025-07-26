package com.devsyncit.instagramclone

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("MyApp", "Application started, registering lifecycle observer")

//        val statePluginFactory = StreamStatePluginFactory(
//            config = StatePluginConfig(
//                backgroundSyncEnabled = true,
//                userPresence = true,
//            ),
//            appContext = applicationContext
//        )
//
//        val offlinePluginFactory = StreamOfflinePluginFactory(applicationContext)
//
//        ChatClient.Builder(getString(R.string.stream_api_key), applicationContext)
//            .withPlugins(statePluginFactory, offlinePluginFactory)
//            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
//            .build()

    }
}

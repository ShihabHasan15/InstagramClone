package com.devsyncit.instagramclone

import android.app.Application
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.common.feature.messages.composer.MessageComposerController

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = applicationContext
        )

        val offlinePluginFactory = StreamOfflinePluginFactory(applicationContext)

        ChatClient.Builder(getString(R.string.stream_api_key), applicationContext)
            .withPlugins(statePluginFactory, offlinePluginFactory)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .build()
    }
}

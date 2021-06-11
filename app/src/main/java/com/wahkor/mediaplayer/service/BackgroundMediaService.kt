package com.wahkor.mediaplayer.service

import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService

class BackgroundMediaService: MediaBrowserService() {
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

}
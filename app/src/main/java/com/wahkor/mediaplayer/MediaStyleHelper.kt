package com.wahkor.mediaplayer

import android.content.Context
import android.media.session.MediaSession
import androidx.core.app.NotificationCompat
import com.wahkor.mediaplayer.receiver.AudioReceiver


object MediaStyleHelper {
    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of [MediaMetadataCompat.getDescription] to extract the appropriate information.
     * @param context Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    fun from(
        context: Context, mediaSession: MediaSession
    ): NotificationCompat.Builder {
        val controller= mediaSession.controller
        val mediaMetadata=controller.metadata
        val description= mediaMetadata?.description
        val channelId="123654"
        val builder = NotificationCompat.Builder(context,channelId)
        builder
            .setContentTitle(description?.title)
            .setContentText(description?.subtitle)
            .setSubText(description?.description)
            .setLargeIcon(description?.iconBitmap)
            .setContentIntent(controller.sessionActivity)
            //.setDeleteIntent(            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return builder
    }
}
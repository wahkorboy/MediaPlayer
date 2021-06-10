import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.os.Bundle
import android.os.PowerManager
import android.service.media.MediaBrowserService
import android.text.TextUtils
import com.wahkor.mediaplayer.receiver.AudioReceiver


class BackgroundMediaService(context: Context?) :MediaBrowserService(), MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener{
    private var mediaPlayer:MediaPlayer?=null
    private var mediaSession:MediaSession?=null
    private var mNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
            }
        }
    }
    private var mediaSessionCallback= object :MediaSession.Callback(){
        override fun onPlay() {
            super.onPlay()
        }

        override fun onPause() {
            super.onPause()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
        }

    }





    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            BrowserRoot(getString(R.string.ok), null)
        } else null

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        result.sendResult(null);
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        AudioReceiver().handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();
    }

    private fun initNoisyReceiver() {
        mediaPlayer= MediaPlayer()
        mediaPlayer!!.setWakeMode(applicationContext,PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setVolume(1.0f,1.0f)
    }

    private fun initMediaSession() {
    }

    private fun initMediaPlayer() {
    }
}
package com.wahkor.mediaplayer;


import android.content.ComponentName;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wahkor.mediaplayer.service.AudioService;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;

    private Button mPlayPauseToggleButton;

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
            mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
            MediaControllerCompat.setMediaController(MainActivity.this,mMediaControllerCompat);
            //setSupportMediaController(mMediaControllerCompat);
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().playFromMediaId(String.valueOf(R.raw.abandonedluna), null);

        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    mCurrentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayPauseToggleButton = (Button) findViewById(R.id.button);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, AudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mCurrentState == STATE_PAUSED ) {
                    MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
                    mCurrentState = STATE_PLAYING;
                } else {
                    if( MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                        MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
                    }

                    mCurrentState = STATE_PAUSED;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
        }

        mMediaBrowserCompat.disconnect();
    }
}

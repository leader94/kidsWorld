package com.ps.kidsworld.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.ps.kidsworld.R;


public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.background_music);
        player.setLooping(true); // Set looping
        player.setVolume(50, 50);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (player != null) {
            player.start();
        }
        return START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onLowMemory() {

    }
}

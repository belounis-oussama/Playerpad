package com.example.playerpad;

import static com.example.playerpad.PlayerActivity.listSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    MyBinder mBinder= new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles=new ArrayList<>();
    Uri uri;
    int position=-1;
    ActionPlaying actionPlaying;

    @Override
    public void onCreate() {
        super.onCreate();


    }



    public class MyBinder extends Binder {

        MusicService getService()
        {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int myPosition =intent.getIntExtra("servicePosition",-1);
        String actionName=intent.getStringExtra("ActionName");

        if (myPosition != -1)
        {
            PlayMedia(myPosition);
        }

        if (actionName != null)
        {
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "play pause", Toast.LENGTH_SHORT).show();
                    break;

                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    break;

                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        return START_STICKY;
    }

    private void PlayMedia(int startPosition) {

        musicFiles=listSongs;
        position=startPosition;

        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }

    }


    void start()
    {
        mediaPlayer.start();
    }

    void pause()
    {
        mediaPlayer.pause();
    }

    boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    void stop()
    {
        mediaPlayer.stop();
    }

    void release()
    {
        mediaPlayer.release();
    }

    int getDuration()
    {
        return mediaPlayer.getDuration();
    }

    void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }

    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }

    void createMediaPlayer(int position)
    {
        uri =Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);
    }


    void OnCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (actionPlaying != null)
        {
            actionPlaying.next_btnClicked();
        }

        createMediaPlayer(position);
        mediaPlayer.start();
        OnCompleted();
    }

}

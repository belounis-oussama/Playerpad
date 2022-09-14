package com.example.playerpad;

import static com.example.playerpad.ApplicationClass.ACTION_NEXT;
import static com.example.playerpad.ApplicationClass.ACTION_PLAY;
import static com.example.playerpad.ApplicationClass.ACTION_PREVIOUS;
import static com.example.playerpad.ApplicationClass.CHANNEL_ID1;
import static com.example.playerpad.PlayerActivity.listSongs;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {

        mediaSessionCompat=new MediaSessionCompat(getBaseContext(),"My Audio");
        super.onCreate();

    }


    public class MyBinder extends Binder {

        MusicService getService() {
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

        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");

        if (myPosition != -1) {
            PlayMedia(myPosition);
        }

        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    Toast.makeText(this, "play pause", Toast.LENGTH_SHORT).show();

                    if (actionPlaying != null) {
                        actionPlaying.play_pause_btnClicked();
                    }
                    break;

                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();

                    if (actionPlaying != null) {
                        actionPlaying.next_btnClicked();
                    }
                    break;

                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();

                    if (actionPlaying != null) {
                        actionPlaying.prev_btnClicked();
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    private void PlayMedia(int startPosition) {

        musicFiles = listSongs;
        position = startPosition;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }

    }


    void start() {
        mediaPlayer.start();
    }

    void pause() {
        mediaPlayer.pause();
    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    void stop() {
        mediaPlayer.stop();
    }

    void release() {
        mediaPlayer.release();
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    void createMediaPlayer(int positionInner) {
        position=positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }


    void OnCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        if (actionPlaying != null) {
            actionPlaying.next_btnClicked();

            if (mediaPlayer != null)
            {

                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
            }
        }

    }

    void setCallBack(ActionPlaying actionPlaying)
    {
        this.actionPlaying =actionPlaying;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void showNotification(int playpasueBtn)
    {

        Intent intent =new Intent(this,PlayerActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE );


        Intent previntent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent prevPending=PendingIntent.getBroadcast(this,0,previntent,PendingIntent.FLAG_IMMUTABLE);



        Intent pauseintent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pausePending=PendingIntent.getBroadcast(this,0,pauseintent,PendingIntent.FLAG_IMMUTABLE);



        Intent nextintent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent nextPending=PendingIntent.getBroadcast(this,0,nextintent,PendingIntent.FLAG_IMMUTABLE);


        byte [] picture =null;
        picture =getAlbumArt(musicFiles.get(position).getPath());
        Bitmap thumb=null;
        if (picture != null)
        {
            thumb= BitmapFactory.decodeByteArray(picture,0,picture.length);
        }
        else
        {
            thumb= BitmapFactory.decodeResource(getResources(),R.drawable.ic_round_music_note_24);
        }

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID1)
                .setSmallIcon(playpasueBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_round_skip_previous_24,"Previous",prevPending)
                .addAction(playpasueBtn,"Pause",pausePending)
                .addAction(R.drawable.ic_round_skip_next_24,"Next",nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        startForeground(2,notification);

        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.notify(0,notification);

    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte [] album= retriever.getEmbeddedPicture();
        retriever.release();
        return album;
    }

}

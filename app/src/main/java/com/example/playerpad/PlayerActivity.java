package com.example.playerpad;

import static com.example.playerpad.AlbumDetailsAdapter.albumFiles;
import static com.example.playerpad.ApplicationClass.ACTION_NEXT;
import static com.example.playerpad.ApplicationClass.ACTION_PLAY;
import static com.example.playerpad.ApplicationClass.ACTION_PREVIOUS;
import static com.example.playerpad.ApplicationClass.CHANNEL_ID1;
import static com.example.playerpad.ApplicationClass.CHANNEL_ID2;
import static com.example.playerpad.MainActivity.musicFiles;
import static com.example.playerpad.MainActivity.repatBoolean;
import static com.example.playerpad.MainActivity.shuffleBoolean;
import static com.example.playerpad.MusicAdapter.mFiles;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity
        implements ActionPlaying, ServiceConnection {

    TextView songname, artistname, duration_played, duration_total;
    ImageView cover_art, nextt_btn, prev_btn, back_btn, suffle_btn, repeat_btn;
    FloatingActionButton play_pause_btn;
    SeekBar seekBar;
    int position = -1;
    Uri uri;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    // static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nexThread;
    MusicService musicService;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();



        initWidget();
        getIntentExtras();




        //getColors
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;


        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (musicService != null && b) {
                    musicService.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formatedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        suffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    suffle_btn.setImageResource(R.drawable.ic_round_shuffle_24);
                } else {
                    shuffleBoolean = true;
                    suffle_btn.setImageResource(R.drawable.ic_round_shuffle_on);
                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repatBoolean) {
                    repatBoolean = false;
                    repeat_btn.setImageResource(R.drawable.ic_round_repeat_24);
                } else {
                    repatBoolean = true;
                    repeat_btn.setImageResource(R.drawable.ic_round_repeat_on);
                }
            }
        });
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        playThread();
        nexThread();
        prevThread();

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThread() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        prev_btnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void nexThread() {
        nexThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextt_btn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        next_btnClicked();
                    }
                });
            }
        };
        nexThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void next_btnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repatBoolean) {
                position = ((position + 1) % listSongs.size());
            }

            //else repeat
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_round_pause_24);
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repatBoolean) {
                position = ((position + 1) % listSongs.size());
            }


            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_round_play_arrow_24);
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void prev_btnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();


            if (shuffleBoolean && !repatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }


            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_round_pause_24);
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_round_play_arrow_24);
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
        }
    }

    private void playThread() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                play_pause_btn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        play_pause_btnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void play_pause_btnClicked() {

        if (musicService.isPlaying()) {
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
            musicService.showNotification(R.drawable.ic_round_play_arrow_24);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            musicService.showNotification(R.drawable.ic_round_pause_24);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formatedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String secounds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + secounds;
        totalNew = minutes + ":" + "0" + secounds;

        if (secounds.length() == 1) {
            return totalNew;
        } else {
            return totalout;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getIntentExtras() {

        position = getIntent().getIntExtra("position", -1);

        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails")) {
            listSongs = albumFiles;
        } else {
            listSongs = mFiles;
        }


        if (listSongs != null) {
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);

            uri = Uri.parse(listSongs.get(position).getPath());
        }

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);


    }

    private void initWidget() {
        songname = findViewById(R.id.song_name);
        artistname = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        cover_art = findViewById(R.id.cover_art);
        nextt_btn = findViewById(R.id.next_btn);
        prev_btn = findViewById(R.id.prev_btn);

        suffle_btn = findViewById(R.id.shuffle_btn);
        repeat_btn = findViewById(R.id.repeat_btn);
        play_pause_btn = findViewById(R.id.playpause);
        seekBar = findViewById(R.id.seek_bar);
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap, boolean isdrawble) {

        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (isdrawble) {
                    imageView.setImageResource(R.drawable.ic_round_music_note_24);
                } else {
                    Glide.with(context).load(bitmap).into(imageView);
                }
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animOut);
    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formatedTime(durationTotal));

        //art album
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        boolean isDrawble;
        if (art != null) {
            isDrawble = false;
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, cover_art, bitmap, isDrawble);
        } else {
            isDrawble = true;
            Bitmap myLogo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_round_music_note_24);

            ImageAnimation(this, cover_art, myLogo, isDrawble);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();

        musicService.setCallBack(this);

       // Toast.makeText(musicService, "Connected" + musicService, Toast.LENGTH_SHORT).show();


        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);

        songname.setText(listSongs.get(position).getTitle());
        artistname.setText(listSongs.get(position).getArtist());

        musicService.OnCompleted();
        musicService.showNotification(R.drawable.ic_round_pause_24);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

}
package com.example.playerpad;

import static com.example.playerpad.AlbumDetailsAdapter.albumFiles;
import static com.example.playerpad.MainActivity.musicFiles;
import static com.example.playerpad.MainActivity.repatBoolean;
import static com.example.playerpad.MainActivity.shuffleBoolean;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    TextView songname,artistname,duration_played,duration_total;
    ImageView cover_art,nextt_btn,prev_btn,back_btn,suffle_btn,repeat_btn;
    FloatingActionButton play_pause_btn;
    SeekBar seekBar;
    int position =-1;
    Uri uri;
    static ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static MediaPlayer mediaPlayer;
    private Handler handler =new Handler();
    private Thread playThread,prevThread,nexThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initWidget();
        getIntentExtras();

        //getColors
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;


        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;


        songname.setText(listSongs.get(position).getTitle());
        artistname.setText(listSongs.get(position).getArtist());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null && b)
                {
                    mediaPlayer.seekTo(i*1000);
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
                if (mediaPlayer != null)
                {
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formatedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        suffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean)
                {
                    shuffleBoolean=false;
                    suffle_btn.setImageResource(R.drawable.ic_round_shuffle_24);
                }
                else
                {
                    shuffleBoolean=true;
                    suffle_btn.setImageResource(R.drawable.ic_round_shuffle_on);
                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repatBoolean)
                {
                    repatBoolean=false;
                    repeat_btn.setImageResource(R.drawable.ic_round_repeat_24);
                }
                else
                {
                    repatBoolean=true;
                    repeat_btn.setImageResource(R.drawable.ic_round_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        playThread();
        nexThread();
        prevThread();

        super.onResume();
    }

    private void prevThread() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
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
        nexThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextt_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        next_btnClicked();
                    }
                });
            }
        };
        nexThread.start();
    }

    private void next_btnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repatBoolean)
            {
                position=((position+1)%listSongs.size());
            }

            //else repeat
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repatBoolean)
            {
                position=((position+1)%listSongs.size());
            }



            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void prev_btnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();


            if (shuffleBoolean && !repatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repatBoolean)
            {
                position=((position-1)<0 ? (listSongs.size() -1):(position-1)  );
            }



            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repatBoolean)
            {
                position=((position-1)<0 ? (listSongs.size() -1):(position-1)  );
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songname.setText(listSongs.get(position).getTitle());
            artistname.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
        }
    }

    private void playThread() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                play_pause_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play_pause_btnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void play_pause_btnClicked() {

        if (mediaPlayer.isPlaying())
        {
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
             play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
             mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formatedTime(int mCurrentPosition ) {
        String totalout ="";
        String totalNew ="";
        String secounds =String.valueOf(mCurrentPosition %60);
        String minutes =String.valueOf(mCurrentPosition/60);
        totalout = minutes+":"+secounds;
        totalNew=minutes+":"+"0"+secounds;

        if (secounds.length()==1)
        {
            return totalNew;
        }
        else
        {
            return totalout;
        }
    }

    private void getIntentExtras() {

        position=getIntent().getIntExtra("position", -1);

        String sender=getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails"))
        {
            listSongs=albumFiles;
        }
        else
        {
            listSongs= musicFiles;
        }



        if (listSongs!=null)
        {
            play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            uri=Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);



    }

    private void initWidget() {
        songname=findViewById(R.id.song_name);
        artistname=findViewById(R.id.song_artist);
        duration_played=findViewById(R.id.duration_played);
        duration_total=findViewById(R.id.duration_total);
        cover_art=findViewById(R.id.cover_art);
        nextt_btn=findViewById(R.id.next_btn);
        prev_btn=findViewById(R.id.prev_btn);
        back_btn=findViewById(R.id.back_btn);
        suffle_btn=findViewById(R.id.shuffle_btn);
        repeat_btn=findViewById(R.id.repeat_btn);
        play_pause_btn=findViewById(R.id.playpause);
        seekBar=findViewById(R.id.seek_bar);
    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal= Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formatedTime(durationTotal));

        //art album
        byte[] art=retriever.getEmbeddedPicture();
        if (art !=null)
        {
            Glide.with(this).asBitmap().load(art).into(cover_art);
        }
        else
        {
            cover_art.setImageResource(R.drawable.ic_round_music_note_24);

        }
    }
}
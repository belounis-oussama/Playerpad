package com.example.playerpad;

import static com.example.playerpad.MainActivity.ARTIST_TO_FRAG;
import static com.example.playerpad.MainActivity.PATH_TO_FRAG;
import static com.example.playerpad.MainActivity.SHOW_MINI_PLAYER;
import static com.example.playerpad.MainActivity.SONG_NAME_TO_FRAG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NowPlayingFragment extends Fragment implements ServiceConnection {

    ImageView nextbtn,albumArt;
    TextView artistname,songname;
    FloatingActionButton playPauseBtn;
    View view;
    MusicService musicService;



    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_now_playing, container, false);

        artistname=view.findViewById(R.id.song_name_MiniPlayer);
        songname=view.findViewById(R.id.song_artist_MiniPlayer);
        nextbtn=view.findViewById(R.id.ski_next_bottom);
        albumArt=view.findViewById(R.id.bottom_album_art);
        playPauseBtn=view.findViewById(R.id.play_pause_btn_miniPlayer);


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (musicService != null)
                {
                    musicService.nextBtnClicked();
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (musicService != null)
                {
                    musicService.playPauseBtnClicked();
                    if (musicService.isPlaying())
                    {
                        playPauseBtn.setImageResource(R.drawable.ic_round_pause_24);
                    }
                    else
                    {
                        playPauseBtn.setImageResource(R.drawable.ic_round_play_arrow_24);

                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SHOW_MINI_PLAYER)
        {
            if (PATH_TO_FRAG != null)
            {
                byte[] art=getAlbumArt(PATH_TO_FRAG);

                if (art != null)
                {
                    Glide.with(getContext()).load(art).into(albumArt);
                }
                else
                {
                    albumArt.setImageResource(R.drawable.ic_round_music_note_24);
                }

                songname.setText(SONG_NAME_TO_FRAG);
                artistname.setText(ARTIST_TO_FRAG);

                Intent intent=new Intent(getContext(),MusicService.class);
                if (getContext() != null)
                {
                    getContext().bindService(intent,this, Context.BIND_AUTO_CREATE);
                }

            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getContext() != null)
        {
            getContext().unbindService(this);
        }
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte [] album= retriever.getEmbeddedPicture();
        retriever.release();
        return album;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService =binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService= null;
    }
}
package com.example.playerpad;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NowPlayingFragment extends Fragment {

    ImageView nextbtn,albumArt;
    TextView artistname,songname;
    FloatingActionButton playPauseBtn;
    View view;

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

        return view;
    }
}
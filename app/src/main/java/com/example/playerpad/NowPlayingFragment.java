package com.example.playerpad;

import static com.example.playerpad.MainActivity.PATH_TO_FRAG;
import static com.example.playerpad.MainActivity.SHOW_MINI_PLAYER;

import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    @Override
    public void onResume() {
        super.onResume();

        if (SHOW_MINI_PLAYER)
        {
            if (PATH_TO_FRAG != null)
            {
                byte[] art=getAlbumArt(PATH_TO_FRAG);
                Glide.with(getContext()).load(art).into(albumArt);

                songname.setText(PATH_TO_FRAG);
            }

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

}
package com.example.playerpad;

import static com.example.playerpad.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumart;
    String albumname;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeofApp();
        setContentView(R.layout.activity_album_details);

        initWidget();

        albumname=getIntent().getStringExtra("albumname");
        int j=0;

        for (int i=0;i<musicFiles.size();i++)
        {
            if (albumname.equals(musicFiles.get(i).getAlbum()))
            {
                albumSongs.add(j,musicFiles.get(i));
                j++;
            }
        }

        byte[] image=getAlbumArt(albumSongs.get(0).getPath());
        if (image != null)
        {
            Glide.with(this).load(image).into(albumart);
        }
        else
        {
            albumart.setImageResource(R.drawable.ic_baseline_album_24);
        }





    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(albumSongs.size()<1) )
        {
            albumDetailsAdapter =new AlbumDetailsAdapter(this,albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        }
    }
    private void setThemeofApp() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int theme=preferences.getInt("ThemeNumber",0);

        switch (theme)
        {
            case 0:
                setTheme(R.style.Theme_Playerpad);
                break;

            case 1:
                setTheme(R.style.Theme_Playerpad2);
                break;

            case 2:
                setTheme(R.style.Theme_Playerpad4);
                break;
            case 3:
                setTheme(R.style.Theme_Playerpad3);
                break;

            case 4:
                setTheme(R.style.Theme_Playerpad5);

        }

        //setTheme(R.style.Theme_Playerpad4);
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte [] album= retriever.getEmbeddedPicture();
        retriever.release();
        return album;
    }

    private void initWidget() {
        recyclerView=findViewById(R.id.recycleview);
        albumart=findViewById(R.id.albumart);
    }
}
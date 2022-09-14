package com.example.playerpad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public int REQUESTCODE=1;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean =false,repatBoolean=false;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    private String MY_SORT_PREFE="sortOrder";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        permission();

    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCODE);
        }
        else
        {
            musicFiles=getAllAudio(this);
            //initialise the widgets
            initWidget();
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context)
    {

        SharedPreferences preferences=getSharedPreferences(MY_SORT_PREFE,MODE_PRIVATE);
        String sortOrder=preferences.getString("sorting","sortByName");
        ArrayList<String> duplicat=new ArrayList<>();
        albums.clear();

        ArrayList<MusicFiles> audioListtemp =new ArrayList<>();

        String order=null;

        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder)
        {
            case "sortByName": order=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
                break;

            case "sortByDate": order=MediaStore.MediaColumns.DATE_ADDED+" ASC";
                break;

            case "sortBySize": order=MediaStore.MediaColumns.SIZE+" DESC";
                break;
        }
        String [] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,//path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,

        };

        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,order);
        if (cursor !=null)
        {
            while(cursor.moveToNext())
            {
                String album =cursor.getString(0);
                String title =cursor.getString(1);
                String duration =cursor.getString(2);
                String path =cursor.getString(3);
                String artist =cursor.getString(4);
                String id =cursor.getString(5);

                MusicFiles musicFile= new MusicFiles(path,title,artist,album,duration,id);

                audioListtemp.add(musicFile);
                if (!duplicat.contains(album))
                {
                    albums.add(musicFile);
                    duplicat.add(album);
                }


            }
            cursor.close();
        }
        return audioListtemp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUESTCODE)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                musicFiles=getAllAudio(this);
                initWidget();
            }
            else  //permision denied
            //we basically resend the pop up to accept the external storage permission
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCODE);
            }
        }
    }

    private void initWidget() {
        ViewPager viewPager=findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragment(new AlbumFragment(),"Album");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }



    public static  class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles= new ArrayList<>();

        }

        void addFragment(Fragment fragment,String title)
        {
            fragments.add(fragment);
            titles.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);

        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem=menu.findItem(R.id.search_option);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput=newText.toLowerCase();
        ArrayList<MusicFiles> myfiles=new ArrayList<>();
        for (MusicFiles song: musicFiles)
        {
            if (song.getTitle().toLowerCase().contains(userInput))
            {
                myfiles.add(song);
            }
        }

        SongsFragment.musicAdapter.updateList(myfiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        SharedPreferences.Editor editor =getSharedPreferences(MY_SORT_PREFE,MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.sortName:
                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.sortDate:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.sortSize:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
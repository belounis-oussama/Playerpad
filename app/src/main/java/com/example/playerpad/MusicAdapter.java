package com.example.playerpad;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MusicFiles>mFiles;

    MusicAdapter( Context context,ArrayList<MusicFiles>mFiles)
    {
        this.context=context;
        this.mFiles=mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.music_items,parent,false);

        return new MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.filename.setText(mFiles.get(position).getTitle());

        byte[] image=getAlbumArt(mFiles.get(position).getPath());
        if (image !=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.album_art);
        }
        else
        {
            holder.album_art.setImageResource(R.drawable.ic_round_music_note_24);
        }






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PlayerActivity.class);

                intent.putExtra("position",position);

                context.startActivity(intent);
            }
        });

        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu  popupMenu=new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.delete:
                                deleteFile(position,view);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

    }

    public void deleteFile(int position,View v)
    {

        Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file=new File(mFiles.get(position).getPath());
        boolean deleted=file.delete();
        if (deleted)
        {
            context.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(v,"File deleted",Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            Snackbar.make(v,"File can't deleted",Snackbar.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView album_art,menu_more;
        TextView filename;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            album_art=itemView.findViewById(R.id.music_img);
            filename=itemView.findViewById(R.id.music_file_name);
            menu_more=itemView.findViewById(R.id.menu_more);

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

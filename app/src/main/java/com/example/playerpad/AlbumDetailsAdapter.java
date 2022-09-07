package com.example.playerpad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder>{
    private Context context;
    static ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumDetailsAdapter(Context context, ArrayList<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.music_items,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.album_name.setText(albumFiles.get(position).getTitle());

        byte[] image=getAlbumArt(albumFiles.get(position).getPath());
        if (image !=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.albumImage);
        }
        else
        {
            holder.albumImage.setImageResource(R.drawable.ic_baseline_album_24);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImage;
        TextView album_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            album_name=itemView.findViewById(R.id.music_file_name);
            albumImage=itemView.findViewById(R.id.music_img);

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

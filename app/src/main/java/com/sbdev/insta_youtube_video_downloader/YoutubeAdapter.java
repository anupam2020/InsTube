package com.sbdev.insta_youtube_video_downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder> {

    ArrayList<YoutubeModel> arrayList;
    Context context;

    public YoutubeAdapter(ArrayList<YoutubeModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YoutubeViewHolder(LayoutInflater.from(context).inflate(R.layout.youtube_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {

        Glide.with(context).load(arrayList.get(holder.getAdapterPosition()).ytURL).into(holder.img);



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView tvQuality;
        ImageView download;

        public YoutubeViewHolder(@NonNull View itemView) {
            super(itemView);

            img=itemView.findViewById(R.id.itemCircularImg);
            tvQuality=itemView.findViewById(R.id.itemQualityText);
            download=itemView.findViewById(R.id.itemDownloadBtn);

        }
    }


}

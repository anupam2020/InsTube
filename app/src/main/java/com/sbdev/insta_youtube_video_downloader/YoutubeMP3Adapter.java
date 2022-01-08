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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class YoutubeMP3Adapter extends RecyclerView.Adapter<YoutubeMP3Adapter.YoutubeViewHolder> {

    ArrayList<YoutubeMP3Model> arrayList;
    Context context;

    public YoutubeMP3Adapter(ArrayList<YoutubeMP3Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YoutubeViewHolder(LayoutInflater.from(context).inflate(R.layout.youtube_mp3_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {

        Glide.with(context)
                .load(arrayList.get(holder.getAdapterPosition()).imgURL)
                .placeholder(R.drawable.ic_baseline_image_search_24_black)
                .error(R.drawable.ic_outline_image_not_supported_24_black)
                .into(holder.img);

        holder.tvQuality.setText("QUALITY: "+arrayList.get(holder.getAdapterPosition()).quality);
        holder.tvType.setText(arrayList.get(holder.getAdapterPosition()).type);

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url=arrayList.get(holder.getAdapterPosition()).ytURL;
                String title=arrayList.get(holder.getAdapterPosition()).title;

                downloadURL(url,title);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView tvQuality,tvType;
        ImageView download;

        public YoutubeViewHolder(@NonNull View itemView) {
            super(itemView);

            img=itemView.findViewById(R.id.itemCircularImgMP3);
            tvQuality=itemView.findViewById(R.id.itemQualityTextMP3);
            tvType=itemView.findViewById(R.id.itemTypeTextMP3);
            download=itemView.findViewById(R.id.itemDownloadBtnMP3);

        }
    }

    private void downloadURL(String url,String title)
    {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp3");

        downloadManager.enqueue(request);

        DynamicToast.make(context,"Downloading file!",
                context.getResources().getDrawable(R.drawable.ic_outline_download_for_offline_24_red),2000).show();

    }


}

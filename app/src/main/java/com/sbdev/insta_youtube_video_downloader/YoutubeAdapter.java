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

        Glide.with(context)
                .load(arrayList.get(holder.getAdapterPosition()).imgURL)
                .placeholder(R.drawable.ic_baseline_image_search_24_black)
                .error(R.drawable.ic_outline_image_not_supported_24_black)
                .into(holder.img);

        holder.tvQuality.setText(arrayList.get(holder.getAdapterPosition()).quality);
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

            img=itemView.findViewById(R.id.itemCircularImg);
            tvQuality=itemView.findViewById(R.id.itemQualityText);
            tvType=itemView.findViewById(R.id.itemTypeText);
            download=itemView.findViewById(R.id.itemDownloadBtn);

        }
    }

    private void downloadURL(String url,String title)
    {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp4");

        downloadManager.enqueue(request);

        DynamicToast.make(context,"Downloading file!",
                context.getResources().getDrawable(R.drawable.ic_outline_download_for_offline_24),2500).show();

    }


}

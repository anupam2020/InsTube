package com.sbdev.insta_youtube_video_downloader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    ArrayList<SearchModel> arrayList;
    Context context;
    ClipboardManager clipboard;

    public SearchAdapter(ArrayList<SearchModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        Glide.with(context).load(arrayList.get(holder.getAdapterPosition()).mainImgURL).into(holder.mainImg);
        Glide.with(context).load(arrayList.get(holder.getAdapterPosition()).circleImgURL).into(holder.authorImg);

        holder.duration.setText(arrayList.get(holder.getAdapterPosition()).duration);
        holder.title.setText(arrayList.get(holder.getAdapterPosition()).title);
        holder.author.setText(arrayList.get(holder.getAdapterPosition()).author);
        holder.views.setText(arrayList.get(holder.getAdapterPosition()).views+" views");
        holder.daysAgo.setText(arrayList.get(holder.getAdapterPosition()).daysAgo);


        holder.moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_more,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId())
                        {

                            case R.id.copyLink:
                                Log.d("Link",arrayList.get(holder.getAdapterPosition()).vidID);

                                String str=arrayList.get(holder.getAdapterPosition()).vidID;

                                str="https://youtu.be/"+str;
                                Log.d("newStr",str);
                                ClipData clip = ClipData.newPlainText("label", str);
                                clipboard.setPrimaryClip(clip);
                                DynamicToast.make(context, "Link successfully copied!", context.getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                        context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.black), 2000).show();

                                break;

                        }

                        return true;
                    }
                });

                popupMenu.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView mainImg,moreImg;
        CircleImageView authorImg;
        TextView duration,title,author,views, daysAgo;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mainImg=itemView.findViewById(R.id.searchItemImg);
            moreImg=itemView.findViewById(R.id.searchItemMore);
            authorImg=itemView.findViewById(R.id.searchItemCircleImg);
            duration=itemView.findViewById(R.id.searchItemTime);
            title=itemView.findViewById(R.id.searchItemTitle);
            author=itemView.findViewById(R.id.searchItemAuthor);
            views=itemView.findViewById(R.id.searchItemViews);
            daysAgo=itemView.findViewById(R.id.searchItemDaysAgo);

        }
    }

}

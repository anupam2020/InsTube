package com.sbdev.insta_youtube_video_downloader;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YoutubeFragment extends Fragment {

    private TextInputEditText ytEditText;

    private Button pasteBtn,downloadBtn;

    private ClipboardManager clipboard;
    private String pasteData = "";

    private OkHttpClient client;

    private String strTime="";

    private String ID="";

    private TextView vidTitle,qualityTV,timeTV;

    private ImageView vidImg,download;

    private ProgressBar progressBar;

    private CircleImageView circleImageView;

    private CardView cardView;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ytEditText=view.findViewById(R.id.youtubeLink);
        pasteBtn=view.findViewById(R.id.youtubePaste);
        downloadBtn=view.findViewById(R.id.youtubeDownload);
        vidTitle=view.findViewById(R.id.youtubeVideoTitle);
        vidImg=view.findViewById(R.id.youtubeVideoImg);
        progressBar=view.findViewById(R.id.youtubeProgress);
        download=view.findViewById(R.id.youtubeDownloadBtn);
        qualityTV=view.findViewById(R.id.youtubeQualityText);
        timeTV=view.findViewById(R.id.youtubeTimeText);
        circleImageView=view.findViewById(R.id.youtubeCircularImg);
        cardView=view.findViewById(R.id.youtubeCard2);

        progressBar.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);


        clipboard= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        client=new OkHttpClient();

        pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                pasteData = item.getText().toString();

                ytEditText.setText(pasteData);

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                strTime=Long.toString(System.nanoTime());

                String ytLink=ytEditText.getText().toString();

                ytLink=ytLink.trim();

                if(ytLink.isEmpty())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    DynamicToast.makeWarning(getActivity(),"Field cannot be empty!",2000).show();
                }
                else
                {

                    ID=ytLink.substring(ytLink.lastIndexOf('/')+1);

                    Request request = new Request.Builder()
                            .url("https://youtube-search-and-download.p.rapidapi.com/video?id="+ID)
                            .get()
                            .addHeader("x-rapidapi-host", "youtube-search-and-download.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "e8f6c57650msh666fef2e3a110b5p13b950jsn4359d608e124")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            if(response.isSuccessful())
                            {

                                String res=response.body().string();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                            JSONObject jsonObject=new JSONObject(res);

                                            JSONObject videoDetails=jsonObject.getJSONObject("videoDetails");
                                            String title=videoDetails.getString("title");
                                            int lengthSeconds=videoDetails.getInt("lengthSeconds");

                                            vidTitle.setText(title);
                                            timeTV.setText((int)lengthSeconds/60+" mins");

                                            JSONObject thumbnail=videoDetails.getJSONObject("thumbnail");
                                            JSONArray thumbnails=thumbnail.getJSONArray("thumbnails");

                                            JSONObject index=thumbnails.getJSONObject(thumbnails.length()-1);
                                            String url=index.getString("url");

                                            Glide.with(getActivity())
                                                    .load(url)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                    .into(vidImg);
                                            Glide.with(getActivity())
                                                    .load(url)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                    .into(circleImageView);


                                            JSONObject streamingData=jsonObject.getJSONObject("streamingData");

                                            JSONArray formats=streamingData.getJSONArray("formats");

                                            JSONObject formats_index=formats.getJSONObject(formats.length()-1);

                                            String quality=formats_index.getString("quality");
                                            String qualityLabel=formats_index.getString("qualityLabel");
                                            String downloadURL=formats_index.getString("url");

                                            qualityTV.setText(qualityLabel+"- "+quality);

                                            progressBar.setVisibility(View.INVISIBLE);
                                            cardView.setVisibility(View.VISIBLE);

                                            download.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    downloadURL(downloadURL);
                                                }
                                            });

                                        } catch (JSONException e) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                        }

                                    }
                                });

                            }

                        }
                    });

                }

            }
        });

    }


    private void downloadURL(String url)
    {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager= (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,strTime+".mp4");

        downloadManager.enqueue(request);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube, container, false);
    }
}
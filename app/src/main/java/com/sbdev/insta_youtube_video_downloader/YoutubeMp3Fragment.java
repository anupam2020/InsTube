package com.sbdev.insta_youtube_video_downloader;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YoutubeMp3Fragment extends Fragment {

    private TextInputEditText ytEditText;

    private Button pasteBtn,downloadBtn;

    private ClipboardManager clipboard;
    private String pasteData = "";

    private OkHttpClient client;

    private String ID="";

    private TextView vidTitle,qualityTV,timeTV;

    private ImageView vidImg,download;

    private ProgressBar progressBar;

    private CircleImageView circleImageView;

    private CardView cardView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ytEditText=view.findViewById(R.id.youtubeMP3Link);
        pasteBtn=view.findViewById(R.id.youtubeMP3Paste);
        downloadBtn=view.findViewById(R.id.youtubeMP3Download);
        vidTitle=view.findViewById(R.id.youtubeMP3VideoTitle);
        vidImg=view.findViewById(R.id.youtubeMP3VideoImg);
        progressBar=view.findViewById(R.id.youtubeMP3Progress);
        download=view.findViewById(R.id.youtubeMP3DownloadBtn);
        qualityTV=view.findViewById(R.id.youtubeMP3QualityText);
        timeTV=view.findViewById(R.id.youtubeMP3TimeText);
        circleImageView=view.findViewById(R.id.youtubeMP3CircularImg);
        cardView=view.findViewById(R.id.youtubeMP3Card2);

        progressBar.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);


        clipboard= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        client=new OkHttpClient();

        pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                pasteData = item.getText().toString();

                ytEditText.setText(pasteData);

            }
        });

        progressBar=view.findViewById(R.id.youtubeMP3Progress);
        progressBar.setVisibility(View.INVISIBLE);


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);

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
                            .addHeader("x-rapidapi-key", "19c7e07597mshd4a487bebda6ef4p1c4c7fjsna9c61e2c34f8")
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

                                            JSONObject streamingData=jsonObject.getJSONObject("streamingData");
                                            JSONArray adaptiveFormats=streamingData.getJSONArray("adaptiveFormats");

                                            JSONObject index=adaptiveFormats.getJSONObject(adaptiveFormats.length()-1);

                                            long approxDurationMs=index.getLong("approxDurationMs");
                                            timeTV.setText((int)(approxDurationMs/1000)/60+" mins");

                                            String mimeType=index.getString("mimeType");
                                            qualityTV.setText(mimeType.substring(0,10).toUpperCase());

                                            String url=index.getString("url");


                                            JSONObject videoDetails=jsonObject.getJSONObject("videoDetails");

                                            String title=videoDetails.getString("title");
                                            vidTitle.setText(title);

                                            JSONObject thumbnail=videoDetails.getJSONObject("thumbnail");
                                            JSONArray thumbnails=thumbnail.getJSONArray("thumbnails");

                                            JSONObject indexJSON=thumbnails.getJSONObject(thumbnails.length()-1);
                                            String urlJSON=indexJSON.getString("url");

                                            Glide.with(getActivity())
                                                    .load(urlJSON)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                    .into(vidImg);
                                            Glide.with(getActivity())
                                                    .load(urlJSON)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                    .into(circleImageView);

                                            progressBar.setVisibility(View.INVISIBLE);
                                            cardView.setVisibility(View.VISIBLE);

                                            download.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    downloadURL(url,title);
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

    private void downloadURL(String url,String title)
    {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager= (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp3");

        downloadManager.enqueue(request);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube_mp3, container, false);
    }
}
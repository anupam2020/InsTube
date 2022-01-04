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
import android.util.Log;
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
import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InstaFragment extends Fragment {

    private TextInputEditText instaEditText;

    private Button pasteBtn,downloadBtn;

    private ClipboardManager clipboard;
    private String pasteData = "";

    private OkHttpClient client;

    private String strTime="";

    private String ID="";

    private TextView vidTitle,timeTV,viewsTV;

    private ImageView vidImg,download;

    private ProgressBar progressBar;

    private CircleImageView circleImageView;

    private CardView cardView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instaEditText=view.findViewById(R.id.instaLink);
        pasteBtn=view.findViewById(R.id.instaPaste);
        downloadBtn=view.findViewById(R.id.instaDownload);
        vidTitle=view.findViewById(R.id.instaVideoTitle);
        vidImg=view.findViewById(R.id.instaVideoImg);
        progressBar=view.findViewById(R.id.instaProgress);
        download=view.findViewById(R.id.instaDownloadBtn);
        timeTV=view.findViewById(R.id.instaTimeText);
        viewsTV=view.findViewById(R.id.instaViews);
        circleImageView=view.findViewById(R.id.instaCircularImg);
        cardView=view.findViewById(R.id.instaCard2);

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

                instaEditText.setText(pasteData);

            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);

                strTime=Long.toString(System.nanoTime());

                String ytLink=instaEditText.getText().toString();

                ytLink=ytLink.trim();

                if(ytLink.isEmpty())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    DynamicToast.makeWarning(getActivity(),"Field cannot be empty!",2000).show();
                }
                else
                {

                    int lastIndex=ytLink.lastIndexOf('/');

                    ID=ytLink.substring(lastIndex-11,lastIndex);

                    Log.d("ID",ID);

                    Request request = new Request.Builder()
                            .url("https://instagram28.p.rapidapi.com/media_info?short_code="+ID)
                            .get()
                            .addHeader("x-rapidapi-host", "instagram28.p.rapidapi.com")
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

                                            JSONObject data=jsonObject.getJSONObject("data");

                                            JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                            String display_url=shortcode_media.getString("display_url");

                                            Glide.with(getActivity())
                                                    .load(display_url)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                    .into(vidImg);
                                            Glide.with(getActivity())
                                                    .load(display_url)
                                                    .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                    .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                    .into(circleImageView);

                                            boolean is_video=shortcode_media.getBoolean("is_video");
                                            if(is_video)
                                            {

                                                String video_url=shortcode_media.getString("video_url");
                                                double video_duration=shortcode_media.getDouble("video_duration");
                                                timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                long viewsCount=shortcode_media.getLong("video_view_count");
                                                String strViews=getFormattedAmount(viewsCount);
                                                viewsTV.setText(strViews+" views");

                                                progressBar.setVisibility(View.INVISIBLE);
                                                cardView.setVisibility(View.VISIBLE);

                                                download.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        downloadURL(video_url);
                                                    }
                                                });

                                            }


                                        } catch (JSONException e) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                        }

                                    }
                                });

                            }
                            else
                            {
                                Log.e("Response",response.message());

                                Request request = new Request.Builder()
                                        .url("https://instagram-scraper2.p.rapidapi.com/media_info?short_code="+ID)
                                        .get()
                                        .addHeader("x-rapidapi-host", "instagram-scraper2.p.rapidapi.com")
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

                                                        JSONObject data=jsonObject.getJSONObject("data");

                                                        JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                                        String display_url=shortcode_media.getString("display_url");

                                                        Glide.with(getActivity())
                                                                .load(display_url)
                                                                .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                .into(vidImg);
                                                        Glide.with(getActivity())
                                                                .load(display_url)
                                                                .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                .into(circleImageView);

                                                        boolean is_video=shortcode_media.getBoolean("is_video");
                                                        if(is_video)
                                                        {

                                                            String video_url=shortcode_media.getString("video_url");
                                                            double video_duration=shortcode_media.getDouble("video_duration");
                                                            timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                            long viewsCount=shortcode_media.getLong("video_view_count");
                                                            String strViews=getFormattedAmount(viewsCount);
                                                            viewsTV.setText(strViews+" views");

                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            cardView.setVisibility(View.VISIBLE);

                                                            download.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    downloadURL(video_url);
                                                                }
                                                            });

                                                        }


                                                    } catch (JSONException e) {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                    }

                                                }
                                            });

                                        }
                                        else
                                        {
                                            Log.e("Response",response.message());

                                            Request request = new Request.Builder()
                                                    .url("https://instagram28.p.rapidapi.com/media_info?short_code="+ID)
                                                    .get()
                                                    .addHeader("x-rapidapi-host", "instagram28.p.rapidapi.com")
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

                                                                    JSONObject data=jsonObject.getJSONObject("data");

                                                                    JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                                                    String display_url=shortcode_media.getString("display_url");

                                                                    Glide.with(getActivity())
                                                                            .load(display_url)
                                                                            .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                            .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                            .into(vidImg);
                                                                    Glide.with(getActivity())
                                                                            .load(display_url)
                                                                            .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                            .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                            .into(circleImageView);

                                                                    boolean is_video=shortcode_media.getBoolean("is_video");
                                                                    if(is_video)
                                                                    {

                                                                        String video_url=shortcode_media.getString("video_url");
                                                                        double video_duration=shortcode_media.getDouble("video_duration");
                                                                        timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                        long viewsCount=shortcode_media.getLong("video_view_count");
                                                                        String strViews=getFormattedAmount(viewsCount);
                                                                        viewsTV.setText(strViews+" views");

                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        cardView.setVisibility(View.VISIBLE);

                                                                        download.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                downloadURL(video_url);
                                                                            }
                                                                        });

                                                                    }


                                                                } catch (JSONException e) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                    else
                                                    {
                                                        Log.e("Response",response.message());

                                                        Request request = new Request.Builder()
                                                                .url("https://instagram-scraper2.p.rapidapi.com/media_info?short_code="+ID)
                                                                .get()
                                                                .addHeader("x-rapidapi-host", "instagram-scraper2.p.rapidapi.com")
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

                                                                                JSONObject data=jsonObject.getJSONObject("data");

                                                                                JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                                                                String display_url=shortcode_media.getString("display_url");

                                                                                Glide.with(getActivity())
                                                                                        .load(display_url)
                                                                                        .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                        .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                        .into(vidImg);
                                                                                Glide.with(getActivity())
                                                                                        .load(display_url)
                                                                                        .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                        .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                        .into(circleImageView);

                                                                                boolean is_video=shortcode_media.getBoolean("is_video");
                                                                                if(is_video)
                                                                                {

                                                                                    String video_url=shortcode_media.getString("video_url");
                                                                                    double video_duration=shortcode_media.getDouble("video_duration");
                                                                                    timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                    long viewsCount=shortcode_media.getLong("video_view_count");
                                                                                    String strViews=getFormattedAmount(viewsCount);
                                                                                    viewsTV.setText(strViews+" views");

                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                    cardView.setVisibility(View.VISIBLE);

                                                                                    download.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            downloadURL(video_url);
                                                                                        }
                                                                                    });

                                                                                }


                                                                            } catch (JSONException e) {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                            }

                                                                        }
                                                                    });

                                                                }
                                                                else
                                                                {
                                                                    Log.e("Response",response.message());

                                                                    Request request = new Request.Builder()
                                                                            .url("https://instagram28.p.rapidapi.com/media_info?short_code="+ID)
                                                                            .get()
                                                                            .addHeader("x-rapidapi-host", "instagram28.p.rapidapi.com")
                                                                            .addHeader("x-rapidapi-key", "49f2e9d8eamshff24c9679874ca7p149867jsn79bdeabe5c66")
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

                                                                                            JSONObject data=jsonObject.getJSONObject("data");

                                                                                            JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                                                                            String display_url=shortcode_media.getString("display_url");

                                                                                            Glide.with(getActivity())
                                                                                                    .load(display_url)
                                                                                                    .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                                    .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                                    .into(vidImg);
                                                                                            Glide.with(getActivity())
                                                                                                    .load(display_url)
                                                                                                    .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                                    .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                                    .into(circleImageView);

                                                                                            boolean is_video=shortcode_media.getBoolean("is_video");
                                                                                            if(is_video)
                                                                                            {

                                                                                                String video_url=shortcode_media.getString("video_url");
                                                                                                double video_duration=shortcode_media.getDouble("video_duration");
                                                                                                timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                                long viewsCount=shortcode_media.getLong("video_view_count");
                                                                                                String strViews=getFormattedAmount(viewsCount);
                                                                                                viewsTV.setText(strViews+" views");

                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                cardView.setVisibility(View.VISIBLE);

                                                                                                download.setOnClickListener(new View.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(View v) {
                                                                                                        downloadURL(video_url);
                                                                                                    }
                                                                                                });

                                                                                            }


                                                                                        } catch (JSONException e) {
                                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                                            DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                                        }

                                                                                    }
                                                                                });

                                                                            }
                                                                            else
                                                                            {
                                                                                Log.e("Response",response.message());

                                                                                Request request = new Request.Builder()
                                                                                        .url("https://instagram-scraper2.p.rapidapi.com/media_info?short_code="+ID)
                                                                                        .get()
                                                                                        .addHeader("x-rapidapi-host", "instagram-scraper2.p.rapidapi.com")
                                                                                        .addHeader("x-rapidapi-key", "49f2e9d8eamshff24c9679874ca7p149867jsn79bdeabe5c66")
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

                                                                                                        JSONObject data=jsonObject.getJSONObject("data");

                                                                                                        JSONObject shortcode_media=data.getJSONObject("shortcode_media");

                                                                                                        String display_url=shortcode_media.getString("display_url");

                                                                                                        Glide.with(getActivity())
                                                                                                                .load(display_url)
                                                                                                                .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                                                .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                                                .into(vidImg);
                                                                                                        Glide.with(getActivity())
                                                                                                                .load(display_url)
                                                                                                                .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                                                .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                                                .into(circleImageView);

                                                                                                        boolean is_video=shortcode_media.getBoolean("is_video");
                                                                                                        if(is_video)
                                                                                                        {

                                                                                                            String video_url=shortcode_media.getString("video_url");
                                                                                                            double video_duration=shortcode_media.getDouble("video_duration");
                                                                                                            timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                                            long viewsCount=shortcode_media.getLong("video_view_count");
                                                                                                            String strViews=getFormattedAmount(viewsCount);
                                                                                                            viewsTV.setText(strViews+" views");

                                                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                                                            cardView.setVisibility(View.VISIBLE);

                                                                                                            download.setOnClickListener(new View.OnClickListener() {
                                                                                                                @Override
                                                                                                                public void onClick(View v) {
                                                                                                                    downloadURL(video_url);
                                                                                                                }
                                                                                                            });

                                                                                                        }


                                                                                                    } catch (JSONException e) {
                                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                                        DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                                                    }

                                                                                                }
                                                                                            });

                                                                                        }
                                                                                        else
                                                                                        {

                                                                                            Request request = new Request.Builder()
                                                                                                    .url("https://instagram130.p.rapidapi.com/media-info?code="+ID)
                                                                                                    .get()
                                                                                                    .addHeader("x-rapidapi-host", "instagram130.p.rapidapi.com")
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

                                                                                                                    String display_url=jsonObject.getString("display_url");

                                                                                                                    Glide.with(getActivity())
                                                                                                                            .load(display_url)
                                                                                                                            .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                                                            .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                                                            .into(vidImg);
                                                                                                                    Glide.with(getActivity())
                                                                                                                            .load(display_url)
                                                                                                                            .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                                                            .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                                                            .into(circleImageView);

                                                                                                                    boolean is_video=jsonObject.getBoolean("is_video");
                                                                                                                    if(is_video)
                                                                                                                    {

                                                                                                                        String video_url=jsonObject.getString("video_url");
                                                                                                                        double video_duration=jsonObject.getDouble("video_duration");
                                                                                                                        timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                                                        long viewsCount=jsonObject.getLong("video_view_count");
                                                                                                                        String strViews=getFormattedAmount(viewsCount);
                                                                                                                        viewsTV.setText(strViews+" views");

                                                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                                                        cardView.setVisibility(View.VISIBLE);

                                                                                                                        download.setOnClickListener(new View.OnClickListener() {
                                                                                                                            @Override
                                                                                                                            public void onClick(View v) {
                                                                                                                                downloadURL(video_url);
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }


                                                                                                                } catch (JSONException e) {
                                                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                                                    DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                                                                }

                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                    else
                                                                                                    {

                                                                                                        Request request = new Request.Builder()
                                                                                                                .url("https://instagram130.p.rapidapi.com/media-info?code="+ID)
                                                                                                                .get()
                                                                                                                .addHeader("x-rapidapi-host", "instagram130.p.rapidapi.com")
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

                                                                                                                                String display_url=jsonObject.getString("display_url");

                                                                                                                                Glide.with(getActivity())
                                                                                                                                        .load(display_url)
                                                                                                                                        .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                                                                        .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                                                                        .into(vidImg);
                                                                                                                                Glide.with(getActivity())
                                                                                                                                        .load(display_url)
                                                                                                                                        .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                                                                        .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                                                                        .into(circleImageView);

                                                                                                                                boolean is_video=jsonObject.getBoolean("is_video");
                                                                                                                                if(is_video)
                                                                                                                                {

                                                                                                                                    String video_url=jsonObject.getString("video_url");
                                                                                                                                    double video_duration=jsonObject.getDouble("video_duration");
                                                                                                                                    timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                                                                    long viewsCount=jsonObject.getLong("video_view_count");
                                                                                                                                    String strViews=getFormattedAmount(viewsCount);
                                                                                                                                    viewsTV.setText(strViews+" views");

                                                                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                                                                    cardView.setVisibility(View.VISIBLE);

                                                                                                                                    download.setOnClickListener(new View.OnClickListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onClick(View v) {
                                                                                                                                            downloadURL(video_url);
                                                                                                                                        }
                                                                                                                                    });

                                                                                                                                }


                                                                                                                            } catch (JSONException e) {
                                                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                                                DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                                                                                                            }

                                                                                                                        }
                                                                                                                    });

                                                                                                                }
                                                                                                                else
                                                                                                                {

                                                                                                                    Request request = new Request.Builder()
                                                                                                                            .url("https://instagram130.p.rapidapi.com/media-info?code="+ID)
                                                                                                                            .get()
                                                                                                                            .addHeader("x-rapidapi-host", "instagram130.p.rapidapi.com")
                                                                                                                            .addHeader("x-rapidapi-key", "49f2e9d8eamshff24c9679874ca7p149867jsn79bdeabe5c66")
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

                                                                                                                                            String display_url=jsonObject.getString("display_url");

                                                                                                                                            Glide.with(getActivity())
                                                                                                                                                    .load(display_url)
                                                                                                                                                    .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                                                                                                                    .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                                                                                                                    .into(vidImg);
                                                                                                                                            Glide.with(getActivity())
                                                                                                                                                    .load(display_url)
                                                                                                                                                    .placeholder(R.drawable.ic_baseline_image_search_24_black)
                                                                                                                                                    .error(R.drawable.ic_outline_image_not_supported_24_black)
                                                                                                                                                    .into(circleImageView);

                                                                                                                                            boolean is_video=jsonObject.getBoolean("is_video");
                                                                                                                                            if(is_video)
                                                                                                                                            {

                                                                                                                                                String video_url=jsonObject.getString("video_url");
                                                                                                                                                double video_duration=jsonObject.getDouble("video_duration");
                                                                                                                                                timeTV.setText(String.format("%.2f",video_duration)+" secs");

                                                                                                                                                long viewsCount=jsonObject.getLong("video_view_count");
                                                                                                                                                String strViews=getFormattedAmount(viewsCount);
                                                                                                                                                viewsTV.setText(strViews+" views");

                                                                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                                                                cardView.setVisibility(View.VISIBLE);

                                                                                                                                                download.setOnClickListener(new View.OnClickListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onClick(View v) {
                                                                                                                                                        downloadURL(video_url);
                                                                                                                                                    }
                                                                                                                                                });

                                                                                                                                            }


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

                                                                                                }
                                                                                            });

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

                                                }
                                            });

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


    private void downloadURL(String url) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, strTime + ".mp4");

        downloadManager.enqueue(request);

    }

    private String getFormattedAmount(long amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insta, container, false);
    }
}
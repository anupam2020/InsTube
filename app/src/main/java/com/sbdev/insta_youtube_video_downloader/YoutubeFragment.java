package com.sbdev.insta_youtube_video_downloader;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicHint;
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

    private String ID="";

    private TextView vidTitle,vidTime;

    private ImageView vidImg;

    private ProgressBar progressBar;

    private ArrayList<YoutubeModel> arrayList;

    private YoutubeAdapter adapter;

    private RecyclerView recyclerView;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ytEditText=view.findViewById(R.id.youtubeLink);
        pasteBtn=view.findViewById(R.id.youtubePaste);
        downloadBtn=view.findViewById(R.id.youtubeDownload);
        vidTitle=view.findViewById(R.id.youtubeVideoTitle);
        vidTime=view.findViewById(R.id.youtubeVideoTime);
        vidImg=view.findViewById(R.id.youtubeVideoImg);
        progressBar=view.findViewById(R.id.youtubeProgress);
        recyclerView=view.findViewById(R.id.youtubeRecycler);

        progressBar.setVisibility(View.INVISIBLE);

        arrayList=new ArrayList<>();

        adapter=new YoutubeAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

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

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(haveStoragePermission(v))
                {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    progressBar.setVisibility(View.VISIBLE);
                    arrayList.clear();

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
                                                long lengthSeconds=videoDetails.getLong("lengthSeconds");
                                                String lenSec=String.format("%.2f",(double)lengthSeconds/60)+" mins";

                                                vidTitle.setText(title);
                                                vidTime.setText("Time: "+lenSec);

                                                JSONObject thumbnail=videoDetails.getJSONObject("thumbnail");
                                                JSONArray thumbnails=thumbnail.getJSONArray("thumbnails");

                                                JSONObject index=thumbnails.getJSONObject(thumbnails.length()-1);
                                                String urlIMG=index.getString("url");

                                                Glide.with(getActivity())
                                                        .load(urlIMG)
                                                        .placeholder(R.drawable.ic_baseline_image_search_24_resized)
                                                        .error(R.drawable.ic_outline_image_not_supported_24_resized)
                                                        .into(vidImg);


                                                JSONObject streamingData=jsonObject.getJSONObject("streamingData");

                                                JSONArray formats=streamingData.getJSONArray("formats");

                                                for(int i=0;i<formats.length();i++)
                                                {

                                                    JSONObject indexJSON=formats.getJSONObject(i);

                                                    String quality=indexJSON.getString("quality");
                                                    String qualityLabel=indexJSON.getString("qualityLabel");
                                                    String mimeType=indexJSON.getString("mimeType");

                                                    String newMimeType=mimeType.substring(0,mimeType.indexOf(';')).toUpperCase();

                                                    String newQuality=(qualityLabel+": "+quality).toUpperCase();

                                                    String vidURL=indexJSON.getString("url");

                                                    arrayList.add(new YoutubeModel(urlIMG,vidURL,newQuality,newMimeType,title));

                                                    progressBar.setVisibility(View.GONE);

                                                }

                                                adapter.notifyDataSetChanged();


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

            }
        });

    }


    private void downloadURL(String url,String title)
    {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));

        DownloadManager downloadManager= (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp4");

        downloadManager.enqueue(request);

        DynamicToast.make(getActivity(),"Downloading file!",
                getActivity().getResources().getDrawable(R.drawable.ic_outline_download_for_offline_24_red),2000).show();

    }

    public  boolean haveStoragePermission(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Snackbar.make(view,"InsTube needs storage permission to download files!",2500).show();
                return false;
            }
        }

        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube, container, false);
    }
}
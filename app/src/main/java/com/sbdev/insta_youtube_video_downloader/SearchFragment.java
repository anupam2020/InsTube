package com.sbdev.insta_youtube_video_downloader;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {

    private ProgressBar progressBar;

    private Button search;

    private TextInputEditText searchText;

    private RecyclerView recyclerView;

    private SearchAdapter adapter;

    private ArrayList<SearchModel> arrayList;

    private OkHttpClient client;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        search=view.findViewById(R.id.searchDownload);
        progressBar=view.findViewById(R.id.searchProgress);
        searchText=view.findViewById(R.id.searchLink);

        recyclerView=view.findViewById(R.id.searchRecycler);

        arrayList=new ArrayList<>();
        client=new OkHttpClient();

        adapter=new SearchAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.INVISIBLE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);
                arrayList.clear();

                String text=searchText.getText().toString();

                text=text.trim();

                if(text.isEmpty())
                {
                    DynamicToast.makeWarning(getActivity(),"Field cannot be empty",2000).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else
                {

                    Request request = new Request.Builder()
                            .url("https://youtube-search-results.p.rapidapi.com/youtube-search/?q="+text)
                            .get()
                            .addHeader("x-rapidapi-host", "youtube-search-results.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "19c7e07597mshd4a487bebda6ef4p1c4c7fjsna9c61e2c34f8")
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                                }
                            });
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

                                            JSONArray items=jsonObject.getJSONArray("items");

                                            for(int i=0;i<items.length();i++)
                                            {

                                                JSONObject index=items.getJSONObject(i);

                                                if ((index.has("id")
                                                        && index.getString("id") != null
                                                        && index.getString("id").trim().length() != 0))
                                                {

                                                    String title=index.getString("title");
                                                    String vidID=index.getString("id");

                                                    JSONObject bestThumbnail=index.getJSONObject("bestThumbnail");
                                                    String url=bestThumbnail.getString("url");

                                                    JSONObject author=index.getJSONObject("author");
                                                    String name=author.getString("name");

                                                    JSONArray avatars=author.getJSONArray("avatars");
                                                    JSONObject zero=avatars.getJSONObject(0);

                                                    String authorImgURL=zero.getString("url");

                                                    long views=index.getLong("views");
                                                    String convertedViews=getFormattedAmount(views);
                                                    String duration=index.getString("duration");
                                                    String uploadedAt=index.getString("uploadedAt");

                                                    arrayList.add(new SearchModel(url,authorImgURL,duration,title,name,convertedViews,uploadedAt,vidID));

                                                }


                                            }

                                            adapter.notifyDataSetChanged();
                                            progressBar.setVisibility(View.INVISIBLE);

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

    private String getFormattedAmount(long amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}
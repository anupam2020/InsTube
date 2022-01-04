package com.sbdev.insta_youtube_video_downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PlayVideoWebView extends AppCompatActivity {

    private WebView webView;

    private WebViewClient client;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_web_view);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        webView=findViewById(R.id.playVideoWebView);
        progressBar=findViewById(R.id.playVideoProgress);

        client=new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        };
        webView.setWebViewClient(client);
        webView.getSettings().setJavaScriptEnabled(true);

        if(getIntent().getStringExtra("playVidURL")!=null)
        {
            webView.loadUrl(getIntent().getStringExtra("playVidURL"));
        }

    }
}
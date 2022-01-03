package com.sbdev.insta_youtube_video_downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class TermsConditionsActivity extends AppCompatActivity {

    private WebView webView;

    private ImageView back;

    private WebViewClient client;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        webView=findViewById(R.id.termsWebView);
        back=findViewById(R.id.termsBack);
        progressBar=findViewById(R.id.termsProgress);

        webView.loadUrl("file:///android_asset/terms.html");

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



}
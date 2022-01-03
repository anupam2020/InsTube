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

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;

    private ImageView back;

    private WebViewClient client;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        webView=findViewById(R.id.privacyWebView);
        back=findViewById(R.id.privacyBack);
        progressBar=findViewById(R.id.privacyProgress);

        webView.loadUrl("file:///android_asset/privacy.html");

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
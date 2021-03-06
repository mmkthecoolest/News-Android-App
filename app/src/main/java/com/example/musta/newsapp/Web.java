package com.example.musta.newsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Web extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        WebView browser = (WebView) findViewById(R.id.browser);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl(intent.getStringExtra("URL"));
        toaster("Loading webpage...", false);
    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }
}

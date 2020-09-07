package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rebook.automart.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/30/2019.
 */

public class SliderWebActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar toolbar;
    String webName,webUrl;
    private Context mContext;
    private FrameLayout mContainer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_web);
        ButterKnife.bind(this);



        webName = getIntent().getStringExtra("web_name");
        webUrl = getIntent().getStringExtra("web_url");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText(webName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        WebView myWebView  = findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(webUrl);
        myWebView.getSettings().setJavaScriptEnabled(true);

        // Set WebView client
        myWebView.setWebChromeClient(new WebChromeClient());

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


                myWebView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressDialog=new ProgressDialog(SliderWebActivity.this);
                        progressDialog.setMessage(getString(R.string.waiting));
                        progressDialog.show();
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        progressDialog.dismiss();
                    }
                });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
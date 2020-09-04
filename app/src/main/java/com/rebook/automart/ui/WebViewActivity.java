package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.rebook.automart.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/30/2019.
 */

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar toolbar;
    String paymentMethod,bookingId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.waiting));
        dialog.show();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText("Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        paymentMethod = getIntent().getStringExtra("payment_method");
        bookingId = getIntent().getStringExtra("encrypt_booking_id");
        Log.e("payment method",paymentMethod);
        Log.e("booking id ",bookingId);

        WebView myWebView = (WebView) findViewById(R.id.payment_web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


       // myWebView.loadUrl("https://developer.android.com/guide/webapps/webview");

       /* myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }
        });*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },8000);
        myWebView.setWebChromeClient(new WebChromeClient());
        //myWebView.loadUrl(url);
        myWebView.loadUrl("https://www.automart.com.mm/booking/payment_confirmation/"+paymentMethod+"/"+bookingId+"");


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                overridePendingTransition(0,0);
                startActivity(intent);
                finish();
                return true;
            case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }
}

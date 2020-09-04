package com.rebook.automart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.util.TinyDB;

/**
 * Created by Dell on 1/2/2019.
 */

public class StartActivity extends AppCompatActivity {

    TinyDB tinyDB;
    boolean login;

    private Handler updateHandler =new Handler();
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            goSlider();
        }
    };

  /* private Handler updateHandler = new Handler();
   Runnable runnable = new Runnable() {
       @Override
       public void run() {
           startActivity(new Intent(StartActivity.this, SlideActivity.class));
           finish();
       }
   };*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tinyDB = new TinyDB(StartActivity.this);
        login = tinyDB.getBoolean(Config.LOG_IN);

        updateHandler.postDelayed(runnable,2000);

    }
    private void goSlider(){
        if (login){
            startActivity(new Intent(this,MainActivity.class));
            overridePendingTransition(0,0);
        }else {
            startActivity(new Intent(this, SlideActivity.class));
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateHandler.removeCallbacksAndMessages(null);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

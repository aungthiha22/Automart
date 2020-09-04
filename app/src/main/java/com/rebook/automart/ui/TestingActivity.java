package com.rebook.automart.ui;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.HomeAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.DataRespone;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.ProductImage;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class TestingActivity extends AppCompatActivity{

    @BindView(R.id.testing_button)Button btnTesting;

    OkHttpClient okHttpClient;
    TinyDB tinyDB;
    SyncPostService syncPostService;
    String name ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_activity);

        ButterKnife.bind(this);
        tinyDB=new TinyDB(this);
        okHttpClient = new OkHttpClient();

        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(Config.MAIN_URL)
                .build();
        okHttpClient.setCertificatePinner(certificatePinner);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.MAIN_URL+Config.API)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setClient(new OkClient(okHttpClient))
                .build();
        syncPostService = restAdapter.create(SyncPostService.class);

        btnTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncPostService.getSearchProduct("ball joint", "2001Suzuki Swift 1200 cc K12C ZC53S", 1,
                        new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                Toast.makeText(TestingActivity.this, "success", Toast.LENGTH_SHORT).show();

                                JsonObject jsonObjectData = jsonObject.get("data").getAsJsonObject();
                                JsonArray jsonArrayFit = jsonObjectData.get("fit").getAsJsonArray();
                                JsonArray jsonArrayProduct = jsonObjectData.get("normal").getAsJsonArray();

                                Log.e("testing","fit________________"+jsonArrayFit.size());
                                Log.e("testing","normal_____________"+jsonArrayProduct.size());


                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });

            }
        });

    }
}
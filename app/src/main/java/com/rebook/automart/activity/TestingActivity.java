package com.rebook.automart.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

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
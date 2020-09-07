package com.rebook.automart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZawgyiTextView;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class OrderCancelActivity extends AppCompatActivity {
    public static final String ORDER_ID= "order_id";
    public static final String ORDER_NAME= "order_name";
    @BindView(R.id.cancel_name)TextView txtName;
    @BindView(R.id.cancel_spinner)Spinner spinner;
    @BindView(R.id.cancel_desc)EditText txtDesc;
    @BindView(R.id.cancel)Button btnCancel;
    @BindView(R.id.submit)Button btnSubmit;
    @BindView(R.id.toolbar)Toolbar toolbar;
    OkHttpClient okHttpClient;
    TinyDB tinyDB ;
    SyncPostService syncPostService;
    String intentName;
    int intentId;
    String spinnerValue;

    String []arr = {"Answer Questionnaires for Order Cancelation",
                    "တစ်ခြားပစ္စည်းအမျိုးအစား ပြောင်းဝယ်ချင်လို့",
                    "တစ်ခြားနေရာမှာ ဈေးသက်သာတာတွေ့လို့",
                    "စိတ်ပြောင်းသွားလို့",
                    "ငွေပေးချေမှုပြောင်းချင်လို့",
                    "ပစ္စည်းအေရေအတွက် ထပ်တိုးချင်လို့",
                    "ဘောင်ချာခွဲမှာချင်လို့",
                    "တစ်ခြားပစ္စည်းနဲ့ တွဲမှာမလို့",
                    "ပစ္စည်းပို့ချိန်ကြာလို့",
                    "ပစ္စည်းပို့ခများလို့",
                    "ပစ္စည်းပို့မယ့်ရက် အတိအကျ မသိရလို့",
                    "လိပ်စာပြောင်းချင်လို့",
                    "ကူပွန်ကုတ် ထည့်ဖို့မေ့ခဲ့လို့",
                    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_cancel);
        ButterKnife.bind(this);

        intentId = getIntent().getIntExtra(ORDER_ID,0);
        intentName = getIntent().getStringExtra(ORDER_NAME);
        tinyDB = new TinyDB(OrderCancelActivity.this);
        okHttpClient = new OkHttpClient();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText(getResources().getString(R.string.cancel_order));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        txtName.setText(intentName);
        CertificatePinner certificatePinner=new CertificatePinner.Builder()
                .add(Config.MAIN_URL)
                .build();
        okHttpClient.setCertificatePinner(certificatePinner);
        RestAdapter restAdapter=new RestAdapter.Builder()
                .setEndpoint(Config.MAIN_URL+Config.API)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setClient(new OkClient(okHttpClient))
                .build();
        syncPostService=restAdapter.create(SyncPostService.class);

        spinner.setAdapter(getSpinnerAdapter(R.array.cancel_spanner));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Name","____________"+position);
                Log.e("Name","____________"+spinner.getSelectedItemPosition());
                spinnerValue = arr[spinner.getSelectedItemPosition()];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncPostService.getOrderCancel(tinyDB.getString(Config.STORE_TOKEN),
                        String.valueOf(intentId),
                        spinnerValue,
                        txtDesc.getText().toString(),
                        new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                boolean success = jsonObject.get("success").getAsBoolean();
                                if (success){
                                    ZgToast zgToast = new ZgToast(OrderCancelActivity.this);
                                    zgToast.setZgText("Your Order have been Canceled");
                                    zgToast.show();
                                }
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

    }
    private ArrayAdapter<String> getSpinnerAdapter(int spinnerArray) {
        String[] items = this.getResources().getStringArray(spinnerArray);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, R.layout.custom_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        return spinnerAdapter;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                  finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

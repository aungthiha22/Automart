package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.PostAutoMart;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
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

/**
 * Created by Dell on 1/9/2019.
 */

public class SaveVehicle extends AppCompatActivity {

    @BindView(R.id.ripple_save_vehicle)
    RippleView rippleView;
    @BindView(R.id.search_scroll)
    ScrollView scrollView;
    @BindView(R.id.choice_year)
    Spinner yearSpinner;
    @BindView(R.id.choice_brand)
    Spinner brandSpinner;
    @BindView(R.id.choice_engine)
    Spinner engineSpinner;
    @BindView(R.id.choice_model)
    Spinner modelSpinner;
    @BindView(R.id.choice_chassis_code)
    Spinner chassisSpinner;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    SyncPostService syncPostService;
    OkHttpClient okHttpClient;
    ArrayList<PostAutoMart> brand= new ArrayList<>();
    ArrayList<PostAutoMart> model= new ArrayList<>();
    ArrayList<PostAutoMart> engine= new ArrayList<>();
    ArrayList<PostAutoMart> chassis_code= new ArrayList<>();

    ArrayList<String> brandList = new ArrayList<>();
    ArrayList<String> modelList = new ArrayList<>();
    ArrayList<String> engineList = new ArrayList<>();
    ArrayList<String> chassis_codeList = new ArrayList<>();

    String yearClick, brandClick , modelClick, engineClick , chassisClick;
    String brandName, modelName, engineName, chassisName;

    ArrayList<String> year = new ArrayList<>();
    int value = 1972;
    private AppCompatAutoCompleteTextView autoTextView;
    TinyDB tinyDB ;

    ProgressDialog mainProgressDialog;
    SQLiteDatabase db;
    String intentSearch="no";
    int tableId;

    public static SaveVehicle getInstance() {
        return new SaveVehicle();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_vehicle_activity);
        ButterKnife.bind(this);

       tableId = getIntent().getIntExtra("tableId",-1);
       intentSearch = getIntent().getStringExtra("search");

        for (int i = 1; i<50; i++){
            year.add(String.valueOf(value));
            value ++;
            Log.e("yaar _____ ","");

        }

     // year = getResources().getStringArray(R.array.year);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Save Vehicle");
        TextView titleTextView =  mToolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText("Save Vehicle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,year);
        yearSpinner.setAdapter(yearAdapter);

        if (NetService.isInternetAvailable(SaveVehicle.this)) {
            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //yearClick = year[position];
                    yearClick = year.get(position);
                  //  Toast.makeText(SaveVehicle.this, "year id " + yearClick, Toast.LENGTH_SHORT).show();

                    okHttpClient = new OkHttpClient();

                    CertificatePinner certificatePinner = new CertificatePinner.Builder()
                            .add(Config.MAIN_URL)
                            .build();
                    okHttpClient.setCertificatePinner(certificatePinner);
                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Config.MAIN_URL + Config.API)
                            .setLogLevel(RestAdapter.LogLevel.NONE)
                            .setClient(new OkClient(okHttpClient))
                            .build();
                    syncPostService = restAdapter.create(SyncPostService.class);

                    syncPostService.getBrand(yearClick, 1, new Callback<JsonArray>() {
                        @Override
                        public void success(JsonArray jsonElements, Response response) {
                            brandList.clear();
                            brand.clear();
                            for (int i = 0; i < jsonElements.size(); i++) {
                                Gson gson = new Gson();
                                brand.add(gson.fromJson(jsonElements.get(i), PostAutoMart.class));
                                brandList.add(brand.get(i).getName());
                            }

                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                                    SaveVehicle.this, android.R.layout.simple_list_item_1, brandList);
                            // adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            brandSpinner.setAdapter(adapter1);

                            brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    //  brandList.clear();
                                    //  modelList.clear();

                                    brandClick = brand.get(position).getId();
                                    brandName = brand.get(position).getName();

                                    model.clear();
                                    syncPostService.getModelList(yearClick, brandClick, 1, new Callback<JsonArray>() {
                                        @Override
                                        public void success(JsonArray jsonElements, Response response) {
                                            modelList.clear();
                                            model.clear();
                                            for (int i = 0; i < jsonElements.size(); i++) {
                                                Gson gson = new Gson();
                                                model.add(gson.fromJson(jsonElements.get(i), PostAutoMart.class));
                                                modelList.add(model.get(i).getName());
                                            }
                                            ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(
                                                    SaveVehicle.this, android.R.layout.simple_list_item_1, modelList);
                                            modelSpinner.setAdapter(modelAdapter);
                                            modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    // list.clear();
                                                    modelClick = model.get(position).getId();
                                                    modelName = model.get(position).getName();

                                                    syncPostService.getEngineList(yearClick, brandClick, modelClick, 1, new Callback<JsonArray>() {
                                                        @Override
                                                        public void success(JsonArray jsonElements, Response response) {

                                                            engineList.clear();
                                                            engine.clear();
                                                            for (int i = 0; i < jsonElements.size(); i++) {
                                                                Gson gson = new Gson();
                                                                engine.add(gson.fromJson(jsonElements.get(i), PostAutoMart.class));
                                                                engineList.add(engine.get(i).getName());
                                                            }
                                                            final ArrayAdapter<String> engineAdapter = new ArrayAdapter<String>(
                                                                    SaveVehicle.this, android.R.layout.simple_list_item_1, engineList);
                                                            engineSpinner.setAdapter(engineAdapter);
                                                            engineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    engineClick = engine.get(position).getId();
                                                                    engineName = engine.get(position).getName();
                                                                    syncPostService.getChassisCodeList(yearClick, brandClick, modelClick, engineClick, 1,
                                                                            new Callback<JsonArray>() {
                                                                                @Override
                                                                                public void success(JsonArray jsonElements, Response response) {

                                                                                    chassis_code.clear();
                                                                                    chassis_codeList.clear();
                                                                                    for (int i = 0; i < jsonElements.size(); i++) {
                                                                                        Gson gson = new Gson();
                                                                                        chassis_code.add(gson.fromJson(jsonElements.get(i), PostAutoMart.class));
                                                                                        chassis_codeList.add(chassis_code.get(i).getName());
                                                                                    }
                                                                                    ArrayAdapter<String> chassisAdapter = new ArrayAdapter<String>(
                                                                                            SaveVehicle.this, android.R.layout.simple_list_item_1, chassis_codeList);
                                                                                    chassisSpinner.setAdapter(chassisAdapter);
                                                                                    chassisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                        @Override
                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                            chassisClick = chassis_code.get(position).getId();
                                                                                            chassisName = chassis_code.get(position).getName();
                                                                                     //       Toast.makeText(SaveVehicle.this, "Chassis id : " + chassis_code.get(position).getName(), Toast.LENGTH_SHORT).show();
                                                                                        }

                                                                                        @Override
                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void failure(RetrofitError error) {

                                                                                }
                                                                            });


                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void failure(RetrofitError error) {
                                                          //  Toast.makeText(SaveVehicle.this, "error\n " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            // Log.e("name size : ","-------"+brandList.size());
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            ZgToast zgToast = new ZgToast(SaveVehicle.this);
            zgToast.setZgText(getResources().getString(R.string.no_internet));
            zgToast.setError();
            zgToast.show();
        }


        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ZgToast zgToast = new ZgToast(SaveVehicle.this);

                    Log.e("year id : ",yearClick);
                    Log.e("brand id : ", String.valueOf(brandClick));
                    Log.e("model id : ", String.valueOf(modelClick));
                    Log.e("engine id : ", String.valueOf(engineClick));
                    Log.e("chass id : ", String.valueOf(chassisClick));
                    Log.e("__________","____________");
                    Log.e("year Name : ",yearClick);
                    if (brandName == null)
                        return;
                    Log.e("brand Name : ",brandName);
                    if (modelName == null)
                        return;
                    Log.e("model Name : ", modelName);
                    if (engineName == null )
                        return;
                    Log.e("engine Name : ",engineName);
                    if (chassisName == null)
                        return;
                    Log.e("chass Name : ",chassisName);

                    tinyDB = new TinyDB(SaveVehicle.this);
                    tinyDB.putStringMethod("default_vehicle","yes");
                    tinyDB.putStringMethod("vehicle_year",yearClick);
                    tinyDB.putStringMethod("vehicle_brand",brandName);
                    tinyDB.putStringMethod("vehicle_engine",engineName);
                    tinyDB.putStringMethod("vehicle_model",modelName);
                    tinyDB.putStringMethod("vehicle_chassis",chassisName);

                    zgToast.setZgText("You have been saved your vehicle type");

                    if (intentSearch.equals("yes")){
                        startActivity(new Intent(SaveVehicle.this, ActivitySearch.class));
                    }else {
                        startActivity(new Intent(SaveVehicle.this, MainActivity.class));
                    }
                    overridePendingTransition(0,0);
                    finish();

                zgToast.show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent intent = new Intent(SaveVehicle.this, MainActivity.class);
                //startActivity(intent);
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
       // startActivity(new Intent(SaveVehicle.this,MainActivity.class));
        //overridePendingTransition(0,0);
        finish();
    }
}



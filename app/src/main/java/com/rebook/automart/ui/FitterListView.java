package com.rebook.automart.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.ProductSearchPageAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.Fit;
import com.rebook.automart.model.Product;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
 * Created by Dell on 2/26/2019.
 */

public class FitterListView extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.product_search_list)RecyclerView searchRecycler;
    @BindView(R.id.swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emptyView)TextView emptyView;

    SyncPostService syncPostService;
    ProductSearchPageAdapter searchPageAdapter;
    OkHttpClient okHttpClient;
    //LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zawgyiToast;
    boolean isRefresh,isLoadMore;
    String type ;
    ArrayList<Product> productList;
    String vehicle_name;
    String titleChoice,vehicle_year,vehicle_brand,vehicle_model,vehicle_engine,vehicle_chassis_code;
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;
    TinyDB tinyDB ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitter_list);
        ButterKnife.bind(this);
        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);

        tinyDB = new TinyDB(FitterListView.this);
        titleChoice = getIntent().getStringExtra(ActivitySearch.SEARCH_TEXT);

        final Cursor cursor = db.rawQuery("select * from vehicle",null);
        while (cursor.moveToNext()){
            cursor.getInt(cursor.getColumnIndex("id"));
            vehicle_year = cursor.getString(cursor.getColumnIndex("year"));
            vehicle_brand = cursor.getString(cursor.getColumnIndex("brand"));
            vehicle_engine = cursor.getString(cursor.getColumnIndex("engine"));
            vehicle_model = cursor.getString(cursor.getColumnIndex("model"));
            vehicle_chassis_code = cursor.getString(cursor.getColumnIndex("chassis_code"));
        }
        vehicle_year = tinyDB.getString("vehicle_year");
        vehicle_brand = tinyDB.getString("vehicle_brand");
        vehicle_engine = tinyDB.getString("vehicle_engine");
        vehicle_model = tinyDB.getString("vehicle_model");
        vehicle_chassis_code = tinyDB.getString("vehicle_chassis");
        vehicle_name = vehicle_year+vehicle_brand+" "+vehicle_model+" "+vehicle_engine+" "+vehicle_chassis_code;

       // Log.e("fitterlistview","vehicle name ____________"+vehicle_name);
        if (vehicle_name.length()<=5){
            //Toast.makeText(this, "Save Your vehicle first", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ZgToast zgToast = new ZgToast(FitterListView.this);
                    zgToast.setZgText(getResources().getString(R.string.save_vehicle));
                    zgToast.setError();
                    zgToast.show();
                    Intent intent = new Intent(FitterListView.this,SaveVehicle.class);
                    //intent.putExtra("vehicle","select");
                    startActivity(intent);
                }
            },2000);
            return;
        }


       //Log.e("vehicle name ","_________"+vehicle_name);
       //Log.e("vehicle count ","_________"+vehicle_name.length());
       //Log.e("table count ","_________"+cursor.getCount());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search Product");
        TextView toolbarEditText = mToolbar.findViewById(R.id.toolbar_search_editText);

        toolbarEditText.setText(titleChoice);
        toolbarEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FitterListView.this,ActivitySearch.class));
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        searchPageAdapter = new ProductSearchPageAdapter(FitterListView.this);
        okHttpClient = new OkHttpClient();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FitterListView.this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecycler.setLayoutManager(gridLayoutManager);

        //swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.green, R.color.yellow);
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

        loadData(1);
        // linearLayoutManager=new LinearLayoutManager(getActivity());
        searchRecycler.setAdapter(searchPageAdapter);
        searchRecycler.setLayoutManager(gridLayoutManager);

        searchRecycler.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                searchPageAdapter.showLoading(true);
                searchPageAdapter.notifyDataSetChanged();
                loadData(page);
            }
        };
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.red, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isRefresh=true;
                        endlessRecyclerViewScrollListener.currentPage=0;
                        endlessRecyclerViewScrollListener.previousTotalItemCount=0;
                        endlessRecyclerViewScrollListener.loading=false;
                        loadData(1);
                    }
                });
        searchRecycler.setOnScrollListener(endlessRecyclerViewScrollListener);
    }

    private void loadData(final int page) {
        if(NetService.isInternetAvailable(FitterListView.this)){

            syncPostService.getSearchProduct(titleChoice, vehicle_name, 1
                    , new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (jsonObject != null){


                                JsonObject jsonObjectData = jsonObject.get("data").getAsJsonObject();
                                JsonArray jsonArrayFit = jsonObjectData.get("fit").getAsJsonArray();
                                JsonArray jsonArrayProduct = jsonObjectData.get("normal").getAsJsonArray();

                                productList = new ArrayList<Product>();
                                if (jsonArrayFit.size()>0){
                                    for (int i=0; i<jsonArrayFit.size(); i++){
                                        Gson gson = new Gson();
                                        productList.add(gson.fromJson(jsonArrayFit.get(i),Product.class));
                                    }
                                }

                                for (int i=0; i<jsonArrayProduct.size(); i++){
                                    Gson gson = new Gson();
                                    productList.add(gson.fromJson(jsonArrayProduct.get(i),Product.class));
                                }
                                Log.e("product size ","+++++++++++________"+productList.size());
                                Log.e("normal size ","+++++++++++________"+jsonArrayProduct.size());
                                Log.e("fit list ","+++++++++++++++\t"+jsonArrayFit.size());

                                searchPageAdapter.setFit(jsonArrayFit.size());
                               /* JsonArray jsonArrayFit = jsonObjectData.get("fit").getAsJsonArray();
                                for(int i=0;i<jsonArrayFit.size();i++){
                                    Gson gson=new Gson();
                                    fitList.add(gson.fromJson(jsonArrayFit.get(i),Fit.class));
                                }
                                Log.e("fit size ", "___________"+String.valueOf(fitList.size()));*/
                                if(page!=1){
                                    searchPageAdapter.append(productList);
                                    searchPageAdapter.notifyDataSetChanged();
                                }
                                else {
                                    searchPageAdapter.replaceWith(productList);
                                    searchPageAdapter.notifyDataSetChanged();
                                }
                                searchPageAdapter.showLoading(false);

                            }else {
                                emptyView.setText(getResources().getString(R.string.no_data));
                                searchRecycler.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);

                            }
                        }
                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

        }
        else {
            swipeRefreshLayout.setRefreshing(false);
            zawgyiToast=new ZgToast(FitterListView.this);
            zawgyiToast.setZgText("no_internet");
            zawgyiToast.setError();
            zawgyiToast.show();
            if(!isRefresh || !isLoadMore){
                searchRecycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(FitterListView.this,MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FitterListView.this,MainActivity.class));
        finish();
    }

}
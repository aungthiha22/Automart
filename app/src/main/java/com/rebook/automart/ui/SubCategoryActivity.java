package com.rebook.automart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.SubCategoryAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.PostAutoMart;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.widget.ZgToast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
 * Created by ATHK on 1/29/2019.
 */

public class SubCategoryActivity extends AppCompatActivity{

    public static final String CATEGORY_ID = "category_id";
    public static final String SUB_CATEGORY_ID = "sub_category_id";
    public static final String CATEGORY_NAME = "category_name";
    @BindView(R.id.scl_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.scl_swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scl_progressBar)ProgressBar progressBar;
    @BindView(R.id.scl_emptyView)TextView emptyView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    SyncPostService syncPostService;

    String category_id ,category_name;
    OkHttpClient okHttpClient;
    ArrayList<PostAutoMart> postList;
    LinearLayoutManager linearLayoutManager;

    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zawgyiToast;
    boolean isRefresh,isLoadMore;
    SubCategoryAdapter subCategoryAdapter ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sub Categories");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText(getResources().getString(R.string.subCategory));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        category_id = getIntent().getStringExtra(CATEGORY_ID);
        category_name = getIntent().getStringExtra(CATEGORY_NAME);
        titleTextView.setText(category_name);
        subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this);
        okHttpClient = new OkHttpClient();


        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.blue,R.color.blue,R.color.red);

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

        linearLayoutManager=new LinearLayoutManager(SubCategoryActivity.this);
        recyclerView.setAdapter(subCategoryAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(subCategoryAdapter);
        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                subCategoryAdapter.showLoading(true);
                //customAdapter.notifyDataSetChanged();
                //Log.i("onScroll",""+page+"\ttotalItemsCount = "+totalItemsCount+"\t type = "+getArguments().getString(TYPE));
                loadData(page);
            }
        };
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh=true;
                endlessRecyclerViewScrollListener.currentPage=0;
                endlessRecyclerViewScrollListener.previousTotalItemCount=0;
                endlessRecyclerViewScrollListener.loading=false;
                loadData(1);
            }
        });


    }
    public void loadData( final int page){

//        customAdapter.setType("rent");
        syncPostService.getSubCategory(category_id,page, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if(jsonElements!=null){

                    if(jsonElements.size()>0){
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        postList=new ArrayList<PostAutoMart>();
                        for(int i=0;i<jsonElements.size();i++){
                            Gson gson=new Gson();
                            postList.add(gson.fromJson(jsonElements.get(i),PostAutoMart.class));
                        }
                        if(page !=1){
                            subCategoryAdapter.append(postList);
                            Log.e("append size : ", String.valueOf(postList.size()));
                        }
                        else {
                            subCategoryAdapter.replaceWith(postList);
                            Log.e("replaceWith : ", String.valueOf(postList.size()));
                        }
                    }
                }
                else {
                    if(page==1){
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setText("no internet connection");
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                subCategoryAdapter.showLoading(false);
                subCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(true);//
        MenuItem setting = menu.findItem(R.id.setting);
        setting.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SubCategoryActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return true;
            case R.id.search:
                startActivity(new Intent(SubCategoryActivity.this,ActivitySearch.class));
                overridePendingTransition(0,0);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SubCategoryActivity.this,MainActivity.class));
        finish();

    }
}

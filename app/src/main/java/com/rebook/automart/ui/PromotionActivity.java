package com.rebook.automart.ui;

import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.HomeAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.Product;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
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
 * Created by Dell on 2/18/2019.
 */

public class PromotionActivity extends AppCompatActivity {


    @BindView(R.id.scl_recyclerView)RecyclerView recyclerView;
    @BindView(R.id.scl_swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scl_progressBar)ProgressBar progressBar;
    @BindView(R.id.scl_emptyView)TextView emptyView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    SyncPostService syncPostService;

    OkHttpClient okHttpClient;
    ArrayList<Product> postList;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zgToast;
    boolean isRefresh,isLoadMore;
    HomeAdapter homeAdapter ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);


        titleTextView.setText("Promotion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        homeAdapter = new HomeAdapter(PromotionActivity.this);
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
        GridLayoutManager gridLayoutManagerNew = new GridLayoutManager(PromotionActivity.this,2);
        gridLayoutManagerNew.setOrientation(LinearLayoutManager.VERTICAL);

        linearLayoutManager=new LinearLayoutManager(PromotionActivity.this);
        recyclerView.setAdapter(homeAdapter);
        recyclerView.setLayoutManager(gridLayoutManagerNew);

        recyclerView.setAdapter(homeAdapter);
        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                homeAdapter.showLoading(true);
                homeAdapter.notifyDataSetChanged();
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
        progressBar.setVisibility(View.VISIBLE);
        homeAdapter.setType("promotion_list");
        if (NetService.isInternetAvailable(PromotionActivity.this)) {

            //Log.e("Sub Category id","________"+sub_category_id);
            syncPostService.getPromotionList(1, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    swipeRefreshLayout.setRefreshing(false);
                    JsonArray jsonArrayData = jsonObject.get("data").getAsJsonArray();

                    if (jsonArrayData != null) {

                        if (jsonArrayData.size() > 0) {

                            emptyView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            postList = new ArrayList<Product>();
                            for (int i = 0; i < jsonArrayData.size(); i++) {
                                Gson gson = new Gson();
                                postList.add(gson.fromJson(jsonArrayData.get(i), Product.class));

                            }
                            if (page != 1) {
                                homeAdapter.append(postList);
                                homeAdapter.notifyDataSetChanged();
                            } else {
                                homeAdapter.replaceWith(postList);
                                homeAdapter.notifyDataSetChanged();
                            }
                        }else {
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            },100);*/
                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            };
                            handler.postDelayed(runnable,1000);
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setText(getResources().getString(R.string.no_data));
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        if (page == 1) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setText(getResources().getString(R.string.no_data));
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                    homeAdapter.showLoading(false);
                    homeAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });


        }else {
            zgToast = new ZgToast(PromotionActivity.this);
            zgToast.setError();
            zgToast.setZgText(getResources().getString(R.string.no_internet));
            zgToast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(PromotionActivity.this, MainActivity.class);
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
        startActivity(new Intent(PromotionActivity.this,MainActivity.class));
        finish();
    }

}

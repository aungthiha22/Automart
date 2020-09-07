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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.UserOrderAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.DataRespone;
import com.rebook.automart.model.Product;
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

public class UserOrderActivity extends AppCompatActivity {

    @BindView(R.id.scl_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.scl_swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scl_progressBar)ProgressBar progressBar;
    @BindView(R.id.scl_emptyView)TextView emptyView;
    @BindView(R.id.toolbar)Toolbar toolbar;

    public static final String INTENT_VALUE = "intent_value";
    TextView toolbar_textView;
    SyncPostService syncPostService;
    UserOrderAdapter userOrderAdapter;
    OkHttpClient okHttpClient;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zawgyiToast;
    boolean isRefresh,isLoadMore;
    ArrayList<Product> userOrderList = new ArrayList<>();
    TinyDB tinyDB;
    String intentValue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar_textView = toolbar.findViewById(R.id.toolbar_title);
        okHttpClient = new OkHttpClient();
        tinyDB = new TinyDB(UserOrderActivity.this);
        userOrderAdapter = new UserOrderAdapter(UserOrderActivity.this);
        toolbar_textView.setText("User Orders");
        intentValue = getIntent().getStringExtra(INTENT_VALUE);
        if (Integer.parseInt(intentValue) == 0){
            toolbar_textView.setText("Pending Orders");
        }
        else if (intentValue.equals("1")){
            toolbar_textView.setText("Payment Complete Orders");
        }
        else if (intentValue.equals("2")){
            toolbar_textView.setText("Cancel Orders");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        recyclerView.setLayoutManager(linearLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.red, R.color.blue);
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
        linearLayoutManager=new LinearLayoutManager(UserOrderActivity.this);
        recyclerView.setAdapter(userOrderAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                userOrderAdapter.showLoading(true);
                userOrderAdapter.notifyDataSetChanged();
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
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);
        
        
    }
    private void loadData(final int page) {
        if(NetService.isInternetAvailable(UserOrderActivity.this)){
            syncPostService.getUserOrderList(
                    tinyDB.getString(Config.STORE_TOKEN),
                    intentValue,
                    page,
                    new Callback<DataRespone>() {
                @Override
                public void success(DataRespone respone, Response response) {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    if(respone!=null){
                        if(respone.getOrders().getData().size()>0){
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            if(page!=1){
                                userOrderAdapter.append(respone.getOrders().getData());
                                userOrderAdapter.notifyDataSetChanged();                                    }
                            else {
                                userOrderAdapter.replaceWith(respone.getOrders().getData());
                                userOrderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    else {
                        if(page==1){
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setText(getString(R.string.no_data));
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                    userOrderAdapter.showLoading(false);
                    userOrderAdapter.notifyDataSetChanged();
                }
                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(UserOrderActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    ZgToast zgToast = new ZgToast(UserOrderActivity.this);
                    zgToast.setError();
                    zgToast.setGravity(Gravity.CENTER, 0, 0);
                    zgToast.setZgText("Your account is expired , Please logout and then login again");
                    zgToast.show();
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    startActivity(new Intent(UserOrderActivity.this,LoginActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                }
            });
        }
        else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            zawgyiToast=new ZgToast(UserOrderActivity.this);
            zawgyiToast.setZgText(getString(R.string.no_internet));
            zawgyiToast.setError();
            zawgyiToast.show();
            if(!isRefresh || !isLoadMore){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
        finish();
    }
}

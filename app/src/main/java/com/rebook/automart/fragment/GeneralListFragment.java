package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.CustomAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.Product;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
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
 * Created by YDN on 7/3/2017.
 */
public class GeneralListFragment extends Fragment {
    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.category_progressBar)ProgressBar progressBar;
    @BindView(R.id.emptyView)TextView emptyView;
    SyncPostService syncPostService;
    CustomAdapter customAdapter;
    OkHttpClient okHttpClient;
    ArrayList<Product> list ;
    //LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zawgyiToast;
    boolean isRefresh,isLoadMore;
    String type ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient=new OkHttpClient();
        customAdapter=new CustomAdapter(getContext());
    }
    public static GeneralListFragment getInstance(String type){
        GeneralListFragment generalListFragment=new GeneralListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        generalListFragment.setArguments(bundle);
        return generalListFragment;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_list,null,false);
        ButterKnife.bind(this,view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);


        progressBar.setVisibility(View.VISIBLE);
        type = getArguments().getString("type");
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
        // linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(gridLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                customAdapter.showLoading(true);
                customAdapter.notifyDataSetChanged();
                loadData(page);
            }
        };
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.red, R.color.blue);
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
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);
        return view;
    }

    private void loadData(final int page) {
        if(NetService.isInternetAvailable(getActivity())){
            if(getArguments().getString("type").equals("profile")){
                Toast.makeText(getActivity(), "This is Working State", Toast.LENGTH_SHORT).show();


            }
            else if (getArguments().getString("type").equals("new_arrival")){
                customAdapter.setType("new_arrival");
                syncPostService.getProductLatest(page, new Callback<JsonArray>() {
                    @Override
                    public void success(JsonArray jsonElements, Response response) {

                        Log.e("success ","success"+String.valueOf(jsonElements.size()));
                        if(jsonElements!=null){
                            if(jsonElements.size()>0){
                                    emptyView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    list = new ArrayList<Product>();
                                    for(int i=0;i<jsonElements.size();i++){
                                        Gson gson=new Gson();
                                        list.add(gson.fromJson(jsonElements.get(i),Product.class));
                                    }
                                    if(page!=1){
                                        customAdapter.append(list);
                                        Log.e("××××+++ append: ", String.valueOf(list.size()));
                                    }
                                    else {
                                        customAdapter.replaceWith(list);
                                        Log.e("××××+++ replace: ", String.valueOf(list.size()));
                                    }
                                }
                            }
                            else {
                                if(page==1){
                                    recyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                            }
                            customAdapter.showLoading(false);
                            customAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }


        }
        else {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            zawgyiToast=new ZgToast(getActivity());
            zawgyiToast.setZgText("no_internet");
            zawgyiToast.setError();
            zawgyiToast.show();
            if(!isRefresh || !isLoadMore){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }
}

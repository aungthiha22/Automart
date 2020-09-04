package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rebook.automart.Config;
import com.rebook.automart.adapter.BrandAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.PostAutoMart;
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
import com.rebook.automart.R;

/**
 * Created by YDN on 7/3/2017.
 */
public class ItemListFragment extends Fragment {
    public static final String TYPE="type";
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.category_progressBar)ProgressBar progressBar;
    @BindView(R.id.emptyView)TextView emptyView;
    SyncPostService syncPostService;
   // CustomAdapter customAdapter;
    BrandAdapter brandAdapter;
    OkHttpClient okHttpClient;
    ArrayList<PostAutoMart> postList;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ZgToast zawgyiToast;
    boolean isRefresh,isLoadMore;
    String type ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient=new OkHttpClient();
  //      customAdapter=new CustomAdapter(getActivity());
        brandAdapter = new BrandAdapter(getActivity());
    }
    public static ItemListFragment getInstance(String type){
        ItemListFragment itemListFragment=new ItemListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TYPE,type);
        itemListFragment.setArguments(bundle);
        return itemListFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_list,null,false);
        ButterKnife.bind(this,view);


        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.blue,R.color.blue,R.color.red);

        type = getArguments().getString(TYPE);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);


        loadData(1);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(brandAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                brandAdapter.showLoading(true);
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
        return view;
    }

    private void loadData(final int page) {
        if(NetService.isInternetAvailable(getActivity())){
            if(getArguments().getString(TYPE).equals("category")){
                brandAdapter.setType("category");
                syncPostService.getBrandItem(new Callback<JsonArray>() {
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
                                if(page!=1){
                                    brandAdapter.append(postList);
                                }
                                else {
                                    brandAdapter.replaceWith(postList);
                                }
                            }
                        }
                        else {
                            if(page==1){
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        }
                        brandAdapter.showLoading(false);
                        brandAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressBar.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

        }
        else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            zawgyiToast=new ZgToast(getActivity());
            zawgyiToast.setZgText(getResources().getString(R.string.no_internet));
            zawgyiToast.setError();
            zawgyiToast.show();
            if(!isRefresh || !isLoadMore){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

}

package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.Utils;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


/**
 * Created by Dell on 4/10/2019.
 */

public class BlogDetailActivity extends AppCompatActivity {

    @BindView(R.id.blog_detail_image)ImageView imageView;
    @BindView(R.id.blog_detail_title)TextView txtBlogTitle;
    @BindView(R.id.blog_detail_description)TextView txtBlogDescription;
    @BindView(R.id.toolbar)Toolbar toolbar;
    TextView toolbar_textView;
    OkHttpClient okHttpClient = new OkHttpClient();
    SyncPostService syncPostService;
    String blogId ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_detail_activity);
        ButterKnife.bind(this);

        blogId = getIntent().getStringExtra("id");
        Log.e("blog Detail","blog id is "+blogId);
        setSupportActionBar(toolbar);
        toolbar_textView = toolbar.findViewById(R.id.toolbar_title);
        toolbar_textView.setText("Blog Detail");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

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
        syncPostService.getBlogDetail(blogId, 1, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
               // Toast.makeText(BlogDetailActivity.this, "success ooooook success", Toast.LENGTH_SHORT).show();
                txtBlogTitle.setText(jsonObject.get("title").getAsString());
                txtBlogDescription.setText(Html.fromHtml(jsonObject.get("description").getAsString()));
                if (jsonObject.get("image").getAsString().equals("")){
                    imageView.setImageResource(R.drawable.automart_logo);
                }
                else {
                    Glide.with(BlogDetailActivity.this).load(Config.MAIN_URL + "storage/" + jsonObject.get("image").getAsString())
                            .override(Utils.getPx(BlogDetailActivity.this, 200),
                                    Utils.getPx(BlogDetailActivity.this, 180))
                            .into(imageView);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

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
}

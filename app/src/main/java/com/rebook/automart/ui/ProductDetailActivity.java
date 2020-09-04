package com.rebook.automart.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.HomeAdapter;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.ProductImage;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Dell on 2/15/2019.
 */

public class ProductDetailActivity extends AppCompatActivity {

  //  @BindView(R.id.product_detail_title)TextView productDetailTitle;
    @BindView(R.id.detail_scrollView)ScrollView scrollView;
    @BindView(R.id.slider_image) SliderLayout sliderImage;
    @BindView(R.id.product_detail_image)ImageView productDetailimage;
    @BindView(R.id.detail_price)TextView price;
    @BindView(R.id.product_detail_brand)TextView brand;
    @BindView(R.id.product_detail_available)TextView available;
    @BindView(R.id.product_detail_description)TextView description;
    @BindView(R.id.product_detail_addToCard)TextView addToCart;
    @BindView(R.id.product_detail_addToFavouriate)TextView addToFavourite;
    @BindView(R.id.product_detail_buyNow)TextView buyNow;
    @BindView(R.id.product_detail_buyNow_cardView)CardView buyNowCardView;
    @BindView(R.id.cardViewAddToCard)CardView cardViewAddToCard;
    @BindView(R.id.product_detail)TextView detail;
    @BindView(R.id.review)TextView review;
    @BindView(R.id.additional_information)TextView information;
    @BindView(R.id.detail_recycler)RecyclerView detailRecycler;
    @BindView(R.id.main_layout)LinearLayout mainLayout;
    @BindView(R.id.decrease_image)ImageView decreaseImage;
    @BindView(R.id.increase_image)ImageView increaseImage;
    @BindView(R.id.product_detail_quantity)TextView txtQuantity;
  //@BindView(R.id.swipeRefresh)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)Toolbar mToolbar ;
    @BindView(R.id.download_layout)RelativeLayout downloadLayout;
    @BindView(R.id.specification_layout)RelativeLayout specificationLayout;

    ArrayList<Product> intentArray;
    ArrayList<Product> list = new ArrayList<>();
   // String subCategoryId;
    HomeAdapter homeAdapter;
    OkHttpClient okHttpClient;
    SyncPostService syncPostService;
    //LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    boolean isRefresh,isLoadMore;
   // String productId;
    String productDetailId, intentDescription,intentSpecification;
    int subId,orderQuantity ,productPrice,productQuantity ,productAddprice,productPromotion;
    int star;
    String rateReview;
    ZgToast zgToast ;
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;

    ProgressDialog progressDialog ;
    String tableName, tablePromotion,tablePrice,tableImageUrl,tableQuantity;
    ArrayList<ProductImage> imageList = new ArrayList<>();
    String downloadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_activity);
        ButterKnife.bind(this);
        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);

        //this product detail id is testing
        productDetailId = getIntent().getStringExtra(SubCategoryActivity.CATEGORY_ID);
        //productDetailId = "1005";
        subId = getIntent().getIntExtra(SubCategoryActivity.SUB_CATEGORY_ID,0);
        //subId = 27;
        Log.e("P Detail","_______________"+productDetailId);
        Log.e("sub id","_______________"+subId);

        scrollView.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(ProductDetailActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.waiting));
        progressDialog.show();
      //mainLayout.setVisibility(View.GONE);

        productDetailimage.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Product Detail");
        TextView titleTextView = mToolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText("Product Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        okHttpClient = new OkHttpClient();
        homeAdapter = new HomeAdapter(this);
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
        if (shouldAskPermissions()){
            askPermissions();
        }
        if (NetService.isInternetAvailable(ProductDetailActivity.this)) {
            syncPostService.getProductDetail(productDetailId, 1, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    if (jsonObject != null) {
                        JsonArray jsonArrayImage = jsonObject.get("product_images").getAsJsonArray();
                        final ArrayList<String> photos = new ArrayList<>();
                        if (jsonArrayImage.size()>0){
                            for (int i=0; i<jsonArrayImage.size(); i++){
                                Gson gson = new Gson();
                                imageList.add(gson.fromJson(jsonArrayImage.get(i),ProductImage.class));
                                photos.add(imageList.get(i).getImage());
                            }
                        }else {
                            photos.add(jsonObject.get("image1").getAsString());
                            photos.add(jsonObject.get("image2").getAsString());
                        }

                        if (photos.size() != 0 ) {
                            productDetailimage.setVisibility(View.GONE);

                            DisplayMetrics metrics = new DisplayMetrics();
                            ProductDetailActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                            float yInches= metrics.heightPixels/metrics.ydpi;
                            float xInches= metrics.widthPixels/metrics.xdpi;
                            double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                            if (diagonalInches>=6.5){
                                // 6.5inch device or bigger
                                 LinearLayout.LayoutParams layoutParams =
                                   new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                           Utils.getPx(ProductDetailActivity.this, 550));
                                sliderImage.setLayoutParams(layoutParams);
                                for (String url : photos) {
                                    Log.e("______url is ",url);
                                    DefaultSliderView defaultSliderView = new DefaultSliderView(ProductDetailActivity.this);
                                    defaultSliderView.image(Config.MAIN_URL+"storage/"+url);
                                    sliderImage.addSlider(defaultSliderView);
                                    //sliderImage.addSlider(defaultSliderView);

                                }
                                sliderImage.setDuration(4000);
                                sliderImage.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                sliderImage.startAutoCycle(4000, 4000, false);
                                sliderImage.setCustomAnimation(new DescriptionAnimation());
                            }else{
                                // smaller device
                                LinearLayout.LayoutParams layoutParams =
                                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                Utils.getPx(ProductDetailActivity.this, 240));
                                sliderImage.setLayoutParams(layoutParams);
                                for (String url : photos) {
                                    Log.e("______url is ",url);
                                    DefaultSliderView defaultSliderView = new DefaultSliderView(ProductDetailActivity.this);
                                    defaultSliderView.image(Config.MAIN_URL+"storage/"+url);
                                    sliderImage.addSlider(defaultSliderView);
                                    //sliderImage.addSlider(defaultSliderView);

                                }
                                sliderImage.setDuration(4000);
                                sliderImage.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                sliderImage.startAutoCycle(4000, 4000, false);
                                sliderImage.setCustomAnimation(new DescriptionAnimation());

                            }
                        }
                        else {
                           // Toast.makeText(ProductDetailActivity.this, "image is null", Toast.LENGTH_SHORT).show();
                            Glide.with(ProductDetailActivity.this)
                                    .load(Config.MAIN_URL+"storage/"+jsonObject.get("image1").getAsString())
                                    .override(Utils.getPx(ProductDetailActivity.this,400),
                                            Utils.getPx(ProductDetailActivity.this,180))
                                    .into(productDetailimage);
                        }
                        try {
                            intentDescription = jsonObject.get("description").getAsString();
                            intentSpecification = jsonObject.get("specification").getAsString();
                            downloadUrl = Config.MAIN_URL+"storage/"+jsonObject.get("information").getAsString();
                            if (!jsonObject.get("information").getAsString().equals("")){
                                downloadLayout.setVisibility(View.VISIBLE);
                            }
                            if (!intentSpecification.equals("")){
                                specificationLayout.setVisibility(View.VISIBLE);
                            }
                        }catch (UnsupportedOperationException e){
                            intentDescription = "no";
                            e.getMessage();
                            Log.e("message is ","__________-"+e.getMessage());
                        }

                        JsonObject rating = jsonObject.get("rating").getAsJsonObject();
                        star = rating.get("rating").getAsInt();
                        rateReview = rating.get("count").getAsString();
                        tableName = jsonObject.get("name").getAsString() ;
                        tableImageUrl = jsonObject.get("image1").getAsString();
                        productPrice = jsonObject.get("price").getAsInt();
                        productQuantity = jsonObject.get("quantity").getAsInt();
                        productPromotion = jsonObject.get("promotion").getAsInt();
                        price.setText(jsonObject.get("price").getAsString() +" MMK");
                        brand.setText(jsonObject.get("name").getAsString());
                        //productDetailTitle.setText(jsonObject.get("name").getAsString());
                        jsonObject.get("sold_quantity").getAsString();

                        if (productPromotion == 0){
                            productAddprice = productPrice ;
                        }else {
                            productAddprice = productPrice - ((productPrice/ 100) * productPromotion);
                            price.setText(productAddprice+"MMK");
                        }
                       // Toast.makeText(ProductDetailActivity.this, "promotion price is "+productAddprice, Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                scrollView.setVisibility(View.VISIBLE);
                                // mainLayout.setVisibility(View.VISIBLE);
                            }
                        },100);
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }else {
            ZgToast zgToast = new ZgToast(ProductDetailActivity.this);
            zgToast.setError();
            zgToast.setGravity(Gravity.CENTER, 0, 0);
            zgToast.setZgText(getResources().getString(R.string.no_internet));
            zgToast.show();

        }
        cardViewAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor cursor = db.rawQuery("select * from product where id = '"+productDetailId+"' and type = 0 ",null);
                if (cursor.getCount() == 1) {
                    Snackbar.make(v, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    zgToast = new ZgToast(ProductDetailActivity.this);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", productDetailId);
                    contentValues.put("imageUrl",tableImageUrl);
                    contentValues.put("type", 0);
                    contentValues.put("name", tableName);
                    contentValues.put("price", productPrice);
                    contentValues.put("addPrice", productAddprice);
                    contentValues.put("promotion", productPromotion);
                    contentValues.put("checkBox", "no");
                    contentValues.put("orderQuantity", 1);
                    contentValues.put("quantity", tableQuantity);
                    db.insert("product", null, contentValues);

                    zgToast.setZgText("you have been added shopping cart");
                    zgToast.show();
                    //startActivity(new Intent(ProductDetailActivity.this,MainActivity.class));
                    //finish();
                }




            }
        });
        /*addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor = db.rawQuery("select * from product where id = '"+productDetailId+"' and type = 0 ",null);
                if (cursor.getCount() == 1) {
                    Snackbar.make(v, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    zgToast = new ZgToast(ProductDetailActivity.this);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", productDetailId);
                    contentValues.put("imageUrl",tableImageUrl);
                    contentValues.put("type", 0);
                    contentValues.put("name", tableName);
                    contentValues.put("price", productPrice);
                    contentValues.put("addPrice", productAddprice);
                    contentValues.put("promotion", productPromotion);
                    contentValues.put("checkBox", "no");
                    contentValues.put("orderQuantity", 1);
                    contentValues.put("quantity", tableQuantity);
                    db.insert("product", null, contentValues);

                    zgToast.setZgText("you have been added shopping cart");
                    zgToast.show();
                    //startActivity(new Intent(ProductDetailActivity.this,MainActivity.class));
                    //finish();
                }
            }
        });*/
        addToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor = db.rawQuery("select * from product where id = '"+productDetailId+"' and type = 1 ",null);

                if (cursor.getCount() == 1) {
                    Snackbar.make(v, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    zgToast = new ZgToast(ProductDetailActivity.this);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", productDetailId);
                    contentValues.put("imageUrl",tableImageUrl);
                    contentValues.put("type", 1);
                    contentValues.put("name", tableName);
                    contentValues.put("price", productPrice);
                    contentValues.put("addPrice", productAddprice);
                    contentValues.put("promotion", productPromotion);
                    contentValues.put("rating", star);
                    contentValues.put("review", rateReview);
                    contentValues.put("checkBox", "no");
                    contentValues.put("orderQuantity", 1);
                    contentValues.put("quantity", tableQuantity);
                    db.insert("product", null, contentValues);
                    zgToast.setZgText("you have been added favourite");
                    zgToast.show();
                }



            }
        });
        decreaseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(txtQuantity.getText().toString())>1) {
                    txtQuantity.setText(Integer.parseInt(txtQuantity.getText().toString()) - 1 + "");
                }
            }
        });
        increaseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity > Integer.parseInt(txtQuantity.getText().toString())) {
                    txtQuantity.setText(Integer.parseInt(txtQuantity.getText().toString()) + 1 + "");
                }else {
                    ZgToast zgToast = new ZgToast(ProductDetailActivity.this);
                    zgToast.setZgText("quantity no enough");
                }
            }
        });

        buyNowCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderQuantity = Integer.parseInt(txtQuantity.getText().toString());
                Log.e("ProductDetail","order quantity\t"+String.valueOf(orderQuantity));

                Intent intent = new Intent(ProductDetailActivity.this,BuyNowActivity.class);

                intent.putExtra("id",productDetailId);
                intent.putExtra("imageUrl",tableImageUrl);
                intent.putExtra("name",tableName);
                intent.putExtra("original_price",productPrice);
                intent.putExtra("addPrice",productAddprice);
                intent.putExtra("promotion",tablePromotion);
                intent.putExtra("orderQuantity",orderQuantity);
                startActivity(intent);
                finish();

            }
        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this,ViewDetailActivity.class);
                intent.putExtra("id",productDetailId);
                intent.putExtra("description",intentDescription);
                startActivity(intent);
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this,ViewDetailActivity.class);
                intent.putExtra("id",productDetailId);
                intent.putExtra("description",intentSpecification);
                startActivity(intent);

            }
        });
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFile().execute(downloadUrl);
                Log.e("p Detail","_________________"+downloadUrl);
            }
        });

        loadData(1);

        GridLayoutManager gridLayoutManagerNew = new GridLayoutManager(ProductDetailActivity.this,2);
        gridLayoutManagerNew.setOrientation(LinearLayoutManager.VERTICAL);
      //  linearLayoutManager=new LinearLayoutManager(ProductDetailActivity.this);
        detailRecycler.setAdapter(homeAdapter);
        detailRecycler.setLayoutManager(gridLayoutManagerNew);

        detailRecycler.setAdapter(homeAdapter);
        //recyclerView.setLayoutManager(gridLayoutManager);
        detailRecycler.setItemAnimator(new DefaultItemAnimator());
        endlessRecyclerViewScrollListener=new EndlessRecyclerViewScrollListener(gridLayoutManagerNew) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                isLoadMore=true;
                homeAdapter.showLoading(true);
                //customAdapter.notifyDataSetChanged();
                //Log.i("onScroll",""+page+"\ttotalItemsCount = "+totalItemsCount+"\t type = "+getArguments().getString(TYPE));
                loadData(page);
            }
        };
        detailRecycler.setOnScrollListener(endlessRecyclerViewScrollListener);
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh=true;
                endlessRecyclerViewScrollListener.currentPage=0;
                endlessRecyclerViewScrollListener.previousTotalItemCount=0;
                endlessRecyclerViewScrollListener.loading=false;
                loadData(1);
            }
        });*/
    }
    public void loadData(final int page) {

        homeAdapter.setType("product_list");
        syncPostService.getProductList(String.valueOf(subId), page, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                //  swipeRefreshLayout.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
                if (jsonElements != null) {

                    if (jsonElements.size() > 0) {
                        //  emptyView.setVisibility(View.GONE);
                        // recyclerView.setVisibility(View.VISIBLE);
                        list = new ArrayList<Product>();
                        for (int i = 0; i < jsonElements.size(); i++) {
                            Gson gson = new Gson();
                            list.add(gson.fromJson(jsonElements.get(i), Product.class));

                        }
                        if (page != 1) {
                            homeAdapter.append(list);
                            Log.e("append size : ", String.valueOf(list.size()));
                        } else {
                            homeAdapter.replaceWith(list);
                            Log.e("replaceWith : ", String.valueOf(list.size()));
                        }
                    }
                } else {
                    if (page == 1) {
                        //recyclerView.setVisibility(View.GONE);
                        //emptyView.setText("no internet connection");
                        //emptyView.setVisibility(View.VISIBLE);
                    }
                }
                homeAdapter.showLoading(false);
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                //progressBar.setVisibility(View.GONE);
                //emptyView.setVisibility(View.VISIBLE);
                //recyclerView.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ProductDetailActivity.this,MainActivity.class));
                finish();
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
        startActivity(new Intent(ProductDetailActivity.this,MainActivity.class));
        finish();
    }
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ProductDetailActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                //fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "AutoMart/";
                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();
            // Display File path after downloading
            ZgToast zgToast = new ZgToast(getApplicationContext());
            zgToast.setZgText(message);
            zgToast.show();
        }
    }

}

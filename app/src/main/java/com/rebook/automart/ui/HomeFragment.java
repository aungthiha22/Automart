package com.rebook.automart.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.adapter.HomeCategoryAdapter;
import com.rebook.automart.adapter.HomeSlidePagerAdapter;
import com.rebook.automart.adapter.TestingSlidePagerAdapter;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.Rating;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by ATHK on 1/11/2019.
 */

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";
    private static ViewPager mPager;
    //int[] img;
    private static int currentPage = 0;
   // private static int NUM_PAGES = 5;
    public static final String TYPE = "type";
   // @BindView(R.id.sliderLayout) SliderLayout sliderLayout;

    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.recyclerView_category)RecyclerView categoryRecycler;
    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    @BindView(R.id.recyclerView2)RecyclerView recyclerView2;
    @BindView(R.id.select_vehicle)Button selectVehicle;
    @BindView(R.id.update_vehicle)Button updateVehicle;
    @BindView(R.id.emptyView)TextView emptyView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.select_your_vehicle)TextView selectYourVehicle;
    @BindView(R.id.img_1)ImageView adsImage1;
    @BindView(R.id.img_2)ImageView adsImage2;
    @BindView(R.id.img_3)ImageView adsImage3;
    @BindView(R.id.img_4)ImageView adsImage4;
    @BindView(R.id.ads_image_one)ImageView adsImageOne;

    SyncPostService syncPostService;
    //CustomAdapter customAdapter;
   // HomeAdapter homeAdapter;
    OkHttpClient okHttpClient;
    String type;
   // ArrayList<PostAutoMart> postList = new ArrayList<>();
    int star;
    String review;
    TestingSlidePagerAdapter testingSlidePagerAdapter;

    ArrayList<Product> bestSeller = new ArrayList<>();
    ArrayList<Product> newArrival = new ArrayList<>();
    ArrayList<Product> categoryList = new ArrayList<>();
    ArrayList<Product> sliderArray = new ArrayList<>();
    ArrayList<Product> adsArray = new ArrayList<>();
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;
    int discountPrice;
    int discount;
    int price;
    ZgToast zgToast;
   // ProgressDialog dialog ;
    int tableId;
    TinyDB tinyDB;
    String imageUrl1,imageUrl2,imageUrl3,imageUrl4;
    String goAdsUrl1,goAdsUrl2,goAdsUrl3,goAdsUrl4,goAdsOneUrl;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  homeAdapter=new HomeAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_home, container, false);
       // pager = (ViewPager) view.findViewById(R.id.viewPager);
        ButterKnife.bind(this, view);
        tinyDB = new TinyDB(getActivity());
        selectVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SaveVehicle.class);
                intent.putExtra("search","no");
                startActivity(intent);
                getActivity().finish();
            }
        });


        okHttpClient=new OkHttpClient();
        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
        type = "news";
        //pager = (ViewPager) view.findViewById(R.id.viewPager);
        //indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
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

        syncPostService.getAdvertisementOne(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches>=6.5) {
                    // 6.5inch device or bigger
                    Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+jsonObject.get("image").getAsString())
                            .override(Utils.getPx(getActivity(),880),
                                    Utils.getPx(getActivity(),280))
                            .into(adsImageOne);
                }else {
                    Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+jsonObject.get("image").getAsString())
                            .override(Utils.getPx(getActivity(),680),
                                    Utils.getPx(getActivity(),180))
                            .into(adsImageOne);
                }

                goAdsOneUrl = jsonObject.get("product_url").getAsString();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        syncPostService.getAdvertisement(new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonArray, Response response) {
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.size(); i++) {
                    adsArray.add(gson.fromJson(jsonArray.get(i), Product.class));
                    imageUrl1 = adsArray.get(0).getImage();
                    goAdsUrl1 = adsArray.get(0).getPruductUrl();
                    if (i==1) {
                        imageUrl2 = adsArray.get(1).getImage();
                        goAdsUrl2 = adsArray.get(1).getPruductUrl();
                    }
                    if (i==2) {
                        imageUrl3 = adsArray.get(2).getImage();
                        goAdsUrl3 = adsArray.get(2).getPruductUrl();
                    }
                    if (i==3) {
                        imageUrl4 = adsArray.get(3).getImage();
                        goAdsUrl4 = adsArray.get(3).getPruductUrl();
                    }
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    float yInches= metrics.heightPixels/metrics.ydpi;
                    float xInches= metrics.widthPixels/metrics.xdpi;
                    double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                    if (diagonalInches>=6.5){
                        // 6.5inch device or bigger
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl1)
                                .override(Utils.getPx(getActivity(),680),
                                        Utils.getPx(getActivity(),280))
                                .into(adsImage1);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl2)
                                .override(Utils.getPx(getActivity(),900),
                                        Utils.getPx(getActivity(),280))
                                .into(adsImage2);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl3)
                                .override(Utils.getPx(getActivity(),900),
                                        Utils.getPx(getActivity(),280))
                                .into(adsImage3);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl4)
                                .override(Utils.getPx(getActivity(),900),
                                        Utils.getPx(getActivity(),280))
                                .into(adsImage4);

                    }else {
                        // smaller device
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl1)
                                .override(Utils.getPx(getActivity(),310),
                                        Utils.getPx(getActivity(),180))
                                .into(adsImage1);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl2)
                                .override(Utils.getPx(getActivity(),500),
                                        Utils.getPx(getActivity(),180))
                                .into(adsImage2);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl3)
                                .override(Utils.getPx(getActivity(),500),
                                        Utils.getPx(getActivity(),180))
                                .into(adsImage3);
                        Glide.with(getActivity()).load(Config.MAIN_URL+"storage/"+imageUrl4)
                                .override(Utils.getPx(getActivity(),500),
                                        Utils.getPx(getActivity(),180))
                                .into(adsImage4);

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        adsImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SliderWebActivity.class);
                intent.putExtra("web_url",goAdsOneUrl);
                intent.putExtra("web_name",getString(R.string.app_name));
                startActivity(intent);
            }
        });

        adsImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SliderWebActivity.class);
                intent.putExtra("web_url",goAdsUrl1);
                intent.putExtra("web_name",getString(R.string.app_name));
                startActivity(intent);
            }
        });
        adsImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SliderWebActivity.class);
                intent.putExtra("web_url",goAdsUrl2);
                intent.putExtra("web_name",getString(R.string.app_name));
                startActivity(intent);
            }
        });
        adsImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SliderWebActivity.class);
                intent.putExtra("web_url",goAdsUrl3);
                intent.putExtra("web_name",getString(R.string.app_name));
                startActivity(intent);
            }
        });
        adsImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SliderWebActivity.class);
                intent.putExtra("web_url",goAdsUrl4);
                intent.putExtra("web_name",getString(R.string.app_name));
                startActivity(intent);
            }
        });


        mPager = view.findViewById(R.id.viewPager);
        final CircleIndicator indicator = view.findViewById(R.id.indicator);
        if (NetService.isInternetAvailable(getActivity())) {
            syncPostService.getSlider(1, new Callback<JsonArray>() {
                @Override
                public void success(JsonArray jsonElements, Response response) {/*

                    Gson gson = new Gson();
                    for (int i = 0; i < jsonElements.size(); i++) {
                        sliderArray.add(gson.fromJson(jsonElements.get(i), Product.class));
                    }

                    mPager.setAdapter(new HomeSlidePagerAdapter(getActivity(),sliderArray));
                    indicator.setViewPager(mPager);

                    // Auto start of viewpager
                    final Handler handler = new Handler();
                    final Runnable update = new Runnable() {
                        public void run() {
                            if (currentPage == sliderArray.size()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentPage = 0;
                                    }
                                },2000);

                            }
                            mPager.setCurrentItem(currentPage++, true);
                        }
                    };
                    Timer swipeTimer = new Timer();
                    swipeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(update);
                        }
                    }, 12500, 12500);
*/

                    if (jsonElements.size() != 0) {
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonElements.size(); i++) {
                            sliderArray.add(gson.fromJson(jsonElements.get(i), Product.class));
                        }
                        testingSlidePagerAdapter = new TestingSlidePagerAdapter(getActivity(), sliderArray);
                        //   Log.e("homeslider ", "______________ : " + sliderArray.size());
                        mPager.setAdapter(testingSlidePagerAdapter);
                        indicator.setViewPager(mPager);
                        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageSelected(int position) {
                                currentPage = position;
                            }
                            @Override
                            public void onPageScrolled(int arg0, float arg1, int arg2) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public void onPageScrollStateChanged(int state) {
                                // Toast.makeText(getApplicationContext(), "context changed", Toast.LENGTH_SHORT).show();

                                if (state == ViewPager.SCROLL_STATE_IDLE) {
                                    int pageCount = sliderArray.size();
                                    if (currentPage == 0) {
                                        mPager.setCurrentItem(pageCount - 1, false);
                                    } else if (currentPage == pageCount - 1) {
                                        mPager.setCurrentItem(0, false);
                                    }
                                }
                            }
                        });
                        final Handler handler = new Handler();
                        final Runnable update = new Runnable() {
                            @Override
                            public void run() {
                                if (currentPage == sliderArray.size()) {
                                    currentPage = 0;
                                }
                                mPager.setCurrentItem(currentPage++, true);
                            }
                        };
                        Timer swipeTimer = new Timer();
                        swipeTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(update);
                            }
                        }, 7000, 7000);
                        // Toast.makeText(getActivity(), "slider Array " + sliderArray.size(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();
                    }



                }
                @Override
                public void failure(RetrofitError error) {
//                    Toast.makeText(getActivity(), "Error is :\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    scrollView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
            final HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(getActivity());
            categoryRecycler.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();
            GridLayoutManager CategoryLayoutManager = new GridLayoutManager(getActivity(),1);
            CategoryLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categoryRecycler.setLayoutManager(CategoryLayoutManager);
            syncPostService.getBrandItem(new Callback<JsonArray>() {
                @Override
                public void success(JsonArray jsonElements, Response response) {
                    //postList.clear();
                    if(jsonElements!=null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        },1000);
                        categoryList.clear();
                        Gson gson=new Gson();
                        if(jsonElements.size()>0){
                            for(int i=0;i<jsonElements.size();i++){
                                categoryList.add(gson.fromJson(jsonElements.get(i),Product.class));

                            }
                            categoryAdapter.append(categoryList);
                            // Log.e("dolor_sing product list ","_______________"+ String.valueOf(newArrival.size()));
                            // Log.e("dolor_sing product list ","_______________"+ String.valueOf(jsonElements.size()));
                        }else {
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    // Toast.makeText(getActivity(), "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        final HomeAdapter homeAdapter = new HomeAdapter(getActivity());
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        syncPostService.getProductLatest(1, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                //postList.clear();
                if(jsonElements!=null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    },1000);
                    newArrival.clear();
                    Gson gson=new Gson();
                    if(jsonElements.size()>0){
                        for(int i=0;i<jsonElements.size();i++){
                            newArrival.add(gson.fromJson(jsonElements.get(i),Product.class));

                        }
                            homeAdapter.append(newArrival);
                      // Log.e("dolor_sing product list ","_______________"+ String.valueOf(newArrival.size()));
                      // Log.e("dolor_sing product list ","_______________"+ String.valueOf(jsonElements.size()));
                    }else {
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
               // Toast.makeText(getActivity(), "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        final HomeAdapterNew1 homeAdapterNew = new HomeAdapterNew1(getActivity());
        recyclerView2.setAdapter(homeAdapterNew);
        homeAdapterNew.notifyDataSetChanged();
        GridLayoutManager gridLayoutManagerNew = new GridLayoutManager(getActivity(),1);
        gridLayoutManagerNew.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(gridLayoutManagerNew);
        syncPostService.getProductBestSeller( 1, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                //postList.clear();
                if(jsonElements!=null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                       }
                    },1000);
                    if(jsonElements.size()>0){
                        for(int i=0;i<jsonElements.size();i++){
                            Gson gson=new Gson();
                            bestSeller.add(gson.fromJson(jsonElements.get(i),Product.class));

                        }
                        homeAdapterNew.append(bestSeller);
                      //  Log.e("product list ","_______________"+ String.valueOf(bestSeller.size()));
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                scrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
//                Toast.makeText(getActivity(), "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        }else {
            zgToast = new ZgToast(getActivity());
            scrollView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            zgToast.setError();
            zgToast.setZgText(getResources().getString(R.string.no_internet));
            zgToast.show();
        }

        db.execSQL("create table if not exists vehicle (id integer primary key autoincrement ," +
                "year text," +
                "brand text," +
                "engine text," +
                "model text," +
                "chassis_code text)");

        final Cursor cursor = db.rawQuery("select * from vehicle",null);
        while (cursor.moveToNext()){
            tableId = cursor.getInt(cursor.getColumnIndex("id"));
        }
       // Toast.makeText(getActivity(), "cursor.getCount is : "+cursor.getCount(), Toast.LENGTH_SHORT).show();

        if (tinyDB.getString("default_vehicle").equals("yes")){
            selectVehicle.setText("update vehicle");
        }else {

        }
        if (cursor.getCount() > 0){
            selectVehicle.setVisibility(View.VISIBLE);
            updateVehicle.setVisibility(View.VISIBLE);
            selectYourVehicle.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
        Context context;
        public HomeAdapter(Context context){
            this.context=context;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(context).inflate(R.layout.layout_item_copy,parent,false);
            ViewHolder loadingViewHolder=new ViewHolder(view);
            return loadingViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (newArrival == null){
            }else {
                holder.nameText.setText(newArrival.get(position).getName());
                Glide.with(context).load(Config.MAIN_URL+"storage/"+newArrival.get(position).getImage1())
                        .override(Utils.getPx(context,250),
                        Utils.getPx(context,180))
                        .into(holder.image);
                Log.e("Home frag","image url "+Config.MAIN_URL+"storage/"+newArrival.get(position).getImage1());
                Rating rating = new Rating();
                rating = newArrival.get(position).getRating();
                star = rating.getRating();
                review = rating.getCount();
                holder.ratingBar.setRating(star);
                holder.txtReview.setText("("+review+")");
                discount = newArrival.get(position).getPromotion();
                price = newArrival.get(position).getPrice();
                holder.promotion.setText("-"+discount+"%");
                holder.realPrice.setText(String.valueOf(price));
                holder.realPrice.setText(Html.fromHtml(price+""));
                if (discount== 0){
                    discountPrice = price;
                    holder.promotionPrice.setText(String.valueOf(price)+" MMK");
                    holder.realPrice.setVisibility(View.GONE);
                    holder.discountLayout.setVisibility(View.GONE);
                }
                else {
                    discountPrice = price - ((price / 100) * discount);
                    holder.promotionPrice.setText(String.valueOf(discountPrice));
                    Log.e("Home fragment","dis price \t"+discountPrice);
                }

                holder.rippleAddToCard.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        discount = newArrival.get(position).getPromotion();
                        price = newArrival.get(position).getPrice();
                        if (discount == 0){
                            discountPrice = price;
                        }else {
                            discountPrice = price - ((price / 100) * discount);
                        }

                        zgToast = new ZgToast(context);
                        Cursor cursor = db.rawQuery("select * from product where id = '"+newArrival.get(position).getId()+"' and type = 0 ",null);
                        if (cursor.getCount() == 1) {
                           /* zgToast.setError();
                            zgToast.setZgText("You already add to shopping cart");*/
                            Snackbar.make(rippleView, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("id", newArrival.get(position).getId());
                            contentValues.put("imageUrl",newArrival.get(position).getImage1());
                            contentValues.put("type", 0);
                            contentValues.put("name", newArrival.get(position).getName());
                            contentValues.put("price", newArrival.get(position).getPrice());
                            contentValues.put("addPrice", discountPrice);
                            contentValues.put("promotion", newArrival.get(position).getPromotion());
                            contentValues.put("checkBox", "no");
                            contentValues.put("orderQuantity", 1);
                            contentValues.put("quantity", newArrival.get(position).getQuantity());
                            contentValues.put("sku",newArrival.get(position).getSku());
                            db.insert("product", null, contentValues);

                            Snackbar.make(rippleView, "You have been added shopping cart", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                            getActivity().finish();
                        }
                        //zgToast.show();
                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("HomeFragment ","sub category id __________\t"+newArrival.get(position).getSubCategoryId());
                        Log.e("HomeFragment ","id __________\t"+newArrival.get(position).getId());

                        Intent intent = new Intent(context,ProductDetailActivity.class);
                        intent.putExtra(SubCategoryActivity.CATEGORY_ID,newArrival.get(position).getId());
                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,newArrival.get(position).getSubCategoryId());
                        startActivity(intent);
                        ((Activity)context).finish();
                    }
                });


            }
        }

        @Override
        public int getItemCount() {
            return newArrival.size();

        }
        public void append(ArrayList<Product> list){
            // products1.clear();
            newArrival = list;
            //Log.e("list size ","________"+products1.size());
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView txtReview;
            ImageView image;
            RatingBar ratingBar ;
            RippleView rippleAddToCard;
            TextView nameText;
            TextView promotion;
            TextView promotionPrice;
            TextView realPrice;
            CardView cardView ;
            RelativeLayout discountLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                txtReview = itemView.findViewById(R.id.review);
                nameText = itemView.findViewById(R.id.title);
                image = itemView.findViewById(R.id.image);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                promotion = itemView.findViewById(R.id.promotion);
                promotionPrice= itemView.findViewById(R.id.promotionPrice);
                realPrice = itemView.findViewById(R.id.realPrice);
                rippleAddToCard = itemView.findViewById(R.id.ripple_add_card);
                cardView = itemView.findViewById(R.id.itemCardView);
                discountLayout = itemView.findViewById(R.id.discount_layout_copy);

            }
        }

    }

    public class HomeAdapterNew1 extends RecyclerView.Adapter<HomeAdapterNew1.ViewHolder> {

        Context context;
        public HomeAdapterNew1(Context context){
            this.context=context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(context).inflate(R.layout.layout_item_copy,parent,false);
            ViewHolder loadingViewHolder=new ViewHolder(view);
            return loadingViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (bestSeller == null){
            }else {
                holder.textView.setText(bestSeller.get(position).getName());
                holder.image.setImageResource(R.mipmap.ic_launcher);
                //  holder.title.setText(Html.fromHtml(products1.get(position).getName()));
                /*Picasso.with(context).load("http://68.183.189.90/storage/"+bestSeller.get(position).getImage1())
                        .resize(Utils.getPx(context,200),                                Utils.getPx(context,180)).into((holder).image);
                (holder).ratingBar.setRating(bestSeller.get(position).getQuantity());*/

                Glide.with(context).load(Config.MAIN_URL+"storage/"+bestSeller.get(position).getImage1())
                        .override(Utils.getPx(context,200),
                                Utils.getPx(context,180))
                        .into(holder.image);
                Rating rating = new Rating();
                rating = bestSeller.get(position).getRating();
                star = rating.getRating();
                review = rating.getCount();
                holder.ratingBar.setRating(star);
                holder.txtReview.setText("( "+review+" )");


                discount = bestSeller.get(position).getPromotion();
                price = bestSeller.get(position).getPrice();
                holder.promotion.setText("-"+discount+"%");
               // holder.realPrice.setText(String.valueOf(price));
                holder.realPrice.setText(Html.fromHtml(price+""));
                if (discount== 0){
                    holder.promotionPrice.setText(String.valueOf(price)+" MMK");
                    holder.realPrice.setVisibility(View.GONE);
                    holder.discount_layout.setVisibility(View.GONE);
                }
                else {
                    discountPrice = price - ((price / 100) * discount);
                    holder.promotionPrice.setText(String.valueOf(discountPrice));

                }

                holder.rippleAddToCard.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        discount = bestSeller.get(position).getPromotion();
                        price = bestSeller.get(position).getPrice();
                        Log.e(TAG,"original price : "+price);
                        Log.e("discount ","_____________"+discount);

                        if (discount == 0){
                            discountPrice = price;
                        }else {
                            discountPrice = price - ((price / 100) * discount);
                        }

                        zgToast = new ZgToast(context);
                        Cursor cursor = db.rawQuery("select * from product where id = '"+bestSeller.get(position).getId()+"' and type = 0 ",null);
                        if (cursor.getCount() == 1) {
                            /*zgToast.setError();
                            zgToast.setZgText("you already add to shopping cart");*/
                            Snackbar.make(rippleView, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }else {

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("id", bestSeller.get(position).getId());
                            contentValues.put("imageUrl",bestSeller.get(position).getImage1());
                            contentValues.put("type", 0);
                            contentValues.put("name", bestSeller.get(position).getName());
                            contentValues.put("price", bestSeller.get(position).getPrice());
                            contentValues.put("addPrice", discountPrice);
                            Log.e("on click ","________"+String.valueOf(discountPrice));
                            contentValues.put("promotion", bestSeller.get(position).getPromotion());
                            contentValues.put("checkBox", "no");
                            contentValues.put("orderQuantity", 1);
                            contentValues.put("quantity", bestSeller.get(position).getQuantity());
                            contentValues.put("sku", bestSeller.get(position).getSku());
                            db.insert("product", null, contentValues);

                            //zgToast.setZgText("you have been added shopping cart");
                            Snackbar.make(rippleView, "You have been added shopping cart", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            startActivity(new Intent(getActivity(),MainActivity.class));
                            getActivity().finish();
                            //zgToast.show();

                        }


                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
                        intent.putExtra(SubCategoryActivity.CATEGORY_ID,bestSeller.get(position).getId());
                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,bestSeller.get(position).getSubCategoryId());
                        startActivity(intent);
                        ((Activity)context).finish();
                    }
                });
            }
        }

        public void append(ArrayList<Product> list){
            // products1.clear();
           // bestSeller.addAll(list);
            bestSeller = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return bestSeller.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView txtReview;
            ImageView image;
            RatingBar ratingBar ;
            RippleView rippleAddToCard;
            public TextView textView;
            TextView promotion;
            TextView promotionPrice;
            TextView realPrice;
            CardView cardView;
            RelativeLayout discount_layout;

            public ViewHolder(View itemView) {
                super(itemView);
                txtReview = itemView.findViewById(R.id.review);
                textView = itemView.findViewById(R.id.title);
                image = itemView.findViewById(R.id.image);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                promotion = itemView.findViewById(R.id.promotion);
                promotionPrice= itemView.findViewById(R.id.promotionPrice);
                realPrice = itemView.findViewById(R.id.realPrice);
                rippleAddToCard = itemView.findViewById(R.id.ripple_add_card);
                cardView = itemView.findViewById(R.id.itemCardView);
                discount_layout = itemView.findViewById(R.id.discount_layout_copy);
            }
        }
    }
}

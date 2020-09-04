package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.Rating;
import com.rebook.automart.ui.MainActivity;
import com.rebook.automart.ui.ProductDetailActivity;
import com.rebook.automart.ui.SubCategoryActivity;
import com.rebook.automart.util.Utils;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.rebook.automart.widget.ZgToast;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_LOADING=2;
    public static final int TYPE_ITEM=1;
    List<Product> posts=new ArrayList<>();
    Context context;
    String type;
    int width,height;
    boolean isShow;
    int discountPrice;
    int discount;
    int price;
    int star ;
    String review;
    SQLiteDatabase db;
    String path = "data/data/com.rebook.automart/database";

    public HomeAdapter(Context context){
        this.context=context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }
    public void setType(String type) {
        this.type=type;
    }
    public void append(ArrayList<Product> list){
        //  posts.addAll(list);
        posts = list;
        notifyDataSetChanged();
    }
    public void replaceWith(ArrayList<Product> list){
        posts=list;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
        if(viewType==TYPE_ITEM){
            View view= LayoutInflater.from(context).inflate(R.layout.layout_item,parent,false);
            ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }
        else if(viewType==TYPE_LOADING){
            View view=LayoutInflater.from(context).inflate(R.layout.layout_loading,parent,false);
            LoadingViewHolder loadingViewHolder=new LoadingViewHolder(view);
            return loadingViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //  Post post = new Post();
        if(holder instanceof LoadingViewHolder){
            if(isShow){
                ((LoadingViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
            }
            else {
                ((LoadingViewHolder)holder).progressBar.setVisibility(View.GONE);
                //notifyDataSetChanged();
            }
        }
        if(holder instanceof ViewHolder){
            if (type.equals("product_list")){
                Rating rating = new Rating();
                rating = posts.get(position).getRating();
                star = rating.getRating();
                review = rating.getCount();
                ((ViewHolder) holder).ratingBar.setRating(star);
                ((ViewHolder) holder).txtReview.setText(review);

                ((ViewHolder) holder).title.setText(Html.fromHtml(posts.get(position).getName()));
                Glide.with(context).load(Config.MAIN_URL+"storage/"+posts.get(position).getImage1())
                        .override(Utils.getPx(context,200),
                                Utils.getPx(context,180))
                        .into(((ViewHolder) holder).image);
                discount = posts.get(position).getPromotion();
                price = posts.get(position).getPrice();
                ((ViewHolder) holder).promotion.setText("-"+discount+"%");
                ((ViewHolder) holder).realPrice.setText(Html.fromHtml(price+""));
                Log.e("Discount price","___________\t"+discount+"");
                Log.e("Position is ","_____________\t"+position+"");

                if (discount== 0){
                    discountPrice = price;
                    ((ViewHolder) holder).promotionPrice.setText(price+" MMK");
                    ((ViewHolder) holder).discountLayout.setVisibility(View.GONE);
                    ((ViewHolder) holder).realPrice.setVisibility(View.GONE);

                }
                else {
                    ((ViewHolder) holder).discountLayout.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).realPrice.setVisibility(View.VISIBLE);
                    discountPrice = price - ((price / 100) * discount);
                    ((ViewHolder) holder).promotionPrice.setText(discountPrice+" MMK");
                }
                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("HomeAdapter","_________________ "+posts.get(position).getSubCategoryId());
                        Intent intent = new Intent(context,ProductDetailActivity.class);
                        intent.putExtra(SubCategoryActivity.CATEGORY_ID,posts.get(position).getId());
                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,posts.get(position).getSubCategoryId());
                        context.startActivity(intent);
                        ((Activity)context).finish();

                    }
                });

            }else if (type.equals("promotion_list")){
                ((ViewHolder) holder).txtReview.setText(review);

                ((ViewHolder) holder).title.setText(Html.fromHtml(posts.get(position).getName()));
                Glide.with(context).load(Config.MAIN_URL+"storage/"+posts.get(position).getImage1())
                        .override(Utils.getPx(context,200),
                                Utils.getPx(context,180))
                        .into(((ViewHolder) holder).image);
                discount = posts.get(position).getPromotion();
                price = posts.get(position).getPrice();
                ((ViewHolder) holder).promotion.setText("-"+discount+"%");
                ((ViewHolder) holder).realPrice.setText(Html.fromHtml(price+""));
                Log.e("Discount price","___________\t"+discount+"");
                Log.e("Position is ","_____________\t"+position+"");

                if (discount== 0){
                    discountPrice = price;
                    ((ViewHolder) holder).promotionPrice.setText(price+" MMK");
                    ((ViewHolder) holder).discountLayout.setVisibility(View.GONE);
                    ((ViewHolder) holder).realPrice.setVisibility(View.GONE);

                }
                else {
                    ((ViewHolder) holder).discountLayout.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).realPrice.setVisibility(View.VISIBLE);
                    discountPrice = price - ((price / 100) * discount);
                    ((ViewHolder) holder).promotionPrice.setText(discountPrice+" MMK");
                }
                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("HomeAdapter","_________________ "+posts.get(position).getSubCategoryId());
                        Intent intent = new Intent(context,ProductDetailActivity.class);
                        intent.putExtra(SubCategoryActivity.CATEGORY_ID,posts.get(position).getId());
                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,posts.get(position).getSubCategoryId());
                        context.startActivity(intent);
                        ((Activity)context).finish();

                    }
                });


            }
            ((ViewHolder) holder).rippleAddtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discount = posts.get(position).getPromotion();
                    price = posts.get(position).getPrice();
                    if (discount == 0){
                        discountPrice = price;
                    }else {
                        discountPrice = price - ((price / 100) * discount);
                    }
                    Cursor cursor = db.rawQuery("select * from product where id = '"+posts.get(position).getId()+"' and type = 0 ",null);
                    ZgToast zgToast = new ZgToast(context);
                    if (cursor.getCount() == 1){
                       // zgToast.setError();
                       // zgToast.setZgText("you already add to shopping cart");
                        Snackbar.make(v, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", posts.get(position).getId());
                        contentValues.put("imageUrl",posts.get(position).getImage1());
                        contentValues.put("type", 0);
                        contentValues.put("name", posts.get(position).getName());
                        contentValues.put("price", posts.get(position).getPrice());
                        contentValues.put("addPrice", discountPrice );
                        contentValues.put("promotion", posts.get(position).getPromotion());
                        contentValues.put("checkBox", "no");
                        contentValues.put("orderQuantity", 1);
                        contentValues.put("quantity", posts.get(position).getQuantity());
                        db.insert("product", null, contentValues);

                        Snackbar.make(v, "You have been added shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                       // zgToast.setZgText("you have been added shopping cart");
                       // context.startActivity(new Intent(context, MainActivity.class));
                       // ((Activity)context).finish();
                    }
                    //zgToast.show();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(posts==null || posts.size()==0){
            return 0;
        }
        return posts.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position!=0 && position==posts.size())? TYPE_LOADING : TYPE_ITEM;
    }
    public void showLoading(boolean status){
        isShow=status;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.title)TextView title;
        @BindView(R.id.image)ImageView image;
        @BindView(R.id.promotion) TextView promotion;
        @BindView(R.id.realPrice) TextView realPrice;
        @BindView(R.id.promotionPrice) TextView promotionPrice;
        @BindView(R.id.ripple_add_card)RippleView rippleAddtoCart;
        @BindView(R.id.review)TextView txtReview;
        @BindView(R.id.itemCardView)CardView cardView;
        @BindView(R.id.ratingBar) RatingBar ratingBar ;
        @BindView(R.id.discount_layout)RelativeLayout discountLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.loading)ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

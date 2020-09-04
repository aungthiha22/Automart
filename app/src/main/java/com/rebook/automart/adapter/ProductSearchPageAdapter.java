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
import com.rebook.automart.widget.ZgToast;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class ProductSearchPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_LOADING=2;
        public static final int TYPE_ITEM=1;
        ArrayList<Product> productsList=new ArrayList<>();
        Context context;
        String type;
        int fitValue;
        int width,height;
        ZgToast zawgyiToast;
        boolean isShow;
        int discountPrice;
        int discount;
        int price;
        SQLiteDatabase db;
        String path = "data/data/com.rebook.automart/database";

        public ProductSearchPageAdapter(Context context){
                this.context=context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;
        }
        public void setType(String type) {
                this.type=type;
        }
        public void setFit(int fitValue){
                this.fitValue=fitValue;
        }
        public void append(ArrayList<Product> list){
               // postsAuto.addAll(list);
                this.productsList = list;
                notifyDataSetChanged();
        }
        public void replaceWith(ArrayList<Product> list){
                //postsAuto=list;
                productsList = list;
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
//                               notifyDataSetChanged();
                        }
                        else {
                                ((LoadingViewHolder)holder).progressBar.setVisibility(View.GONE);

                        }
                }
                if(holder instanceof ViewHolder){

                        Rating rating = new Rating();
                        rating = productsList.get(position).getRating();
                        ((ViewHolder) holder).ratingBar.setRating(rating.getRating());
                        ((ViewHolder) holder).txtReview.setText(rating.getCount());
                        Log.e("fitterAdapter","______________"+fitValue);
                        Log.e("fitterAdapter","position ______________"+position);
                        if (position<fitValue){
                                ((ViewHolder) holder).fitImage.setVisibility(View.VISIBLE);
                        }

                        ((ViewHolder) holder).title.setText(productsList.get(position).getName());
                       /* Picasso.with(context).load(Config.MAIN_URL+"storage/" + fitlist.get(position).getImage1())
                                .resize(Utils.getPx(context, 200),
                                        Utils.getPx(context, 180)).into(((ViewHolder) holder).image);*/
                        Glide.with(context).load(Config.MAIN_URL+"storage/"+productsList.get(position).getImage1())
                                .override(Utils.getPx(context,200),
                                        Utils.getPx(context,180))
                                .into(((ViewHolder) holder).image);

                        discount = productsList.get(position).getPromotion();
                        price = productsList.get(position).getPrice();
                        ((ViewHolder) holder).promotion.setText("-"+discount+"%");

                        if (discount == 0){
                                ((ViewHolder) holder).promotionPrice.setText(String.valueOf(price)+"MMK");
                                ((ViewHolder) holder).discountLayout.setVisibility(View.GONE);
                                ((ViewHolder) holder).promotionPrice.setText(String.valueOf(price)+"MMK");
                                ((ViewHolder) holder).realPrice.setVisibility(View.GONE);

                        }else {
                                discountPrice = price - ((price / 100) * discount);
                                ((ViewHolder) holder).promotionPrice.setText(String.valueOf(String.valueOf(discountPrice)));
                        }
                        ((ViewHolder) holder).realPrice.setText(Html.fromHtml("<u>"+String.valueOf(price)+"</u>MMK"));
                        ((ViewHolder) holder).itemCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        Intent intent = new Intent(context, ProductDetailActivity.class);
                                        /*intent.putExtra(SubCategoryActivity.CATEGORY_ID,newArrival.get(position).getId());
                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,newArrival.get(position).getSubCategoryId());*/
                                        intent.putExtra(SubCategoryActivity.CATEGORY_ID,String.valueOf(productsList.get(position).getId()));
                                        intent.putExtra(SubCategoryActivity.SUB_CATEGORY_ID,productsList.get(position).getSubCategoryId());
                                        context.startActivity(intent);
                                        ((Activity)context).finish();
                                }
                        });
                        ((ViewHolder) holder).rippleAddtoCart.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                                @Override
                                public void onComplete(RippleView rippleView) {
                                        discount = productsList.get(position).getPromotion();
                                        price = productsList.get(position).getPrice();
                                        if (discount == 0){
                                                discountPrice = price;
                                        }else {
                                                discountPrice = price - ((price / 100) * discount);
                                        }
                                        Cursor cursor = db.rawQuery("select * from product where id = '"+productsList.get(position).getId()+"' and type = 0 ",null);
                                        ZgToast zgToast = new ZgToast(context);
                                        if (cursor.getCount() == 1){
                                                Snackbar.make(rippleView, "You already add to shopping cart", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                        }else {
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("id", productsList.get(position).getId());
                                                contentValues.put("imageUrl",productsList.get(position).getImage1());
                                                contentValues.put("type", 0);
                                                contentValues.put("name", productsList.get(position).getName());
                                                contentValues.put("price", productsList.get(position).getPrice());
                                                contentValues.put("addPrice", discountPrice);
                                                contentValues.put("promotion", discount);
                                                contentValues.put("checkBox", "no");
                                                contentValues.put("orderQuantity", 1);
                                                contentValues.put("quantity", productsList.get(position).getQuantity());
                                                db.insert("product", null, contentValues);
                                                Snackbar.make(rippleView, "You have been added shopping cart", Snackbar.LENGTH_LONG)
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
                if(productsList==null || productsList.size()==0){
                        return 0;
                }
                return productsList.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
                return (position!=0 && position==productsList.size())? TYPE_LOADING : TYPE_ITEM;
        }
        public void showLoading(boolean status){
                isShow=status;
        }
        public class ViewHolder extends RecyclerView.ViewHolder{

                @BindView(R.id.fit_image)ImageView fitImage;
                @BindView(R.id.image)ImageView image;
                @BindView(R.id.promotion) TextView promotion;
                @BindView(R.id.realPrice) TextView realPrice;
                @BindView(R.id.promotionPrice) TextView promotionPrice;
                @BindView(R.id.ripple_add_card)RippleView rippleAddtoCart;
                @BindView(R.id.ratingBar)RatingBar ratingBar ;
                @BindView(R.id.itemCardView)CardView itemCardView;
                @BindView(R.id.title)TextView title;
                @BindView(R.id.discount_layout)RelativeLayout discountLayout;
                @BindView(R.id.review)TextView txtReview;

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

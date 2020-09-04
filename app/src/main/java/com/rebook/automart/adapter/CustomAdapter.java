package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.R;
import com.rebook.automart.model.Product;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_LOADING=2;
        public static final int TYPE_ITEM=1;
        List<Product> posts=new ArrayList<>();
        Context context;
        String type;
        int width,height;
        boolean isShow;
        public CustomAdapter(Context context){
                this.context=context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;
        }
        public void setType(String type) {
                this.type=type;
        }
        public void append(List<Product> list){
                posts.addAll(list);
                notifyDataSetChanged();
        }
        public void replaceWith(List<Product> list){
                posts=list;
                notifyDataSetChanged();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

                       if (type.equals("new_arrival")){
                               ((ViewHolder) holder).title.setText(posts.get(position).getName());
                              /* Glide.with(context).load("http://68.183.189.90/storage/"+newArrival.get(position).getImage1())
                                       .override(Utils.getPx(context,200),
                                               Utils.getPx(context,180))
                                       .into(holder.image);
                               (holder).ratingBar.setRating(4);

                               discount = newArrival.get(position).getPromotion();
                               price = newArrival.get(position).getPrice();
                               holder.promotion.setText("-"+discount+"%");

                               holder.realPrice.setText(String.valueOf(price));
                               holder.realPrice.setText(Html.fromHtml("<u><i>"+price+"</i></u>"));
                               if (discount== 0){
                                       holder.promotionPrice.setText(String.valueOf(price));
                                       holder.realPrice.setVisibility(View.GONE);
                                       holder.discountLayout.setVisibility(View.GONE);
                               }
                               else {
                                       discountPrice = price - ((price / 100) * discount);
                                       holder.promotionPrice.setText(String.valueOf(discountPrice));
                               }*/


                       }
                       else {
                               ((ViewHolder) holder).image.setImageResource(R.mipmap.ic_launcher);
                               ((ViewHolder)holder).title.setText(Html.fromHtml(posts.get(position).getName()));
                               ((ViewHolder) holder).ratingBar.setRating(posts.get(position).getQuantity());
                              // Toast.makeText(context, ""+posts.get(position).getImage1(), Toast.LENGTH_SHORT).show();
                       }
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
               // @BindView(R.id.text_layout)LinearLayout textLayout;
               // @BindView(R.id.title_new)TextView titleNew;
                @BindView(R.id.ratingBar) RatingBar ratingBar ;

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

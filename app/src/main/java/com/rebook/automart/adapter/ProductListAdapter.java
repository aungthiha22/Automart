package com.rebook.automart.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.Rating;
import com.rebook.automart.util.Utils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_LOADING=2;
        public static final int TYPE_ITEM=1;
        List<Product> posts=new ArrayList<>();
        Context context;
        String type;
        String review;
        int star;
        boolean isShow;
       /* public ProductListAdapter(Context context){
                this.context=context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;
        }*/
       /* public void setType(String type) {
                this.type=type;
        }
        public void append(List<Product> list){
                posts.addAll(list);
                Toast.makeText(context, "array size is : "+ list.size(), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
        }
        public void replaceWith(List<Product> list){
                posts=list;
                Toast.makeText(context, "array size is : "+list.size(), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
        }*/
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
                       /* if (type.equals("news")){
                               // ((ViewHolder) holder).textLayout.setVisibility(View.VISIBLE);
                                ((ViewHolder) holder).ratingBar.setRating(2);
                        }*/
                        /*if (type.equals("rent")){
                              //  ((ViewHolder) holder).titleNew.setVisibility(View.VISIBLE);
                               // ((ViewHolder) holder).titleNew.setText(Html.fromHtml(posts.get(position).getPostTitle()));
                        }*/
                    Rating rating = new Rating();
                    rating = posts.get(position).getRating();
                    star = rating.getRating();
                    review = rating.getCount();
                    ((ViewHolder) holder).ratingBar.setRating(star);
                    ((ViewHolder) holder).txtReview.setText(review);


                    ((ViewHolder) holder).image.setImageResource(R.mipmap.ic_launcher);
                    ((ViewHolder)holder).title.setText(Html.fromHtml(posts.get(position).getName()));
                   /* Picasso.with(context).load("http://192.168.8.100/storage/"+posts.get(position).getImage1())
                                .resize(Utils.getPx(context,200),
                                        Utils.getPx(context,180)).into(((ViewHolder) holder).image);*/
                    Glide.with(context).load(Config.MAIN_URL + "storage/" + posts.get(position).getImage())
                            .override(Utils.getPx(context, 200),
                                    Utils.getPx(context, 180))
                            .into(((ViewHolder) holder).image);

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
            @BindView(R.id.itemCardView)CardView cardView;
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

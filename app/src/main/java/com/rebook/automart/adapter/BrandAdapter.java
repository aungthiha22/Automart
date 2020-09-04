package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rebook.automart.R;
import com.rebook.automart.model.PostAutoMart;
import com.rebook.automart.ui.ProductList;
import com.rebook.automart.ui.SubCategoryActivity;
import com.rebook.automart.widget.ZgToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_LOADING=2;
        public static final int TYPE_ITEM=1;
        List<PostAutoMart> postsAuto=new ArrayList<>();
        Context context;
        ZgToast zawgyiToast;
        String type;
        int width,height;
        boolean isShow;

        public BrandAdapter(Context context){
                this.context=context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;
        }
        public void setType(String type) {
                this.type=type;
        }
        public void append(ArrayList<PostAutoMart> list){
               // postsAuto.addAll(list);
                this.postsAuto = list;
                notifyDataSetChanged();
        }
        public void replaceWith(ArrayList<PostAutoMart> list){
                //postsAuto=list;
                postsAuto = list;
                notifyDataSetChanged();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType==TYPE_ITEM){
                        View view= LayoutInflater.from(context).inflate(R.layout.category_item,parent,false);
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
                        ((ViewHolder) holder).categoryName.setText(postsAuto.get(position).getName());
                        ((ViewHolder) holder).itemCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        //Toast.makeText(context, "this is card id : " + postsAuto.get(position).getName(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, ProductList.class);

                                        intent.putExtra(ProductList.INTENT_NAME,postsAuto.get(position).getName());
                                        context.startActivity(intent);
                                        ((Activity) context).finish();

                                }

                        });


                }
        }

        @Override
        public int getItemCount() {
                if(postsAuto==null || postsAuto.size()==0){
                        return 0;
                }
                return postsAuto.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
                return (position!=0 && position==postsAuto.size())? TYPE_LOADING : TYPE_ITEM;
        }
        public void showLoading(boolean status){
                isShow=status;
        }
        public class ViewHolder extends RecyclerView.ViewHolder{

                @BindView(R.id.itemCardView)CardView itemCardView;
                @BindView(R.id.category_name)TextView categoryName;

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

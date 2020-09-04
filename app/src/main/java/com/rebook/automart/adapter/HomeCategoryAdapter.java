package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.ui.ProductList;
import com.rebook.automart.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 3/21/2019.
 */

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<Product> arrayList = new ArrayList();
    public HomeCategoryAdapter(Context context){
        this.context=context;
    }

    @Override
    public HomeCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.home_category_item,parent,false);
        ViewHolder loadingViewHolder=new ViewHolder(view);
        return loadingViewHolder;
    }

    @Override
    public void onBindViewHolder(final HomeCategoryAdapter.ViewHolder holder, final int position) {

        if (arrayList == null){
        }else {
          holder.txtName.setText(arrayList.get(position).getName());
            Glide.with(context).load(Config.MAIN_URL+"storage/"+arrayList.get(position).getImage())
                    .override(Utils.getPx(context,200),
                            Utils.getPx(context,120))
                    .into(holder.imageView);
          holder.cardView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //Toast.makeText(context, "name is \n"+arrayList.get(position).getName(), Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(context, ProductList.class);
                  intent.putExtra(ProductList.INTENT_NAME,arrayList.get(position).getName());
                  context.startActivity(intent);
                  //((Activity)context).finish();
                  Log.e("H Category ","adapter ____________"+arrayList.get(position).getName());
                  ((Activity)context).finish();
              }
          });
        }
    }

    public void append(ArrayList<Product> list){
        // products1.clear();
        // bestSeller.addAll(list);
        arrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       @BindView(R.id.home_category_item_image)ImageView imageView;
       @BindView(R.id.home_category_item_name)TextView txtName ;
       @BindView(R.id.home_category_cardView)CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


           // image = itemView.findViewById(R.id.image);

        }
    }

}
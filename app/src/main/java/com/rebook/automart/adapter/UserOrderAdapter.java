package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.R;
import com.rebook.automart.model.Data;
import com.rebook.automart.model.Product;
import com.rebook.automart.ui.BlogDetailActivity;
import com.rebook.automart.ui.OrderCancelActivity;
import com.rebook.automart.ui.UserOrderActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YDN on 7/3/2017.
 */
public class UserOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_LOADING=2;
        public static final int TYPE_ITEM=1;
        List<Data> posts=new ArrayList<>();
        Context context;
        String type;
        int width,height;
        boolean isShow;
        public UserOrderAdapter(Context context){
                this.context=context;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                height = displayMetrics.heightPixels;
                width = displayMetrics.widthPixels;
        }
        public void setType(String type) {
                this.type=type;
        }
        public void append(List<Data> list){
                posts.addAll(list);
                notifyDataSetChanged();
        }
        public void replaceWith(List<Data> list){
                posts=list;
                notifyDataSetChanged();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType==TYPE_ITEM){
                        View view= LayoutInflater.from(context).inflate(R.layout.user_order_item,parent,false);
                        ViewHolder viewHolder=new ViewHolder(view);
                        return viewHolder;
                }
                else if(viewType==TYPE_LOADING){
                        View view= LayoutInflater.from(context).inflate(R.layout.layout_loading,parent,false);
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

                        ((ViewHolder) holder).txtBookingRef.setText(posts.get(position).getBookingRef());
                        ((ViewHolder) holder).txtUserOrderAmt.setText(posts.get(position).getNetAmount());
                        ((ViewHolder) holder).txtUserOrderPayment.setText(posts.get(position).getPaymentMethod());
                        ((ViewHolder) holder).txtUserOrderCreate.setText(posts.get(position).getCreatedAt());
                        ((ViewHolder) holder).txtuserOrderStatus.setText(posts.get(position).getPaymentStatus());
                        int status = Integer.parseInt(posts.get(position).getPaymentStatus());
                        if (status == 0){
                            ((ViewHolder) holder).cardViewCancelOrder.setVisibility(View.VISIBLE);
                            ((ViewHolder) holder).txtuserOrderStatus.setText("Pending");
                            ((ViewHolder) holder).txtuserOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.yellow)); ;

                        }else if (status == 1){
                            ((ViewHolder) holder).txtuserOrderStatus.setText("Success");
                            ((ViewHolder) holder).txtuserOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.green));
                        }else if (status == 2){
                            ((ViewHolder) holder).txtuserOrderStatus.setText("Cancel");
                            ((ViewHolder) holder).txtuserOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.red));
                        }
                        ((ViewHolder) holder).cardViewCancelOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, OrderCancelActivity.class);
                                intent.putExtra(OrderCancelActivity.ORDER_ID,posts.get(position).getId());
                                intent.putExtra(OrderCancelActivity.ORDER_NAME,posts.get(position).getBookingRef());
                                ((Activity)context).overridePendingTransition(0,0);
                                context.startActivity(intent);


                            }
                        });



                }
        }

        @Override
        public int getItemCount() {
                if(posts==null || posts.size()==0){
                        return 0;
                }
                /*if(itemsCount==posts.size()){
                    return posts.size()+1;
                }else {
                    return posts.size();
                }*/
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
                @BindView(R.id.user_order_booking_ref)TextView txtBookingRef;
                @BindView(R.id.user_order_amt)TextView txtUserOrderAmt;
                @BindView(R.id.user_order_payment)TextView txtUserOrderPayment;
                @BindView(R.id.user_order_create)TextView txtUserOrderCreate;
                @BindView(R.id.user_order_status)TextView txtuserOrderStatus;
                @BindView(R.id.card_cancel_order)CardView cardViewCancelOrder;

                public ViewHolder(View itemView) {
                        super(itemView);
                        ButterKnife.bind(this,itemView);
                }
        }
        public class LoadingViewHolder extends RecyclerView.ViewHolder{
                @BindView(R.id.loading)
                ProgressBar progressBar;
                public LoadingViewHolder(View itemView) {
                        super(itemView);
                        ButterKnife.bind(this,itemView);
                }
        }

}


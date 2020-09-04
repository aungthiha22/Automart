package com.rebook.automart.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 2/18/2019.
 */

public class ShoppingCardFragment extends Fragment {

    @BindView(R.id.add_to_cart_list)RecyclerView recyclerView;
    @BindView(R.id.emptyView)TextView emptyView;
    @BindView(R.id.add_to_cart_order)public Button btnAddToCartCheckout;
    @BindView(R.id.txtTotalAmt)public TextView txtTotalAmt;


    CartAdapter cartAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Product> orderList = new ArrayList<>();
    ArrayList<Product> showAmountList = new ArrayList<>();
    SQLiteDatabase db;
    public int totalCount = 0 ;
    public int itemPrice,samePriceInItem;

    public static ShoppingCardFragment getInstance(String type){
        ShoppingCardFragment fragment=new ShoppingCardFragment();
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //homeAdapter=new HomeAdapter(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_to_card_list, container, false);
        ButterKnife.bind(this, view);

        db = SQLiteDatabase.openDatabase(Config.path,null,SQLiteDatabase.CREATE_IF_NECESSARY);

        Cursor forOrderButtonCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
        btnAddToCartCheckout.setText("checkout ("+forOrderButtonCursor.getCount()+")");
        orderList.clear();
        while (forOrderButtonCursor.moveToNext()){
            Product product = new Product();
            product.setAddTablePrice(forOrderButtonCursor.getInt(forOrderButtonCursor.getColumnIndex("addPrice")));
            product.setQuantity(forOrderButtonCursor.getInt(forOrderButtonCursor.getColumnIndex("orderQuantity")));
            orderList.add(product);
        }
        for (int i=0 ; i<orderList.size(); i++){
            itemPrice += (orderList.get(i).getAddTablePrice()*orderList.get(i).getQuantity() );
            Log.e("Shopping fragment","add table price"+String.valueOf(orderList.get(i).getAddTablePrice()));
        }
        txtTotalAmt.setText(String.valueOf(itemPrice)+" MMK");

        linearLayoutManager=new LinearLayoutManager(getActivity());
        cartAdapter = new CartAdapter(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        orderList = new ArrayList<>();
        orderList = getTableData();
        if (orderList.size() == 0){
            emptyView.setText("There are no Cart");
            emptyView.setVisibility(View.VISIBLE
            );
        }
        cartAdapter.append(orderList);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btnAddToCartCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor intentCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                if (intentCursor.getCount()>0) {
                    // startActivity(new Intent(getActivity(), OrderListActivity.class));
                    startActivity(new Intent(getActivity(),PromotionAndPayment.class));
                    getActivity().finish();

                }else {
                   /* ZgToast zgToast = new ZgToast(getActivity());
                    zgToast.setError();
                    zgToast.setZgText("Please, select item at least one");
                    zgToast.show();*/
                    Snackbar.make(v, "Please, select item at least one",
                            Snackbar.LENGTH_LONG).setAction("Action", null)
                            .setActionTextColor(Color.RED)
                            .show();
                }
            }
        });




        return view;
    }

    public ArrayList<Product> getTableData(){
        Cursor cursor = db.rawQuery("select * from product where type = 0",null);

        while (cursor.moveToNext()){
            Product product = new Product();
            product.setId(cursor.getString(cursor.getColumnIndex("id")));
            product.setName(cursor.getString(cursor.getColumnIndex("name")));
            product.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
            product.setAddTablePrice(cursor.getInt(cursor.getColumnIndex("addPrice")));
            product.setPromotion(cursor.getInt(cursor.getColumnIndex("promotion")));
            product.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
            product.setOrderQuantity(cursor.getInt(cursor.getColumnIndex("orderQuantity")));
            product.setCheckOrder(cursor.getString(cursor.getColumnIndex("checkBox")));
            product.setImage1(cursor.getString(cursor.getColumnIndex("imageUrl")));

            orderList.add(product);

        }

        return orderList;
    }
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_LOADING=2;
    public static final int TYPE_ITEM=1;
    List<Product> posts=new ArrayList<>();
    Context context;
    String type;
    int width,height;
    boolean isShow;


    public CartAdapter(Context context){
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
        View view= LayoutInflater.from(context).inflate(R.layout.add_to_card_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
            /*if(viewType==TYPE_ITEM){
                View view= LayoutInflater.from(context).inflate(R.layout.add_to_card_item,parent,false);
                CartAdapter.ViewHolder viewHolder=new CartAdapter.ViewHolder(view);
                return viewHolder;
            }
            else if(viewType==TYPE_LOADING){
                View view=LayoutInflater.from(context).inflate(R.layout.layout_loading,parent,false);
                CartAdapter.LoadingViewHolder loadingViewHolder=new CartAdapter.LoadingViewHolder(view);
                return loadingViewHolder;
            }
            return null;*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //  Post post = new Post();
            /*if(holder instanceof CartAdapter.LoadingViewHolder){
                if(isShow){
                   // ((CartAdapter.LoadingViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    ((CartAdapter.LoadingViewHolder)holder).progressBar.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                }
            }*/
        if(holder instanceof ViewHolder){


            Glide.with(context).load(Config.MAIN_URL + "storage/" + posts.get(position).getImage1())
                    .override(Utils.getPx(context, 200),
                            Utils.getPx(context, 180))
                    .into(((ViewHolder) holder).imageView);
            ((ViewHolder) holder).txtTitle.setText(posts.get(position).getName());
            ((ViewHolder) holder).txtPrice.setText(String.valueOf(posts.get(position).getAddTablePrice()));
            ((ViewHolder) holder).txtQuantity.setText(String.valueOf(posts.get(position).getOrderQuantity()));

            if (posts.get(position).getCheckOrder().equals("yes")){
                ((ViewHolder) holder).checkBox.setChecked(true);
            }
            else {
                ((ViewHolder) holder).checkBox.setChecked(false);
            }
            ((ViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    itemPrice = 0;
                    samePriceInItem = 0;
                    totalCount = (posts.get(position).getPrice())*(posts.get(position).getOrderQuantity());

                    if (((ViewHolder) holder).checkBox.isChecked()) {
                        String checkSQL = "UPDATE product SET checkBox = 'yes' WHERE id= " + posts.get(position).getId();
                        db.execSQL(checkSQL);
                        Cursor orderCheckCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                        //addToCartOrder.setText("order ("+orderCheckCursor.getCount()+")");
                        showAmountList.clear();
                        while (orderCheckCursor.moveToNext()){
                            Product product = new Product();
                            product.setAddTablePrice(orderCheckCursor.getInt(orderCheckCursor.getColumnIndex("addPrice")));
                            product.setQuantity(orderCheckCursor.getInt(orderCheckCursor.getColumnIndex("orderQuantity")));
                            showAmountList.add(product);
                        }
                    }else {
                        String checkSQL = "UPDATE product SET checkBox = 'no' WHERE id= " + posts.get(position).getId();
                        db.execSQL(checkSQL);
                        Cursor orderUnCheckCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                        //addToCartOrder.setText("order ("+orderUnCheckCursor.getCount()+")");
                        showAmountList.clear();
                        while (orderUnCheckCursor.moveToNext()){
                            Product product = new Product();
                            product.setAddTablePrice(orderUnCheckCursor.getInt(orderUnCheckCursor.getColumnIndex("addPrice")));
                            product.setQuantity(orderUnCheckCursor.getInt(orderUnCheckCursor.getColumnIndex("orderQuantity")));
                            showAmountList.add(product);

                        }
                    }

                    itemPrice = 0;
                    txtTotalAmt.setText(0+" MMK");
                    for (int i=0 ; i<showAmountList.size(); i++){
                        itemPrice += (showAmountList.get(i).getAddTablePrice()*showAmountList.get(i).getQuantity() );

                    }
                    txtTotalAmt.setText(itemPrice+" MMK");
                    Cursor orderCheckCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                    btnAddToCartCheckout.setText("checkout ("+orderCheckCursor.getCount()+")");
                }
            });

            ((ViewHolder) holder).imgDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString())>1) {
                        ((ViewHolder) holder).txtQuantity.setText(Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString()) - 1 + "");
                        String decreaseSQL = "UPDATE product SET orderQuantity = '"+Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString())+"' WHERE id= "+ posts.get(position).getId();
                        db.execSQL(decreaseSQL);
                        showAmountList.clear();
                        Cursor decreaseCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                        while (decreaseCursor.moveToNext()){
                            Product product = new Product();
                            product.setAddTablePrice(decreaseCursor.getInt(decreaseCursor.getColumnIndex("addPrice")));
                            product.setQuantity(decreaseCursor.getInt(decreaseCursor.getColumnIndex("orderQuantity")));
                            showAmountList.add(product);
                        }
                        itemPrice = 0;
                        txtTotalAmt.setText(0+" MMK");
                        for (int i=0 ; i<showAmountList.size(); i++){
                            itemPrice += (showAmountList.get(i).getAddTablePrice()*showAmountList.get(i).getQuantity() );
                            Log.e("Total price is ","_________"+orderList.get(i).getQuantity());
                        }
                        txtTotalAmt.setText(itemPrice+" MMK");
                    }
                }
            });

            ((ViewHolder) holder).imgIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*count = list.get(position).getQuantity()+1;*/
                    if ((posts.get(position).getQuantity()-1) >= Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString())) {
                        ((ViewHolder) holder).txtQuantity.setText(Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString()) + 1 + "");
                        String increaseSQL = "UPDATE product SET orderQuantity = '"+Integer.parseInt(((ViewHolder) holder).txtQuantity.getText().toString())+"' WHERE id= "+ posts.get(position).getId();
                        db.execSQL(increaseSQL);
                        showAmountList.clear();
                        Cursor decreaseCursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);
                        while (decreaseCursor.moveToNext()){
                            Product product = new Product();
                            product.setAddTablePrice(decreaseCursor.getInt(decreaseCursor.getColumnIndex("addPrice")));
                            product.setQuantity(decreaseCursor.getInt(decreaseCursor.getColumnIndex("orderQuantity")));
                            showAmountList.add(product);
                        }
                        itemPrice = 0;
                        txtTotalAmt.setText(0+" MMK");
                        for (int i=0 ; i<showAmountList.size(); i++){
                            itemPrice += (showAmountList.get(i).getAddTablePrice()*showAmountList.get(i).getQuantity() );
                            Log.e("Total price is ","_________"+showAmountList.get(i).getQuantity());

                        }
                        txtTotalAmt.setText(itemPrice+" MMK");
                    }else {
                        ZgToast zgToast = new ZgToast(getActivity());
                        zgToast.setError();
                        zgToast.setZgText("quantity no enough");
                        zgToast.show();
                    }


                }
            });
            ((ViewHolder) holder).imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.execSQL("delete from product where id = '"+posts.get(position).getId()+"' and type = 0 " );
                    startActivity(new Intent(getActivity(),MainActivity.class));
                    getActivity().finish();

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if(posts==null || posts.size()==0){
            return 0;
        }
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position!=0 && position==posts.size())? TYPE_LOADING : TYPE_ITEM;
    }
    public void showLoading(boolean status){
        isShow=status;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.add_to_cart_image)ImageView imageView;
        @BindView(R.id.add_to_cart_title)TextView txtTitle;
        @BindView(R.id.add_to_cart_price)TextView txtPrice;
        @BindView(R.id.add_to_cart_checkBox)CheckBox checkBox;
        @BindView(R.id.increase_image)ImageView imgIncrease;
        @BindView(R.id.decrease_image)ImageView imgDecrease;
        @BindView(R.id.delete_image)ImageView imgDelete;
        @BindView(R.id.quantity)TextView txtQuantity;

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

}
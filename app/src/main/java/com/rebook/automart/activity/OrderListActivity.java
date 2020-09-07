package com.rebook.automart.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/26/2019.
 */

public class OrderListActivity extends AppCompatActivity {

    @BindView(R.id.order_list)ListView orderListView;
    @BindView(R.id.order)public Button btnOrder;
    @BindView(R.id.order_amount)public TextView txtAmount;
    ArrayList<Product> orderList = new ArrayList<>();
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;
    int totalAmount ;
    String fromProductDetail = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_activity);
        ButterKnife.bind(this);

        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        orderList = getTableData();

        JSONArray jsonArray = itemListToJsonConvert(orderList);
        Toast.makeText(this, "Json array size "+String.valueOf(jsonArray.length()), Toast.LENGTH_SHORT).show();


        OrderAdapter orderAdapter = new OrderAdapter(OrderListActivity.this,1);
        orderListView.setAdapter(orderAdapter);
        for (int i=0 ; i<orderList.size(); i++){
            totalAmount += (orderList.get(i).getPrice()*orderList.get(i).getOrderQuantity());;
        }
        txtAmount.setText(String.valueOf(totalAmount)+" mmk");



    }
    public ArrayList<Product> getTableData(){
        Cursor cursor = db.rawQuery("select * from product where type = 0 and checkBox = 'yes'",null);

        while (cursor.moveToNext()){
            Product product = new Product();
            product.setId(cursor.getString(cursor.getColumnIndex("id")));
            product.setName(cursor.getString(cursor.getColumnIndex("name")));
            product.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
            product.setAddTablePrice(cursor.getInt(cursor.getColumnIndex("addPrice")));
            product.setOrderQuantity(cursor.getInt(cursor.getColumnIndex("orderQuantity")));
            product.setImage1(cursor.getString(cursor.getColumnIndex("imageUrl")));

            orderList.add(product);

        }

        return orderList;
    }


    public JSONArray itemListToJsonConvert(ArrayList<Product> list) {

       // JSONObject jResult = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

        for (int i = 0; i < list.size(); i++) {
            JSONObject jGroup = new JSONObject();// /sub Object

            try {
                jGroup.put("ItemMasterID", list.get(i).getId());
                jGroup.put("ID", list.get(i).getId());
                jGroup.put("Name", list.get(i).getName());
                jGroup.put("price", list.get(i).getPrice());

                jArray.put(jGroup);

                // /itemDetail Name is JsonArray Name
                //jResult.put("itemDetail", jArray);
               // return jResult;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jArray;
    }


    public class OrderAdapter extends ArrayAdapter {
        public OrderAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_item, null, false);
            int totalAmtInList;
            ImageView imageView = convertView.findViewById(R.id.order_image);
            TextView txtName = convertView.findViewById(R.id.order_name);
            TextView txtPrice = convertView.findViewById(R.id.order_price);
            TextView txtQuantity = convertView.findViewById(R.id.order_quantity);
            TextView txtAmount = convertView.findViewById(R.id.amount);

            Glide.with(OrderListActivity.this)
                    .load(Config.MAIN_URL+"storage/"+orderList.get(position).getImage1())
                    .override(Utils.getPx(OrderListActivity.this,200),
                            Utils.getPx(OrderListActivity.this,180))
                    .into(imageView);

            txtName.setText(orderList.get(position).getName());
            txtPrice.setText(String.valueOf(orderList.get(position).getPrice()));
            txtQuantity.setText(String.valueOf(orderList.get(position).getOrderQuantity()));

            totalAmtInList = (orderList.get(position).getPrice()*orderList.get(position).getOrderQuantity());
            txtAmount.setText(String.valueOf(totalAmtInList));
            Log.e("Orderlistactivity",String.valueOf(totalAmtInList));


            return convertView;
        }

    }
}

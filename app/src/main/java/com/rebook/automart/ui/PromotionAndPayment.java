package com.rebook.automart.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.model.Shop;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Dell on 4/30/2019.
 */

public class PromotionAndPayment extends AppCompatActivity {

    public static final String TAG = "PromotionAndPayment";

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.linearLayout2)LinearLayout linearLayout2;
    @BindView(R.id.phone_number)EditText editPhoneNumber;
    @BindView(R.id.shipping_address)EditText editShippingAddress;
    @BindView(R.id.bank_transfer)RadioButton radioBankTransfer;
    @BindView(R.id.mpu)RadioButton radioMpu;
    @BindView(R.id.credit_sale)RadioButton radioCreditSale;
    @BindView(R.id.cash_on_deliver)RadioButton radioCashOnDeliver;
    @BindView(R.id.wave_pay)RadioButton radioWavePay;
    @BindView(R.id.kbz_pay)RadioButton radioKBZPay;
    @BindView(R.id.promo_code)EditText editPromoCode;
    @BindView(R.id.ripple_apply)RippleView rippleViewApply;
    @BindView(R.id.shop_spinner)Spinner spinnerShop;
    @BindView(R.id.choice_shop_label)TextView labelShop;
    @BindView(R.id.card_spinner_shop)CardView cardViewShop;
    @BindView(R.id.check_promo_code)Button checkPromo;
    @BindView(R.id.txt_promo_code)TextView txtPromoCode;
    @BindView(R.id.txt_total_amount)TextView txtTotalAmount;
   // @BindView(R.id.txt_amount_with_tax)TextView txtAmtWithTax;
    @BindView(R.id.txt_final_amount)TextView txtFinalValue;
    @BindView(R.id.promo_layout)LinearLayout promoLayout;
    //@BindView(R.id.promo_expire_show)TextView  promoExpireShow;
    @BindView(R.id.main_layout)RelativeLayout mainLayout;
    @BindView(R.id.lbl_promo)TextView lblPromo;
    @BindView(R.id.layout_net_amount)LinearLayout layoutNetAmount;

    String radioFlag ,shippingAddress,promoCode,phoneNumber;
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;
    ArrayList<Product> orderList = new ArrayList<>();
    ArrayList<Shop> shopList = new ArrayList<>();
    ArrayList<String> shopNameList = new ArrayList<>();
    int totalAmount /*, amountWithTax*/,lastAmount;
    OkHttpClient okHttpClient = new OkHttpClient();
    TinyDB tinyDB;
    SyncPostService syncPostService ;
    String paymentMethod,bookingId,shopName,saleManUserId ;
    int promoAmount;
    ProgressDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_payment_activity);
        ButterKnife.bind(this);

        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        orderList = getTableData();
        tinyDB = new TinyDB(PromotionAndPayment.this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading. Please wait...");

        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(getResources().getString(R.string.payment_title));
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);
        titleTextView.setText(getResources().getString(R.string.payment_title));

        final JSONArray jsonArray = itemListToJsonConvert(orderList);
        Log.e("array is ","\t"+jsonArray.toString());

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
        if (tinyDB.getString(Config.SALE_MAN).equals("saleman")){
            labelShop.setVisibility(View.VISIBLE);
            cardViewShop.setVisibility(View.VISIBLE);
            syncPostService.getShopSaleMan(tinyDB.getString(Config.STORE_TOKEN), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    shopNameList.clear();
                    shopList.clear();
                    JsonArray jsonArrayShop = jsonObject.get("shops").getAsJsonArray();
                    for (int i=0; i<jsonArrayShop.size(); i++){
                        Gson gson = new Gson();
                        shopList.add(gson.fromJson(jsonArrayShop.get(i),Shop.class));
                        shopNameList.add(shopList.get(i).getName());
                        Log.e("shop list","__________"+shopNameList.get(i));

                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PromotionAndPayment.this, android.R.layout.simple_spinner_item, shopNameList);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    spinnerShop.setAdapter(dataAdapter);
                    spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            shopName = shopNameList.get(position);
                            saleManUserId = String.valueOf(shopList.get(position).getUserId());
                            Log.e("Promo ","shop name __________"+shopName);
                            Log.e("Promo ","user id is__________"+saleManUserId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    ZgToast zgToast = new ZgToast(PromotionAndPayment.this);
                    zgToast.setError();
                    zgToast.setGravity(Gravity.CENTER, 0, 0);
                    zgToast.setZgText("Your account is expired , Please logout and then login again");
                    zgToast.show();
                }
            });

            Log.e("FaceBook","sale man \t---------------"+saleManUserId);
        }else {
            labelShop.setVisibility(View.GONE);
            cardViewShop.setVisibility(View.GONE);
            saleManUserId = tinyDB.getString(Config.SOCIAL_LOGIN_ID);
            Log.e("FaceBook","not sale man ---------------"+saleManUserId);
        }

        editShippingAddress.setText(tinyDB.getString(Config.SHIPPING_ADDRESS));
        editPhoneNumber.setText(tinyDB.getString(Config.PHONE_NUMBER));
        totalAmount = 0;
        for (int i=0 ; i<orderList.size(); i++){
            totalAmount += (orderList.get(i).getPrice()*orderList.get(i).getOrderQuantity());;
        }


        checkPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (editPromoCode.getText().toString().length()<=0){
                    editPromoCode.setError("Enter Promo Code");
                    editPromoCode.requestFocus();
                    return;
                }*/
                if (radioBankTransfer.isChecked()) {
                    radioFlag = "bank_transfer";
                } else if (radioMpu.isChecked()) {
                    radioFlag = "mpu";
                } else if (radioCreditSale.isChecked()) {
                    radioFlag = "credit_sale";
                } else if (radioCashOnDeliver.isChecked()) {
                    radioFlag = "cod";
                }else if (radioWavePay.isChecked()){
                    radioFlag = "wave_pay";
                }else if (radioKBZPay.isChecked()){
                    radioFlag = "kbz_pay";
                }

                promoCode = editPromoCode.getText().toString();
                if (promoCode.length() <= 0) {
                    promoCode = "0";
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    },1000);
                    txtTotalAmount.setText(totalAmount + "\tMMK");
                    //amountWithTax = (totalAmount * 5) / 100;
                   // txtAmtWithTax.setText(amountWithTax + "\tMMK");
                    //lastAmount = totalAmount + amountWithTax;
                    lastAmount = totalAmount; // remove amountWithTax
                    txtFinalValue.setText(lastAmount + "\tMMK");
                    linearLayout2.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    // editPromoCode.setText("promo is expire");
                    promoLayout.setVisibility(View.GONE);
                    layoutNetAmount.setVisibility(View.GONE);
                }else {
                    syncPostService.checkPromo(
                            tinyDB.getString(Config.STORE_TOKEN),
                            promoCode,
                            radioFlag,
                            new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    },1000);
                                    final boolean successPromo = jsonObject.get("success").getAsBoolean();

                                    if (successPromo) {
                                        JsonObject jsonObject1 = jsonObject.get("promo").getAsJsonObject();
                                        promoAmount = Integer.parseInt(jsonObject1.get("amount").getAsString());
                                        String promoType = jsonObject1.get("type").getAsString();
                                        linearLayout2.setVisibility(View.GONE);
                                        scrollView.setVisibility(View.VISIBLE);
                                        if (promoType.equals("Amount")) {
                                            //Toast.makeText(PromotionAndPayment.this, "success"+promoAmount, Toast.LENGTH_SHORT).show();
                                            txtPromoCode.setText(promoAmount + "\tMMK");

                                            txtTotalAmount.setText(totalAmount + "\tMMK");
                                           // amountWithTax = (totalAmount * 5) / 100;
                                           // txtAmtWithTax.setText(amountWithTax + "\tMMK");
                                            //lastAmount = totalAmount - promoAmount + amountWithTax;
                                            lastAmount = totalAmount - promoAmount; // remove tax amount
                                            txtFinalValue.setText(lastAmount + "\tMMK");
                                        }else if (promoType.equals("Percent")){
                                            lblPromo.setText("Promo\t("+promoAmount+"%)");

                                            promoAmount = (totalAmount*promoAmount) / 100;
                                            txtPromoCode.setText(promoAmount + "\tMMK");
                                            txtTotalAmount.setText(totalAmount + "\tMMK");
                                            //amountWithTax = (totalAmount * 5) / 100;
                                           // txtAmtWithTax.setText(amountWithTax + "\tMMK");
                                            //lastAmount = totalAmount - promoAmount + amountWithTax;
                                            lastAmount = totalAmount - promoAmount ; // remove tax amount;
                                            txtFinalValue.setText(lastAmount + "\tMMK");
                                        }
                                    } else {
                                        String message = jsonObject.get("message").getAsString();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PromotionAndPayment.this);
                                        builder.setTitle("Confirm");
                                        builder.setMessage(message);
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                txtPromoCode.setText(promoAmount + "\tMMK");
                                                txtTotalAmount.setText(totalAmount + "\tMMK");
                                                //amountWithTax = (totalAmount * 5) / 100;
                                                //txtAmtWithTax.setText(amountWithTax + "\tMMK");
                                                //lastAmount = totalAmount + amountWithTax;
                                                lastAmount = totalAmount ; // remove total amount
                                                txtFinalValue.setText(lastAmount + "\tMMK");
                                                linearLayout2.setVisibility(View.GONE);
                                                scrollView.setVisibility(View.VISIBLE);
                                                // editPromoCode.setText("promo is expire");
                                                promoLayout.setVisibility(View.GONE);
                                                layoutNetAmount.setVisibility(View.GONE);
                                            }
                                        });
                                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing
                                                dialog.dismiss();
                                            }
                                        });
                                   /* builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });*/

                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    },1000);
                                    ZgToast zgToast = new ZgToast(PromotionAndPayment.this);
                                    zgToast.setError();
                                    zgToast.setGravity(Gravity.CENTER, 0, 0);
                                    zgToast.setZgText("Your account is expired , Please logout and then login again");
                                    zgToast.show();
                                    startActivity(new Intent(PromotionAndPayment.this,LoginActivity.class));
                                    finish();
                                }
                            });
                }



                //  Toast.makeText(PromotionAndPayment.this, "Radio value is "+radioFlag, Toast.LENGTH_SHORT).show();
            }
        });

        rippleViewApply.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //Log.e("promo","sale man in button ____________ "+saleManUserId);

                shippingAddress = editShippingAddress.getText().toString();
                phoneNumber = editPhoneNumber.getText().toString();
                tinyDB.putStringMethod(Config.SHIPPING_ADDRESS,shippingAddress);
                tinyDB.putStringMethod(Config.PHONE_NUMBER,phoneNumber);

                if (phoneNumber.length()<=0){
                    editPhoneNumber.setError(getResources().getString(R.string.enter_ph_number));
                    editPhoneNumber.requestFocus();
                    return;
                }
                if (shippingAddress.length()<=0){
                    editShippingAddress.setError(getResources().getString(R.string.enter_shipping_address));
                    editShippingAddress.requestFocus();
                    return;
                }

                if (radioBankTransfer.isChecked()){
                    radioFlag = "bank_transfer";
                }else if (radioMpu.isChecked()){
                    radioFlag = "mpu";
                }else if (radioCreditSale.isChecked()){
                    radioFlag = "credit_sale";
                }else if (radioCashOnDeliver.isChecked()){
                    radioFlag = "cod";
                }
                else if (radioWavePay.isChecked()){
                    radioFlag = "wave_pay";
                }else if (radioKBZPay.isChecked()){
                    radioFlag = "kbz_pay";
                }
                Log.i("Payment ","____________\t"+jsonArray.toString());
                Log.i("Payment ","____________\t"+tinyDB.getString(Config.STORE_TOKEN));
                Log.i(TAG , "user_id is : "+saleManUserId);

                dialog.show();
                Log.i(TAG,"user id ................"+jsonArray.toString());
                Log.i(TAG,"user id ................"+ tinyDB.getString(Config.STORE_TOKEN));
                Log.i(TAG,"user id ................"+radioFlag);
                Log.i(TAG,"user id ................"+totalAmount);
                Log.i(TAG,"user id ................"+promoCode);
                Log.i(TAG,"user id ................"+editShippingAddress.getText().toString());
                Log.i(TAG,"user id ................"+saleManUserId);
                Log.i(TAG,"user id ................"+promoCode);
                Log.i(TAG,"user id ................"+editPhoneNumber.getText().toString());
                Log.i(TAG,"user id ................"+lastAmount);
                syncPostService.getPaymentConfirm(jsonArray.toString(),
                        tinyDB.getString(Config.STORE_TOKEN)
                        ,radioFlag,
                        String.valueOf(totalAmount),
                        promoCode,
                        editShippingAddress.getText().toString(),
                        String.valueOf(saleManUserId),
                        String.valueOf(promoAmount),
                        editPhoneNumber.getText().toString(),
                        "0",
                        String.valueOf(lastAmount),
                        new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        dialog.dismiss();
                        txtPromoCode.setText(promoAmount+"\tMMK");
                        txtTotalAmount.setText(totalAmount+"\tMMK");
                       // txtAmtWithTax.setText(amountWithTax+"\tMMK");

                        paymentMethod = jsonObject.get("payment_method").getAsString();
                        bookingId = jsonObject.get("encrypt_booking_id").getAsString();

                        Log.e(TAG,"payment method  : "+paymentMethod);
                        Log.e(TAG,"booking id : "+bookingId);
                        Intent intent = new Intent(PromotionAndPayment.this,WebViewActivity.class);
                        intent.putExtra("payment_method",paymentMethod);
                        intent.putExtra("encrypt_booking_id", bookingId);
                        startActivity(intent);
                        finish();
                        /*dialog.dismiss();


                        LayoutInflater factory = LayoutInflater.from(PromotionAndPayment.this);
                        final View dialogView = factory.inflate(R.layout.dialog, null);
                        final AlertDialog deleteDialog = new AlertDialog.Builder(PromotionAndPayment.this).create();
                        deleteDialog.setView(dialogView);
                        //final TextView textView = deleteDialogView.findViewById(R.id.textTest);

                        TextView dialogAmt = dialogView.findViewById(R.id.dialog_amt);
                        TextView dialogPromo = dialogView.findViewById(R.id.dialog_promotion);
                        TextView dialogTotal = dialogView.findViewById(R.id.dialog_total_amt);
                        dialogAmt.setText(String.valueOf(totalAmt));
                        dialogPromo.setText(String.valueOf(promoAmount));
                        dialogTotal.setText(String.valueOf(subTotalAmt));*/
                        /*dialogView.findViewById(R.id.dialog_yes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PromotionAndPayment.this,WebViewActivity.class);
                                intent.putExtra("payment_method",paymentMethod);
                                intent.putExtra("encrypt_booking_id", bookingId);
                                startActivity(intent);
                                finish();


                            }
                        });
                        dialogView.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // textView.setText("NO");
                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.show();*/


                            }
                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        ZgToast zgToast = new ZgToast(PromotionAndPayment.this);
                        zgToast.setError();
                        zgToast.setGravity(Gravity.CENTER, 0, 0);
                        zgToast.setZgText("Your account is expired , Please logout and then login again");
                        zgToast.show();
                        Log.e("Error ","______________"+error.getMessage());
                        startActivity(new Intent(PromotionAndPayment.this,LoginActivity.class));
                        finish();
                    }
                });

            }
        });

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
            product.setSku(cursor.getString(cursor.getColumnIndex("sku")));

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
                jGroup.put("id", list.get(i).getId());
                jGroup.put("sku", list.get(i).getSku());
                jGroup.put("name", list.get(i).getName());
                jGroup.put("original_price", list.get(i).getPrice());
                jGroup.put("price", list.get(i).getAddTablePrice());
                jGroup.put("promotion", list.get(i).getPromotion());
                jGroup.put("quantity", list.get(i).getOrderQuantity());
                jGroup.put("image", list.get(i).getImage1());

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(PromotionAndPayment.this,MainActivity.class);
                overridePendingTransition(0,0);
                startActivity(intent);
                finish();
                return true;
            case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PromotionAndPayment.this,MainActivity.class);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }

}

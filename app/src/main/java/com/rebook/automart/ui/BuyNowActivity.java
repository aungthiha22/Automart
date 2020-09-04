package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Shop;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Dell on 4/29/2019.
 */

public class BuyNowActivity extends AppCompatActivity {

    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.linearLayout2)LinearLayout linearLayout2;
    @BindView(R.id.phone_number)EditText editPhoneNumber;
    @BindView(R.id.shipping_address)EditText editShippingAddress;
    @BindView(R.id.bank_transfer)RadioButton radioBankTransfer;
    @BindView(R.id.mpu)RadioButton radioMpu;
    @BindView(R.id.credit_sale)RadioButton radioCreditSale;
    @BindView(R.id.cash_on_deliver)RadioButton radioCashOnDeliver;
    @BindView(R.id.promo_code)EditText editPromoCode;
    @BindView(R.id.ripple_apply)RippleView rippleViewApply;
    @BindView(R.id.shop_spinner)Spinner spinnerShop;
    @BindView(R.id.choice_shop_label)TextView labelShop;
    @BindView(R.id.card_spinner_shop)CardView cardViewShop;
    @BindView(R.id.check_promo_code)Button checkPromo;
    @BindView(R.id.txt_promo_code)TextView txtPromoCode;
    @BindView(R.id.txt_total_amount)TextView txtTotalAmount;
//    @BindView(R.id.txt_amount_with_tax)TextView txtAmtWithTax;
    @BindView(R.id.txt_final_amount)TextView txtFinalValue;
    @BindView(R.id.promo_layout)LinearLayout promoLayout;
    @BindView(R.id.lbl_promo)TextView lblPromo;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.layout_net_amount)LinearLayout layoutNetAmount;

    String id,imageUrl, name,radioFlag,promoCode,phoneNumber,shippingAddress;
    int originalPrice,addPrice,promotion,orderQuantity;
    OkHttpClient okHttpClient = new OkHttpClient();
    TinyDB tinyDB ;
    SyncPostService syncPostService ;
    String paymentMethod,bookingId ,saleManUserId;
    int promoAmount ;
    ArrayList<Shop> shopList = new ArrayList<>();
    ArrayList<String> shopNameList = new ArrayList<>();
    String shopName;
    int totalAmount , amountWithTax,lastAmount;
    ProgressDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_payment_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("");
        TextView titleTextView =  toolbar.findViewById(R.id.toolbar_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);
        titleTextView.setText("");
        tinyDB = new TinyDB(BuyNowActivity.this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading. Please wait...");
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
                        shopList.add(gson.fromJson(jsonArrayShop.get(i), Shop.class));
                        shopNameList.add(shopList.get(i).getName());
                        Log.e("shop list","__________"+shopNameList.get(i));

                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BuyNowActivity.this, android.R.layout.simple_spinner_item, shopNameList);
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
                    Toast.makeText(BuyNowActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            labelShop.setVisibility(View.GONE);
            cardViewShop.setVisibility(View.GONE);
            saleManUserId = tinyDB.getString(Config.SOCIAL_LOGIN_ID);
        }


        editShippingAddress.setText(tinyDB.getString(Config.SHIPPING_ADDRESS));
        editPhoneNumber.setText(tinyDB.getString(Config.PHONE_NUMBER));
        id = getIntent().getStringExtra("id");
        imageUrl = getIntent().getStringExtra("imageUrl");
        name = getIntent().getStringExtra("name");
        originalPrice = getIntent().getIntExtra("original_price",1);
        addPrice = getIntent().getIntExtra("addPrice", 1);
        promotion = getIntent().getIntExtra("promotion",0);
        orderQuantity = getIntent().getIntExtra("orderQuantity", 0);

        Log.e("BuyNow ","imageUrl\t"+imageUrl);
        Log.e("BuyNow ","imageUrl\t"+name);
        Log.e("BuyNow ","imageUrl\t"+String.valueOf(addPrice));
        Log.e("BuyNow ","imageUrl\t"+String.valueOf(orderQuantity));

        final JSONArray jArray = new JSONArray();// /ItemDetail jsonArray
        JSONObject jGroup = new JSONObject();// /sub Object

        try {
            jGroup.put("id", id);
            jGroup.put("name", name);
            jGroup.put("sku", "");
            jGroup.put("original_price", originalPrice);
            jGroup.put("price", addPrice);
            jGroup.put("promotion", promotion);
            jGroup.put("quantity", orderQuantity);
            jGroup.put("image", imageUrl);


            jArray.put(jGroup);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        checkPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                promoCode = editPromoCode.getText().toString();

                /*if (editPromoCode.getText().toString().length()<=0){
                    editPromoCode.setError("Enter Promo Code");
                    editPromoCode.requestFocus();
                    return;
                }*/
                if (radioBankTransfer.isChecked()){
                    radioFlag = "bank_transfer";
                }else if (radioMpu.isChecked()){
                    radioFlag = "mpu";
                }else if (radioCreditSale.isChecked()){
                    radioFlag = "credit_sale";
                }else if (radioCashOnDeliver.isChecked()){
                    radioFlag = "cod";
                }
                //Toast.makeText(PromotionAndPayment.this,"Toast "+radioFlag,Toast.LENGTH_SHORT).show();
                if (promoCode.length()<=0){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }},1000);
                    promoCode = "0";
                    linearLayout2.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    promoLayout.setVisibility(View.GONE);
                    layoutNetAmount.setVisibility(View.GONE);
                    txtPromoCode.setText(promoAmount+"\tMMK");
                    totalAmount = addPrice*orderQuantity;
                    txtTotalAmount.setText(totalAmount+"\tMMK");
                    amountWithTax = (totalAmount*5)/100;
                   // txtAmtWithTax.setText(amountWithTax+"\tMMK");
                    lastAmount = totalAmount+amountWithTax;
                    txtFinalValue.setText(lastAmount+"\tMMK");

                }else {
                    syncPostService.checkPromo(
                            tinyDB.getString(Config.STORE_TOKEN),
                            promoCode,
                            radioFlag,
                            new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, final Response response) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }},1000);
                                    boolean successPromo = jsonObject.get("success").getAsBoolean();

                                    if (successPromo){
                                        JsonObject jsonObject1 = jsonObject.get("promo").getAsJsonObject();
                                        promoAmount = Integer.parseInt(jsonObject1.get("amount").getAsString());
                                        String promoType = jsonObject1.get("type").getAsString();
                                        linearLayout2.setVisibility(View.GONE);
                                        scrollView.setVisibility(View.VISIBLE);
                                        if (promoType.equals("Amount")) {
                                            //Toast.makeText(PromotionAndPayment.this, "success"+promoAmount, Toast.LENGTH_SHORT).show();
                                            txtPromoCode.setText(promoAmount + "\tMMK");
                                            totalAmount = addPrice * orderQuantity;
                                            txtTotalAmount.setText(totalAmount + "\tMMK");
                                            amountWithTax = (totalAmount * 5) / 100;
                                           // txtAmtWithTax.setText(amountWithTax + "\tMMK");
                                            lastAmount = totalAmount - promoAmount + amountWithTax;
                                            txtFinalValue.setText(lastAmount + "\tMMK");
                                        }else if (promoType.equals("Percent")){
                                            lblPromo.setText("Promo\t("+promoAmount+"%)");
                                            totalAmount = addPrice * orderQuantity;
                                            promoAmount = (totalAmount*promoAmount) / 100;
                                            txtPromoCode.setText(promoAmount + "\tMMK");
                                            txtTotalAmount.setText(totalAmount + "\tMMK");
                                            amountWithTax = (totalAmount * 5) / 100;
                                          //  txtAmtWithTax.setText(amountWithTax + "\tMMK");
                                            lastAmount = totalAmount - promoAmount + amountWithTax;
                                            txtFinalValue.setText(lastAmount + "\tMMK");
                                        }
                                    }
                                    else {

                                        String message = jsonObject.get("message").getAsString();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(BuyNowActivity.this);
                                        builder.setTitle("Confirm");
                                        builder.setMessage(message);
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                txtPromoCode.setText(promoAmount+"\tMMK");
                                                totalAmount = addPrice*orderQuantity;
                                                txtTotalAmount.setText(totalAmount+"\tMMK");
                                                amountWithTax = (totalAmount*5)/100;
                                               // txtAmtWithTax.setText(amountWithTax+"\tMMK");
                                                lastAmount = totalAmount+amountWithTax;
                                                txtFinalValue.setText(lastAmount+"\tMMK");
                                                linearLayout2.setVisibility(View.GONE);
                                                scrollView.setVisibility(View.VISIBLE);
                                                editPromoCode.setText("promo");
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
                                    ZgToast zgToast = new ZgToast(BuyNowActivity.this);
                                    zgToast.setError();
                                    zgToast.setGravity(Gravity.CENTER, 0, 0);
                                    zgToast.setZgText("Your account is expired , Please logout and then login again");
                                    zgToast.show();
                                    startActivity(new Intent(BuyNowActivity.this,LoginActivity.class));
                                    finish();
                                }
                            });
                }


            }
        });

        //Log.e("buynow activity","______________"+jArray.toString());

        rippleViewApply.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
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


                tinyDB.putStringMethod(Config.SHIPPING_ADDRESS,editShippingAddress.getText().toString());
                tinyDB.putStringMethod(Config.PHONE_NUMBER,editPhoneNumber.getText().toString());
                if (radioBankTransfer.isChecked()){
                    radioFlag = "bank_transfer";
                }else if (radioMpu.isChecked()){
                    radioFlag = "mpu";
                }else if (radioCreditSale.isChecked()){
                    radioFlag = "credit_sale";
                }else if (radioCashOnDeliver.isChecked()){
                    radioFlag = "cod";
                }

               dialog.show();

                Log.e("add price","______________"+addPrice);
                syncPostService.getPaymentConfirm(jArray.toString(),
                        tinyDB.getString(Config.STORE_TOKEN)
                        ,radioFlag,
                        String.valueOf(totalAmount),
                        promoCode,
                        editShippingAddress.getText().toString(),
                        String.valueOf(saleManUserId),
                        String.valueOf(promoAmount),
                        editPhoneNumber.getText().toString(),
                        String.valueOf(amountWithTax),
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

                        Log.e("payment ","method __________"+paymentMethod);
                        Log.e("booking ","id __________"+bookingId);
                        Intent intent = new Intent(BuyNowActivity.this,WebViewActivity.class);
                        intent.putExtra("payment_method",paymentMethod);
                        intent.putExtra("encrypt_booking_id", bookingId);
                        startActivity(intent);
                        finish();

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        ZgToast zgToast = new ZgToast(BuyNowActivity.this);
                        zgToast.setError();
                        zgToast.setGravity(Gravity.CENTER, 0, 0);
                        zgToast.setZgText("Your account is expired , Please logout and then login again");
                        zgToast.show();
                        startActivity(new Intent(BuyNowActivity.this,LoginActivity.class));
                        finish();
                        Log.e("Error ","______________"+error.getMessage());

                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BuyNowActivity.this,MainActivity.class);
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
        Intent intent = new Intent(BuyNowActivity.this,MainActivity.class);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }
}

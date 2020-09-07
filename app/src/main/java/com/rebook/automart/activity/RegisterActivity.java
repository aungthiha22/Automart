package com.rebook.automart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.google.android.gms.analytics.HitBuilders;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


/**
 * Created by Dell on 1/4/2019.
 */

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.register_password)EditText password;
    @BindView(R.id.register_email)EditText email;
    @BindView(R.id.register_username)EditText username;
    @BindView(R.id.register_address)EditText address;
    @BindView(R.id.register_sign_in)Button signIn;
    @BindView(R.id.ripple_register)RippleView rippleRegister;

    OkHttpClient okHttpClient;
    SyncPostService syncPostService;
    TinyDB tinyDB;
    ZgToast zgToast;
    Boolean flag = false;
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_register);
        ButterKnife.bind(this);
        okHttpClient = new OkHttpClient();

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

        tinyDB = new TinyDB(RegisterActivity.this) ;
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "sing in", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this,LoginActivity .class));
                overridePendingTransition(0,0);
                finish();
            }
        });
       rippleRegister.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
           @Override
           public void onComplete(RippleView rippleView) {
               flag = false;
               if (email.getText().toString().length() == 0){
                   email.setError("Please , Enter Email");
                   email.requestFocus();
                   return;
               }else if (!checkEmail(email.getText().toString())){
                   email.setError("Your Email Format Incorrect");
                   email.requestFocus();
                   return;
               }
               if (password.getText().toString().length() == 0){
                   password.setError("Please , Enter Password");
                   password.requestFocus();
                   return;
               }else if (password.getText().toString().length() < 6){
                   password.setError("password at least 6 characters");
                   password.requestFocus();
                   return;
               }
               if (username.getText().toString().length() == 0){
                   username.setError("Please , Enter Username");
                   username.requestFocus();
                   return;
               }

               if (address.getText().toString().length() == 0){
                   address.setError("Please , Enter Address");
                   address.requestFocus();
                   return;
               }
               zgToast = new ZgToast(RegisterActivity.this);
               syncPostService.getRegister(email.getText().toString(),
                       password.getText().toString(),
                       username.getText().toString(),
                       address.getText().toString(),
                       new Callback<JsonObject>() {
                           @Override
                           public void success(JsonObject jsonObject, Response response) {
                               try {
                                   flag = jsonObject.get("success").getAsBoolean() == true ;
                               }catch (NullPointerException e){
                                   e.getStackTrace();
                               }

                               if (flag){
                                   zgToast.setError();
                                   zgToast.setZgText(jsonObject.get("message").getAsString());
                               }else {
                                   zgToast.setZgText("Confirm your email and then You can login with your email");
                                   Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                               zgToast.show();

                           }

                           @Override
                           public void failure(RetrofitError error) {
                               Toast.makeText(RegisterActivity.this, "ERROR"+
                                       error.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });

               // startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
               // tinyDB.putBoolean(Config.LOG_IN,true);
               // overridePendingTransition(0,0);
               // finish();


           }
       });

    }
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
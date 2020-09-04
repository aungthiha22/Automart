package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.andexert.library.RippleView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Review;
import com.rebook.automart.model.Shop;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    @BindView(R.id.login_forget_password)TextView forgetPassword;
    @BindView(R.id.show_password)CheckBox checkShowPassword;
    @BindView(R.id.remember_me)CheckBox checkBox;
    @BindView(R.id.login_username)TextView username;
    @BindView(R.id.login_password)TextView password;
    @BindView(R.id.ripple_login)RippleView rippleLogin;
    @BindView(R.id.google_login)SignInButton signInButtonWhitgoogle;
    @BindView(R.id.google)Button googleTesting;
    @BindView(R.id.fb)Button btnFaceBook;

    ZgToast zgToast;
    OkHttpClient okHttpClient;
    SyncPostService syncPostService;
    GoogleApiClient googleApiClient;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    public static final int REQ_CODE = 9001;
    private CallbackManager callbackManager;
    LoginButton login_button;
    String id;
    String  token_from_api;
    String mAccountName;
    String message = null;
    TinyDB tinyDB;
    String name, email,imageURL,create_at,update_at;
    final List<Review> list = new ArrayList<>();
    List<Shop> shopList = new ArrayList<>();
    ProgressDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_login);
        ButterKnife.bind(this);
        login_button        = (LoginButton) findViewById(R.id.facebook_login);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        tinyDB = new TinyDB(LoginActivity.this);
        okHttpClient = new OkHttpClient();
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getResources().getString(R.string.logging));

        Log.e("/////////////","/////////////////////");

       // printKeyHash();
       // getHashkey();

        //this is for google login
        String serverClientId = getString(R.string.client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        //this is for api
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


        login_button.setReadPermissions(Arrays.asList("public_profile","email"));

        btnFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetService.isInternetAvailable(LoginActivity.this)){
                    onClickFacebookButton(v);

                }else {
                    ZgToast zgToast = new ZgToast(LoginActivity.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }
            }
        });

        login_button.registerCallback(callbackManager,
                new FacebookCallback < LoginResult > () {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.e("accessToken","----------------\t"+ accessToken);
                        syncPostService.logInSocial("facebook", accessToken, new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                token_from_api = jsonObject.get("token").getAsString();
                                JsonObject jsonObjectUser = jsonObject.get("user").getAsJsonObject();

                                tinyDB.putStringMethod(Config.SOCIAL_LOGIN_ID,String.valueOf(jsonObjectUser.get("id").getAsInt()));
                                Log.e("user id","_________________"+tinyDB.getString(Config.SOCIAL_LOGIN_ID));
                                name = jsonObjectUser.get("full_name").getAsString();
                                email = jsonObjectUser.get("email").getAsString();

                                JsonArray jsonArrayProvider = jsonObjectUser.get("providers").getAsJsonArray();
                                for (int i=0; i<jsonArrayProvider.size(); i++){
                                    Gson gson = new Gson();
                                    list.add(gson.fromJson(jsonArrayProvider.get(i),Review.class));

                                    imageURL = list.get(0).getAvatar();
                                    create_at = list.get(0).getCreatedAt();
                                    update_at = list.get(0).getUpdatedAt();

                                    // Log.e("LoginActivity new ","_____"+list.get(0).getCreatedAt());
                                    // Log.e("LoginActivity new ","_____"+list.get(0).getUpdatedAt());
                                }
                                Log.e("LoginActivity new ","_____"+token_from_api);

                                tinyDB.putStringMethod(Config.STORE_TOKEN,token_from_api);
                                tinyDB.putStringMethod(Config.STORE_IMAGE_URL,imageURL);
                                tinyDB.putStringMethod(Config.STORE_NAME,name);
                                tinyDB.putStringMethod(Config.STORE_EMAIL,email);
                                tinyDB.putStringMethod(Config.STORE_CREATE_AT,create_at);
                                tinyDB.putStringMethod(Config.STORE_UPDATE_AT,update_at);

                                tinyDB.putBoolean(Config.LOG_IN,true);

                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });

                        LoginManager.getInstance().logOut();

                    }


                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });


        checkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        rippleLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                message = "ok" ;
                if (NetService.isInternetAvailable(LoginActivity.this)) {
                    if (username.getText().toString().length() == 0) {
                        username.setError("Please , Enter Email");
                        username.requestFocus();
                        return;
                    }else if (!checkEmail(username.getText().toString())){
                        username.setError("Your Email Format Incorrect");
                        username.requestFocus();
                        return;
                    }

                    if (password.getText().toString().length() == 0) {
                        password.setError("Please , Enter Password");
                        password.requestFocus();
                        return;
                    }
                    dialog.show();
                    syncPostService.getLogin(username.getText().toString(),
                            password.getText().toString(),
                            new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    dialog.dismiss();
                                    zgToast = new ZgToast(LoginActivity.this);
                                    try {
                                        message = jsonObject.get("message").getAsString();
                                    }catch (NullPointerException e){
                                        e.getStackTrace();
                                    }
                                    if (message.equals("invalid_credentials")){
                                        zgToast.setError();
                                        zgToast.setGravity(Gravity.CENTER, 0, 0);
                                        zgToast.setZgText("Your email or password incorrect");

                                    }else {
                                        zgToast.setZgText("Success, you are login ");
                                        JsonObject jsonObjectUser = jsonObject.get("user").getAsJsonObject();
                                        tinyDB.putStringMethod(Config.SOCIAL_LOGIN_ID,String.valueOf(jsonObjectUser.get("id").getAsInt()));
                                        JsonArray jsonArrayRole = jsonObjectUser.get("roles").getAsJsonArray();
                                        for (int i=0; i<jsonArrayRole.size(); i++){
                                            Gson gson = new Gson();
                                            shopList.add(gson.fromJson(jsonArrayRole.get(i),Shop.class));

                                        }
                                        tinyDB.putStringMethod(Config.SALE_MAN,shopList.get(0).getName());
                                        tinyDB.putStringMethod(Config.STORE_TOKEN,jsonObject.get("token").getAsString());
                                        Log.e("Login activity","token is ___________\t"+jsonObject.get("token").getAsString());
                                       // tinyDB.putStringMethod(Config.STORE_IMAGE_URL,imageURL);
                                        tinyDB.putStringMethod(Config.STORE_NAME,jsonObjectUser.get("full_name").getAsString());
                                        tinyDB.putStringMethod(Config.STORE_EMAIL,username.getText().toString());
                                        tinyDB.putStringMethod(Config.STORE_CREATE_AT,jsonObjectUser.get("created_at").getAsString());
                                        tinyDB.putStringMethod(Config.STORE_UPDATE_AT,jsonObjectUser.get("updated_at").getAsString());
                                        if (checkBox.isChecked()) {
                                            tinyDB.putBoolean(Config.LOG_IN, true);
                                        }
                                       startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                       finish();
                                    }
                                    zgToast.show();

                                   /* if (checkBox.isChecked()) {
                                        tinyDB.putBoolean(Config.LOG_IN, true);
                                    }*/
                                    // startActivity(intent);
                                    //finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                }
                            });
                }else {
                    ZgToast zgToast = new ZgToast(LoginActivity.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }

            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                finish();
            }
        });

        signInButtonWhitgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,REQ_CODE);
            }
        });
        googleTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetService.isInternetAvailable(LoginActivity.this)) {
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, REQ_CODE);
                }else {
                    ZgToast zgToast = new ZgToast(LoginActivity.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }
            }
        });



    }
    public void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();

            tinyDB.putStringMethod(Config.STORE_NAME,account.getDisplayName());
            tinyDB.putStringMethod(Config.STORE_EMAIL,account.getEmail());

            mAccountName = account.getEmail();
            if (account.getPhotoUrl() == null){
                Log.e("LogActivity","url is null");
            }
            else{
                Log.e("LogActivity","url is not null"+account.getPhotoUrl());
                tinyDB.putStringMethod(Config.STORE_IMAGE_URL,account.getPhotoUrl().toString());
            }
            Log.e("TestingActivity : ","name\t_________"+account.getDisplayName());
            Log.e("TestingActivity : ","email\t_________"+account.getEmail());
            Log.e("TestingActivity : ","name\t_________"+account.getPhotoUrl());
        }

    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {

                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
                syncPostService.logInSocial("google", token, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        token_from_api = jsonObject.get("token").getAsString();
                        tinyDB.putStringMethod(Config.STORE_TOKEN,token_from_api);

                        JsonObject jsonObjectUser = jsonObject.get("user").getAsJsonObject();
                        tinyDB.putStringMethod(Config.SOCIAL_LOGIN_ID,String.valueOf(jsonObjectUser.get("id").getAsInt()));
                        name = jsonObjectUser.get("full_name").getAsString();
                        email = jsonObjectUser.get("email").getAsString();

                        JsonArray jsonArrayProvider = jsonObjectUser.get("providers").getAsJsonArray();
                        for (int i=0; i<jsonArrayProvider.size(); i++){
                            Gson gson = new Gson();
                            list.add(gson.fromJson(jsonArrayProvider.get(i),Review.class));

                            imageURL = list.get(0).getAvatar();
                            create_at = list.get(0).getCreatedAt();
                            update_at = list.get(0).getUpdatedAt();
                            Log.e("Create at ",list.get(0).getCreatedAt());
                            Log.e("Update at ",list.get(0).getUpdatedAt());

                            tinyDB.putStringMethod(Config.STORE_TOKEN,token_from_api);
                            tinyDB.putStringMethod(Config.STORE_IMAGE_URL,imageURL);
                            tinyDB.putStringMethod(Config.STORE_NAME,name);
                            tinyDB.putStringMethod(Config.STORE_EMAIL,email);
                            tinyDB.putStringMethod(Config.STORE_CREATE_AT,create_at);
                            tinyDB.putStringMethod(Config.STORE_UPDATE_AT,update_at);
                            tinyDB.putBoolean(Config.LOG_IN,true);

                            // Log.e("LoginActivity new ","_____"+list.get(0).getCreatedAt());
                            // Log.e("LoginActivity new ","_____"+list.get(0).getUpdatedAt());
                        }
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                Log.e("DoInBackground","__________"+token);



            } catch (IOException e) {

            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e("TAG", e.getMessage());
            }
            return token;
        }
    }
    public void onClickFacebookButton(View view) {
        if (view == btnFaceBook) {
            login_button.performClick();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, ""+getResources().getString(R.string.no_internet),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
            new RetrieveTokenTask().execute(mAccountName);

        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
      //  Config.tracker.setScreenName(Config.APP_NANE+"\tLogin Activity");
      //  Config.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    private void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.rebook.automart", PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("keyHash ", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    private boolean checkEmail(String email) {
        return RegisterActivity.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    public void getHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("Base64 new", Base64.encodeToString(md.digest(),Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
    }
}
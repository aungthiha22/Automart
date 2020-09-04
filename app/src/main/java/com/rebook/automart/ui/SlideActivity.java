package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.rebook.automart.adapter.ViewPagerAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.rebook.automart.model.Review;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Dell on 1/2/2019.
 */
public class SlideActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.start_google_login)SignInButton signInButtonWhitgoogle;
    @BindView(R.id.start_google)Button googleLogin;
    @BindView(R.id.start_fb)Button btnFaceBook;

    Button signUp;
    Button signIn;
    GoogleApiClient googleApiClient;
    LoginButton startFacebook;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    public static final int REQ_CODE = 9001;
    ViewPager viewpager;
    PagerAdapter adapter;
    int[] img;
    private static int currentPage = 0;
    private static int NUM_PAGES = 5;
    TinyDB tinyDB ;
    OkHttpClient okHttpClient;
    SyncPostService syncPostService;
    CallbackManager callbackManager;
    String mAccountName,token_from_api;
    String name, email,imageURL,create_at,update_at;
    final List<Review> list = new ArrayList<>();
    ProgressDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_activity);
        ButterKnife.bind(this);
        okHttpClient = new OkHttpClient();

        dialog = new ProgressDialog(SlideActivity.this);
        dialog.setMessage(getResources().getString(R.string.waiting));
        startFacebook        = (LoginButton) findViewById(R.id.start_facebook_login);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        tinyDB = new TinyDB(SlideActivity.this);

        String serverClientId = getString(R.string.client_id);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SlideActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        img = new int[]{R.drawable.car_1,
                R.drawable.car_2,
                R.drawable.car_1, R.drawable.car_2};

        viewpager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(SlideActivity.this, img);
        viewpager.setAdapter(adapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewpager);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // Toast.makeText(getApplicationContext(), "context changed", Toast.LENGTH_SHORT).show();
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int pageCount = img.length;
                    if (currentPage == 0) {
                        viewpager.setCurrentItem(pageCount - 1, false);
                    } else if (currentPage == pageCount - 1) {
                        viewpager.setCurrentItem(0, false);
                    }
                }
            }
        });
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewpager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);
       /* connectFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SlideActivity.this, "connect facebook", Toast.LENGTH_SHORT).show();
            }
        });*/
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
        startFacebook.setReadPermissions(Arrays.asList("public_profile","email"));
        btnFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetService.isInternetAvailable(SlideActivity.this)){
                    onClickStartFacebookButton(v);

                }else {
                    ZgToast zgToast = new ZgToast(SlideActivity.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }
            }
        });
        final List<Review> list = new ArrayList<>();
        startFacebook.registerCallback(callbackManager,
                new FacebookCallback< LoginResult >() {
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
                               // Toast.makeText(SlideActivity.this, "list is \t"+jsonArrayProvider.size(), Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity new ","_____"+token_from_api);

                                tinyDB.putStringMethod(Config.STORE_TOKEN,token_from_api);
                                tinyDB.putStringMethod(Config.STORE_IMAGE_URL,imageURL);
                                tinyDB.putStringMethod(Config.STORE_NAME,name);
                                tinyDB.putStringMethod(Config.STORE_EMAIL,email);
                                tinyDB.putStringMethod(Config.STORE_CREATE_AT,create_at);
                                tinyDB.putStringMethod(Config.STORE_UPDATE_AT,update_at);

                                tinyDB.putBoolean(Config.LOG_IN,true);

                                startActivity(new Intent(SlideActivity.this,MainActivity.class));
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


        signIn = findViewById(R.id.sing_in);
        signUp = findViewById(R.id.sign_up);
        
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(SlideActivity.this, "this is sign in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SlideActivity.this,LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SlideActivity.this, "this is sign up", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SlideActivity.this,RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetService.isInternetAvailable(SlideActivity.this)) {

                    dialog.show();
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, REQ_CODE);
                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    },5000);*/
                }else {
                    ZgToast zgToast = new ZgToast(SlideActivity.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }
            }
        });
    }
    public void onClickStartFacebookButton(View view) {
        if (view == btnFaceBook) {
            startFacebook.performClick();
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
            dialog.dismiss();

        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

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

                        startActivity(new Intent(SlideActivity.this,MainActivity.class));
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
            }catch (RuntimeException e){
                Log.e("TAG", e.getMessage());
            }

            return token;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
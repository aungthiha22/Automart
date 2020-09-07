package com.rebook.automart.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.rebook.automart.R;
import com.rebook.automart.listener.EndlessRecyclerViewScrollListener;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.BadgeUtil;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.OkHttpClient;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    DrawerLayout notificationCount1;
    @BindView(R.id.toolbar)Toolbar toolbar;
    ArrayList<String> nameList ;
    GoogleApiClient googleApiClient;
    OkHttpClient okHttpClient;
    SyncPostService syncPostService;
    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase database;
    boolean doubleBackToExitPressedOnce = false;
    private static final String EMAIL = "email";
    TinyDB tinyDB ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle(getResources().getString(R.string.app_name));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment(),"item").commit();
                    return true;

                case R.id.navigation_shopping_card:
                    toolbar.setTitle("Cart");
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        if (fragment != null && fragment.getTag() != null && !(fragment.getTag()
                                .equals("card"))) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    ShoppingCardFragment.getInstance("cart"),"cart").commit();
                        }
                    }
                    return true;

                case R.id.navigation_categories:
                    toolbar.setTitle(getResources().getString(R.string.our_top_brand));
                    if(EndlessRecyclerViewScrollListener.previousTotalItemCount!=0){
                        EndlessRecyclerViewScrollListener.previousTotalItemCount=0;
                        EndlessRecyclerViewScrollListener.currentPage=0;
                        EndlessRecyclerViewScrollListener.loading=false;
                    }
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        if (fragment != null && fragment.getTag() != null && !(fragment.getTag()
                                .equals("category"))) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    ItemListFragment.getInstance("category"),"category").commit();
                        }
                    }
                    return true;
               case R.id.navigation_search:
                    startActivity(new Intent(MainActivity.this,ActivitySearch.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;

                case R.id.navigation_profile:

                    toolbar.setTitle("Profile");
                    if(EndlessRecyclerViewScrollListener.previousTotalItemCount!=0){
                        EndlessRecyclerViewScrollListener.previousTotalItemCount=0;
                        EndlessRecyclerViewScrollListener.currentPage=0;
                        EndlessRecyclerViewScrollListener.loading=false;
                    }
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        if (fragment != null && fragment.getTag() != null && !(fragment.getTag()
                                .equals("profile"))) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    ProfileFragment.getInstance("profile"),"profile").commit();
                        }
                    }

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        notificationCount1 = findViewById(R.id.drawer_layout);

        tinyDB = new TinyDB(MainActivity.this);
        toolbar.setTitle("AutoMart");
        database= SQLiteDatabase.openDatabase(path,null, SQLiteDatabase.CREATE_IF_NECESSARY);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(getString(R.string.client_id))
                .requestServerAuthCode(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MainActivity.this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        //printKeyHash();
        //db.execSQL("drop table if exists vehicle");

        database.execSQL("create table if not exists product(id text ," +
                "name text," +
                "imageUrl text," +
                "type integer," +
                "price integer," +
                "addPrice integer," +
                "engine text," +
                "description text," +
                "promotion integer," +
                "rating integer," +
                "review text," +
                "sku text," +
                "checkBox text," +
                "orderQuantity integer," +
                "quantity integer)");


      //  printKeyHash();
        setSupportActionBar(toolbar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        Cursor cursor = database.rawQuery("select * from product where type = 0",null);

        if (cursor.getCount() != 0) {
            BadgeUtil.Companion.addBadge(1, navigation, this, cursor.getCount() + "");
        }

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
               // item.setShiftingMode(false);
                // item.setPadding(0, 15, 0, 0);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment(),"item").commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        ZgToast zgToast = new ZgToast(MainActivity.this);
        zgToast.setZgText(getString(R.string.to_exit));
        zgToast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(),"item").commit();

        } else if (id == R.id.menu_new_arrival) {
            toolbar.setTitle("New Arrival");
            for (Fragment fragment : getSupportFragmentManager().getFragments()){
                if (fragment !=null && fragment.getTag() != null && !(fragment.getTag().equals("new_arrival"))){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            GeneralListFragment.getInstance("new_arrival")).commit();
                }
            }
        }else if (id == R.id.menu_promotion){
            startActivity(new Intent(MainActivity.this, PromotionActivity.class));
            overridePendingTransition(0,0);
            finish();

        }
        else if (id == R.id.menu_about_us) {
            startActivity(new Intent(MainActivity.this,AboutAsActivity.class));
            overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
            finish();

        } else if (id == R.id.menu_brand) {

        } else if (id == R.id.menu_logout) {
            tinyDB.clear();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    ZgToast zgToast = new ZgToast(MainActivity.this);
                    zgToast.setZgText("your account is logout");
                    zgToast.show();

                }
            });

            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            this.finish();

        } else if (id == R.id.menu_blog) {
            startActivity(new Intent(MainActivity.this,OurPartners.class));
            overridePendingTransition(0,0);

        }else if (id == R.id.menu_contact_us){
            startActivity(new Intent(MainActivity.this,ContactUs.class));
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void printKeyHash(){
       try {
           PackageInfo info = getPackageManager().getPackageInfo("com.rebook.automart",PackageManager.GET_SIGNATURES);
           for (Signature signature:info.signatures){
               MessageDigest md = MessageDigest.getInstance("SHA");
               md.update(signature.toByteArray());
               Log.e("keyHash ", Base64.encodeToString(md.digest(),Base64.DEFAULT));
           }
       }catch (PackageManager.NameNotFoundException e){

       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.setting:
                return true;
            case R.id.search:
                startActivity(new Intent(MainActivity.this,ActivitySearch.class));
                this.overridePendingTransition(0,0);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(true);//
        MenuItem setting = menu.findItem(R.id.setting);
        setting.setVisible(false);
        return true;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

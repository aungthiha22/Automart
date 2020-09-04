package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.NetService;
import com.rebook.automart.util.TinyDB;
import com.rebook.automart.widget.ZgToast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by ATHK on 1/9/2019.
 */

public class ActivitySearch extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.list)ListView myList;
    AutoCompleteTextView autoCompleteTextView;
    public static final String SEARCH_TEXT = "search_text";
    OkHttpClient okHttpClient ;
    SyncPostService syncPostService;
    ArrayAdapter<String> adapter;

    String path = "data/data/com.rebook.automart/database";
    SQLiteDatabase db;
    private ArrayList<String> searchNames = new ArrayList<>();
    // searchName Main Array list
    private ArrayList<String> partialNames = new ArrayList<String>();
    // partialNnames is Search Array lists
   // private AppCompatAutoCompleteTextView autoTextView;
    ArrayList<Product> list = new ArrayList<>();
    String titleChoice , year,brand,model,engine,chassis_code;
    ProgressDialog progressDialog ;
    TinyDB tinyDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);


        progressDialog = new ProgressDialog(ActivitySearch.this);
        progressDialog.setMessage(getResources().getString(R.string.waiting));
        progressDialog.show();
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },1000);*/


        okHttpClient = new OkHttpClient();
        tinyDB = new TinyDB(ActivitySearch.this);
       /* vehicle_year = tinyDB.getString("vehicle_year");
        vehicle_brand = tinyDB.getString("vehicle_brand");
        vehicle_model = tinyDB.getString("vehicle_model");
        vehicle_engine = tinyDB.getString("vehicle_engine");
        vehicle_chassis_code = tinyDB.getString("vehicle_chassis");*/
       // year = getResources().getStringArray(R.array.year);
        db = SQLiteDatabase.openDatabase(path , null , SQLiteDatabase.CREATE_IF_NECESSARY);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  mToolbar.findViewById(R.id.toolbar_title);
        //EditText titleEditText = mToolbar.findViewById(R.id.toolbar_editText);
        autoCompleteTextView = mToolbar.findViewById(R.id.toolbar_auto_editText);
        titleTextView.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.VISIBLE);
        autoCompleteTextView.setText("");

        titleTextView.setText("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);
        String vehicle_year,vehicle_brand,vehicle_engine,vehicle_model,vehicle_chassis_code,vehicle_name;
        vehicle_year = tinyDB.getString("vehicle_year");
        vehicle_brand = tinyDB.getString("vehicle_brand");
        vehicle_engine = tinyDB.getString("vehicle_engine");
        vehicle_model = tinyDB.getString("vehicle_model");
        vehicle_chassis_code = tinyDB.getString("vehicle_chassis");
        vehicle_name = vehicle_year+vehicle_brand+" "+vehicle_model+" "+vehicle_engine+" "+vehicle_chassis_code;

        if (vehicle_name.length()<=5){
            //Toast.makeText(this, "Save Your vehicle first", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ZgToast zgToast = new ZgToast(ActivitySearch.this);
                    zgToast.setZgText(getResources().getString(R.string.save_vehicle));
                    zgToast.setError();
                    zgToast.show();
                    Intent intent = new Intent(ActivitySearch.this,SaveVehicle.class);
                    intent.putExtra("search","yes");
                    startActivity(intent);
                    finish();
                }
            },1000);
            return;
        }

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


        if (NetService.isInternetAvailable(ActivitySearch.this)) {
            syncPostService.getSearchList(1, new Callback<JsonArray>() {
                @Override
                public void success(JsonArray jsonElements, Response response) {
                    if (jsonElements != null) {
                        if (jsonElements.size() > 0) {
                            for (int i = 0; i < jsonElements.size(); i++) {
                                Gson gson = new Gson();
                                list.add(gson.fromJson(jsonElements.get(i), Product.class));
                                searchNames.add(list.get(i).getName());
                               // Log.e("Search is ","name is ________\t"+searchNames.get(i).toString());
                            }
                            progressDialog.dismiss();

                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                   // Toast.makeText(ActivitySearch.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            ZgToast zgToast = new ZgToast(ActivitySearch.this);
            zgToast.setError();
            zgToast.setZgText(getResources().getString(R.string.no_internet));
            zgToast.show();
        }
      /*  adapter = new ArrayAdapter<String>(this,
               android.R.layout.select_dialog_item,searchNames);
       autoCompleteTextView.setDropDownBackgroundResource(R.color.white);
       autoCompleteTextView.setThreshold(1); //will start working from first character
       autoCompleteTextView.setAdapter(adapter);*/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, partialNames);
        myList.setAdapter(adapter);
        //autoCompleteTextView.setText("Tom");

        AlterAdapter();
        //adapter.getFilter().filter("search text");
        myList.setOnItemClickListener(onItemClickListener);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (NetService.isInternetAvailable(ActivitySearch.this)) {
                    AlterAdapter();
                }else {
                    ZgToast zgToast = new ZgToast(ActivitySearch.this);
                    zgToast.setError();
                    zgToast.setZgText(getResources().getString(R.string.no_internet));
                    zgToast.show();
                }
            }

            // Not used for this program
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            // Not uses for this program
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });
        /*searchTesting.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(TestingActivity.this, "search Testing" +searchTesting.getText().toString(), Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
*/
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    titleChoice = autoCompleteTextView.getText().toString();
                    Intent intent = new Intent(ActivitySearch.this,FitterListView.class);
                    intent.putExtra(SEARCH_TEXT,titleChoice);
                    overridePendingTransition(0,0);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
       autoCompleteTextView.setOnItemClickListener(onItemClickListener);



    }

    private void AlterAdapter() {
        if (autoCompleteTextView.getText().toString().isEmpty()) {
            partialNames.clear();
            adapter.notifyDataSetChanged();
        }
        else {
            partialNames.clear();
            for (int i = 0; i < searchNames.size(); i++) {
                if (searchNames.get(i).toString().toUpperCase().contains(autoCompleteTextView.getText().toString().toUpperCase())) {
                    partialNames.add(searchNames.get(i).toString());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(ActivitySearch.this,""+ adapterView.getItemAtPosition(i),Toast.LENGTH_SHORT).show();
                   // Log.e(" title is ", (String) adapterView.getItemAtPosition(i));
                    titleChoice = (String) adapterView.getItemAtPosition(i);

                    //cacheList.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(ActivitySearch.this,FitterListView.class);
                    intent.putExtra(SEARCH_TEXT,titleChoice);
                    overridePendingTransition(0,0);
                    startActivity(intent);
                    finish();
                }
            };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivitySearch.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);//
        search.setIcon(R.mipmap.car_1);
       /* MenuItem setting = menu.findItem(R.id.setting);
        setting.setVisible(false);*/
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivitySearch.this,MainActivity.class));
        finish();
    }


}
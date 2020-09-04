package com.rebook.automart.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.widget.ZgToast;
import com.squareup.okhttp.OkHttpClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Dell on 3/28/2019.
 */

public class ContactUs extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.address_textView)TextView address_tv;
    @BindView(R.id.contact_name)EditText contactName;
    @BindView(R.id.contact_email)EditText contactEmail;
    @BindView(R.id.contact_ph)EditText contactPhone;
    @BindView(R.id.contact_message)EditText contactMessage;
    @BindView(R.id.contact_submit)Button submit;
    TextView toolBar_tv;
    String contact_name,contact_email,contact_ph,contact_message;
    OkHttpClient okHttpClient = new OkHttpClient();
    SyncPostService syncPostService;

    ProgressDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
        ButterKnife.bind(this);

        okHttpClient = new OkHttpClient();
        dialog = new ProgressDialog(ContactUs.this);
        dialog.setMessage("Please wait contacting....");

        setSupportActionBar(mToolbar);
        toolBar_tv = mToolbar.findViewById(R.id.toolbar_title);
        toolBar_tv.setText("Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.MAIN_URL+Config.API)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setClient(new OkClient(okHttpClient))
                .build();
        syncPostService = restAdapter.create(SyncPostService.class);


        address_tv.setText(Html.fromHtml(getResources().getString(R.string.address_auto)));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact_name = contactName.getText().toString();
                contact_email = contactEmail.getText().toString();
                contact_ph = contactPhone.getText().toString();
                contact_message = contactMessage.getText().toString();

                if (contact_name.length() < 1) {
                    contactName.setError("Enter your name");
                    contactName.requestFocus();
                    return;
                }
                if (contact_email.length() < 1) {
                    contactEmail.setError("Enter your email");
                    contactEmail.requestFocus();
                    return;
                }else if (!checkEmail(contact_email)){
                    contactEmail.setError("Your Email Format Incorrect");
                    contactEmail.requestFocus();
                    return;
                }

                if (contactPhone.length() < 1) {
                    contactPhone.setError("Enter your phone number");
                    contactPhone.requestFocus();
                    return;
                }
                if (contactMessage.length() < 1) {
                    contactMessage.setError("Enter your message");
                    contactMessage.requestFocus();
                    return;
                }
              //  Toast.makeText(ContactUs.this, "Your Contact OK", Toast.LENGTH_SHORT).show();
                Log.e("Contact", "name___________" + contact_name);
                Log.e("Contact", "email__________" + contact_email);
                Log.e("Contact", "ph_____________" + contact_ph);
                Log.e("Contact", "message________" + contact_message);
                dialog.show();
                syncPostService.getContact(contact_name, contact_email, contact_ph, contact_message,
                        new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                dialog.dismiss();
                                ZgToast zgToast = new ZgToast(ContactUs.this);
                                zgToast.setZgText("Your contact is OK");
                                zgToast.show();
                                startActivity(new Intent(ContactUs.this,MainActivity.class));
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(ContactUs.this, "error is "+
                                        error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ContactUs.this,MainActivity.class));
                finish();
        }

        return true;
    }
    private boolean checkEmail(String email) {
        return RegisterActivity.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContactUs.this,MainActivity.class));
        overridePendingTransition(0,0);
        finish();
    }
}

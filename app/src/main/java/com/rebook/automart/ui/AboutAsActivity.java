package com.rebook.automart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.rebook.automart.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rebook.automart.widget.ZawgyiTextView;
import org.xml.sax.XMLReader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ATHK on 1/14/2019.
 */

public class AboutAsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.myanmar_text)ZawgyiTextView txtMyanmar;
    @BindView(R.id.about_us_1)TextView aboutUs1;
    @BindView(R.id.about_us_2)TextView aboutUs2;
    @BindView(R.id.about_us_3)TextView aboutUs3;
    SupportMapFragment supportMapFragment;
    GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        ButterKnife.bind(this);

        txtMyanmar.setText(Html.fromHtml(getResources().getString(R.string.text_myanmar)));
        aboutUs1.setText(Html.fromHtml(getResources().getString(R.string.about_us_1),null,new UlTagHandler()));
        aboutUs2.setText(Html.fromHtml(getResources().getString(R.string.about_us_2),null,new UlTagHandler()));
        aboutUs3.setText(Html.fromHtml(getResources().getString(R.string.about_us_3)));
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getView().setVisibility(View.VISIBLE);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                LatLng latLng = new LatLng(Double.parseDouble("16.8661"), Double.parseDouble("96.195099"));
               // LatLng latLng = new LatLng(Double.parseDouble("18.1879"), Double.parseDouble("95.5288"));
                Log.e("google map is : ","____________"+ String.valueOf(latLng));
                map.addMarker(new MarkerOptions().position(latLng).title("shop")).setDraggable(true);
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                UiSettings uisetting = map.getUiSettings();
                uisetting.setZoomControlsEnabled(true);
                uisetting.setCompassEnabled(true);
                uisetting.setRotateGesturesEnabled(true);
                uisetting.setScrollGesturesEnabled(true);
                uisetting.setZoomGesturesEnabled(true);
                uisetting.setTiltGesturesEnabled(true);
            }
        });

        //return  view;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  mToolbar.findViewById(R.id.toolbar_title);

        titleTextView.setText("About As");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AboutAsActivity.this, MainActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
                finish();
                return true;
            case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static class UlTagHandler implements Html.TagHandler{
        boolean isOL;
        int a=1;
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if(tag.equals("ul") && !opening) output.append("\n");
            if(tag.equals("ol") && !opening) {
                output.append("\n");
                a=1;
            }
            if(tag.equals("ol") && opening){
                isOL=true;
            }
            if(isOL){
                if(tag.equals("li") && opening) output.append("\n\t"+ a++ +".\t");
            }
            else {
                if(tag.equals("li") && opening) output.append("\n\t•\t");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AboutAsActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
        finish();
    }
}

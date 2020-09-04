package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.ui.SliderWebActivity;
import com.rebook.automart.util.Utils;
import com.rebook.automart.widget.ZgToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TestingSlidePagerAdapter extends PagerAdapter {

    ArrayList<Product> sliderList = new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    //int position = 3;

    public TestingSlidePagerAdapter(Activity mainActivity, ArrayList<Product> sliderList) {
        this.context = mainActivity;
       // this.img = img;
        this.sliderList = sliderList;
    }

    @Override
    public int getCount() {
        return sliderList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((RelativeLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView image;
        TextView slideName;
        TextView slideDesciption;
       // Button btnBuyNow;
        RippleView rippleView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.slider_item, container, false);
        image = (ImageView) itemview.findViewById(R.id.slider_item_image);
        slideName = itemview.findViewById(R.id.slider_item_name);
        slideDesciption = itemview.findViewById(R.id.slider_item_description);
        rippleView = itemview.findViewById(R.id.slider_item_buy_now);
        //btnBuyNow = itemview.findViewById(R.id.buy_now);
        //image.setImageResource(img[position]);
        slideName.setText(sliderList.get(position).getName());
        slideDesciption.setText(sliderList.get(position).getDescription());
        Picasso.with(context).load(Config.MAIN_URL+"storage/"+sliderList.get(position).getImage())
                .resize(Utils.getPx(context,400),Utils.getPx(context,230)).into((image));
        //Log.e("get image : ","______"+sliderList.get(position).getImage1());
        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SliderWebActivity.class);
                intent.putExtra("web_name",sliderList.get(position).getName());
                intent.putExtra("web_url",sliderList.get(position).getPruductUrl());
                context.startActivity(intent);

            }
        });



        //add item.xml to viewpager
        ((ViewPager) container).addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public float getPageWidth(int position) {
        return 1f;   //it is used for set page width of view pager
    }
}

package com.rebook.automart.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rebook.automart.R;


public class ViewPagerAdapter extends PagerAdapter {

    int[] img;
    LayoutInflater inflater;
    Context context;
    //int position = 3;

    public ViewPagerAdapter(Activity mainActivity, int[] img) {
        this.context = mainActivity;
        this.img = img;
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((RelativeLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imgae;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.item, container, false);
        imgae = (ImageView) itemview.findViewById(R.id.item_image);
        imgae.setImageResource(img[position]);

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

package com.rebook.automart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.model.Product;
import com.rebook.automart.ui.SliderWebActivity;
import com.rebook.automart.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeSlidePagerAdapter extends PagerAdapter {

   // private ArrayList<Integer> images;
    public LayoutInflater inflater;
    public Context context;
    ArrayList<Product> pList = new ArrayList<>();

    public HomeSlidePagerAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.pList=list;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        inflater = LayoutInflater.from(context);
        View viewGroup = inflater.inflate(R.layout.slider_item, view, false);
        ImageView myImage = viewGroup.findViewById(R.id.slider_item_image);
        RippleView rippleView = viewGroup.findViewById(R.id.slider_item_buy_now);
        TextView txtname = viewGroup.findViewById(R.id.slider_item_name);
        TextView txtDescription = viewGroup.findViewById(R.id.slider_item_description);

        txtname.setText(pList.get(position).getName());
        txtDescription.setText(pList.get(position).getDescription());
        //myImage.setImageResource(pList.get(position).getImage());
        Picasso.with(context).load(Config.MAIN_URL+"storage/"+pList.get(position).getImage())
                .resize(Utils.getPx(context,400),Utils.getPx(context,230)).into((myImage));

        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SliderWebActivity.class);
                intent.putExtra("web_name",pList.get(position).getName());
                intent.putExtra("web_url",pList.get(position).getPruductUrl());
                context.startActivity(intent);

            }
        });
        view.addView(viewGroup, 0);
        return viewGroup;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
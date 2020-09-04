package com.rebook.automart.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by YDN on 7/3/2017.
 */
public class ZawgyiTextView extends TextView {
    public ZawgyiTextView(Context context) {
        super(context);
        setStyle(context);
    }

    public ZawgyiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStyle(context);
    }

    public ZawgyiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStyle(context);
    }
    public void setStyle(Context context){
        setTypeface(Typeface.createFromAsset(context.getAssets(),"zawgyi.ttf"));
    }
}

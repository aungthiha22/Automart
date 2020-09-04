package com.rebook.automart.widget;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.rebook.automart.R;

/**
 * Created by YDN on 7/5/2017.
 */
public class ZgToast extends Toast {
  TextView textView;
    Context context;
    int px;
    public ZgToast(Context context) {
        super(context);
        this.context=context;
      textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        setDuration(Toast.LENGTH_SHORT);
       // TextView.setBackgroundColor(context.getResources().getColor(R.color.swipe_refresh_color3));
        textView.setTextColor(context.getResources().getColor(R.color.white));
        textView.setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.toast_background
        ));
        px= 25;
        textView.setPadding(px,px,px,px);
        setView(textView);
    }
    public void setZgText(String zgText) {
        textView.setText(zgText);
    }

    public void setError(){
        textView.setTextColor(context.getResources().getColor(R.color.red));
        textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.error_toast_background));
        textView.setPadding(px,px,px,px);
    }
}

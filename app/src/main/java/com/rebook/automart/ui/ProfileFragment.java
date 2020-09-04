package com.rebook.automart.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.andexert.library.RippleView;
import com.rebook.automart.Config;
import com.rebook.automart.R;
import com.rebook.automart.sync.SyncPostService;
import com.rebook.automart.util.TinyDB;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Dell on 4/2/2019.
 */

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_image)ImageView profileImage;
    @BindView(R.id.profile_name)TextView txtProfileName;
    @BindView(R.id.profile_email)TextView txtProfileEmail;
    @BindView(R.id.card_pending)CardView pendingCardView;
    @BindView(R.id.card_payment)CardView paymentCardView;
    @BindView(R.id.card_cancel)CardView cancelCardView;

    String imageUrl;
    SyncPostService syncPostService;
    OkHttpClient okHttpClient;
    TinyDB tinyDB ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient=new OkHttpClient();
        tinyDB = new TinyDB(getActivity());

    }
    public static ProfileFragment getInstance(String type){
        ProfileFragment profileFragment=new ProfileFragment();
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        profileFragment.setArguments(bundle);
        return profileFragment;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_page, null, false);
        ButterKnife.bind(this, view);

        imageUrl = tinyDB.getString(Config.STORE_IMAGE_URL);

        //imageUrl = "https://graph.facebook.com/v3.0/2210309412566504/picture?type=normal";
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
        Log.e("Profile token ","token is "+tinyDB.getString(Config.STORE_TOKEN));

        txtProfileName.setText(tinyDB.getString(Config.STORE_NAME));
        txtProfileEmail.setText(tinyDB.getString(Config.STORE_EMAIL));

        final Intent intent = new Intent(getActivity(),UserOrderActivity.class);
        pendingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(UserOrderActivity.INTENT_VALUE,"0");
                startActivity(intent);
            }
        });
        paymentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(UserOrderActivity.INTENT_VALUE,"1");
                startActivity(intent);
            }
        });
        cancelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(UserOrderActivity.INTENT_VALUE,"2");
                startActivity(intent);
            }
        });


        try {
            new DownLoadImageTask(profileImage).execute(imageUrl);

        } catch (Exception e) {
            Log.e("--Fetch--Error-- photo", e.getMessage());
            e.printStackTrace();
            //set original app logo
            //navHeaderImg.setImageResource(R.drawable.ic_launcher);
        }
        return view;
    }

    private class DownLoadImageTask extends android.os.AsyncTask<String,Void,android.graphics.Bitmap>{
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }
        protected android.graphics.Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            android.graphics.Bitmap logo = null;
            try{
                java.io.InputStream is = new java.net.URL(urlOfImage).openStream();
                logo = android.graphics.BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(android.graphics.Bitmap bitmap){
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Log.e("--Fetch--photo success", "");

                RoundImage circularBitmap = new RoundImage(bitmap);
                imageView.setImageDrawable(circularBitmap);
            }
            else{
                Log.e("--No--profile photo", "");
                //imageView.setImageResource(R.drawable.ic_launcher);
            }
        }
    }
    private  class RoundImage extends Drawable {
        private final android.graphics.Bitmap mBitmap;
        private final android.graphics.Paint mPaint;
        private final android.graphics.RectF mRectF;
        private final int mBitmapWidth;
        private final int mBitmapHeight;

        public RoundImage(android.graphics.Bitmap bitmap) {
            mBitmap = bitmap;
            mRectF = new android.graphics.RectF();
            mPaint = new android.graphics.Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            final android.graphics.BitmapShader shader = new android.graphics.BitmapShader(bitmap, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();

            /*Mangkhek change image width and height to be the same*/
            if(w>h){mBitmapWidth=h;mBitmapHeight=h;}
            else {mBitmapWidth=w;mBitmapHeight =w;}
            /*Mangkhek*/
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {
            canvas.drawOval(mRectF, mPaint);
        }

        @Override
        protected void onBoundsChange(android.graphics.Rect bounds) {
            super.onBoundsChange(bounds);
            mRectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mPaint.getAlpha() != alpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }


        @Override
        public void setFilterBitmap(boolean filter) {
            mPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            mPaint.setDither(dither);
            invalidateSelf();
        }

    }
}

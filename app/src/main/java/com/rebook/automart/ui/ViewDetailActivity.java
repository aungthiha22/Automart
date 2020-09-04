package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rebook.automart.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dell on 3/19/2019.
 */

public class ViewDetailActivity extends AppCompatActivity {

    @BindView(R.id.view_title)TextView txtViewTitle;
    @BindView(R.id.desc_empty)TextView txtEmptyView;
    @BindView(R.id.toolbar)Toolbar toolbar;

    TextView toolbar_textView;

    String id ,intentDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        toolbar_textView = toolbar.findViewById(R.id.toolbar_title);
        toolbar_textView.setText(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        id = getIntent().getStringExtra("id");
        intentDesc = getIntent().getStringExtra("description");
        if (intentDesc.equals("no")){
            txtEmptyView.setVisibility(View.VISIBLE);
            txtViewTitle.setVisibility(View.GONE);

        }else {
            txtEmptyView.setVisibility(View.GONE);
            txtViewTitle.setText(Html.fromHtml(intentDesc));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

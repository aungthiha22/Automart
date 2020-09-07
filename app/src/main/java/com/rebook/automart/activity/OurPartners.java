package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import com.rebook.automart.R;
import org.xml.sax.XMLReader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OurPartners extends AppCompatActivity {

    @BindView(R.id.partner_description)TextView txtPartnerDesc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.our_partners);
        ButterKnife.bind(this);

        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        TextView titleTextView =  mToolbar.findViewById(R.id.toolbar_title);
        //EditText titleEditText = mToolbar.findViewById(R.id.toolbar_editText);
        autoCompleteTextView = mToolbar.findViewById(R.id.toolbar_auto_editText);
        titleTextView.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.VISIBLE);
        autoCompleteTextView.setText("");
        titleTextView.setText("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);*/
        setSupportActionBar(toolbar);
        TextView titleTextView = toolbar.findViewById(R.id.toolbar_title);
        titleTextView.setText(getString(R.string.our_partner));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_ab_back_mtrl_am_alpha);

        txtPartnerDesc.setText(Html.fromHtml(getResources().getString(R.string.partner_desc),null,new UlTagHandler()));
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
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 finish();
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

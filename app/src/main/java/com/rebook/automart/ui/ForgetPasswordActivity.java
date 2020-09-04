package com.rebook.automart.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rebook.automart.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Dell on 1/6/2019.
 */

public class ForgetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.reset_email)TextView resetEmail;
    @BindView(R.id.reset_password_button)Button btnResetPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);
        ButterKnife.bind(this);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetEmail.getText().toString().length() == 0 ){
                    resetEmail.setError("Enter Your Email");
                    resetEmail.requestFocus();
                    return;
                }else if (!checkEmail(resetEmail.getText().toString())){
                    resetEmail.setError("Your Email Format Incorrect");
                    resetEmail.requestFocus();
                    return;
                }

            }
        });
    }
    private boolean checkEmail(String email) {
        return RegisterActivity.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}

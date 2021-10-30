package com.gmijo.mytour;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
package com.dejure.fbclone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dejure.fbclone.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}
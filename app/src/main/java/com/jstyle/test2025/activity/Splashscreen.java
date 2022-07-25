package com.jstyle.test2025.activity;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.jstyle.test2025.R;
import com.jstyle.test2025.activity.ui.BottomNavigBaseActivity;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().hide();
        setContentView(R.layout.activity_splashscreen);
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
              Intent splashscreen = new Intent(Splashscreen.this,DeviceScanActivity.class);
              // Intent splashscreen = new Intent(Splashscreen.this, BottomNavigBaseActivity.class);
               startActivity(splashscreen);
           }
       },3000);
    }
}
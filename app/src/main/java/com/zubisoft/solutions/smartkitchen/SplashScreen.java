package com.zubisoft.solutions.smartkitchen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.zubisoft.solutions.smartkitchen.util.SmartKitchenPreference;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SmartKitchenPreference preference = SmartKitchenPreference.getInstance(getApplicationContext());
                if (preference.isFirstTimeUser()) {
                    startActivity(new Intent(getApplicationContext(), TourActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                finish();

            }
        }, 3000);
    }
}

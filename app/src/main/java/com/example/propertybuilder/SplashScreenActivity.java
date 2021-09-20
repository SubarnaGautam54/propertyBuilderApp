package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding binding;
    int SPLASH_SCREEN_ACTIVE_TIME = 3000;

    public  void adjustFontScale( Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn()) {

                    startActivity(new Intent(SplashScreenActivity.this, DashBoardUserActivity.class));
                }else {

                    Intent intent = new Intent(SplashScreenActivity.this,SignInActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },SPLASH_SCREEN_ACTIVE_TIME);
    }


}
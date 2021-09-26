package com.example.propertybuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityDashboardUserBinding;
import com.google.firebase.messaging.FirebaseMessaging;

public class DashBoardUserActivity extends AppCompatActivity {

    ActivityDashboardUserBinding binding;
    String userId;
    boolean doubleBackToExitPressedOnce = false;

    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    String type;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        type = usersModel.getTypeUser();
        userId = String.valueOf(usersModel.getId());

        FirebaseMessaging.getInstance().subscribeToTopic("user");
//        FirebaseMessaging.getInstance().subscribeToTopic("3");
        if (type.equals("developer") || type.equals("customer") || type.equals("admin")) {

            FirebaseMessaging.getInstance().subscribeToTopic(userId + "");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(userId + "");
        }

        setupToolBar();
        clicks();
    }

    private void clicks() {
        switch (type) {
            case "customer":
                binding.developerViewLy.setVisibility(View.GONE);
                binding.customerViewLy.setVisibility(View.VISIBLE);
                binding.adminViewLy.setVisibility(View.GONE);
                binding.cardHomeCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, HomeActivity.class));
                    }
                });
                binding.cardBookingCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, BookingActivity.class));
                    }
                });
                binding.cardProfileCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, ProfileActivity.class));

                    }
                });
                binding.cardContactCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, ContactActivity.class));
                    }
                });
                binding.cardSettingsCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, SettingActivity.class));
                    }
                });
                binding.cardLogoutCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("user" + "");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(userId + "");
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        Intent intent = new Intent(DashBoardUserActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case "developer":
                binding.developerViewLy.setVisibility(View.VISIBLE);
                binding.customerViewLy.setVisibility(View.GONE);
                binding.adminViewLy.setVisibility(View.GONE);

                binding.cardHomeDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, HomeActivity.class));
                    }
                });
                binding.cardInspectionDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, InspectionRequestActivity.class));
                    }
                });
                binding.cardProfileDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, ProfileActivity.class));
                    }
                });
                binding.cardSitesDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, TaskActivityActivity.class));
                    }
                });
                binding.cardSettingsDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, SettingActivity.class));
                    }
                });
                binding.cardLogoutDeveloper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("user" + "");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(userId + "");
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        Intent intent = new Intent(DashBoardUserActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case "admin":
                binding.developerViewLy.setVisibility(View.GONE);
                binding.customerViewLy.setVisibility(View.GONE);
                binding.adminViewLy.setVisibility(View.VISIBLE);

                binding.cardHomeAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, HomeActivity.class));
                    }
                });
                binding.cardInspectionAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, InspectionRequestActivity.class));
                    }
                });
                binding.cardTaskAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this,TaskActivityActivity.class));
                    }
                });
                binding.cardUserManagementAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DashBoardUserActivity.this, UserManagementActivity.class));
                    }
                });
                binding.cardLogoutAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("user" + "");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(userId + "");
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        Intent intent = new Intent(DashBoardUserActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

                break;
        }
    }

    private void setupToolBar() {
        binding.toolBar.setTitle("My DashBoard");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);
    }
}
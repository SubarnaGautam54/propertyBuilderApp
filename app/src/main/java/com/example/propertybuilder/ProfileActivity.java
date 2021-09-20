package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityProfileBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    int userId;
    String  userImage,userName, address, phoneNumber, email, type;

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
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.profileNameEdt.setEnabled(false);
        binding.profileAddressEdt.setEnabled(false);
        binding.profilePhoneNumberEdt.setEnabled(false);
        binding.profileEmailEdt.setEnabled(false);

        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userImage = usersModel.getUserImage();
        userId = usersModel.getId();
        userName = usersModel.getFullName();
        address = usersModel.getAddress();
        phoneNumber = usersModel.getPhoneNumber();
        email = usersModel.getEmail();
        type = usersModel.getTypeUser();
        if (type.equals("developer")){
            binding.editNameImg.setVisibility(View.GONE);
            binding.profileNameEdt.setEnabled(false);
            binding.editAddressImg.setVisibility(View.GONE);
            binding.profileAddressEdt.setEnabled(false);
            binding.editPhoneNumberImg.setVisibility(View.GONE);
            binding.profilePhoneNumberEdt.setEnabled(false);
            binding.editEmailImg.setVisibility(View.GONE);
            binding.profileEmailEdt.setEnabled(false);
        }

        binding.profileNameEdt.setText(userName);
        binding.profileAddressEdt.setText(address);
        binding.profilePhoneNumberEdt.setText(phoneNumber);
        binding.profileEmailEdt.setText(email);

        clicks();

        Glide.with(ProfileActivity.this)
                .load(Api.IMAGE_BASE_URL+userImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.profileImage);

    }

    private void clicks() {

        binding.closeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.editNameImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileNameEdt.setEnabled(true);
                binding.profileNameEdt.requestFocus();

            }
        });
        binding.editAddressImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileAddressEdt.setEnabled(true);
                binding.profileAddressEdt.requestFocus();

            }
        });
        binding.editPhoneNumberImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profilePhoneNumberEdt.setEnabled(true);
                binding.profilePhoneNumberEdt.requestFocus();

            }
        });
        binding.editEmailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileEmailEdt.setEnabled(true);
                binding.profileEmailEdt.requestFocus();

            }
        });
        binding.confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.confirmChangesBtn.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                String fullName = binding.profileNameEdt.getText().toString();
                String address = binding.profileAddressEdt.getText().toString();
                String phoneNumber = binding.profilePhoneNumberEdt.getText().toString();
                String email = binding.profileEmailEdt.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CUS_PROFILE_UPDATE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);

//                        Toast.makeText(ProfileActivity.this, "wow wow", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                UserModel userModel = new UserModel(userId,userImage,fullName,address,phoneNumber,email,type);
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);
                                finish();
                            }else {
//                                Toast.makeText(ProfileActivity.this, ""+jsonObject.getBoolean("message"), Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.confirmChangesBtn.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.progressBar.setVisibility(View.GONE);
                            binding.confirmChangesBtn.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onResponse: exception"+e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(ProfileActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("user_id", String.valueOf(userId));
                        params.put("full_name", fullName);
                        params.put("address", address);
                        params.put("phone_number", phoneNumber);
                        params.put("email", email);
                        return params;
                    }
                };
                MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
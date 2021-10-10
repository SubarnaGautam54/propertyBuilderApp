package com.example.propertybuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.databinding.ActivityForgotPasswordBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String TAG = "wow123";
    DataProccessor dataProccessor;

    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        dataProccessor = new DataProccessor(this);
        ////////////////////////// otp ////////////////////////

        binding.ccp.registerCarrierNumberEditText(binding.phoneNumber);

        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneNumber.getText().toString();
                if (phone.isEmpty()) {
                    binding.phoneNumber.requestFocus();
                    binding.phoneNumber.setError("Enter Phone Number");
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.sendOtpBtn.setVisibility(View.GONE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CHECK_PHONE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("status")) {
                                    DataProccessor.setStr("phoneNumber", phone);
                                    Intent intent = new Intent(ForgotPasswordActivity.this, OTPVerifyActivity.class);
                                    intent.putExtra("mobile", binding.ccp.getFullNumberWithPlus().replace(" ", ""));
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.sendOtpBtn.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("phone_number", phone);
                            return params;
                        }
                    };
                    MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(stringRequest);

                }
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));
                finish();

            }
        });
    }
//        clicks();

    //    private void clicks() {
//        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = binding.editTextTextEmailAddress.getText().toString();
//                if (email.isEmpty()){
//                    binding.editTextTextEmailAddress.requestFocus();
//                    binding.editTextTextEmailAddress.setError("Enter Your Email Address");
//                }else if (!email.matches(emailPattern)){
//                    binding.editTextTextEmailAddress.requestFocus();
//                    binding.editTextTextEmailAddress.setError("Enter Valid Email Address");
//                }else {
//                    binding.progressBar2.setVisibility(View.VISIBLE);
//                    binding.confirmButton.setVisibility(View.GONE);
//                    Random random = new Random();
//                    code = random.nextInt(8999)+1000;
//
//                    Log.d(TAG, "onClick: "+code);
//
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EMAIL_CONFIRM, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.d(TAG, "onResponse: "+response);
//                            DataProccessor.setInt("code",code);
//                            DataProccessor.setStr("emailFp",email);
//                            startActivity(new Intent(ForgotPasswordActivity.this,OTPVerifyActivity.class));
//                            finish();
//                            binding.progressBar2.setVisibility(View.GONE);
//                            binding.confirmButton.setVisibility(View.VISIBLE);
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d(TAG, "onResponse: "+error.getMessage());
//                            Toast.makeText(ForgotPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            DataProccessor.setInt("code",code);
//                            DataProccessor.setStr("emailFp",email);
//                            startActivity(new Intent(ForgotPasswordActivity.this,OTPVerifyActivity.class));
//                            finish();
//
//                            binding.progressBar2.setVisibility(View.GONE);
//                            binding.confirmButton.setVisibility(View.VISIBLE);
//                        }
//                    }){
//                        @Nullable
//                        @Override
//                        protected Map<String,String> getParams() throws AuthFailureError {
//                            Map<String,String> params = new HashMap<>();
//                            params.put("email",email);
//                            params.put("code", String.valueOf(code));
//                            return params;
//                        }
//                    };
//                    MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(stringRequest);
//                }
//                }
//        });


        @Override
        public void onBackPressed () {
            super.onBackPressed();
            startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
            finish();
        }
    }


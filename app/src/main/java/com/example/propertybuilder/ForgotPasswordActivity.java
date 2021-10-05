package com.example.propertybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.databinding.ActivityForgotPasswordBinding;

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

        clicks();
    }

    private void clicks() {
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextTextEmailAddress.getText().toString();
                if (email.isEmpty()){
                    binding.editTextTextEmailAddress.requestFocus();
                    binding.editTextTextEmailAddress.setError("Enter Your Email Address");
                }else if (!email.matches(emailPattern)){
                    binding.editTextTextEmailAddress.requestFocus();
                    binding.editTextTextEmailAddress.setError("Enter Valid Email Address");
                }else {
                    binding.progressBar2.setVisibility(View.VISIBLE);
                    binding.confirmButton.setVisibility(View.GONE);
                    Random random = new Random();
                    code = random.nextInt(8999)+1000;

                    Log.d(TAG, "onClick: "+code);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EMAIL_CONFIRM, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: "+response);
                            DataProccessor.setInt("code",code);
                            DataProccessor.setStr("emailFp",email);
                            startActivity(new Intent(ForgotPasswordActivity.this,OTPVerifyActivity.class));
                            finish();
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.confirmButton.setVisibility(View.VISIBLE);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onResponse: "+error.getMessage());
                            Toast.makeText(ForgotPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            DataProccessor.setInt("code",code);
                            DataProccessor.setStr("emailFp",email);
                            startActivity(new Intent(ForgotPasswordActivity.this,OTPVerifyActivity.class));
                            finish();

                            binding.progressBar2.setVisibility(View.GONE);
                            binding.confirmButton.setVisibility(View.VISIBLE);
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String,String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("email",email);
                            params.put("code", String.valueOf(code));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));
        finish();
    }
}
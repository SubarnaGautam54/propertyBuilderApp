package com.example.propertybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.example.propertybuilder.databinding.ActivityFnewPasswordBinding;

import java.util.HashMap;
import java.util.Map;

public class FNewPasswordActivity extends AppCompatActivity {

    ActivityFnewPasswordBinding binding;
    public static final String TAG = "wow123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFnewPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        DataProccessor dataProccessor = new DataProccessor(this);
        clicks();
    }

    private void clicks() {
        binding.confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = binding.newPassword.getText().toString();
                String newPasswordConfirm = binding.newPasswordConfirm.getText().toString();
                if (newPassword.isEmpty()){
                    binding.newPassword.requestFocus();
                    binding.newPassword.setError("Type In New Password");
                }else if (newPassword.length() < 6){
                    binding.newPassword.requestFocus();
                    binding.newPassword.setError("Password must be 6 or more characters long");
                }else if (!newPasswordConfirm.matches(newPassword)){
                    binding.newPasswordConfirm.requestFocus();
                    binding.newPasswordConfirm.setError("Confirm Password Does Not Match");
                }else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_PASSWORD, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: "+response);
                            startActivity(new Intent(FNewPasswordActivity.this,SignInActivity.class));
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String,String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("email",DataProccessor.getStr("emailFp"));
                            params.put("password", newPassword);
                            return params;
                        }
                    };
                    MySingleton.getInstance(FNewPasswordActivity.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FNewPasswordActivity.this,ForgotPasswordActivity.class));
        finish();
    }
}
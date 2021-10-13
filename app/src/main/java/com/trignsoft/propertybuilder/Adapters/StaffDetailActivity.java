package com.trignsoft.propertybuilder.Adapters;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityStaffDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class StaffDetailActivity extends AppCompatActivity {

    ActivityStaffDetailBinding binding;
    Intent intent;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String staffImage,staffType;
    int devId;

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
        binding = ActivityStaffDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = getIntent();
        toolBarSetup();
        setIntentValues();
        devId = Integer.parseInt(intent.getStringExtra("staffId"));
        staffImage = intent.getStringExtra("staffImage");
        staffType = intent.getStringExtra("staffType");

        binding.updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.staffFullNameEdt.getText().toString();
                String address = binding.staffAddressEdt.getText().toString();
                String phone = binding.phoneNumberEdt.getText().toString();
                String email = binding.staffEmailEdt.getText().toString();
                String password = binding.staffPasswordEdt.getText().toString();

                if (name.isEmpty()){
                    binding.staffFullNameEdt.requestFocus();
                    binding.staffFullNameEdt.setError("Enter Staff Name");
                }else if (address.isEmpty()){
                    binding.staffAddressEdt.requestFocus();
                    binding.staffAddressEdt.setError("Enter Staff Address");
                }else if (phone.isEmpty()){
                    binding.phoneNumberEdt.requestFocus();
                    binding.phoneNumberEdt.setError("Enter Staff Phone Number");
                }else if (phone.length() < 10){
                    binding.phoneNumberEdt.requestFocus();
                    binding.phoneNumberEdt.setError("Enter Valid Phone Number");
                }else if (email.isEmpty()){
                    binding.staffEmailEdt.requestFocus();
                    binding.staffEmailEdt.setError("Enter Staff Email");
                }else if (!email.matches(emailPattern)){
                    binding.staffEmailEdt.requestFocus();
                    binding.staffEmailEdt.setError("Invalid Email");
                }else if (password.isEmpty()){
                    binding.staffPasswordEdt.requestFocus();
                    binding.staffPasswordEdt.setError("Enter Staff Password");
                }else if (password.length() < 6){
                    binding.staffPasswordEdt.requestFocus();
                    binding.staffPasswordEdt.setError("Password Must Be Atleast 6 charaters long");
                }else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.updateDetails.setVisibility(View.GONE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CUS_PROFILE_UPDATE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: "+response);

//                        Toast.makeText(ProfileActivity.this, "wow wow", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    UserModel userModel = new UserModel(devId,staffImage,name,address,"xxxxxxxxx",email,staffType);
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);
                                    finish();
                                }else {
//                                Toast.makeText(ProfileActivity.this, ""+jsonObject.getBoolean("message"), Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.updateDetails.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.updateDetails.setVisibility(View.VISIBLE);
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
                            params.put("user_id", String.valueOf(devId));
                            params.put("full_name", name);
                            params.put("address", address);
                            params.put("phone_number", phone);
                            params.put("email", email);
                            return params;
                        }
                    };
                    MySingleton.getInstance(StaffDetailActivity.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    private void setIntentValues() {
        Glide.with(StaffDetailActivity.this)
                .load(Api.IMAGE_BASE_URL+intent.getStringExtra("staffImage"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.staffImage);
        binding.staffFullNameEdt.setText(intent.getStringExtra("staffName"));
        binding.staffAddressEdt.setText(intent.getStringExtra("staffAddress"));
        binding.phoneNumberEdt.setText(intent.getStringExtra("staffPhone"));
        binding.staffEmailEdt.setText(intent.getStringExtra("staffEmail"));
        binding.staffPasswordEdt.setText(intent.getStringExtra("staffPassword"));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Staff Details");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
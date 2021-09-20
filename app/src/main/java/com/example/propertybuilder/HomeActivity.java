package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.Adapters.UserAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserDataModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityHomeBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    int userId;
    String type;
    private LinearLayoutManager manager;
    UserAdapter userAdapter;
    private List<UserDataModel> userDataModelList;


    public void adjustFontScale(Configuration configuration) {
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
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        DataProccessor dataProccessor = new DataProccessor(this);
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = usersModel.getId();
//        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
        type = usersModel.getTypeUser();

        if (type.matches("customer")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addPostFeb.setVisibility(View.GONE);
            getDataCustomerApi();
            
        }else if (type.matches("developer")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addPostFeb.setVisibility(View.VISIBLE);
            getDataDeveloperApi();
        }else if (type.matches("admin")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addPostFeb.setVisibility(View.GONE);
            getDataCustomerApi();
        }
        manager = new LinearLayoutManager(this);
        clicks();
        toolBarSetup();
    }

    private void getDataCustomerApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_USER_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.progressBar.setVisibility(View.GONE);
//                Log.d("wow123", "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userDataModelList =  new ArrayList<>();
                    for (int i = 0;i < jsonObject.length()-2;i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");
                        Log.d("wow123", "onResponse: "+jsonObject1.getString("name"));

                        UserDataModel userDataModel = new UserDataModel();
                        userDataModel.setDeveloperPostName(jsonObject1.getString("name"));
                        userDataModel.setDeveloperPostDetail(jsonObject1.getString("description"));
                        userDataModel.setPostId(jsonObject1.getString("id"));
                        userDataModel.setLat(jsonObject1.getString("latitude"));
                        userDataModel.setLng(jsonObject1.getString("longitude"));


                        Log.d("wow123", "onResponse: "+jsonObject1.getString("id"));



                        userDataModelList.add(userDataModel);
                        setupRecyclerViewForPost();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    binding.progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
            }
        });
        MySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    private void getDataDeveloperApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_DEVELOPER_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("wow123", "onResponse: "+response);
                binding.progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userDataModelList =  new ArrayList<>();
                    for (int i = 0;i < jsonObject.length()-2;i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");
//                        Log.d("wow123", "onResponse: "+jsonObject1.getString("name"));
//                        Toast.makeText(HomeActivity.this, , Toast.LENGTH_SHORT).show();
                        UserDataModel userDataModel = new UserDataModel();
                        userDataModel.setDeveloperPostName(jsonObject1.getString("name"));
                        userDataModel.setDeveloperPostDetail(jsonObject1.getString("description"));
                        userDataModel.setPostId(jsonObject1.getString("id"));
                        userDataModel.setLat(jsonObject1.getString("latitude"));
                        userDataModel.setLng(jsonObject1.getString("longitude"));

                        userDataModelList.add(userDataModel);
                        setupRecyclerViewForPost();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    binding.progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
//                params.put("developer_id", String.valueOf(userId));
                params.put("developer_id", String.valueOf(userId));
                return params;
            }
        };
        MySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    private void setupRecyclerViewForPost() {
        binding.recyclerView.setLayoutManager(manager);
        userAdapter = new UserAdapter(this,userDataModelList);
        binding.recyclerView.setAdapter(userAdapter);
        Collections.reverse(userDataModelList);
        userAdapter.notifyDataSetChanged();
    }


    private void clicks() {
        binding.addPostFeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AddPostActivity.class));
            }
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Home");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finishAffinity();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type.matches("customer")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addPostFeb.setVisibility(View.GONE);
            getDataCustomerApi();

        }else if (type.matches("developer")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addPostFeb.setVisibility(View.VISIBLE);
            getDataDeveloperApi();
        }
    }
}
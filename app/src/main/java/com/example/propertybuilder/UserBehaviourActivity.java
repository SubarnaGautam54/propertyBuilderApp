package com.example.propertybuilder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.Adapters.UserBehaviourAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserBehaviourModel;
import com.example.propertybuilder.databinding.ActivityUserBehaviourBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserBehaviourActivity extends AppCompatActivity {
    public static final String TAG = "wow123";

    ActivityUserBehaviourBinding binding;
    List<UserBehaviourModel> userBehaviourModelList;
    UserBehaviourAdapter userBehaviourAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBehaviourBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        toolBarSetup();
        getUserBehaviours();
    }

    private void getUserBehaviours() {
        binding.progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.GET_USER_BEHAVIOUR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userBehaviourModelList =  new ArrayList<>();
                    for (int i = 0;i < jsonObject.length()-2;i++) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i + "");
                        Log.d(TAG, "onResponse: "+jsonObject1);
                        UserBehaviourModel userBehaviourModel = new UserBehaviourModel();
                        userBehaviourModel.setId(jsonObject1.getString("id"));
                        userBehaviourModel.setMessage(jsonObject1.getString("message"));
                        userBehaviourModelList.add(userBehaviourModel);

                    }
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(UserBehaviourActivity.this));
                    userBehaviourAdapter = new UserBehaviourAdapter(UserBehaviourActivity.this,userBehaviourModelList);
                    binding.recyclerView.setAdapter(userBehaviourAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserBehaviourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(UserBehaviourActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        MySingleton.getInstance(UserBehaviourActivity.this).addToRequestQueue(stringRequest);
    }

    private void toolBarSetup() {
        binding.toolBar.setTitle("User Behaviour");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
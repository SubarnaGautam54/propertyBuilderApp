package com.example.propertybuilder.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.Adapters.AppointmentAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.AppointmentModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityAppointmentBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {

    ActivityAppointmentBinding binding;
    public static final String TAG = "wow123";
    List<AppointmentModel> appointmentModelList = new ArrayList<>();
    AppointmentAdapter appointmentAdapter;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = String.valueOf(usersModel.getId());
        toolBarSetup();
        ApiCall();

    }

    private void ApiCall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_APPOINTMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i< jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Log.d(TAG, "onResponse: array"+jsonObject.getString("appointment_id"));
                        AppointmentModel appointmentModel = new AppointmentModel();

                        appointmentModel.setAppointmentMessage(jsonObject.getString("message"));
                        appointmentModel.setAppointmentDate(jsonObject.getString("date"));
                        appointmentModel.setAppointmentTime(jsonObject.getString("time"));
                        appointmentModel.setAppointmentType(jsonObject.getString("type"));
                        appointmentModel.setAppointmentStatus(jsonObject.getString("status"));

                        appointmentModelList.add(appointmentModel);

                        setupRecyclerView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        MySingleton.getInstance(AppointmentActivity.this).addToRequestQueue(stringRequest);
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(this,appointmentModelList);
        Collections.reverse(appointmentModelList);
        binding.recyclerView.setAdapter(appointmentAdapter);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Appointments");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
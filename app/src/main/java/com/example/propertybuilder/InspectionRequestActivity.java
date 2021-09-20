package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
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
import com.example.propertybuilder.Adapters.BookingDateAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.BookingDateModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityInspectionRequestBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectionRequestActivity extends AppCompatActivity {

    ActivityInspectionRequestBinding binding;
    public static final String TAG = "wow123";
    BookingDateAdapter bookingDateAdapter;
    List<BookingDateModel> bookingDateModelList = new ArrayList<>();
    String userId;
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
        binding = ActivityInspectionRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        toolBarSetup();
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = String.valueOf(usersModel.getId());
        String type = String.valueOf(usersModel.getTypeUser());
        if (type.equals("developer")){
            appointmentDeveloper();
        }else if (type.equals("admin")){
            appointmentAdmin();
        }
        
    }

    private void appointmentAdmin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_APPOINTMENT_ADMIN_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0;i < jsonObject.length();i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");
                        Log.d(TAG, "onResponse: date"+jsonObject1.getString("date"));

                        Log.d(TAG, "onResponse: jsonObject1"+jsonObject1);
                        BookingDateModel bookingDateModel = new BookingDateModel();

                        bookingDateModel.setCustomerName(jsonObject1.getString("full_name"));
                        bookingDateModel.setBookingDate(jsonObject1.getString("date"));
                        bookingDateModel.setBookingTime(jsonObject1.getString("time"));
                        bookingDateModel.setBookingType(jsonObject1.getString("type"));
                        bookingDateModel.setCustomerID(jsonObject1.getString("user_id"));
                        bookingDateModel.setAppointmentId(jsonObject1.getString("appointment_id"));
                        bookingDateModel.setAppointmentStatus(jsonObject1.getString("status"));
                        Log.d(TAG, "onResponse: customer"+jsonObject1.getString("user_id"));
                        bookingDateModel.setPostID(jsonObject1.getString("post_id"));
                        Log.d(TAG, "onResponse: post"+jsonObject1.getString("post_id"));


                        bookingDateModelList.add(bookingDateModel);

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
        });
        MySingleton.getInstance(InspectionRequestActivity.this).addToRequestQueue(stringRequest);
    }

    private void appointmentDeveloper() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_APPOINTMENT_DEVELOPER_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0;i < jsonObject.length();i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");
                        Log.d(TAG, "onResponse: date"+jsonObject1.getString("date"));

                        Log.d(TAG, "onResponse: jsonObject1"+jsonObject1);
                        BookingDateModel bookingDateModel = new BookingDateModel();

                        bookingDateModel.setCustomerName(jsonObject1.getString("full_name"));
                        bookingDateModel.setBookingDate(jsonObject1.getString("date"));
                        bookingDateModel.setBookingTime(jsonObject1.getString("time"));
                        bookingDateModel.setBookingType(jsonObject1.getString("type"));
                        bookingDateModel.setCustomerID(jsonObject1.getString("user_id"));
                        bookingDateModel.setAppointmentId(jsonObject1.getString("appointment_id"));
                        bookingDateModel.setAppointmentStatus(jsonObject1.getString("status"));

                        Log.d(TAG, "onResponse: customer"+jsonObject1.getString("user_id"));
                        bookingDateModel.setPostID(jsonObject1.getString("post_id"));
                        Log.d(TAG, "onResponse: post"+jsonObject1.getString("post_id"));


                        bookingDateModelList.add(bookingDateModel);

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
//                params.put("developer_id", String.valueOf(userId));
                params.put("developer_id", userId);
                return params;
            }

        };
        MySingleton.getInstance(InspectionRequestActivity.this).addToRequestQueue(stringRequest);
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingDateAdapter = new BookingDateAdapter(this,bookingDateModelList);
        Collections.reverse(bookingDateModelList);
        binding.recyclerView.setAdapter(bookingDateAdapter);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Enquiry and inspection");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
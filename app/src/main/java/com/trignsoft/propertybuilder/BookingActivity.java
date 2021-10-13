package com.trignsoft.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.Adapters.BookingAdapter;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.Models.UserDataModel;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.trignsoft.propertybuilder.SharedPreference.DataProccessor;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityBookingBinding;
import com.trignsoft.propertybuilder.utils.AppointmentActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    ActivityBookingBinding binding;
    int userId;
    String type;
    public static final String TAG = "wow123";
    private LinearLayoutManager manager;
    BookingAdapter bookingAdapter;
    private List<UserDataModel> userDataModelList;
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
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        DataProccessor dataProccessor = new DataProccessor(this);
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = usersModel.getId();
//        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
        type = usersModel.getTypeUser();
        getDataCustomerApi();
        manager = new LinearLayoutManager(this);
        searchFilter();
        clicks();


    }

    private void clicks() {
        binding.appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingActivity.this, AppointmentActivity.class));
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchBarLy.setVisibility(View.VISIBLE);
                binding.searchView.setVisibility(View.GONE);
                binding.close.setVisibility(View.VISIBLE);
                binding.searchBar.requestFocus();
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(binding.searchBar, InputMethodManager.SHOW_FORCED);
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
                binding.searchBarLy.setVisibility(View.GONE);
                binding.searchView.setVisibility(View.VISIBLE);
                binding.close.setVisibility(View.GONE);
                binding.searchBar.setText("");
            }
        });
    }

    private void searchFilter() {
        binding.searchBar.setOnEditorActionListener(new EditorDoneListnerPost());
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterHSCode(editable.toString());
            }
        });
    }

    private void getDataCustomerApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_USER_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:getcustomer "+response);
                binding.progressBar.setVisibility(View.GONE);

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
                        userDataModel.setDeveloperId(jsonObject1.getString("developer_id"));


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
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
                binding.progressBar.setVisibility(View.GONE);

            }
        });
        MySingleton.getInstance(BookingActivity.this).addToRequestQueue(stringRequest);
    }
    private void setupRecyclerViewForPost() {
        binding.recyclerView.setLayoutManager(manager);
        bookingAdapter = new BookingAdapter(this,userDataModelList);
        binding.recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.notifyDataSetChanged();
    }
    private class EditorDoneListnerPost implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_DONE){
                InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                return true;
            }
            return false;
        }
    }
    private void filterHSCode(String text){
        ArrayList<UserDataModel> filteredList = new ArrayList<>();
        for (UserDataModel item : userDataModelList){
            if (item.getDeveloperPostName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        bookingAdapter.filterList(filteredList);
    }
}
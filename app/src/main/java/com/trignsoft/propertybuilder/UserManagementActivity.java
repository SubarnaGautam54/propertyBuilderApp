package com.trignsoft.propertybuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.Adapters.StaffAdapter;
import com.trignsoft.propertybuilder.Adapters.StaffDetailActivity;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.Models.StaffModel;
import com.example.propertybuilder.R;
import com.example.propertybuilder.databinding.ActivityUserManagementBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserManagementActivity extends AppCompatActivity {
    
    ActivityUserManagementBinding binding;
    public static final String TAG = "wow123";
    List<StaffModel> staffModelList = new ArrayList<>();
    StaffAdapter staffAdapter;
    CircleImageView staffImage;
    BottomSheetDialog bottomSheetDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Bitmap bitmap;
    private final int GALLERY = 1;

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
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        clicks();
        setUpDummyRecyclerView();

    }

    private void setUpDummyRecyclerView() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.GET_ALL_STAFF, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                staffModelList.clear();

                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i=0;i< jsonObject.length();i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");

                        StaffModel staffModel = new StaffModel();

                        staffModel.setStaffId(jsonObject1.getString("id"));
                        staffModel.setStaffName(jsonObject1.getString("full_name"));
                        staffModel.setStaffAddress(jsonObject1.getString("address"));
                        staffModel.setStaffPhone(jsonObject1.getString("phone_no"));
                        staffModel.setStaffEmail(jsonObject1.getString("email"));
                        staffModel.setStaffPassword(jsonObject1.getString("password"));
                        staffModel.setStaffImage(jsonObject1.getString("image"));
                        staffModel.setStaffType(jsonObject1.getString("type_user"));
                        staffModel.setStaffTypeStatus(jsonObject1.getString("account_status"));


                        staffModelList.add(staffModel);

                        setRecyclerView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResponse: "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: "+error.getMessage());
            }
        });
        MySingleton.getInstance(UserManagementActivity.this).addToRequestQueue(stringRequest);

    }

    private void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        staffAdapter = new StaffAdapter(UserManagementActivity.this,staffModelList);
        binding.recyclerView.setAdapter(staffAdapter);
    }

    private void clicks() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.addStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(UserManagementActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_staff);
                ImageView closeBS = bottomSheetDialog.findViewById(R.id.closeBS);
                staffImage = bottomSheetDialog.findViewById(R.id.staffImage);
                assert staffImage != null;
                staffImage.setImageResource(R.drawable.no_image);
                LinearLayout staffPickImage_btn = bottomSheetDialog.findViewById(R.id.staffPickImage_btn);
                EditText staffName = bottomSheetDialog.findViewById(R.id.staffFullNameEdt);
                EditText staffAddressEdt = bottomSheetDialog.findViewById(R.id.staffAddressEdt);
                EditText staffphonenumber = bottomSheetDialog.findViewById(R.id.phoneNumberEdt);
                EditText staffEmailEdt = bottomSheetDialog.findViewById(R.id.staffEmailEdt);
                EditText staffPasswordEdt = bottomSheetDialog.findViewById(R.id.staffPasswordEdt);
                EditText staffConfirmPasswordEdt = bottomSheetDialog.findViewById(R.id.staffConfirmPasswordEdt);
                Button confirmAddStaffBtn = bottomSheetDialog.findViewById(R.id.confirmAddStaff);
                Button cancelAddStaffBtn = bottomSheetDialog.findViewById(R.id.cancelAddStaff);
                CheckBox checkBox = bottomSheetDialog.findViewById(R.id.staffCheckbox);
                ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.staffProgressBar);
                confirmAddStaffBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = staffName.getText().toString();
                        String address = staffAddressEdt.getText().toString();
                        String phone = staffphonenumber.getText().toString();
                        String email = staffEmailEdt.getText().toString();
                        String password = staffPasswordEdt.getText().toString();
                        String cPassword = staffConfirmPasswordEdt.getText().toString();
                        if (name.isEmpty()){
                            staffName.requestFocus();
                            staffName.setError("Enter Staff Name");
                        }else if (address.isEmpty()){
                            staffAddressEdt.requestFocus();
                            staffAddressEdt.setError("Enter Staff Address");
                        }else if (phone.isEmpty()){
                            staffphonenumber.requestFocus();
                            staffphonenumber.setError("Enter Phone Number");
                        }else if (phone.length() < 10){
                            staffphonenumber.requestFocus();
                            staffphonenumber.setError("Invalid Phone Number");
                        }
                        else if (email.isEmpty()){
                            staffEmailEdt.requestFocus();
                            staffEmailEdt.setError("Enter Staff Email");
                        }else if (!email.matches(emailPattern)){
                            staffEmailEdt.requestFocus();
                            staffEmailEdt.setError("Enter Staff Valid Email");
                        }else if (password.isEmpty()){
                            staffPasswordEdt.requestFocus();
                            staffPasswordEdt.setError("Enter Staff Password");
                        }else if (password.length() < 6){
                            staffPasswordEdt.requestFocus();
                            staffPasswordEdt.setError("Password Must Be 6 Characters Long");
                        }else if (!cPassword.matches(password)){
                            staffPasswordEdt.requestFocus();
                            staffPasswordEdt.setError("Confirm Password Does Not Match");
                        }else if (bitmap == null){
                            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image);
                        }else {
                            cancelAddStaffBtn.setVisibility(View.GONE);
                            confirmAddStaffBtn.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                            final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CUS_REGISTER, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: "+response);

//                    Toast.makeText(RegisterActivity.this, "wow wow", Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getBoolean("status")){
                                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//                                            Log.d(TAG, "onResponse: name"+jsonObject1.getString("full_name"));

                                            Intent intent  = new Intent(UserManagementActivity.this, StaffDetailActivity.class);
                                            intent.putExtra("staffId",jsonObject1.getString("id"));
                                            intent.putExtra("staffImage",jsonObject1.getString("image"));
                                            intent.putExtra("staffType",jsonObject1.getString("type_user"));
                                            intent.putExtra("staffName",jsonObject1.getString("full_name"));
                                            intent.putExtra("staffAddress",jsonObject1.getString("address"));
                                            intent.putExtra("staffPhone",jsonObject1.getString("phone_no"));
                                            intent.putExtra("staffEmail",jsonObject1.getString("email"));
                                            intent.putExtra("staffPassword",jsonObject1.getString("password"));
                                            startActivity(intent);

                                            bottomSheetDialog.cancel();
//                                            startActivity(new Intent(UserManagementActivity.this,DashBoardUserActivity.class));
//                                            finish();
//                                            startActivity(new Intent(UserManagementActivity.this, StaffDetailActivity.class));

//                            startActivity(new Intent(RegisterActivity.this,SignInActivity.class));
                                        }else {
//                            Toast.makeText(RegisterActivity.this, ""+jsonObject.getBoolean("status"), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onResponse: "+jsonObject.getBoolean("status"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        cancelAddStaffBtn.setVisibility(View.VISIBLE);
                                        confirmAddStaffBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        Log.d(TAG, "onResponse: exception"+e.getMessage());
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    cancelAddStaffBtn.setVisibility(View.VISIBLE);
                                    confirmAddStaffBtn.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(RegisterActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("full_name", name);
                                    params.put("address", address);
                                    params.put("phone_number", phone);
                                    params.put("email", email);
                                    params.put("password", password);
                                    params.put("img", encodedImage);
                                    params.put("type_user", "developer");
                                    params.put("account_status", "activated");
                                    return params;
                                }
                            };
                            MySingleton.getInstance(UserManagementActivity.this).addToRequestQueue(stringRequest);
                        }

                    }
                });
                closeBS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });
                staffPickImage_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
//                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(takePicture, 0);
                                        openGallery();
                                        break;

                                    case DialogInterface.BUTTON_NEUTRAL:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
                        builder.setMessage("Select Image From Below Option.").setPositiveButton("Gallery", dialogClickListener).setNeutralButton("cancel",dialogClickListener).show();
                    }
                });
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            staffPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            staffConfirmPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        else{
                            staffPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            staffConfirmPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                });
                cancelAddStaffBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.show();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpDummyRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    Log.d(TAG, "onActivityResult: "+bitmap);
                    staffImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
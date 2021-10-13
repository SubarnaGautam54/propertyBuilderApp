package com.trignsoft.propertybuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.R;
import com.example.propertybuilder.databinding.ActivityRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    Uri selectedImage;
    Bitmap imageBitmap;
    Drawable drawable;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.profileImage.setImageResource(R.drawable.no_image);
        clicks();

        binding.pickImageBtn.setOnClickListener(new View.OnClickListener() {
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

//                            case DialogInterface.BUTTON_NEGATIVE:
//                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(pickPhoto, 1);
//                                break;

                            case DialogInterface.BUTTON_NEUTRAL:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("Select Image From Below Option.").setPositiveButton("Gallery", dialogClickListener).setNeutralButton("cancel",dialogClickListener).show();
            }
        });
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY);
    }
    private void clicks() {
        binding.loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,SignInActivity.class));
                finish();

            }
        });
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidations();
            }
        });
        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    binding.passwordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.confirmPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    binding.passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.confirmPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    private void checkValidations() {
        String fullName = binding.fullNameEdt.getText().toString();
        String address = binding.addressEdt.getText().toString();
        String phoneNumber = binding.phoneNumberEdt.getText().toString();
        String email = binding.emailEdt.getText().toString();
        String password = binding.passwordEdt.getText().toString();
        String confirmPassword = binding.confirmPasswordEdt.getText().toString();

        if (fullName.isEmpty()){
            binding.fullNameEdt.requestFocus();
            binding.fullNameEdt.setError("Enter Full Name");
        }else if (address.isEmpty()){
            binding.addressEdt.requestFocus();
            binding.addressEdt.setError("Enter Your Address");
        }else if (phoneNumber.isEmpty()){
            binding.phoneNumberEdt.requestFocus();
            binding.phoneNumberEdt.setError("Enter Your Phone Number");
        }else if (phoneNumber.length() < 10){
            binding.phoneNumberEdt.requestFocus();
            binding.phoneNumberEdt.setError("Invalid Phone Number");
        } else if (email.isEmpty()){
            binding.emailEdt.requestFocus();
            binding.emailEdt.setError("Enter Your Email");
        }else if (!email.matches(emailPattern)){
            binding.emailEdt.requestFocus();
            binding.emailEdt.setError("Invalid Email Address");
        }else if (password.isEmpty()){
            binding.passwordEdt.requestFocus();
            binding.passwordEdt.setError("Password Is Required");
        }else if (password.length() < 6){
            binding.passwordEdt.requestFocus();
            binding.passwordEdt.setError("Password Must Be 6 Or More Then 6 Characters Long");
        }else if (!confirmPassword.matches(password)){
            binding.confirmPasswordEdt.requestFocus();
            binding.confirmPasswordEdt.setError("Password Does Not Match");
        }else if (bitmap == null){
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.no_image);
        }else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.registerBtn.setVisibility(View.GONE);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            Log.d(TAG, "checkValidations: "+encodedImage);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CUS_REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: "+response);

//                    Toast.makeText(RegisterActivity.this, "wow wow", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("status")){
//                            Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterActivity.this,SignInActivity.class));
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: "+jsonObject.getBoolean("status"));
                            binding.progressBar.setVisibility(View.GONE);
                            binding.registerBtn.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: exception"+e.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                        binding.registerBtn.setVisibility(View.VISIBLE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(RegisterActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.registerBtn.setVisibility(View.VISIBLE);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("full_name", fullName);
                    params.put("address", address);
                    params.put("phone_number", phoneNumber);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("img", encodedImage);
                    params.put("type_user", "customer");
                    params.put("account_status", "activated");
                    return params;
                }
            };
            MySingleton.getInstance(RegisterActivity.this).addToRequestQueue(stringRequest);
        }


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
                    binding.profileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this,SignInActivity.class));
        finish();
    }
}
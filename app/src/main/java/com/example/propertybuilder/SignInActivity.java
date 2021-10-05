package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivitySignInBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    public static final String TAG = "wow123";
    boolean doubleBackToExitPressedOnce = false;
    public static final String FIREBASE_NOTIFICATION_SERVER_KEY = "key=AAAAjEeogSU:APA91bEa1suwdOU23my3AcKQKobAethPxjdrg-G1HY-woF7jJoIGGeV-D0JGRuUagygOc1MdZKBajB8V9zQvQ4uaie7LEq3ZEZi5CZ1lwx8QjOFlGMhdBEX1DQlzGzY21MNL2TOqSC3y";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
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
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        DataProccessor dataProccessor = new DataProccessor(this);
        clicks();
    }

    private void clicks() {
        binding.forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
            }
        });
        binding.signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,RegisterActivity.class));
                finish();
            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
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
                }
                else{
                    binding.passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    private void checkValidations() {
        String email = binding.emailEdt.getText().toString();
        String password = binding.passwordEdt.getText().toString();

        if (email.isEmpty()){
            binding.emailEdt.requestFocus();
            binding.emailEdt.setError("Enter Your Email Address");
        }else if (!email.matches(emailPattern)){
            binding.emailEdt.requestFocus();
            binding.emailEdt.setError("Invalid Email");
        }else if (password.isEmpty()){
            binding.passwordEdt.requestFocus();
            binding.passwordEdt.setError("Enter Your Password");
        }else if (password.length() < 6){
            binding.passwordEdt.requestFocus();
            binding.passwordEdt.setError("Password Must Be 6 Or More Then 6 Characters Long");
        }else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.GONE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CUS_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: "+response);

//                    Toast.makeText(SignInActivity.this, "wow wow", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("status")){

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            Log.d(TAG, "onResponse: "+jsonObject1);
                            DataProccessor.setStr("activateId", String.valueOf(jsonObject1.getInt("id")));
                            DataProccessor.setStr("activateName", jsonObject1.getString("full_name"));
                            if (jsonObject1.getString("account_status").equals("deactivated")){
                                binding.progressBar.setVisibility(View.GONE);
                                binding.loginBtn.setVisibility(View.VISIBLE);
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignInActivity.this);
                                builder1.setTitle("Account Deactivated!");
                                builder1.setMessage("Do you want to activate this account");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.Activate_ACCOUNT, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Log.d("wow123", "onResponse: activated" + response);
//                                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                                                        createnotification("accountActivated","Account Activated",DataProccessor.getStr("activateName")+" Account Has Been Activated","3");
                                                        Toast.makeText(SignInActivity.this, "Your Account Has Been Activated Try Sign In Again!", Toast.LENGTH_SHORT).show();
//                                                        createnotification("activateAccount","Account Activated",userName+" Has Deactivated His/Her Account!","3");

                                                        dialog.dismiss();
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                }) {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("account_status", "activated");
                                                        params.put("id", DataProccessor.getStr("activateId"));
                                                        return params;
                                                    }
                                                };
                                                MySingleton.getInstance(SignInActivity.this).addToRequestQueue(stringRequest);
                                                dialog.cancel();
                                                dialog.cancel();
                                            }
                                        });
                                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });


                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }else {
                                Log.d(TAG, "onResponse:+password "+jsonObject1.getString("password"));
                                DataProccessor.setStr(Api.USER_PASSWORD,jsonObject1.getString("password"));

                                DataProccessor.setStr("userId",jsonObject1.getString("id"));
                                UserModel userModel = new UserModel(
                                        jsonObject1.getInt("id"),
                                        jsonObject1.getString("image"),
                                        jsonObject1.getString("full_name"),
                                        jsonObject1.getString("address"),
                                        jsonObject1.getString("phone_no"),
                                        jsonObject1.getString("email"),
                                        jsonObject1.getString("type_user")
                                );

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);
                                startActivity(new Intent(SignInActivity.this, DashBoardUserActivity.class));
                                finish();
                            }

                        }else {
//                            Toast.makeText(SignInActivity.this, ""+jsonObject.getBoolean("status"), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                            binding.loginBtn.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: exception"+e.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginBtn.setVisibility(View.VISIBLE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error);
                    Toast.makeText(SignInActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loginBtn.setVisibility(View.VISIBLE);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            MySingleton.getInstance(SignInActivity.this).addToRequestQueue(stringRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();
    }

    private void startAnimation() {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        binding.welcomeToText.startAnimation(animation1);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        binding.propertyBuilderAppText.startAnimation(animation2);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top);
        binding.getStartedText.startAnimation(animation3);
    }
    public void createnotification(String topic, String title, String message, String id) {
        Log.i(TAG, "createnotification: notification");
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("topic", topic);
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notification.put("to", "/topics/" + id);
            notification.put("data", notifcationBody);
            sendNotification(notification);
        } catch (JSONException e) {
            Log.i(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", FIREBASE_NOTIFICATION_SERVER_KEY);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
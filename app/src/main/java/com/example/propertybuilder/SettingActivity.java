package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.propertybuilder.databinding.ActivitySettingBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {


    public static final String TAG = "wow123";
    ActivitySettingBinding binding;
    public static final String FIREBASE_NOTIFICATION_SERVER_KEY = "key=AAAAjEeogSU:APA91bEa1suwdOU23my3AcKQKobAethPxjdrg-G1HY-woF7jJoIGGeV-D0JGRuUagygOc1MdZKBajB8V9zQvQ4uaie7LEq3ZEZi5CZ1lwx8QjOFlGMhdBEX1DQlzGzY21MNL2TOqSC3y";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    BottomSheetDialog bottomSheetDialog;
    String oldPassSp, userId, userName;

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
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        DataProccessor dataProccessor = new DataProccessor(this);
        oldPassSp = DataProccessor.getStr(Api.USER_PASSWORD);
        Log.d(TAG, "onCreate:old Pass  " + oldPassSp);
        toolBarSetup();
        clicks();
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = String.valueOf(usersModel.getId());
        userName = usersModel.getFullName();
    }

    private void clicks() {

        binding.deactivateAccountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(SettingActivity.this, "Account Deactivated", Toast.LENGTH_SHORT).show();
            }
        });
        binding.deleteAccountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete this Account")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_STAFF, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("wow123", "onResponse: " + response);
                                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                                        Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                                        startActivity(intent);

                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("id", userId);
                                        return params;
                                    }
                                };
                                MySingleton.getInstance(SettingActivity.this).addToRequestQueue(stringRequest);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        binding.changePasswordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(SettingActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_change_password);
                EditText oldPass = bottomSheetDialog.findViewById(R.id.oldPassword);
                EditText newPass = bottomSheetDialog.findViewById(R.id.newPassword);
                EditText confirmNewPass = bottomSheetDialog.findViewById(R.id.confirmNewPassword);
                Button confirm = bottomSheetDialog.findViewById(R.id.confirmChangePasswordBtn);
                Button cancel = bottomSheetDialog.findViewById(R.id.cancelBottomSheet);
                CheckBox showPassword = bottomSheetDialog.findViewById(R.id.checkbox);

                assert confirm != null;
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert oldPass != null;
                        String oldPassCh = oldPass.getText().toString();
                        assert newPass != null;
                        String newPassCh = newPass.getText().toString();
                        assert confirmNewPass != null;
                        String cNewPassCh = confirmNewPass.getText().toString();

                        if (oldPassCh.isEmpty()) {
                            oldPass.requestFocus();
                            oldPass.setError("Field Is Empty");
                        } else if (!oldPassCh.matches(oldPassSp)) {
                            oldPass.requestFocus();
                            oldPass.setError("Old Password Does Not Match");
                        } else if (newPassCh.isEmpty()) {
                            newPass.requestFocus();
                            newPass.setError("Field Is Empty");
                        } else if (newPassCh.length() < 6) {
                            newPass.requestFocus();
                            newPass.setError("Password Must Be 6 or More Then 6 Characters");
                        } else if (!cNewPassCh.matches(newPassCh)) {
                            confirmNewPass.requestFocus();
                            confirmNewPass.setError("Password Does Not Match To New Password");
                        } else {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.CHANGE_PASSWORD, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(SettingActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("user_id", userId);
                                    params.put("old_password", oldPassCh);
                                    params.put("new_password", newPassCh);
                                    return params;
                                }
                            };
                            MySingleton.getInstance(SettingActivity.this).addToRequestQueue(stringRequest);
                        }
                    }
                });
                assert cancel != null;
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });

                assert showPassword != null;
                showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        assert oldPass != null;
                        if (!isChecked) {
                            oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            assert newPass != null;
                            newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            assert confirmNewPass != null;
                            confirmNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            oldPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            assert newPass != null;
                            newPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            assert confirmNewPass != null;
                            confirmNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                });
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.show();
            }
        });
        binding.deactivateAccountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                builder1.setMessage("Deactivate This Account");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DEACTIVATE_ACCOUNT, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("wow123", "onResponse: " + response);
                                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        userBehaviour(userName + " Has Deactivated the Account.");
                                        createnotification("deactivate", "Account Deactivated", userName + " Has Deactivated the Account!", "3");
                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("account_status", "deactivated");
                                        params.put("id", userId);
                                        return params;
                                    }
                                };
                                MySingleton.getInstance(SettingActivity.this).addToRequestQueue(stringRequest);
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
            }
        });
        binding.deleteAccountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                builder1.setMessage("Are You Sure You Want To Delete This Account!");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_STAFF, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("wow123", "onResponse: " + response);
                                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                                        Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        userBehaviour(userName + " Has Deleted the Account.");
                                        createnotification("de_account", "Account Deleted", userName + " Has Deleted the Account!", "3");
                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
//                params.put("developer_id", String.valueOf(userId));
                                        params.put("id", userId);
                                        return params;
                                    }
                                };
                                MySingleton.getInstance(SettingActivity.this).addToRequestQueue(stringRequest);
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
            }
        });
    }

    private void userBehaviour(String message) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.POST_USER_BEHAVIOUR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: ");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("message", message);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Settings");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
package com.trignsoft.propertybuilder.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.trignsoft.propertybuilder.Models.UserModel;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "user_registration";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USER_IMAGE = "userImage";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_TYPE = "userType";



    private static SharedPrefManager mInstance;
    private static Context ctx;

    private SharedPrefManager(Context context) {
        ctx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public void userLogin(UserModel usersModel) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, usersModel.getId());
        editor.putString(KEY_USER_IMAGE, usersModel.getUserImage());
        editor.putString(KEY_FULL_NAME, usersModel.getFullName());
        editor.putString(KEY_ADDRESS, usersModel.getAddress());
        editor.putString(KEY_PHONE_NUMBER, usersModel.getPhoneNumber());
        editor.putString(KEY_EMAIL, usersModel.getEmail());
        editor.putString(KEY_USER_TYPE, usersModel.getTypeUser());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FULL_NAME, null) != null;
    }

    public UserModel getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserModel(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USER_IMAGE, null),
                sharedPreferences.getString(KEY_FULL_NAME, null),
                sharedPreferences.getString(KEY_ADDRESS, null),
                sharedPreferences.getString(KEY_PHONE_NUMBER, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_USER_TYPE, null)
        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
//        ctx.startActivity(new Intent(ctx, SignInActivity.class));
    }

}

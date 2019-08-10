package com.zubisoft.solutions.smartkitchen.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SmartKitchenPreference {

    private static final String SMART_KITCHEN = "com.zubisoft.solutionsed.dlh";
    private static final String IS_FIRST_TIME = "is_first_user";
    private static final String LOGIN_STATUS = "status";
    private static SmartKitchenPreference INSTANCE;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SmartKitchenPreference(Context context) {

        sharedPreferences = context.getSharedPreferences(SMART_KITCHEN, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SmartKitchenPreference getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SmartKitchenPreference(context);
        }
        return INSTANCE;
    }

    public boolean isFirstTimeUser() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    public void setFirstTimeUser(boolean firstTime) {
        editor.putBoolean(IS_FIRST_TIME, firstTime);
        editor.commit();
    }

    public void logUserDetails(String uid, String id) {
        editor.putString("uid", uid);
        editor.putString("id", id);
        editor.commit();
    }

    public void setLoginStatus(boolean status) {
        editor.putBoolean(LOGIN_STATUS, status);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(LOGIN_STATUS, true);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> details = new HashMap<>();
        details.put("uid", sharedPreferences.getString("uid", ""));
        details.put("id", sharedPreferences.getString("id", ""));

        return details;
    }
}

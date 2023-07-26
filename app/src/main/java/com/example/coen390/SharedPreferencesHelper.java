package com.example.coen390;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.coen390.Models.User;

public class SharedPreferencesHelper {
    private SharedPreferences sharedPreferences;
    public SharedPreferencesHelper(Context context)
    {
        sharedPreferences = context.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE );
    }
    public void saveUnverifiedUserData(User user)//String name,String uid, String address, String unit,String role, Boolean verified, String email, String phone)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("unverifiedUserName", (user.getFirstName()) + " " + user.getLastName());
        editor.putString("unverifiedUserUID", user.getUid());
        editor.putString("unverifiedUserAddress", user.getAddress());
        editor.putString("unverifiedUserUnit", user.getUnit());
        editor.putString("unverifiedUserEmail", "");
        editor.putString("unverifiedUserPhone", "");
        editor.putString("unverifiedUserRole", user.getRole());
        editor.putBoolean("verified", user.isVerified());
        editor.commit();
    }
    public User getUnverifiedUserData()
    {
        String name = sharedPreferences.getString("unverifiedUserName", null);
        String address = sharedPreferences.getString("unverifiedUserAddress", null);
        String unit = sharedPreferences.getString("unverifiedUserUnit", null);
        String email = sharedPreferences.getString("unverifiedUserEmail", null);
        String phone = sharedPreferences.getString("unverifiedUserPhone", null);
        String uid = sharedPreferences.getString("unverifiedUserUID", null);
        String role = sharedPreferences.getString("unverifiedUserRole", null);
        Boolean verified = sharedPreferences.getBoolean("verified", false);
        return new User(name, "", uid, address, unit, "", "", role, verified);
    }
}

package com.example.coen390.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.coen390.Models.User;

public class SharedPreferencesHelper {
    private SharedPreferences sharedPreferences;
    public SharedPreferencesHelper(Context context)
    {
        sharedPreferences = context.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE );
    }
    public String getSignedInUserAuthToken()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String addy = sharedPreferences.getString("loggedInUserAuthToken", "");
        return addy;
    }
    public void saveSignedInUserAuthToken(String name)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUserAuthToken", name);
        editor.commit();
    }
    public void saveSignedInUserAddress(String address)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUserAddress", address);
        editor.commit();
    }
    public String getSignedInUserFirstName()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String addy = sharedPreferences.getString("loggedInUserFirstName", "");
        return addy;
    }
    public void saveSignedInUserFirstName(String name)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUserFirstName", name);
        editor.commit();
    }
    public String getSignedInUserAddress()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String addy = sharedPreferences.getString("loggedInUserAddress", "");
        return addy;
    }
    public void saveUnverifiedUserData(User user)//String name,String uid, String address, String unit,String role, Boolean verified, String email, String phone)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("unverifiedUserName", (user.getFirstName()) + " " + user.getLastName());
        editor.putString("unverifiedUserUID", user.getUid());
        editor.putString("unverifiedUserAddress", user.getAddress());
        editor.putString("unverifiedUserUnit", user.getUnit());
        editor.putString("unverifiedUserEmail", user.getEmail());
        editor.putString("unverifiedUserPhone", user.getPhone());
        editor.putString("unverifiedUserRole", user.getRole());
        editor.putBoolean("verified", user.isVerified());
        editor.commit();
    }
    public User getUnverifiedUserData()
    {
        String name = sharedPreferences.getString("unverifiedUserName", null);
        String address = sharedPreferences.getString("unverifiedUserAddress", null);
        String unit = sharedPreferences.getString("unverifiedUserUnit", null);
        String email = sharedPreferences.getString("unverifiedUserEmail", "");
        String phone = sharedPreferences.getString("unverifiedUserPhone", "");
        String uid = sharedPreferences.getString("unverifiedUserUID", null);
        String role = sharedPreferences.getString("unverifiedUserRole", null);
        Boolean verified = sharedPreferences.getBoolean("verified", false);
        return new User(name, "", uid, address, unit, "", "", role, verified, email, phone);
    }
}

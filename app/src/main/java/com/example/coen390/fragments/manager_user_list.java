package com.example.coen390.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390.ManagerActivity;
import com.example.coen390.ManagerUserProfileActivity;
import com.example.coen390.Models.User;
import com.example.coen390.R;
import com.example.coen390.services.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class manager_user_list extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //TextView testTextView;

    ArrayList<User> userArrayList;
    ListView userListView;
    Button viewUserListButton;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userAddy;
    SharedPreferencesHelper spHelper;
    ArrayList<User> usersInBuilding;
    User managerUser;



    public static manager_user_list newInstance(String param1, String param2) {
        manager_user_list fragment = new manager_user_list();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ManagerActivity managerActivity = ((ManagerActivity) getActivity());
        spHelper = new SharedPreferencesHelper(managerActivity);
        userAddy = spHelper.getSignedInUserAddress();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userlist, container);
        userListView = view.findViewById(R.id.userListView);
        //testTextView = view.findViewById(R.id.testTV);
        queryCurrentUserData(view);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String unitToInspect = usersInBuilding.get(position).getAddress();
                String lastName = usersInBuilding.get(position).getLastName();
                String firstName = usersInBuilding.get(position).getFirstName();
                String unitNum = usersInBuilding.get(position).getUnit();
                //Log.d("Selected Unit", unitToInspect);
                if (unitToInspect == null)
                {
                    return;
                }
                else {
                        //dataPasser.onDataPass(unitToInspect);
                    ManagerActivity managerActivity = ((ManagerActivity) getActivity());
                    Intent intent;
                    intent = new Intent(managerActivity, ManagerUserProfileActivity.class);
                    intent.putExtra("userAddress", unitToInspect);
                    intent.putExtra("userFN", firstName);
                    intent.putExtra("userLN", lastName);
                    intent.putExtra("userUnitNum", unitNum);
                    startActivity(intent);
                }
            }
        });
        //-----------------------------------
        String managerUserAddress = userAddy;
        char ch = '|';
        int cnt = 0;

        for ( int i = 0; i < managerUserAddress.length(); i++) {
            if (managerUserAddress.charAt(i) == ch)
                cnt++;
        }
        if(cnt>4)
        {
            int lastSlash = managerUserAddress.lastIndexOf("|");
            managerUserAddress = managerUserAddress.substring(0,lastSlash);
            //String substringToDelete = managerUserAddress.substring(lastSlash, managerUserAddress.length());
            //managerUserAddress = managerUserAddress.replace(Pattern.quote(substringToDelete),"");
        }
        CollectionReference ref = db.collection("users");
        ref.whereGreaterThanOrEqualTo("address", managerUserAddress)
                .whereLessThanOrEqualTo("address", managerUserAddress + "\uF7FF")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("user list listener failed", "Listen failed.", e);
                            return;
                        }

                        queryAllUsersInBuilding(userAddy, view);
                        Log.d("user list listener established", "Successful establishment listener for users");
                    }
                });

        return view;
    }

    void queryAllUsersInBuilding(String managerUserAddy, View view)
    {
        usersInBuilding = new ArrayList<User>();

        String managerUserAddress= managerUserAddy;
        char ch = '|';
        int cnt = 0;

        for ( int i = 0; i < managerUserAddress.length(); i++) {
            if (managerUserAddress.charAt(i) == ch)
                cnt++;
        }
        if(cnt>4)
        {
            int lastSlash = managerUserAddress.lastIndexOf("|");
            managerUserAddress = managerUserAddress.substring(0,lastSlash);
            //String substringToDelete = managerUserAddress.substring(lastSlash, managerUserAddress.length());
            //managerUserAddress = managerUserAddress.replace(Pattern.quote(substringToDelete),"");
        }

        CollectionReference ref = db.collection("users");
        ref.whereGreaterThanOrEqualTo("address", managerUserAddress)
                .whereLessThanOrEqualTo("address", managerUserAddress + "\uF7FF")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersInBuilding.clear();
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("list of users from manager", document.getId() + " => " + document.getData());

                                User tmp = document.toObject(User.class);//new User(firstName, lastName, uid, country, province,city, street, address, unit, boxNumber, accessCode, Role);
                                if(tmp.getUnit() == null)
                                {
                                    tmp.setUnit("");
                                }
                                if(tmp.isVerified() == true) {
                                    usersInBuilding.add(tmp);//check for null!
                                }
                                //userArrayList.add(tmp);
                                String formatted_data[];

                                Log.d("User tmp", tmp.getUnit());
                                if(usersInBuilding == null || usersInBuilding.size() == 0)
                                {
                                    formatted_data = new String[1];
                                    formatted_data[0] = "No users to display"; //+ managerUser.getCountry();
                                }
                                else {
                                    formatted_data = new String[usersInBuilding.size()];
                                    for (int i = 0; i < formatted_data.length; i++) {
                                        if(usersInBuilding.get(i).getRole().contains("manager") == false)
                                            formatted_data[i] = usersInBuilding.get(i).getFirstName() +" " + usersInBuilding.get(i).getLastName() + " Unit " + usersInBuilding.get(i).getUnit();
                                        else
                                            formatted_data[i] = usersInBuilding.get(i).getFirstName() +" " + usersInBuilding.get(i).getLastName() + " (Manager)";

                                    }
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, formatted_data);
                                userListView.setAdapter(arrayAdapter);
                            }

                        } else {
                            //Toast.makeText(manager_user_list.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    void queryCurrentUserData(View view)
    {
        FirebaseUser user = mAuth.getCurrentUser();//needed?
        db.collection("users")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User userInfo = document.toObject(User.class);
                                queryAllUsersInBuilding(userInfo.getAddress(), view);
                            }


                        } else {
                            //Toast.makeText(LoginActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

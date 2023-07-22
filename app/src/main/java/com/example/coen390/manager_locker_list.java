package com.example.coen390;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390.Models.RecyclerViewLockerItem;
import com.example.coen390.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class manager_locker_list extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //TextView testTextView;

    ArrayList<User> userArrayList;
    ListView lockerListView;
    Button viewUserListButton;
    FirebaseUser user;
    ArrayList<String> lockers;
    ArrayList<RecyclerViewLockerItem> lockerItemArrayList;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    LockerRecyclerViewAdapter adapter;
    User managerUser;



    public static manager_locker_list newInstance(String param1, String param2) {
        manager_locker_list fragment = new manager_locker_list();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lockerlist, container);
        recyclerView = view.findViewById(R.id.lockersListView);
        lockerItemArrayList = new ArrayList<>();
        //lockerItemArrayList.add(new RecyclerViewLockerItem("String accessCode", "String boxNumber", "String userDocName", new Timestamp(123,12), new Timestamp(123,12), false));
        //lockerItemArrayList.add(new RecyclerViewLockerItem("String accessCode", "another boxNumber", "String userDocName", new Timestamp(123,12), new Timestamp(123,12), false));
        adapter = new LockerRecyclerViewAdapter(lockerItemArrayList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        //lockerListView = view.findViewById(R.id.lockersListView);
        //testTextView = view.findViewById(R.id.testTV);
        queryCurrentUserData(view);



        return view;
    }

    void queryAllUsersInBuilding(User managerUser, View view)
    {
        ArrayList<User> usersInBuilding = new ArrayList<User>();

        String managerUserAddress= managerUser.getAddress();
        char ch = '|';
        int cnt = 0;

        for ( int i = 0; i < managerUserAddress.length(); i++) {
            if (managerUserAddress.charAt(i) == ch)
                cnt++;
        }
        if(cnt>4)
        {
            int lastSlash = managerUserAddress.lastIndexOf("|");
            managerUserAddress = managerUserAddress.substring(0,managerUserAddress.length()-1);
            //String substringToDelete = managerUserAddress.substring(lastSlash, managerUserAddress.length());
            //managerUserAddress = managerUserAddress.replace(Pattern.quote(substringToDelete),"");
        }

        CollectionReference ref = db.collection("boxes");
        ref.whereGreaterThanOrEqualTo("boxAddress", managerUserAddress)
                .whereLessThanOrEqualTo("boxAddress", managerUserAddress + "\uF7FF")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("list of lockers from manager", document.getId() + " => " + document.getData());
                                String boxNumber = document.get("boxNumber").toString();
                                String unit = document.get("userDocName").toString();
                                String accessCode = document.get("accessCode").toString();
                                Timestamp pickupTime = (Timestamp) document.get("events.pickupTimestamp");
                                Date pickupDate = pickupTime.toDate();
                                Timestamp deliveryTime = (Timestamp) document.get("events.deliveryTimestamp");
                                Date deliveryDate = deliveryTime.toDate();
                                lockers = new ArrayList<>();

                                //userArrayList.add(tmp);
                                String formatted_data[];
                                if(lockers == null || lockers.size() == 0)
                                {
                                    lockers.add("No lockers to show");
                                }
                                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, lockers);
                                //lockerListView.setAdapter(arrayAdapter);
                                RecyclerViewLockerItem temp = new RecyclerViewLockerItem(accessCode, boxNumber, unit, deliveryDate, pickupDate, false);
                                lockerItemArrayList.add(temp);
                                adapter.notifyDataSetChanged();
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
                                //Log.d("document STUFFFFF", document.getId() + " => " + document.getData().get("role"));
                                User userInfo = document.toObject(User.class);
                                queryAllUsersInBuilding(userInfo, view);
                            }


                        } else {
                            //Toast.makeText(LoginActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}


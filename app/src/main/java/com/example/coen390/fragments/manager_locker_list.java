package com.example.coen390.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390.ManagerActivity;
import com.example.coen390.adapters.LockerRecyclerViewAdapter;
import com.example.coen390.Models.RecyclerViewLockerItem;
import com.example.coen390.Models.User;
import com.example.coen390.R;
import com.example.coen390.services.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class manager_locker_list extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //TextView testTextView;

    ArrayList<User> userArrayList;
    ListView lockerListView;
    Button viewUserListButton;
    FirebaseUser user;
    String userAddy;
    ArrayList<String> lockers;
    ArrayList<RecyclerViewLockerItem> lockerItemArrayList;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    LockerRecyclerViewAdapter adapter;
    SharedPreferencesHelper spHelper;
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
        ManagerActivity managerActivity = ((ManagerActivity) getActivity());
        spHelper = new SharedPreferencesHelper(managerActivity);
        userAddy = spHelper.getSignedInUserAddress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lockerlist, container);
        recyclerView = view.findViewById(R.id.lockersListView);
        lockerItemArrayList = new ArrayList<>();
        adapter = new LockerRecyclerViewAdapter(lockerItemArrayList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        queryCurrentUserData(view);
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
        }
        CollectionReference ref = db.collection("boxes");
        ref.whereGreaterThanOrEqualTo("boxAddress", managerUserAddress)
                .whereLessThanOrEqualTo("boxAddress", managerUserAddress + "\uF7FF")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("box list listener failed", "Listen failed.", e);
                            return;
                        }
                        lockerItemArrayList.clear();
                        queryAllBoxesInBuilding(userAddy, view);
                        Log.d("box list listener established", "Successful establishment listener for boxes");
                    }
                });

        return view;
    }

    void queryAllBoxesInBuilding(String managerUserAddy, View view)
    {
        ArrayList<User> usersInBuilding = new ArrayList<User>();

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
        }

        CollectionReference ref = db.collection("boxes");
        ref.whereGreaterThanOrEqualTo("boxAddress", managerUserAddress)
                .whereLessThanOrEqualTo("boxAddress", managerUserAddress + "\uF7FF")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lockerItemArrayList.clear();
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
                                String formatted_data[];
                                if(lockers == null || lockers.size() == 0)
                                {
                                    lockers.add("No lockers to show");
                                }
                                RecyclerViewLockerItem temp = new RecyclerViewLockerItem(accessCode, boxNumber, unit, deliveryDate, pickupDate, false);
                                lockerItemArrayList.add(temp);
                                adapter.notifyDataSetChanged();
                            }

                        } else {

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
                                queryAllBoxesInBuilding(userInfo.getAddress(), view);
                            }


                        } else {

                        }
                    }
                });
    }
}


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

import com.example.coen390.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class manager_user_list extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //TextView testTextView;

    ArrayList<User> userArrayList;
    ListView userListView;
    Button viewUserListButton;
    FirebaseUser user;
    FirebaseAuth mAuth;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userlist, container);
        userListView = view.findViewById(R.id.userListView);
        //testTextView = view.findViewById(R.id.testTV);
        queryCurrentUserData(view);



        return view;
    }

    void queryAllUsersInBuilding(User managerUser, View view)
    {
        ArrayList<User> usersInBuilding = new ArrayList<User>();

        CollectionReference ref = db.collection("users");
        ref.whereEqualTo("country", managerUser.getCountry()).whereEqualTo("province",managerUser.getProvince()).whereEqualTo("city", managerUser.getCity()).whereEqualTo("street", managerUser.getStreet()).whereEqualTo("address", managerUser.getAddress())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("list of users from manager", document.getId() + " => " + document.getData());

                                User tmp = document.toObject(User.class);//new User(firstName, lastName, uid, country, province,city, street, address, unit, boxNumber, accessCode, Role);
                                usersInBuilding.add(tmp);//check for null!
                                //userArrayList.add(tmp);
                                String formatted_data[];
                                Log.d("User tmp", tmp.getUnit());
                                if(usersInBuilding == null || usersInBuilding.size() == 0)
                                {
                                    formatted_data = new String[1];
                                    formatted_data[0] = "No users to display" + managerUser.country; //+ managerUser.getCountry();
                                }
                                else {
                                    formatted_data = new String[usersInBuilding.size()];
                                    for (int i = 0; i < formatted_data.length; i++) {
                                        formatted_data[i] = usersInBuilding.get(i).getFirstName() +" " + usersInBuilding.get(i).getLastName() + " Unit " + usersInBuilding.get(i).getUnit();
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
        DocumentReference docRef = db.collection("users").document(user.getUid());
        Log.d("manager UID IS>>>", "DocumentSnapshot data: " + user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        Log.d("manager doc found", "DocumentSnapshot data: " + document.getData());
                        managerUser = document.toObject(User.class);// new User(firstName, lastName, uid, country, province,city, street, address, unit, boxNumber, accessCode, Role);
                        queryAllUsersInBuilding(managerUser, view);

                    }
                    else
                    {
                        Log.d("doc not found", "No such document");
                    }
                }
                else
                {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //--------------------------------------------------------------------------------------------------------------


 }
}

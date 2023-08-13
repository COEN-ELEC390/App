package com.example.coen390;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.coen390.Models.User;
import com.example.coen390.adapters.EventListAdapter;
import com.example.coen390.fragments.DeleteUserCheckFragment;
import com.example.coen390.fragments.DeliveryDataFragment;
import com.example.coen390.fragments.manager_user_list;
import com.example.coen390.services.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ManagerUserProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView nameTV, unitTV, roleTV, emailTV, phoneTV;
    FragmentManager fragmentManager;
    boolean firstCall;
    SharedPreferencesHelper spHelper;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> formattedEventList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> unformattedEventList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    ListView eventListView;

    FirebaseUser user;
    String userAddress, userFN, userLN, userUnitNum, authToken, FCM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user_profile_activity);
        toolbar = (Toolbar) findViewById(R.id.userProfileToolbar);
        setSupportActionBar(toolbar);
        Intent dataIntent = getIntent();
        userAddress = dataIntent.getStringExtra("userAddress");
        userFN = dataIntent.getStringExtra("userFN");
        userLN = dataIntent.getStringExtra("userLN");
        userUnitNum = dataIntent.getStringExtra("userUnitNum");
        firstCall = true;
        spHelper = new SharedPreferencesHelper(ManagerUserProfileActivity.this);
        nameTV = findViewById(R.id.nameTV);
        roleTV = findViewById(R.id.roleTV);
        unitTV = findViewById(R.id.unitNumberTV);
        phoneTV = findViewById(R.id.phoneTV);
        emailTV = findViewById(R.id.emailTV);
        eventListView =findViewById(R.id.userEventsListView);
        fragmentManager = getSupportFragmentManager();
        getUserProfileData(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if(user == null)
        {
            Intent intent = new Intent(ManagerUserProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //do stuff for authenticated user here

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerUserProfileActivity.this,ManagerActivity.class);
                startActivity(intent);
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeliveryDataFragment myDialog = new DeliveryDataFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("HashMap", unformattedEventList.get(position));
                myDialog.setArguments(bundle);
                myDialog.show(fragmentManager, "test");

            }
        });
        //--------------------document listener
        final DocumentReference docRef = db.collection("users").document(userAddress);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("selected user doc snapshot listener failed", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(user != null) {
                        formattedEventList.clear();
                        unformattedEventList.clear();
                        queryCurrentUserData(getApplicationContext());
                        getUserProfileData(getApplicationContext());
                        Log.d("selected user data", "Current data: " + snapshot.getData());
                    }
                } else {
                    Log.d("Selected user data is null", "Current data: null");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manager_user_profile_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.deleteUserItem)
        {
            DeleteUserCheckFragment deleteFrag = new DeleteUserCheckFragment();
            Bundle args = new Bundle();
            args.putString("address", userAddress);
            args.putString("fn", userFN);
            args.putString("ln", userLN);
            args.putString("unit", userUnitNum);

            deleteFrag.setArguments(args);
            deleteFrag.show(getSupportFragmentManager(), "userListDialog");


        }
        return super.onOptionsItemSelected(item);
    }
    void getUserProfileData(Context cc)
    {
        DocumentReference docRef = db.collection("users").document(userAddress);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("USER PROFILE QUERY SUCCESSFUL", "DocumentSnapshot data: " + document.getData());
                    nameTV.setText(document.getData().get("firstName").toString() + " " + document.getData().get("lastName").toString());
                    if(document.getData().get("unit") != null) {
                        unitTV.setText("Unit: " + document.getData().get("unit").toString());
                    }
                    roleTV.setText("Role: " + document.getData().get("role").toString());
                        if(document.getData().get("email")!= null) {
                            emailTV.setText("Email: " + document.getData().get("email").toString());
                        }
                        if(document.getData().get("phone")!= null) {
                            phoneTV.setText("Phone: " + document.getData().get("phone").toString());
                        }

                        //--------------------------------------------------------------------eventList config
                        Map<String, HashMap<String, Object>> events = (Map<String, HashMap<String, Object>>)document.getData().get("events");
                        if(events != null)
                        {
                            formattedEventList.clear();
                            unformattedEventList.clear();
                            Log.d("events map", events.toString());
                            int numberOfEvents = events.size();
                            int count = 0;

                            for(Map.Entry<String, HashMap<String, Object>> e: events.entrySet())
                            {
                                HashMap<String, Object> subMap = e.getValue();
                                String listAccessCode = null;
                                String listBoxNumber = null;
                                Timestamp deliveryTime = null;
                                Timestamp pickupTime = null;
                                for(Map.Entry<String, Object> eventData : subMap.entrySet())
                                {
                                    //Log.d("eventData", eventData.toString());
                                    if(eventData.getKey().toString().contains("deliveryTimestamp"))
                                    {
                                        deliveryTime = (Timestamp) eventData.getValue();
                                    }
                                    else if(eventData.getKey().toString().contains("pickupTimestamp"))
                                    {
                                        pickupTime = (Timestamp) eventData.getValue();
                                    }
                                    else if(eventData.getKey().toString().contains("boxNumber"))
                                    {
                                        String tmp = eventData.getValue().toString();
                                        int start = tmp.indexOf("[");
                                        int end = tmp.indexOf("]");
                                        listBoxNumber = tmp.substring(start+1, end);                                            }
                                    else if(eventData.getKey().toString().contains("accessCode"))
                                    {
                                        String tmp = eventData.getValue().toString();
                                        int start = tmp.indexOf("[");
                                        int end = tmp.indexOf("]");
                                        listAccessCode = tmp.substring(start+1, end);
                                    }

                                }

                                if(listAccessCode != null && deliveryTime != null)
                                {
                                    unformattedEventList.add(subMap);
                                    sortByDate(unformattedEventList);

                                }
                                else
                                {
                                    formattedEventList.add("No events to display");
                                }

                            }
                            sortByDate(unformattedEventList);

                            for(int i = 0; i < unformattedEventList.size(); i++)
                            {
                                Timestamp thing = (Timestamp) unformattedEventList.get(i).get("deliveryTimestamp");
                                String formattedDate = thing.toDate().toString();
                                formattedDate = formattedDate.substring(0, formattedDate.length()-12);
                                formattedEventList.add("" + formattedDate);
                            }
                        }
                        arrayAdapter = new EventListAdapter(ManagerUserProfileActivity.this, formattedEventList);
                        eventListView.setAdapter(arrayAdapter);
                    } else {
                        Log.d("DOCUMENT NOT FOUND", "No such document");
                    }
                } else {
                    Log.d("QUERY FAILED", "get failed with ", task.getException());
                }
            }
        });
    }
    private static void sortByDate(ArrayList<HashMap<String, Object>> arrayList) {
        // Create a custom comparator for Firebase timestamps
        Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> map1, HashMap<String, Object> map2) {
                // Replace "timestamp" with your actual key for the timestamp value
                com.google.firebase.Timestamp timestamp1 = (com.google.firebase.Timestamp) map1.get("deliveryTimestamp");
                com.google.firebase.Timestamp timestamp2 = (com.google.firebase.Timestamp) map2.get("deliveryTimestamp");

                // Compare the timestamps
                return timestamp1.compareTo(timestamp2);
            }
        };

        // Sort the ArrayList using the custom comparator
        Collections.sort(arrayList, comparator.reversed());
    }
    User queryCurrentUserData(Context cc)
    {
        final User[] loggedInUser = new User[1];
        db.collection("users")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("document STUFFFFF", document.getId() + " => " + document.getData());
                                Role = String.valueOf(document.getData().get("role"));
                                String country =  String.valueOf(document.getData().get("country"));
                                String province =  String.valueOf(document.getData().get("province"));
                                String city =  String.valueOf(document.getData().get("city"));
                                String street =  String.valueOf(document.getData().get("street"));
                                String address =  String.valueOf(document.getData().get("address"));
                                String uid =  String.valueOf(document.getData().get("uid"));
                                String unit =  String.valueOf(document.getData().get("unit"));//not totally necessary for the manager
                                String firstName =  String.valueOf(document.getData().get("firstName"));
                                String lastName =  String.valueOf(document.getData().get("lastName"));
                                String email =  String.valueOf(document.getData().get("email"));
                                String phone =  String.valueOf(document.getData().get("phone"));

                                String accessCode = null;
                                String boxNumber = null;
                                String verif = String.valueOf(document.getData().get("verified"));
                                String userAuthToken = String.valueOf(document.getData().get("authToken"));
                                String savedAuthToken = spHelper.getSignedInUserAuthToken();
                                if(userAuthToken.compareTo(savedAuthToken) != 0 && firstCall == false)
                                {
                                    Toast.makeText(cc, "Account accessed on another device. signing you out", Toast.LENGTH_SHORT).show();
                                    signUserOut();
                                    return;
                                }
                                firstCall = false;
                                boolean verified;
                                if(verif != null && verif.contains("true"))
                                {
                                    verified = true;
                                }
                                else {
                                    verified = false;
                                }
                                String combinedAddress = address;
                                combinedAddress = combinedAddress.toLowerCase();
                                combinedAddress.replaceAll(" ", "");
                                loggedInUser[0] =  new User(firstName, lastName, uid, combinedAddress, unit, boxNumber, accessCode, Role, verified, email, phone);

                                //-----------------------------------------------------------firebase cloud messaging config
                                DocumentReference userRef = db.collection("users").document(combinedAddress);

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.d( "Fetching FCM registration token failed", task.getException().toString());
                                                    return;
                                                }

                                                // Get new FCM registration token
                                                String token = task.getResult();
                                                FCM = token;
                                                userRef
                                                        .update("FCM_TOKEN", FCM)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("FCM upload successful", "DocumentSnapshot successfully updated!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("FCM TOKEN UPLOAD FAILED", "Error updating document", e);
                                                            }
                                                        });
                                                // Log and toast
                                                //String msg = getString(R.string.msg_token_fmt, token);
                                                Log.d("FIREBASE CLOUD MESSAGING TOKEN", token);
                                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        } else {
                            Toast.makeText(ManagerUserProfileActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return loggedInUser[0];

    }
    public void signUserOut()
    {
        //-------------------------------------------------------
        FirebaseAuth.getInstance().signOut();
        user = null;
        spHelper.saveSignedInUserAddress("");
        Intent intent = new Intent(ManagerUserProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

package com.example.coen390;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coen390.Models.User;
import com.example.coen390.adapters.EventListAdapter;
import com.example.coen390.fragments.DeliveryDataFragment;
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

public class DeliveryHistoryActivity extends AppCompatActivity {

    Button refreshFeed, viewHistory;
    ArrayList<String> currentUserAddress;
    TextView nothingReadyForPickup;
    ArrayAdapter<String> arrayAdapter;
    String FCM, userAddy;
    boolean firstCall;
    SharedPreferencesHelper spHelper;
    Toolbar toolbar;
    ListView eventListView;
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> formattedEventList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> unformattedEventList = new ArrayList<HashMap<String, Object>>();
    FragmentManager fragmentManager;
    User currentUser;
    ArrayList<String> deliveriesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);
        eventListView = findViewById(R.id.eventLV);
        firstCall = true;
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        nothingReadyForPickup = findViewById(R.id.noDeliveries);
        spHelper = new SharedPreferencesHelper(DeliveryHistoryActivity.this);
        userAddy = spHelper.getSignedInUserAddress();
        currentUserAddress = new ArrayList<>();
        TextView titleText = new TextView(this);
        titleText.setText("Your deliveries");
        titleText.setGravity(25);
        titleText.setTextSize(20);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUser = queryCurrentUserData(getApplicationContext());
        Log.d("eventListView", eventListView.toString());
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        if(user == null)
        {
            Intent intent = new Intent(DeliveryHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //do stuff for authenticated user here

        }

        if(formattedEventList != null)
        {
            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, formattedEventList);
            eventListView.setAdapter(arrayAdapter);
        }

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
//-----------------------------------------------------------firebase cloud messaging config

        //------------------------------------------------
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                formattedEventList.clear();
                unformattedEventList.clear();
                queryCurrentUserData(getApplicationContext());
                swipeRefreshLayout.setRefreshing(false);
            };

        });
        toolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryHistoryActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //-----------------------------------------document listener
        final DocumentReference docRef = db.collection("users").document(userAddy);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("user doc snapshot listener failed", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(user != null) {
                        formattedEventList.clear();
                        unformattedEventList.clear();
                        queryCurrentUserData(getApplicationContext());
                        Log.d("Current user data", "Current data: " + snapshot.getData());
                    }
                } else {
                    Log.d("Current user data is null", "Current data: null");
                }
            }
        });
        //}
        //----------------------------------------------
    }

    User queryCurrentUserData(Context cc)
    {
        nothingReadyForPickup.setText("");
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
                                String combinedAddress = /*country + "/" + province + "/" + city + "/" + street + "/" + */address;// + "/" + unit;
                                combinedAddress = combinedAddress.toLowerCase();
                                combinedAddress.replaceAll(" ", "");
                                loggedInUser[0] =  new User(firstName, lastName, uid, combinedAddress, unit, boxNumber, accessCode, Role, verified, email, phone);
                                Map<String, HashMap<String, Object>> events = (Map<String, HashMap<String, Object>>)document.getData().get("events");
                                currentUserAddress.clear();
                                currentUserAddress.add(combinedAddress);
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
                                                Log.d("FIREBASE CLOUD MESSAGING TOKEN", token);
                                            }
                                        });
                                //------------------------------------------------



                                //-----------------------------------------

                                if(events != null)
                                {
                                    unformattedEventList.clear();
                                    formattedEventList.clear();
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
                                                //String[] arr = (String[]) eventData.getValue();
                                                //Log.d("accessCode", eventData.getValue().toString());
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
                            }
                            if(unformattedEventList.size() == 0)
                            {
                                nothingReadyForPickup.setText("You have no deliveries on record");
                            }
                            arrayAdapter = new EventListAdapter(DeliveryHistoryActivity.this, formattedEventList);
                            eventListView.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(DeliveryHistoryActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return loggedInUser[0];

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
    public void signUserOut()
    {
        //-------------------------------------------------------
        FirebaseAuth.getInstance().signOut();
        user = null;
        spHelper.saveSignedInUserAddress("");
        Intent intent = new Intent(DeliveryHistoryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });
}
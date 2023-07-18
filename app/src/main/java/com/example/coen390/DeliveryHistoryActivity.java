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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.coen390.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryHistoryActivity extends AppCompatActivity {

    Button refreshFeed, viewHistory;

    ArrayAdapter<String> arrayAdapter;
    String FCM;
    Toolbar toolbar;
    ListView eventListView;
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> formattedEventList = new ArrayList<>();
    ArrayList<HashMap> unformattedEventList = new ArrayList<>();
    FragmentManager fragmentManager;
    User currentUser;
    ArrayList<String> deliveriesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);
        eventListView = findViewById(R.id.eventLV);
        refreshFeed = findViewById(R.id.refreshFeedButton);
        TextView titleText = new TextView(this);
        titleText.setText("Your deliveries");
        titleText.setGravity(25);
        titleText.setTextSize(20);
        //eventListView.addHeaderView(titleText);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUser = queryCurrentUserData(getApplicationContext());
        Log.d("eventListView", eventListView.toString());
        //Log.d("FORMATTED EVENT LIST", formattedEventList.toString());
        //arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, formattedEventList);
        //eventListView.setAdapter(arrayAdapter);
        //toolbar = (Toolbar) findViewById(R.id.historyToolbarToolbar);
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
        refreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formattedEventList.clear();
                unformattedEventList.clear();
                queryCurrentUserData(getApplicationContext());
            }
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logOutItem)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(DeliveryHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                                //User userInfo = document.toObject(User.class);
                                //Log.d("userInfo UID", userInfo.getUid());
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
                                String accessCode = null;
                                String boxNumber = null;
                                String combinedAddress = /*country + "/" + province + "/" + city + "/" + street + "/" + */address;// + "/" + unit;
                                combinedAddress = combinedAddress.toLowerCase();
                                combinedAddress.replaceAll(" ", "");
                                loggedInUser[0] =  new User(firstName, lastName, uid, combinedAddress, unit, boxNumber, accessCode, Role);
                                Map<String, HashMap<String, Object>> events = (Map<String, HashMap<String, Object>>)document.getData().get("events");

                                //----------------------------------------
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
                                //------------------------------------------------



                                //-----------------------------------------

                                //formattedEventList = new String[numberOfEvents];
                                if(events != null)
                                {
                                    Log.d("events map", events.toString());
                                    int numberOfEvents = events.size();
                                    int count = 0;
                                    //List<Map<String, Object>> eventEntries = null;// = new List<Map<String, Object>>;

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

                                        unformattedEventList.add(subMap);
                                        if(listAccessCode != null && deliveryTime != null)
                                        {
                                            //while(count<numberOfEvents)
                                            //{
                                            //Log.d("deliveryTime", new Date(deliveryTime.getSeconds()*1000).toString());
                                            String t = "asd";
                                            formattedEventList.add("Delivered on: " + deliveryTime.toDate().toString());
                                            //count++;
                                            //}
                                        }
                                        else
                                        {
                                            formattedEventList.add("No events to display");
                                        }

                                        //Map<String, Object> event = e.getValue();
                                        //eventEntries.add(subMap);
                                        //Log.d("event entry", e.toString());
                                        //Log.d("e value",e.getValue());
                                        //DateTime deliveryDate = e.getValue()
                                    }
                                }
                            }
                            arrayAdapter = new ArrayAdapter<String>(cc,android.R.layout.simple_list_item_1, formattedEventList);
                            eventListView.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(DeliveryHistoryActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return loggedInUser[0];
        //deliveriesArrayList =

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

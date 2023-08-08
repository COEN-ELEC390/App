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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.coen390.Models.User;
import com.example.coen390.adapters.EventListAdapter;
import com.example.coen390.adapters.UnverifiedUserListAdapter;
import com.example.coen390.fragments.UnverifiedUserDataFragment;
import com.example.coen390.fragments.manager_locker_list;
import com.example.coen390.fragments.manager_user_list;
import com.example.coen390.services.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity {
    Button viewUsersButton, viewLockersButton;
    ListView unverifiedUsersLV;
    User managerUser;
    Toolbar toolbar;
    ArrayList<User> usersInBuilding;
    ArrayList<String> currentUserAddress;
    String FCM, userAddy, authToken;
    boolean firstCall;
    FragmentManager fragmentManager;
    protected SharedPreferencesHelper spHelper;
    ArrayList<String> formatted_data;


    AppCompatButton logoutButton;
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        currentUserAddress = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        firstCall = true;
        //logoutButton = findViewById(R.id.logout_button);
        viewUsersButton = findViewById(R.id.viewUsersButton);
        viewLockersButton = findViewById(R.id.viewLockersButton);
        unverifiedUsersLV = findViewById(R.id.unverifiedUsersLV);
        spHelper = new SharedPreferencesHelper(ManagerActivity.this);
        userAddy = spHelper.getSignedInUserAddress();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fragmentManager = getSupportFragmentManager();
        //Toast.makeText(this, "Welcome to manager view!", Toast.LENGTH_SHORT).show();
        if(user == null)
        {
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //do stuff for authenticated user here
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                spHelper.saveSignedInUserAuthToken(idToken);
                                authToken = idToken;
                                Log.d("AUTH TOKEN", idToken);
                                DocumentReference userRef = db.collection("users").document(userAddy);
                                userRef
                                        .update("authToken", authToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Auth token updated", "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("AuthToken error", "Error updating document", e);
                                            }
                                        });
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                            }
                        }
                    });
            //managerUser = queryCurrentUserData(getApplicationContext());
        }

        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager_user_list userListDialog = new manager_user_list();
                userListDialog.show(getSupportFragmentManager(), "userListDialog");
            }
        });
        unverifiedUsersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UnverifiedUserDataFragment myDialog = new UnverifiedUserDataFragment();
                spHelper.saveUnverifiedUserData(usersInBuilding.get(position));
                myDialog.show(fragmentManager, "test");
                recreate();
            }
        });
        viewLockersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager_locker_list userListDialog = new manager_locker_list();
                userListDialog.show(getSupportFragmentManager(), "lockerListDialog");
            }
        });
        //-----------------------------------------------------------firebase cloud messaging config
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
                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("FIREBASE CLOUD MESSAGING TOKEN", token);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        //------------------------------------------------
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
                            Log.w("unverified user listener failed", "Listen failed.", e);
                            return;
                        }
                        if(user != null) {
                            queryCurrentUserData(getApplicationContext());
                            getUnverifiedUsers(userAddy, getApplicationContext());
                            Log.d("Unverified user listener established", "Successful establishment listener for unverified users");
                        }
                    }
                });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manager_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logOutItem)
        {
            DocumentReference Ref = db.collection("users").document(currentUserAddress.get(0));
            Ref
                    .update("FCM_TOKEN", "")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("FCM TOKEN SUCCESSFULLY DELETED", Ref.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Error deleting FCM token ", "Error updating document", e);
                        }
                    });
            //------------------------------------------------------
            FirebaseAuth.getInstance().signOut();
            spHelper.saveSignedInUserAddress("");
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
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
                                                //String msg = getString(R.string.msg_token_fmt, token);
                                                Log.d("FIREBASE CLOUD MESSAGING TOKEN", token);
                                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                //------------------------------------------------getting unverified users
                                getUnverifiedUsers(loggedInUser[0].getAddress(), cc);

                            }

                        } else {
                            Toast.makeText(ManagerActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return loggedInUser[0];

    }
    void getUnverifiedUsers(String managerUserAddressInput, Context cc)
    {
        usersInBuilding = new ArrayList<User>();
        formatted_data = new ArrayList<String>();
        String managerUserAddress= managerUserAddressInput;
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
                            formatted_data.clear();
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("list of users from manager", document.getId() + " => " + document.getData());

                                User tmp = document.toObject(User.class);//new User(firstName, lastName, uid, country, province,city, street, address, unit, boxNumber, accessCode, Role);
                                if(tmp.getUnit() == null)
                                {
                                    tmp.setUnit("");
                                }
                                if(tmp.isVerified() == false) {
                                    usersInBuilding.add(tmp);//check for null!
                                    if(tmp.getRole().contains("manager") == false) {
                                        formatted_data.add(tmp.getFirstName() + " " + tmp.getLastName() + ": Unit " + tmp.getUnit());
                                    }
                                    else
                                    {
                                        formatted_data.add(tmp.getFirstName() + " " + tmp.getLastName() + " (Manager)");
                                    }
                                }
                                else {
                                    continue;
                                }
                                //userArrayList.add(tmp);

                                Log.d("User tmp", tmp.getUnit());
                                if(usersInBuilding == null || usersInBuilding.size() == 0)
                                {
                                    formatted_data.clear();
                                    //formatted_data = new String[1];
                                    formatted_data.add("No users to display"); //+ managerUser.getCountry();
                                }
                                else {

                                }
                                ArrayAdapter<String> arrayAdapter = new UnverifiedUserListAdapter(ManagerActivity.this, formatted_data);
                                unverifiedUsersLV.setAdapter(arrayAdapter);
                            }

                        } else {
                            //Toast.makeText(manager_user_list.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public void signUserOut()
    {
        //-------------------------------------------------------
        FirebaseAuth.getInstance().signOut();
        user = null;
        spHelper.saveSignedInUserAddress("");
        Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
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

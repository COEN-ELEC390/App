package com.example.coen390;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.coen390.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {
    Button viewUsersButton, viewLockersButton;
    User managerUser;
    Toolbar toolbar;
    ArrayList<String> currentUserAddress;
    String FCM;

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
        //logoutButton = findViewById(R.id.logout_button);
        viewUsersButton = findViewById(R.id.viewUsersButton);
        viewLockersButton = findViewById(R.id.viewLockersButton);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Toast.makeText(this, "Welcome to manager view!", Toast.LENGTH_SHORT).show();
        if(user == null)
        {
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //do stuff for authenticated user here
            managerUser = queryCurrentUserData();

        }

        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager_user_list userListDialog = new manager_user_list();
                userListDialog.show(getSupportFragmentManager(), "userListDialog");
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
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    User queryCurrentUserData()
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
                                String combinedAddress = address;
                                combinedAddress = combinedAddress.toLowerCase();
                                combinedAddress.replaceAll(" ", "");
                                loggedInUser[0] =  new User(firstName, lastName, uid, combinedAddress, unit, boxNumber, accessCode, Role);
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
                                //------------------------------------------------
                            }

                        } else {
                            Toast.makeText(ManagerActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return loggedInUser[0];

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

package com.example.coen390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390.Models.User;
import com.example.coen390.services.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText emailEdit, passwordEdit;
    AppCompatButton loginButton;
    SharedPreferencesHelper spHelper;
    TextView registerTV;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //open home page
            loginRedirect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spHelper = new SharedPreferencesHelper(LoginActivity.this);

        //------------------------------------------notification request
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        //------------------------------------------

        registerTV = findViewById(R.id.registerLink);
        emailEdit = findViewById(R.id.emailTV);
        passwordEdit = findViewById(R.id.passwordTV);
        loginButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                                    currentUser = mAuth.getCurrentUser();
                                    if(currentUser != null)
                                    {
                                        loginRedirect();
                                    }


                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });

            }
        });
    }

    void loginRedirect()
    {
        //FirebaseUser user = mAuth.getCurrentUser();//needed?
        db.collection("users")
                .whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            String verified = "";
                            String address = "";
                            String firstName = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("document STUFFFFF", document.getId() + " => " + document.getData().get("role"));
                                //User userInfo = document.toObject(User.class);
                                address = document.getData().get("address").toString();
                                firstName = document.getData().get("firstName").toString();
                                Role = String.valueOf(document.getData().get("role"));
                                if(document.getData().get("verified") == null)
                                {
                                    verified = "false";
                                }
                                else {
                                    verified = document.getData().get("verified").toString();
                                }
                            }
                            if(verified.contains("false"))
                            {
                                //--------------------------------------notifying manager that someone needs to be verified
                                queryAllManagersInBuilding(address);
                                //-------------------------------------------------------
                                FirebaseAuth.getInstance().signOut();
                                Intent intent;
                                intent = new Intent(LoginActivity.this, NotVerifiedActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if(Role.contains("manager"))
                            {//redirects to manager page instead
                                Intent intent;
                                intent = new Intent(LoginActivity.this, ManagerActivity.class);
                                spHelper.saveSignedInUserAddress(address);
                                spHelper.saveSignedInUserFirstName(firstName);
                                startActivity(intent);

                            }
                            else
                            {
                                //updateUI(user);
                                Intent intent;
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                spHelper.saveSignedInUserAddress(address);
                                spHelper.saveSignedInUserFirstName(firstName);
                                startActivity(intent);

                            }
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    void queryAllManagersInBuilding(String managerUserAddress)
    {
        ArrayList<User> managersInBuilding = new ArrayList<User>();

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
        Log.d("managerUserAddress", managerUserAddress);
        CollectionReference ref = db.collection("users");
        ref.whereGreaterThanOrEqualTo("address", managerUserAddress)
                .whereLessThanOrEqualTo("address", managerUserAddress + "\uF7FF")
                //.whereEqualTo("role", "manager")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String Role = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("role").toString().contains("manager"))
                                {
                                    Log.d("list of managers from non-verified query", document.getId() + " => " + document.getData());
                                    HashMap<String, Object> newNotification = new HashMap<>();
                                newNotification.put("title", "New User!");
                                newNotification.put("content", "A new user is requesting verification.");
                                newNotification.put("userDocName", document.getId());
                                newNotification.put("type", "userVerificationRequest");

                                db.collection("notifications").document(currentUser.getUid())
                                        .set(newNotification)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Notification to manager successfully written", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Notification to manager failed", "Error writing document", e);
                                            }
                                        });

                            }
                            }


                        } else {
                            //Toast.makeText(manager_user_list.this, "Error accessing documents", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
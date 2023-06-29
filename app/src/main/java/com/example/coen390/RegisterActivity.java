package com.example.coen390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEdit, passwordEdit, firstNameEdit, lastNameEdit, countryEdit, provinceEdit, cityEdit, streetEdit, addressEdit, apartmentNumberEdit;
    AppCompatButton registerButton;
    FirebaseAuth mAuth;
    TextView loginTV;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //open home page
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginTV = findViewById(R.id.loginLink);
        emailEdit = findViewById(R.id.emailTV);
        passwordEdit = findViewById(R.id.passwordTV);
        firstNameEdit = findViewById(R.id.firstNameTV);
        lastNameEdit = findViewById(R.id.lastNameTV);
        countryEdit = findViewById(R.id.countryTV);
        provinceEdit = findViewById(R.id.provinceTV);
        cityEdit = findViewById(R.id.cityTV);
        streetEdit = findViewById(R.id.streetTV);
        addressEdit = findViewById(R.id.addressTV);
        apartmentNumberEdit = findViewById(R.id.unitTV);


        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    //Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();//don't need?

                                    //Adding user to db
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("first_name", firstNameEdit.getText().toString());
                                    userData.put("last_name", lastNameEdit.getText().toString());
                                    userData.put("uid", user.getUid());
                                    userData.put("country", countryEdit.getText().toString());
                                    userData.put("province", provinceEdit.getText().toString());
                                    userData.put("city", cityEdit.getText().toString());
                                    userData.put("street", streetEdit.getText().toString());
                                    userData.put("address", addressEdit.getText().toString());
                                    userData.put("unit", apartmentNumberEdit.getText().toString());
                                    userData.put("box_number", null);
                                    userData.put("access_code", null);



                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                    //Log.d(TAG, "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error writing document", e);
                                                }
                                            });




                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);//login redirects to main
                                    startActivity(intent);
                                    finish();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        });
    }
}
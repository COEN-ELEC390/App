package com.example.coen390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390.Models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.android.BuildConfig;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEdit, passwordEdit, firstNameEdit, lastNameEdit, countryEdit, provinceEdit, cityEdit, streetEdit, addressEdit, apartmentNumberEdit, phoneEdit;
    AppCompatButton registerButton;
    FirebaseAuth mAuth;
    TextView loginTV;
    String country, province, city, street, streetNumber;
    AutocompleteSupportFragment autocompleteSupportFragment;

    User userToBeAdded;
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
        registerButton = findViewById(R.id.registerButton);
        //registerButton.setActivated(false);
        mAuth = FirebaseAuth.getInstance();
        loginTV = findViewById(R.id.loginLink);
        emailEdit = findViewById(R.id.emailTV);
        passwordEdit = findViewById(R.id.passwordTV);
        firstNameEdit = findViewById(R.id.firstNameTV);
        lastNameEdit = findViewById(R.id.lastNameTV);
        //countryEdit = findViewById(R.id.countryTV);
        //provinceEdit = findViewById(R.id.provinceTV);
        //cityEdit = findViewById(R.id.cityTV);
        //streetEdit = findViewById(R.id.streetTV);
        //addressEdit = findViewById(R.id.addressTV);
        apartmentNumberEdit = findViewById(R.id.unitTV);
        phoneEdit = findViewById(R.id.phoneTV);
        Places.initialize(getApplicationContext(), "AIzaSyBiKqXMPMqJTYFcddKoE0Axu3jVphGKFao");
        PlacesClient placesClient = Places.createClient(this);
        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setHint("Building address");
        //List<String> placeTypes = new ArrayList<String>();
        //placeTypes.add()
        ((EditText)autocompleteSupportFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setTextSize(12.0f);
        ((EditText)autocompleteSupportFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setTextColor(Color.BLACK);
        ((EditText)autocompleteSupportFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setHintTextColor(Color.BLACK);
        ((EditText)autocompleteSupportFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)).setTypeface(null, Typeface.BOLD);
        autocompleteSupportFragment.setTypesFilter(Arrays.asList(PlaceTypes.ADDRESS));

        autocompleteSupportFragment.setCountries("CA");
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACE FOUND", "Place: " + place);
                Log.i("PLACE FOUND", "Place: " + place.getAddressComponents().asList());
                autocompleteSupportFragment.setHint(place.getAddress());
               // for(int i = 0; i<place.getAddressComponents().)
                List<AddressComponent> components = place.getAddressComponents().asList();
                for(int i = 0; i < components.size(); i++)
                {
                    if(components.get(i).getTypes().contains("street_number"))
                    {
                        streetNumber = components.get(i).getName();
                        Log.d("Street number: ", components.get(i).getName());
                    }
                    else if(components.get(i).getTypes().contains("route"))
                    {
                        street = components.get(i).getName();
                        Log.d("Street: ", components.get(i).getName());
                    }
                    else if(components.get(i).getTypes().contains("locality"))
                    {
                        city = components.get(i).getName();
                        Log.d("City: ", components.get(i).getName());
                    }
                    else if(components.get(i).getTypes().contains("administrative_area_level_1"))
                    {
                        province = components.get(i).getName();
                        Log.d("Province: ", components.get(i).getName());
                    }
                    else if(components.get(i).getTypes().contains("country"))
                    {
                        country = components.get(i).getName();
                        Log.d("Country: ", components.get(i).getName());
                    }
                }

            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("ERROR, PLACE NOT FOUND", "An error occurred: " + status);
            }
        });
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
                String email, password, firstName, lastName, phone, unit;
                firstName = firstNameEdit.getText().toString();
                lastName = lastNameEdit.getText().toString();
                email = emailEdit.getText().toString();
                phone = phoneEdit.getText().toString();
                password = passwordEdit.getText().toString();
                unit = apartmentNumberEdit.getText().toString();
                if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName))
                {
                    Toast.makeText(RegisterActivity.this, "First or last name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(unit))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter an apartment", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(country) || TextUtils.isEmpty(province) || TextUtils.isEmpty(city) || TextUtils.isEmpty(street) || TextUtils.isEmpty(streetNumber))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }
                //-------------------------------------------------------------
                String country1= country;//Edit.getText().toString();
                String province1= province;//Edit.getText().toString();
                String city1 = city;//Edit.getText().toString();
                String street1= street;//Edit.getText().toString();
                String address1= streetNumber;//addressEdit.getText().toString();
                String unit1= apartmentNumberEdit.getText().toString();
                String combinedAddress1 = country1 + "|" + province1 + "|" + city1 + "|" + street1 + "|" + address1+ "|" + unit1;
                combinedAddress1 = combinedAddress1.toLowerCase();
                combinedAddress1 = combinedAddress1.replaceAll(" ", "");
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                DocumentReference docIdRef = rootRef.collection("users").document(combinedAddress1);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(RegisterActivity.this, "This unit has already been registered. Please consult your building manager.", Toast.LENGTH_SHORT).show();
                                Log.d("document exists", task.getResult().toString());
                            } else {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    //Log.d(TAG, "createUserWithEmail:success");
                                                    //Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                    FirebaseUser user = mAuth.getCurrentUser();//don't need?


                                                    String phone = phoneEdit.getText().toString();
                                                    String unit= apartmentNumberEdit.getText().toString();
                                                    String combinedAddress = country + "|" + province + "|" + city + "|" + street + "|" + streetNumber + "|" + unit;
                                                    combinedAddress = combinedAddress.toLowerCase();
                                                    combinedAddress = combinedAddress.replaceAll(" ", "");

                                                    //Adding user to db
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("firstName", firstNameEdit.getText().toString());
                                                    userData.put("lastName", lastNameEdit.getText().toString());
                                                    userData.put("uid", user.getUid());
                                                    userData.put("address", combinedAddress);
                                                    userData.put("unit", unit);
                                                    userData.put("boxNumber", null);
                                                    userData.put("accessCode", null);
                                                    userData.put("role", "user");//the building manager role must be done directly through database by authorized member.
                                                    userData.put("verified", false);
                                                    userData.put("email", user.getEmail());
                                                    userData.put("phone", phone);

                                                    db.collection("users").document(combinedAddress)
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
                                Log.d("document doesn't exist", "User can be registered");
                            }
                        } else {
                            Log.d("query failed", "Failed with: ", task.getException());
                        }
                    }
                });
                //-------------------------------------------------------------

            }
        });
    }


}
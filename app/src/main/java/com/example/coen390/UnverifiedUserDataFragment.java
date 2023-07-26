package com.example.coen390;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.coen390.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UnverifiedUserDataFragment extends DialogFragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    User user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView descriptionTV, emailTV, phoneTV;
    Button acceptBTN, rejectBTN;
    protected SharedPreferencesHelper spHelper;

    private String mParam1;
    private String mParam2;

    public UnverifiedUserDataFragment() {
        // Required empty public constructor
    }


    public UnverifiedUserDataFragment newInstance(User uu) {
        UnverifiedUserDataFragment fragment = new UnverifiedUserDataFragment();
        Bundle args = new Bundle();

        this.user = uu;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ManagerActivity managerActivity = ((ManagerActivity) getActivity());
        spHelper = new SharedPreferencesHelper(managerActivity);
        user = spHelper.getUnverifiedUserData();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unverified_user_data, container, false);
        descriptionTV = view.findViewById(R.id.descriptionTV);
        emailTV = view.findViewById(R.id.emailTV);
        phoneTV = view.findViewById(R.id.phoneNumberTV);
        acceptBTN = view.findViewById(R.id.acceptBTN);
        rejectBTN = view.findViewById(R.id.rejectBTN);
        descriptionTV.setText(user.getFirstName() + " would like to be verified for unit " + user.getUnit());
        //must add contact info at a later date
        emailTV.setText("Email: ");
        phoneTV.setText("Phone: ");
        acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference washingtonRef = db.collection("users").document(user.getAddress());

                washingtonRef
                        .update("verified", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(user.getFirstName() + "successfully verified", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(user.getFirstName() + "verification failed", "Error updating document", e);
                            }
                        });
                getActivity().recreate();
                dismiss();
            }
        });
        rejectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Bundle bundle = this.getArguments();
        if (getDialog() != null && getDialog().getWindow() != null) {//this is what makes the back window transparent for actual rounded corners
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (bundle != null) {
            user = (User) bundle.getSerializable("userData");
        }
        // Inflate the layout for this fragment
        return view;
    }

}

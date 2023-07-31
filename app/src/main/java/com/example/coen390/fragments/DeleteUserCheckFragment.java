package com.example.coen390.fragments;

import android.content.Intent;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390.ManagerActivity;
import com.example.coen390.ManagerUserProfileActivity;
import com.example.coen390.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteUserCheckFragment extends DialogFragment {

    Button delete, cancel;
    FirebaseAuth mAuth =  FirebaseAuth.getInstance();
    TextView description;
    String address, first, last, unit;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DeleteUserCheckFragment() {
    }
    public static DeleteUserCheckFragment newInstance(String address)
    {
        DeleteUserCheckFragment f = new DeleteUserCheckFragment();
        Bundle args = new Bundle();
        args.putString("address", address);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address = getArguments().getString("address");
        first = getArguments().getString("fn");
        last = getArguments().getString("ln");
        unit = getArguments().getString("unit");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_user_check, container, false);
        delete = view.findViewById(R.id.deleteBTN);
        cancel = view.findViewById(R.id.cancelBTN);
        description = view.findViewById(R.id.description);
        if(first != null && last != null && unit != null) {
            description.setText("Remove " + first + " " + last + " from unit " + unit + "?");
        }
        else {
            description.setText("Are you sure you want to remove this user?");
        }
        if (getDialog() != null && getDialog().getWindow() != null) {//this is what makes the back window transparent for actual rounded corners
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "";
                db.collection("users").document(address)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(address + "successfully deleted", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("unable to reject " + address, "Error deleting document", e);
                            }
                        });
                Toast.makeText(getActivity(), "User Removed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ManagerActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}

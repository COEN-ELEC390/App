package com.example.coen390.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.coen390.R;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryDataFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    HashMap<String, Object> map;
    TextView deliveryDate;
    TextView pickupDate;
    TextView boxNumber;
    TextView accessCode;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeliveryDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeliveryDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public DeliveryDataFragment newInstance(HashMap<String, Object> map) {
        DeliveryDataFragment fragment = new DeliveryDataFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        this.map = map;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_data, container, false);
        deliveryDate = view.findViewById(R.id.deliveryDateTV);
        pickupDate = view.findViewById(R.id.pickupDateTV);
        accessCode = view.findViewById(R.id.accessCodeTV);
        boxNumber = view.findViewById(R.id.boxNumberTV);
        Bundle bundle = this.getArguments();
        if (getDialog() != null && getDialog().getWindow() != null) {//this is what makes the back window transparent for actual rounded corners
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (bundle != null) {
            map = (HashMap<String, Object>) bundle.getSerializable("HashMap");
            for (Map.Entry<String, Object> eventData : map.entrySet()) {
                Log.d("eventData", eventData.toString());
                if (eventData.getKey().toString().contains("deliveryTimestamp")) {
                    Timestamp deliveryTime = (Timestamp) eventData.getValue();
                    deliveryDate.setText("Delivered on: " + deliveryTime.toDate().toString());
                } else if (eventData.getKey().toString().contains("pickupTimestamp")) {
                    if(eventData.getValue() == null)
                    {
                        pickupDate.setText("Has not been picked up");
                    }
                    else
                    {
                        Timestamp pickupTime = (Timestamp) eventData.getValue();
                        pickupDate.setText("Picked up on: " + pickupTime.toDate().toString());
                    }

                } else if (eventData.getKey().toString().contains("boxNumber")) {
                    String tmp = eventData.getValue().toString();
                    int start = tmp.indexOf("[");
                    int end = tmp.indexOf("]");
                    boxNumber.setText("Box Number: " + tmp.substring(start + 1, end));
                } else if (eventData.getKey().toString().contains("accessCode")) {
                    String tmp = eventData.getValue().toString();
                    int start = tmp.indexOf("[");
                    int end = tmp.indexOf("]");
                    accessCode.setText("Access Code: " + tmp.substring(start + 1, end));
                }
            }

        }
        // Inflate the layout for this fragment
        return view;
    }

}
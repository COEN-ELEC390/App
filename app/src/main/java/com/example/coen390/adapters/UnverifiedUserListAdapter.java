package com.example.coen390.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coen390.R;

import java.util.ArrayList;
import java.util.List;

public class UnverifiedUserListAdapter extends ArrayAdapter<String> {


    public UnverifiedUserListAdapter(@NonNull Context context, ArrayList<String> dataArrayList) {
        super(context, R.layout.unverified_user_list_design, dataArrayList);
    }

    public UnverifiedUserListAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public UnverifiedUserListAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }

    public UnverifiedUserListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public UnverifiedUserListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    public UnverifiedUserListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        String listData = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.unverified_user_list_design, parent, false);
        }
        //ImageView listImage = view.findViewById(R.id.listImage);
        TextView userInfo = view.findViewById(R.id.userInfoTV);
        userInfo.setText(listData);
        return view;
    }
}

package com.example.coen390.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coen390.Models.EventListItem;
import com.example.coen390.R;

public class EventListAdapter extends ArrayAdapter<String> {
    public EventListAdapter(@NonNull Context context, ArrayList<String> dataArrayList) {
        super(context, R.layout.event_list_design, dataArrayList);
    }

    public EventListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public EventListAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public EventListAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource);
    }

    public EventListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
        super(context, resource);
    }

    public EventListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource);
    }

    public EventListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        String listData = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_design, parent, false);
        }
        //ImageView listImage = view.findViewById(R.id.listImage);
        TextView deliveryTime = view.findViewById(R.id.deliveryTime);
        deliveryTime.setText(listData);
        return view;
    }
}
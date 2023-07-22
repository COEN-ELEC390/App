package com.example.coen390;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390.Models.RecyclerViewLockerItem;

import java.util.ArrayList;

public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<LockerRecyclerViewAdapter.ViewHolder> {
    ArrayList<RecyclerViewLockerItem> lockerArrayList;
    Context context;

    public LockerRecyclerViewAdapter(ArrayList<RecyclerViewLockerItem> lockerArrayList, Context context) {
        this.lockerArrayList = lockerArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public LockerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manager_locker_design, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LockerRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.boxNumberTV.setText("Locker " + lockerArrayList.get(position).boxNumber);
        holder.boxStatusTV.setText(lockerArrayList.get(position).status);
        holder.accessCodeTV.setText("Last known access code: " + lockerArrayList.get(position).accessCode);
        //holder.userDocNameTV.setText(lockerArrayList.get(position).userDocName);
        String userAddress = lockerArrayList.get(position).userDocName;
        int tmp = userAddress.lastIndexOf('|');
        String unit = userAddress.substring(tmp + 1);
        holder.userDocNameTV.setText("Latest Client: Unit " + unit);
        if(lockerArrayList.get(position).status.contains("OCCUPIED") && lockerArrayList.get(position).deliveryTime != null)
        {
            holder.deliveryTV.setText("Delivered on: " + lockerArrayList.get(position).deliveryTime.toString());
        }
        else if(lockerArrayList.get(position).status.contains("VACANT") && lockerArrayList.get(position).pickupTime != null)
        {
            holder.deliveryTV.setText("Delivered on: " + lockerArrayList.get(position).deliveryTime.toString());
            holder.pickupTV.setText("Picked up on: " + lockerArrayList.get(position).pickupTime.toString());
        }
        holder.titleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockerArrayList.get(position).isVisible)
                {
                    holder.titleLineRL.setVisibility(View.VISIBLE);
                    holder.detailsLineRL.setVisibility(View.GONE);
                    holder.accessCodeRL.setVisibility(View.GONE);
                    holder.deliveryRL.setVisibility(View.GONE);
                    holder.deliveryTV.setVisibility(View.GONE);
                    holder.pickupTV.setVisibility(View.GONE);
                    holder.unitLineRL.setVisibility(View.GONE);
                    holder.userDocNameTV.setVisibility(View.GONE);
                    holder.accessCodeTV.setVisibility(View.GONE);
                    holder.dropdownArrow.setImageResource(R.drawable.ic_dropdown);
                    lockerArrayList.get(position).isVisible = false;
                }
                else
                {
                    holder.titleLineRL.setVisibility(View.GONE);
                    holder.deliveryTV.setVisibility(View.VISIBLE);
                    holder.pickupTV.setVisibility(View.VISIBLE);
                    holder.detailsLineRL.setVisibility(View.VISIBLE);
                    holder.deliveryRL.setVisibility(View.VISIBLE);
                    holder.accessCodeRL.setVisibility(View.VISIBLE);
                    holder.unitLineRL.setVisibility(View.VISIBLE);
                    holder.accessCodeTV.setVisibility(View.VISIBLE);
                    holder.userDocNameTV.setVisibility(View.VISIBLE);
                    holder.dropdownArrow.setImageResource(R.drawable.ic_dropup);
                    lockerArrayList.get(position).isVisible = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lockerArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
            TextView boxNumberTV, boxStatusTV, userDocNameTV, accessCodeTV, deliveryTV, pickupTV;
            LinearLayout titleLL;
        ImageView dropdownArrow;
        RelativeLayout titleLineRL, detailsLineRL, unitLineRL, accessCodeRL, deliveryRL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accessCodeTV = itemView.findViewById(R.id.accessCodeTV);
            titleLL = itemView.findViewById(R.id.titleLinearLayout);
            boxNumberTV = itemView.findViewById(R.id.boxNumberTV);
            boxStatusTV = itemView.findViewById(R.id.lockerStatusTV);
            userDocNameTV = itemView.findViewById(R.id.unitNumberTV);
            titleLineRL = itemView.findViewById(R.id.titleRelativeLayoutLine);
            detailsLineRL = itemView.findViewById(R.id.detailsRelativeLayoutLine);
            unitLineRL = itemView.findViewById(R.id.unitRelativeLayoutLine);
            deliveryTV = itemView.findViewById(R.id.deliveryDateTV);
            accessCodeRL = itemView.findViewById(R.id.accessCodeRelativeLayoutLine);
            pickupTV = itemView.findViewById(R.id.pickupDateTV);
            deliveryRL = itemView.findViewById(R.id.deliveryRelativeLayoutLine);
            dropdownArrow = itemView.findViewById(R.id.dropdownIconIV);
        }
    }
}

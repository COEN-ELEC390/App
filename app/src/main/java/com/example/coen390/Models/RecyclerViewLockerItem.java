package com.example.coen390.Models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class RecyclerViewLockerItem {
    public String accessCode, boxNumber, userDocName, status;
    public Date deliveryTime, pickupTime;
    public boolean isVisible;

    public RecyclerViewLockerItem(String accessCode, String boxNumber, String userDocName, Date deliveryTime, Date pickupTime, boolean isVisible) {
        this.accessCode = accessCode;
        this.boxNumber = boxNumber;
        this.userDocName = userDocName;
        this.deliveryTime = deliveryTime;
        this.pickupTime = pickupTime;
        this.isVisible = isVisible;
        if(pickupTime == null || pickupTime.before(deliveryTime))
        {
            status = "OCCUPIED";
        }
        else
        {
            status = "VACANT";
        }
    }
}

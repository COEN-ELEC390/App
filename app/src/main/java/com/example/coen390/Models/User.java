package com.example.coen390.Models;

public class User {
    String firstName;
    String lastName;
    String uid;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    String unit;


    String role;
    String address, boxNumber, accessCode;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public User(String firstName, String lastName, String uid,String address, String unit, String boxNumber, String accessCode, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.uid = uid;
        this.unit = unit;
        this.boxNumber = boxNumber;
        this.accessCode = accessCode;
        this.role = role;
    }


}
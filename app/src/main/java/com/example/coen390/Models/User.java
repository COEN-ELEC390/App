package com.example.coen390.Models;

public class User {
    String firstName;
    String lastName;
    String uid;
    public String country;
    String province;
    String city;
    String street;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public User(String firstName, String lastName, String uid, String country, String province, String city, String street, String streetNumber, String unit, String boxNumber, String accessCode, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.country = country;
        this.province = province;
        this.city = city;
        this.street = street;
        this.unit = unit;
        this.address = streetNumber;
        this.boxNumber = boxNumber;
        this.accessCode = accessCode;
        this.role = role;
    }


}

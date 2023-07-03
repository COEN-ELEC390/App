package com.example.coen390.Models;

public class User {
    String firstName;
    String lastName;
    String uid;
    String country;
    String province;
    String city;
    String street;
    String apartmentNumber;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    String role;
    String address, boxNumber, accessCode;

    public User(String firstName, String lastName, String uid, String country, String province, String city, String street, String streetNumber, String apartmentNumber, String boxNumber, String accessCode, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.country = country;
        this.province = province;
        this.city = city;
        this.street = street;
        this.apartmentNumber = apartmentNumber;
        this.address = streetNumber;
        this.boxNumber = boxNumber;
        this.accessCode = accessCode;
        this.role = role;
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

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getStreetNumber() {
        return address;
    }

    public void setStreetNumber(String streetNumber) {
        this.address = streetNumber;
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
}

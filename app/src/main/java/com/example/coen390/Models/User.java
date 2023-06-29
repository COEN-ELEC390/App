package com.example.coen390.Models;

public class User {
    String firstName, lastName, uid, country, province, city, street, apartmentNumber;
    int streetNumber, boxNumber, accessCode;

    public User(String firstName, String lastName, String uid, String country, String province, String city, String street, int streetNumber, String apartmentNumber, int boxNumber, int accessCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.country = country;
        this.province = province;
        this.city = city;
        this.street = street;
        this.apartmentNumber = apartmentNumber;
        this.streetNumber = streetNumber;
        this.boxNumber = boxNumber;
        this.accessCode = accessCode;
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

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public int getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(int accessCode) {
        this.accessCode = accessCode;
    }
}

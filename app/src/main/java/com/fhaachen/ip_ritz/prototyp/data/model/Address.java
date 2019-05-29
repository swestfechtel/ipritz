package com.fhaachen.ip_ritz.prototyp.data.model;

public class Address {
    private String _id;
    private String streetAddress;
    private String houseNumber;
    private String zipCode;
    private String city;
    private String country;

    public Address ( String _id , String streetAddress , String houseNumber , String zipCode , String city , String country ) {
        this._id = _id;
        this.streetAddress = streetAddress;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public Address ( String streetAddress , String houseNumber , String zipCode , String city , String country ) {
        this.streetAddress = streetAddress;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public String get_id () {
        return _id;
    }

    public void set_id ( String _id ) {
        this._id = _id;
    }

    public String getStreetAddress () {
        return streetAddress;
    }

    public void setStreetAddress ( String streetAddress ) {
        this.streetAddress = streetAddress;
    }

    public String getHouseNumber () {
        return houseNumber;
    }

    public void setHouseNumber ( String houseNumber ) {
        this.houseNumber = houseNumber;
    }

    public String getZipCode () {
        return zipCode;
    }

    public void setZipCode ( String zipCode ) {
        this.zipCode = zipCode;
    }

    public String getCity () {
        return city;
    }

    public void setCity ( String city ) {
        this.city = city;
    }

    public String getCountry () {
        return country;
    }

    public void setCountry ( String country ) {
        this.country = country;
    }
}

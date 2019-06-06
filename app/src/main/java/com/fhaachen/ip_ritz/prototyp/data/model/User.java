package com.fhaachen.ip_ritz.prototyp.data.model;

import java.util.ArrayList;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    private _id _id;
    private String firstName;
    private String lastName;
    private Address addresses;
    private String job;
    private String language;
    private String passwordHash;
    private String email;
    private String telNumber;
    private ArrayList < String > journeys;
    private String birthdate;
    private boolean registered;
    private String authentificationCode;
    private ArrayList < String > friends;
    private ArrayList < Location > currentLocation;
    private ArrayList < String > prefferedLandingField;
    private ArrayList < String > lastDestinations;


    public User ( com.fhaachen.ip_ritz.prototyp.data.model._id _id , String firstName , String lastName , Address addresses , String job , String language , String passwordHash , String email , String telNumber , ArrayList < String > journeys , String birthdate , boolean registered , String authentificationCode , ArrayList < String > friends , ArrayList < Location > currentLocation , ArrayList < String > prefferedLandingField , ArrayList < String > lastDestinations ) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = addresses;
        this.job = job;
        this.language = language;
        this.passwordHash = passwordHash;
        this.email = email;
        this.telNumber = telNumber;
        this.journeys = journeys;
        this.birthdate = birthdate;
        this.registered = registered;
        this.authentificationCode = authentificationCode;
        this.friends = friends;
        this.currentLocation = currentLocation;
        this.prefferedLandingField = prefferedLandingField;
        this.lastDestinations = lastDestinations;
    }

    public User ( String firstName , String lastName , Address addresses , String job , String language , String passwordHash , String email , String telNumber , ArrayList < String > journeys , String birthdate , boolean registered , String authentificationCode , ArrayList < String > friends , ArrayList < Location > currentLocation , ArrayList < String > prefferedLandingField , ArrayList < String > lastDestinations ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = addresses;
        this.job = job;
        this.language = language;
        this.passwordHash = passwordHash;
        this.email = email;
        this.telNumber = telNumber;
        this.journeys = journeys;
        this.birthdate = birthdate;
        this.registered = registered;
        this.authentificationCode = authentificationCode;
        this.friends = friends;
        this.currentLocation = currentLocation;
        this.prefferedLandingField = prefferedLandingField;
        this.lastDestinations = lastDestinations;
    }

    public com.fhaachen.ip_ritz.prototyp.data.model._id get_id () {
        return _id;
    }

    public void set_id ( com.fhaachen.ip_ritz.prototyp.data.model._id _id ) {
        this._id = _id;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName ( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName ( String lastName ) {
        this.lastName = lastName;
    }

    public Address getAddresses () {
        return addresses;
    }

    public void setAddresses ( Address addresses ) {
        this.addresses = addresses;
    }

    public String getJob () {
        return job;
    }

    public void setJob ( String job ) {
        this.job = job;
    }

    public String getLanguage () {
        return language;
    }

    public void setLanguage ( String language ) {
        this.language = language;
    }

    public String getPasswordHash () {
        return passwordHash;
    }

    public void setPasswordHash ( String passwordHash ) {
        this.passwordHash = passwordHash;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail ( String email ) {
        this.email = email;
    }

    public String getTelNumber () {
        return telNumber;
    }

    public void setTelNumber ( String telNumber ) {
        this.telNumber = telNumber;
    }

    public ArrayList < String > getJourneys () {
        return journeys;
    }

    public void setJourneys ( ArrayList < String > journeys ) {
        this.journeys = journeys;
    }

    public String getBirthdate () {
        return birthdate;
    }

    public void setBirthdate ( String birthdate ) {
        this.birthdate = birthdate;
    }

    public boolean isRegistered () {
        return registered;
    }

    public void setRegistered ( boolean registered ) {
        this.registered = registered;
    }

    public String getAuthentificationCode () {
        return authentificationCode;
    }

    public void setAuthentificationCode ( String authentificationCode ) {
        this.authentificationCode = authentificationCode;
    }

    public ArrayList < String > getFriends () {
        return friends;
    }

    public void setFriends ( ArrayList < String > friends ) {
        this.friends = friends;
    }

    public ArrayList < Location > getCurrentLocation () {
        return currentLocation;
    }

    public void setCurrentLocation ( ArrayList < Location > currentLocation ) {
        this.currentLocation = currentLocation;
    }

    public ArrayList < String > getPrefferedLandingField () {
        return prefferedLandingField;
    }

    public void setPrefferedLandingField ( ArrayList < String > prefferedLandingField ) {
        this.prefferedLandingField = prefferedLandingField;
    }

    public ArrayList < String > getLastDestinations () {
        return lastDestinations;
    }

    public void setLastDestinations ( ArrayList < String > lastDestinations ) {
        this.lastDestinations = lastDestinations;
    }
}

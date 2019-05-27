package com.fhaachen.ip_ritz.prototyp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String surname;

    public String getSurname () {
        return surname;
    }

    public String getMailAddress () {
        return mailAddress;
    }

    private String mailAddress;

    public LoggedInUser ( String userId , String displayName , String surname , String mailAddress ) {
        this.userId = userId;
        this.displayName = displayName;
        this.surname = surname;
        this.mailAddress = mailAddress;
    }

    public String getUserId () {
        return userId;
    }

    public String getDisplayName () {
        return displayName;
    }
}

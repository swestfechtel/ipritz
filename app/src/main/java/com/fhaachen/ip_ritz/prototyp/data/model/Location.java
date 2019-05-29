package com.fhaachen.ip_ritz.prototyp.data.model;

public class Location {
    private String _id;
    private float latitude;
    private float longitude;

    public Location ( String _id , float latitude , float longitude ) {
        this._id = _id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location ( float latitude , float longitude ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String get_id () {
        return _id;
    }

    public void set_id ( String _id ) {
        this._id = _id;
    }

    public float getLatitude () {
        return latitude;
    }

    public void setLatitude ( float latitude ) {
        this.latitude = latitude;
    }

    public float getLongitude () {
        return longitude;
    }

    public void setLongitude ( float longitude ) {
        this.longitude = longitude;
    }
}

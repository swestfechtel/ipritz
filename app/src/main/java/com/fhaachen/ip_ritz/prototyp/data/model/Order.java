package com.fhaachen.ip_ritz.prototyp.data.model;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private String id;
    private String purchaser;
    private ArrayList < String > passengers;
    private Date orderTime;
    private Date startTime;
    private Date arrivalTime;
    private float price;
    private ArrayList < String > startLocation;
    private ArrayList < String > destinationLocation;
    private String startAddress;
    private String destinationAddress;
    private boolean cancelled;
    private String complaints;
    private boolean paid;
    private boolean confirmed;
    private String paymentMethod;
    private int priority;
    private String droneId;
    private boolean completed;

    public Order ( String id , String purchaser , ArrayList < String > passengers , Date orderTime , Date startTime , Date arrivalTime , float price , ArrayList < String > startLocation , ArrayList < String > destinationLocation , String startAddress , String destinationAddress , boolean cancelled , String complaints , boolean paid , boolean confirmed , String paymentMethod , int priority , String droneId , boolean completed ) {
        this.id = id;
        this.purchaser = purchaser;
        this.passengers = passengers;
        this.orderTime = orderTime;
        this.startTime = startTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.startLocation = startLocation;
        this.destinationLocation = destinationLocation;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
        this.cancelled = cancelled;
        this.complaints = complaints;
        this.paid = paid;
        this.confirmed = confirmed;
        this.paymentMethod = paymentMethod;
        this.priority = priority;
        this.droneId = droneId;
        this.completed = completed;
    }

    public Order ( String purchaser , ArrayList < String > startLocation , ArrayList < String > destinationLocation ) {
        this.purchaser = purchaser;
        this.startLocation = startLocation;
        this.destinationLocation = destinationLocation;
    }

    public String getId () {
        return id;
    }

    public void setId ( String id ) {
        this.id = id;
    }

    public String getPurchaser () {
        return purchaser;
    }

    public void setPurchaser ( String purchaser ) {
        this.purchaser = purchaser;
    }

    public ArrayList < String > getPassengers () {
        return passengers;
    }

    public void setPassengers ( ArrayList < String > passengers ) {
        this.passengers = passengers;
    }

    public Date getOrderTime () {
        return orderTime;
    }

    public void setOrderTime ( Date orderTime ) {
        this.orderTime = orderTime;
    }

    public Date getStartTime () {
        return startTime;
    }

    public void setStartTime ( Date startTime ) {
        this.startTime = startTime;
    }

    public Date getArrivalTime () {
        return arrivalTime;
    }

    public void setArrivalTime ( Date arrivalTime ) {
        this.arrivalTime = arrivalTime;
    }

    public float getPrice () {
        return price;
    }

    public void setPrice ( float price ) {
        this.price = price;
    }

    public ArrayList < String > getStartLocation () {
        return startLocation;
    }

    public void setStartLocation ( ArrayList < String > startLocation ) {
        this.startLocation = startLocation;
    }

    public ArrayList < String > getDestinationLocation () {
        return destinationLocation;
    }

    public void setDestinationLocation ( ArrayList < String > destinationLocation ) {
        this.destinationLocation = destinationLocation;
    }

    public String getStartAddress () {
        return startAddress;
    }

    public void setStartAddress ( String startAddress ) {
        this.startAddress = startAddress;
    }

    public String getDestinationAddress () {
        return destinationAddress;
    }

    public void setDestinationAddress ( String destinationAddress ) {
        this.destinationAddress = destinationAddress;
    }

    public boolean isCancelled () {
        return cancelled;
    }

    public void setCancelled ( boolean cancelled ) {
        this.cancelled = cancelled;
    }

    public String getComplaints () {
        return complaints;
    }

    public void setComplaints ( String complaints ) {
        this.complaints = complaints;
    }

    public boolean isPaid () {
        return paid;
    }

    public void setPaid ( boolean paid ) {
        this.paid = paid;
    }

    public boolean isConfirmed () {
        return confirmed;
    }

    public void setConfirmed ( boolean confirmed ) {
        this.confirmed = confirmed;
    }

    public String getPaymentMethod () {
        return paymentMethod;
    }

    public void setPaymentMethod ( String paymentMethod ) {
        this.paymentMethod = paymentMethod;
    }

    public int getPriority () {
        return priority;
    }

    public void setPriority ( int priority ) {
        this.priority = priority;
    }

    public String getDroneId () {
        return droneId;
    }

    public void setDroneId ( String droneId ) {
        this.droneId = droneId;
    }

    public boolean isCompleted () {
        return completed;
    }

    public void setCompleted ( boolean completed ) {
        this.completed = completed;
    }

    private void updateOrder () {

    }
}

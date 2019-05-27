package com.fhaachen.ip_ritz.prototyp.data.model;

import java.util.Date;

public class Order {
    public Order ( String purchaser , String[] startLocation , String[] destinationLocation ) {
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

    public String[] getPassengers () {
        return passengers;
    }

    public void setPassengers ( String[] passengers ) {
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

    public String[] getStartLocation () {
        return startLocation;
    }

    public void setStartLocation ( String[] startLocation ) {
        this.startLocation = startLocation;
    }

    public String[] getDestinationLocation () {
        return destinationLocation;
    }

    public void setDestinationLocation ( String[] destinationLocation ) {
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

    private String id;
    private String purchaser;
    private String[] passengers;
    private Date orderTime;
    private Date startTime;
    private Date arrivalTime;
    private float price;
    private String[] startLocation;
    private String[] destinationLocation;
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
}

package com.example.mapdemo;

import android.support.annotation.DrawableRes;

import com.google.android.gms.maps.model.LatLng;

public class Car {
    @DrawableRes
    public int color;

    public double latitude;
    public double longitude;
    public double lastSpeed;
    public boolean isLeader;

    public Car(int color, double latitude, double longitude, double lastSpeed, boolean isLeader){
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastSpeed = lastSpeed;
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return this.isLeader;
    }

    public Car(){

    }

    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }


}

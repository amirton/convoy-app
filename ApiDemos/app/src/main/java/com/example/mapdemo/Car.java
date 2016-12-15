package com.example.mapdemo;

import android.support.annotation.DrawableRes;

import com.google.android.gms.maps.model.LatLng;

import static com.example.mapdemo.R.drawable.car;

public class Car {
    @DrawableRes
    public int getColor(){
        if (this.isLeader && this.isInEmergency){
            return R.drawable.blueredcar;
        } else if (this.isLeader && this.lastSpeed == 0) {
            return R.drawable.blueyellowcar;
        } else if (this.isLeader) {
            return R.drawable.bluecar;
        } else if (this.isInEmergency){
            return R.drawable.redcar;
        } else if (this.lastSpeed == 0){
            return R.drawable.yellowcar;
        } else {
            return car;
        }
    }

    public double latitude;
    public double longitude;
    public double lastSpeed;
    public boolean isLeader;
    public boolean isInEmergency;

    public Car(double latitude, double longitude, double lastSpeed, boolean isLeader){
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastSpeed = lastSpeed;
        this.isLeader = isLeader;
        this.isInEmergency = false;
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

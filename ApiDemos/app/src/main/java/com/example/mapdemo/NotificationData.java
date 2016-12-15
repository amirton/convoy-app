package com.example.mapdemo;

/**
 * Created by amirt on 09/12/2016.
 */

public class NotificationData {
    private static final int INVALID_VALUE = Integer.MIN_VALUE;

    private String carId;
    private double latitude;
    private double longitude;
    private double lastSpeed;
    private boolean emergency;
    private boolean programmedStop;
    private boolean isLeader;
    private boolean isRegister;

    public String getCarId(){
        return carId;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getLastSpeed(){
        return lastSpeed;
    }
    public boolean isEmergency(){
        return emergency;
    }
    public boolean isProgrammedStop(){
        return programmedStop;
    }
    public boolean isLeader(){
        return isLeader;
    }
    public boolean isRegister() { return isRegister;}

    public boolean hasCarId(){
        return !this.carId.isEmpty();
    }
    public boolean hasLatitude(){
        return this.latitude != INVALID_VALUE;
    }
    public boolean hasLongitude(){
        return this.longitude != INVALID_VALUE;
    }
    public boolean hasLastSpeed(){
        return this.lastSpeed != INVALID_VALUE;
    }

    private NotificationData(){

    }


    public static class Builder {
        NotificationData result;

        public Builder(){
            result = new NotificationData();
            result.carId = "";
            result.latitude = INVALID_VALUE;
            result.longitude = INVALID_VALUE;
            result.lastSpeed = INVALID_VALUE;
        }

        public Builder setCarId(String carId){
            result.carId = carId;
            return this;
        }
        public Builder setLatitude(double latitude){
            result.latitude = latitude;
            return this;
        }
        public Builder setLongitude(double longitude){
            result.longitude = longitude;
            return this;
        }
        public Builder setLastSpeed(double lastSpeed){
            result.lastSpeed = lastSpeed;
            return this;
        }
        public Builder setEmergency(boolean emergency){
            result.emergency = emergency;
            return this;
        }
        public Builder setProgrammedStop(boolean programmedStop){
            result.programmedStop = programmedStop;
            return this;
        }

        public Builder setLeader(boolean isLeader){
            result.isLeader = isLeader;
            return this;
        }

        public Builder setRegister(boolean isRegister){
            result.isRegister = isRegister;
            return this;
        }

        public NotificationData build(){
            return result;
        }
    }
}

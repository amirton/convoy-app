package com.example.mapdemo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;

public class CarData {
    public static Object leaderWayLock = new Object();

    public static HashMap<String, Car> cars = new HashMap<>();

    public static ArrayList<LatLng> leaderWay = new ArrayList<>();

    public static ArrayList<LatLng> getLatLngs(){
        ArrayList<LatLng> result = new ArrayList<>();

        for(Car car : cars.values()){
            result.add(car.getLatLng());
        }

        return result;
    }

    public static LatLngBounds getLatLngBounds(){
        if (getLatLngs() == null
            || getLatLngs().size() == 0){
            return null;
        }

        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (LatLng coords : getLatLngs()){
            builder.include(coords);
        }

        return builder.build();
    }
}

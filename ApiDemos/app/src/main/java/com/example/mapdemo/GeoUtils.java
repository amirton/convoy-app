package com.example.mapdemo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GeoUtils {

    public static LatLng getCentralGeoCoordinate(
            ArrayList<LatLng> geoCoordinates)
    {
        if (geoCoordinates.size() == 1)
        {
            return geoCoordinates.get(0);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (LatLng geoCoordinate : geoCoordinates)
        {
            double latitude = geoCoordinate.latitude * Math.PI / 180;
            double longitude = geoCoordinate.longitude * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        int total = geoCoordinates.size();

        x = x / total;
        y = y / total;
        z = z / total;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);

        return new LatLng(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI);
    }
}

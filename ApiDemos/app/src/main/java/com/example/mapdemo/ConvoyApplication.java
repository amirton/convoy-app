package com.example.mapdemo;

import android.app.Application;

/**
 * Created by amirt on 10/12/2016.
 */

public class ConvoyApplication extends Application {
    private static ConvoyApplication instance;
    public static ConvoyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

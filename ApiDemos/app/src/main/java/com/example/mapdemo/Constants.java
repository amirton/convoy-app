package com.example.mapdemo;

import okhttp3.MediaType;

/**
 * Created by fabiomelo on 12/11/16.
 */

public class Constants {

    //public static final String SERVER_URL = "http://convoyaware.herokuapp.com/";
    public static final String SERVER_URL = "http://convoy-server.azurewebsites.net/";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final long TIME = 300000;
}

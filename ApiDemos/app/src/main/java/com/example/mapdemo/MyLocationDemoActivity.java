/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mapdemo.CarData.cars;
import static com.example.mapdemo.CarData.leaderWay;
import static com.example.mapdemo.R.drawable.car;
import static com.example.mapdemo.R.id.map;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MyLocationDemoActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.home_btn_emergency)
    Button button;

    Ringtone alarm;

    private final static String TAG = MyLocationDemoActivity.class.getName();

    private boolean isLeader = false;

    private static MyLocationDemoActivity instance = null;
    public static MyLocationDemoActivity getInstance(){
        return instance;
    }

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    Handler handler = new Handler();

    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);

        sendRegistrationToServer(MyFirebaseInstanceIdService.getRefreshedToken());

        setContentView(R.layout.my_location_demo);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new PositionListener());

        alarm = RingtoneManager.getRingtone(getApplicationContext(),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createDialogForProgrammedStop();
                handler.postDelayed(this, Constants.TIME);
            }
        }, Constants.TIME);

    }

    public static void sendRegistrationToServer(String token) {
        // Send token to app server.
        Log.i(TAG,"###### Sending Registration to server... ######");
        Log.i(TAG,"#####  " + Constants.SERVER_URL+"register?token="+token + "  #####" );

        new RegisterTokenTask().execute(token);
    }

    private void createDialogForProgrammedStop() {
        playSound();
        if(isLeader){
            (new AlertDialog.Builder(this)).setTitle("DESCANSO!")
                    .setMessage("Tem um posto próximo, você está dirigindo por muito tempo. Deseja parar?")
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopSound();
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            stopSound();

                            Toast.makeText(MyLocationDemoActivity.this, "Enviar mensagem pra o resto da galera", Toast.LENGTH_LONG).show();
                            Map<String, String> result = new HashMap();

                            result.put("isEmergency", false+"");
                            result.put("isProgrammedStop", true+"");
                            result.put("isLeader", isLeader+"");

                            Log.i(TAG, new JSONObject(result).toString());

                            if(MyFirebaseInstanceIdService.getRefreshedToken() != null) {

                                result.put("carId", MyFirebaseInstanceIdService.getRefreshedToken());
                                Log.i(TAG, "##### "+ (new JSONObject(result)).toString() + " #######");

                                new UpdatePositionTask().execute(new JSONObject(result).toString());
                            }
                        }
                    }).create().show();
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        updateMap(true);

    }

    public void updateMap(final boolean gambi){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            @Override
            public void run() {
                mMap.clear();
                for (String car : cars.keySet()){
                    mMap.addMarker(new MarkerOptions().position(cars.get(car).getLatLng())
                            .title(car)
                            .icon(BitmapDescriptorFactory.fromResource(cars.get(car).color)));
                }

                PolylineOptions line = new PolylineOptions();

                synchronized (CarData.leaderWayLock) {
                    LatLng[] points = new LatLng[leaderWay.size()];
                    line.add(leaderWay.toArray(points));
                }

                line.width(5).color(Color.RED);
                mMap.addPolyline(line);

                try {
                    moveMap();
                } catch (Exception ex){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveMap();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void moveMap(){
        LatLngBounds bounds = CarData.getLatLngBounds();

        if(bounds != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.home_btn_emergency)
    public void onEmergencyClick(){
        createConfirmEmergencyDialog();
    }

    @OnLongClick(R.id.home_btn_emergency)
    public boolean onEmergencyLongClick(){
        isLeader = true;
        Toast.makeText(MyLocationDemoActivity.this, "Agora você é o líder!", Toast.LENGTH_LONG).show();
        return isLeader;
    }

    private void createConfirmEmergencyDialog()
    {
        (new AlertDialog.Builder(this)).setTitle("Confirmar envio de emergencia?")
                .setMessage("Todos os outros veículos no seu grupo serão avisados de sua emergência! Deseja continuar?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, String> result = new HashMap();

                        result.put("isEmergency", true+"");
                        result.put("isProgrammedStop", false+"");
                        result.put("isLeader", isLeader+"");

                        Log.i(TAG, new JSONObject(result).toString());

                        if(MyFirebaseInstanceIdService.getRefreshedToken() != null) {

                            result.put("carId", MyFirebaseInstanceIdService.getRefreshedToken());
                            Log.i(TAG, "##### "+ (new JSONObject(result)).toString() + " #######");

                            new UpdatePositionTask().execute(new JSONObject(result).toString());
                        }

               /*         final AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create(); //Read Update
                        alertDialog.setTitle("Emergency Stop!!!");
                        alertDialog.setMessage("O líder parou");

                        alarm = RingtoneManager.getRingtone(getApplicationContext(),
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                        playSound();
                        alertDialog.show();*/

                     /*   // Execute some code after 2 seconds have passed
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopSound();
                                alertDialog.dismiss();
                            }
                        }, 2000);*/


                    }
                }).create().show();
    }


    private void playSound(){
        try {
            //alarm.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopSound(){
        try {
            alarm.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRestDialog()
    {
        (new AlertDialog.Builder(this)).setTitle("Hora de descansar?")
                .setMessage("Seu grupo está dirigindo há X horas sem paradas, que tal parar no próximo posto?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MyLocationDemoActivity.this, "Enviar mensagem pra o resto da galera", Toast.LENGTH_LONG).show();
                    }
                }).create().show();
    }

    private class PositionListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Map<String, String> result = new HashMap();

            result.put("latitude", location.getLatitude() + "");
            result.put("longitude", location.getLongitude() + "");
            result.put("lastSpeed", location.getSpeed() + "");
            result.put("isEmergency", false+"");
            result.put("isProgrammedStop", false+"");
            result.put("isLeader", isLeader+"");

            Log.i(TAG, new JSONObject(result).toString());

            if(MyFirebaseInstanceIdService.getRefreshedToken() != null) {

                result.put("carId", MyFirebaseInstanceIdService.getRefreshedToken());
                Log.i(TAG, "##### "+ (new JSONObject(result)).toString() + " #######");

                new UpdatePositionTask().execute(new JSONObject(result).toString());
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }


    }
}

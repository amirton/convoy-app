package com.example.mapdemo;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.example.mapdemo.CarData.cars;

/**
 * Created by amirt on 09/12/2016.
 */

public class NotificationHandler extends FirebaseMessagingService {

    private static final String TAG = NotificationHandler.class.getSimpleName();
    Ringtone alarm;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> rawData = remoteMessage.getData();

        Log.i("Convoy notification", rawData.toString());

        NotificationData data = parseNotificationData(rawData);

        if (data.isEmergency()){
            alarm = RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            playSound();
            (new AlertDialog.Builder(this)).setTitle("EMERGENCY STOP!")
                    .setMessage("Houve uma parada emergencial")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopSound();
                            dialogInterface.dismiss();
                        }
                    }).create().show();

        } else if (data.isProgrammedStop()){
            alarm = RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            playSound();
            (new AlertDialog.Builder(this)).setTitle("PROGRAMMED STOP!")
                    .setMessage("O houve uma parada programada")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopSound();
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        } else if (data.hasCarId()
                && data.hasLatitude()
                && data.hasLongitude()
                && data.hasLastSpeed()){
            Log.i(TAG, "###### Update position message received #####");
            updateCarPosition(data);
        }
    }


    private void playSound(){
        try {
            alarm.play();
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

    private void updateCarPosition(NotificationData data) {
        Car car;
        if (!cars.containsKey(data.getCarId() + "")){
            car = new Car();
            if(data.isLeader()){
                car.color = R.drawable.bluecar;
                car.isLeader = true;
            }else{
                car.color = R.drawable.car;
                car.isLeader = false;
            }
            car.latitude = data.getLatitude();
            car.longitude = data.getLongitude();
            car.lastSpeed = data.getLastSpeed();
            cars.put(data.getCarId() + "", car);
        }
        //TODO nao entendi
        /*else {
            car = cars.get(data.getCarId() + "");
        }

        car.latitude = data.getLatitude();
        car.longitude = data.getLongitude();
        car.lastSpeed = data.getLastSpeed();
        */
        if (MyLocationDemoActivity.getInstance() != null){
            MyLocationDemoActivity.getInstance().updateMap(true);
        }
    }

    private NotificationData parseNotificationData(Map<String, String> rawData) {
        NotificationData.Builder dataBuilder = new NotificationData.Builder();

        if (rawData.containsKey("carId")){
            dataBuilder.setCarId(rawData.get("carId"));
        }
        if (rawData.containsKey("latitude")){
            dataBuilder.setLatitude(Double.parseDouble(rawData.get("latitude")));
        }
        if (rawData.containsKey("longitude")){
            dataBuilder.setLongitude(Double.parseDouble(rawData.get("longitude")));
        }
        if (rawData.containsKey("lastSpeed")){
            dataBuilder.setLastSpeed(Double.parseDouble(rawData.get("lastSpeed")));
        }
        if (rawData.containsKey("isEmergency")){
            dataBuilder.setEmergency(Boolean.parseBoolean(rawData.get("isEmergency")));
        }
        if (rawData.containsKey("isProgrammedStop")){
            dataBuilder.setProgrammedStop(Boolean.parseBoolean(rawData.get("isProgrammedStop")));
        }
        if (rawData.containsKey("isLeader")){
            dataBuilder.setProgrammedStop(Boolean.parseBoolean(rawData.get("isProgrammedStop")));
        }

        return dataBuilder.build();
    }
}

package com.example.mapdemo;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyActivity extends AppCompatActivity {

    Ringtone alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        ButterKnife.bind(this);

        alarm = RingtoneManager.getRingtone(getApplicationContext(),
                                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        playSound();
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

    @OnClick(R.id.emergency_btn_ok)
    public void onOkClick(){
        stopSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSound();
    }
}

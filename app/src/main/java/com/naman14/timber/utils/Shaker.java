package com.naman14.timber.utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.naman14.timber.MusicPlayer;

/**
 * Created by PS on 6/28/2016.
 */
public class Shaker extends Service implements SensorEventListener {

    SharedPreferences sharedPreferences;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;
    private SensorManager sensorManager;
    private static final String SHAKE_ACTION_PREFERENCE = "shake_action_preference";
    private static final String SHAKE_THRESHOLD_PREFERENCE = "shake_threshold_preference";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                Log.d("Acceleration", "Acceleration is " + acceleration + "m/s^2");

                String threshold = sharedPreferences.getString(SHAKE_THRESHOLD_PREFERENCE, "medium");
                //DREAM_HAWKERS CHAHGE SHAKE_THRESHOLD DEPENDING ON YOUR REQUIREMENT
                float SHAKE_THRESHOLD;
                if(threshold.equals("high"))
                    SHAKE_THRESHOLD = 40.5f;
                else if(threshold.equals("low"))
                    SHAKE_THRESHOLD = 15.5f;
                else
                    SHAKE_THRESHOLD = 25.5f;
                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Log.d("Shake", "Shake, Rattle, and Roll");

                    String opt = sharedPreferences.getString(SHAKE_ACTION_PREFERENCE, "nothing");
                    Log.d("SharedPreference", opt);
                    Log.d("SharedPreference", threshold);
                    if(opt.equals("play")) {
                        MusicPlayer.playOrPause();
                    } else if(opt.equals("next")) {
                        MusicPlayer.next();
                    } else if(opt.equals("prev")) {
                        MusicPlayer.previous(getApplicationContext(), false);
                    }
                }
            }
        }
    }


}

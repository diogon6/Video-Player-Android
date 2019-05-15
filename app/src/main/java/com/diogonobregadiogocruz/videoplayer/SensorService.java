package com.diogonobregadiogocruz.videoplayer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SensorService extends Service {
    private SensorManager _sensorManager;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    //listeners
    private GyroscopeSensorListener gyroscope_listener = new GyroscopeSensorListener();

    VideoMenu videoMenu;

    private long lastUpdate = 0;
    private int cool_down = 0;
    private static final int ROTATION_SENSITIVITY = 2;

    public SensorService() {
        videoMenu = VideoMenu.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int id) {

        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor gyrSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyrSensor != null)
            _sensorManager.registerListener(gyroscope_listener, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            Toast.makeText(getApplicationContext(), "Unfortunately your device does not have Gyroscope", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        _sensorManager.unregisterListener(gyroscope_listener);
    }

    private class GyroscopeSensorListener implements SensorEventListener{

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //If the video is in fullscreen mode we want to change "rotation command"
            // in order to keep the same command int he user's point of view
            int orientation_video_progress;
            int orientation_volume_control;
            if(videoMenu.isFullscreen())
            {
                orientation_video_progress = 0;
                orientation_volume_control = 1;
            }
            else
            {
                orientation_video_progress = 1;
                orientation_volume_control = 0;
            }

            long curTime = System.currentTimeMillis();

            // only allow one update every 100ms.
            if ((curTime - lastUpdate) < cool_down) {
                return;
            }

            lastUpdate = curTime;
            cool_down = 0;

            //Right/Left rotation -> Video Forward/Backward
            if(sensorEvent.values[orientation_video_progress] < - ROTATION_SENSITIVITY)
            {
                videoMenu.slowDownVideo();
                cool_down = 1000;
                return;
            }
            if(sensorEvent.values[orientation_video_progress] > ROTATION_SENSITIVITY)
            {
                videoMenu.speedUpVideo();
                cool_down = 1000;
                return;
            }

            //Up/Down rotation -> Volume Up/Down
            if(sensorEvent.values[orientation_volume_control] < - ROTATION_SENSITIVITY)
            {
                if(videoMenu.isFullscreen())
                {
                    videoMenu.lowerVolume();
                    Toast.makeText(getApplicationContext(), "Volume Down", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    videoMenu.raiseVolume();
                    Toast.makeText(getApplicationContext(), "Volume Up", Toast.LENGTH_SHORT).show();
                }

                cool_down = 1000;
                return;
            }
            if(sensorEvent.values[orientation_volume_control] > ROTATION_SENSITIVITY)
            {
                if(videoMenu.isFullscreen())
                {
                    videoMenu.raiseVolume();
                    Toast.makeText(getApplicationContext(), "Volume Up", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    videoMenu.lowerVolume();
                    Toast.makeText(getApplicationContext(), "Volume Down", Toast.LENGTH_SHORT).show();
                }

                cool_down = 1000;
                return;
            }
        }
    }
}

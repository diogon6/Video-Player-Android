package com.diogonobregadiogocruz.videoplayer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class SensorService extends Service {
    private SensorManager _sensorManager;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    //listeners
    private GyroscopeSensorListener gyroscope_listener = new GyroscopeSensorListener();

    VideoMenu videoMenu;

    private long lastUpdate = 0;                        // Variable used to keep track of the time of last gyroscope trigger
    private int cool_down = 0;                          // Variable used to avoid triggering the gyroscope multiple times in a short time (starts at 0)
    private static final int ROTATION_SENSITIVITY = 2;  // Minimum angular velocity to activate the gyroscope

    public SensorService() {
        // Get the instance of the video menu activity
        videoMenu = VideoMenu.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int id) {

        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get the device's gyroscope
        Sensor gyrSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // If the has gyroscope, then register it
        if (gyrSensor != null)
            _sensorManager.registerListener(gyroscope_listener, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // If it doesn't, inform the user of it
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

        // Called every time the sensor value has changed
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            // If the video is in fullscreen mode we want to change "rotation command"
            // in order to keep the same command in the user's point of view
            int orientation_video_speed_control;
            int orientation_volume_control;
            if(videoMenu.isFullscreen())
            {
                orientation_video_speed_control = 0;
                orientation_volume_control = 1;
            }
            else
            {
                orientation_video_speed_control = 1;
                orientation_volume_control = 0;
            }

            // Get current time
            long curTime = System.currentTimeMillis();

            // If the cool down isn't over yet, return
            // Only allow one update every 1000ms
            if ((curTime - lastUpdate) < cool_down) {
                return;
            }

            // Reset these variables
            lastUpdate = curTime;
            cool_down = 0;

            //Right/Left rotation -> Video Speed Faster/Slower
            if(sensorEvent.values[orientation_video_speed_control] < - ROTATION_SENSITIVITY)
            {
                videoMenu.slowDownVideo();
                cool_down = 1000;
                return;
            }
            if(sensorEvent.values[orientation_video_speed_control] > ROTATION_SENSITIVITY)
            {
                videoMenu.speedUpVideo();
                cool_down = 1000;
                return;
            }

            //Up/Down rotation -> Volume Up/Down
            if(sensorEvent.values[orientation_volume_control] < - ROTATION_SENSITIVITY)
            {
                // Default fullscreen (left tilt) has inverted rotation in the y axis, so in this case we invert the result action as well
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
                // Default fullscreen (left tilt) has inverted rotation in the y axis, so in this case we invert the result action as well
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

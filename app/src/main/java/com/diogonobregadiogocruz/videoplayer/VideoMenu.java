package com.diogonobregadiogocruz.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoMenu extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    VideoView video;
    private Boolean fullscreen;
    private CountDownTimer timer;
    private GestureLibrary gestureLib;
    GestureOverlayView gestureOverlayView;
    private AudioManager audioManager;
    private Boolean looping = false;
    private Boolean verticalOnly = false;
    private static VideoMenu instance = null;
    private int currentPosition = 0;
    Intent _serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        instance = this;
        fullscreen = false;
        video = findViewById(R.id.videoView);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Uri uri;

        Bundle extras = getIntent().getExtras();

        if(extras.getString("Url") == null)
            uri = Uri.parse(extras.getString("Uri"));
        else
            uri = Uri.parse(extras.getString("Url"));

        if(extras.getBoolean("sensorsEnabled"))
        {
            _serviceIntent = new Intent(this, SensorService.class);
            startService(_serviceIntent);
        }

        video.setVideoURI(uri);
        video.start();

        MediaController mediaController = new CustomMediaController(this);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);

        //Determine if the video is horizontal or vertical
        //If it is vertical, it locks the screen rotation to PORTRAIT
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                if(mp.getVideoHeight() > mp.getVideoWidth())
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    verticalOnly = true;
                }
            }
        });

        video.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(looping)
                    video.start();
            }
        });

        gestureOverlayView = findViewById(R.id.gestures);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if(!gestureLib.load())
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        currentPosition = video.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        video.seekTo(currentPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SensorService.class);
        this.stopService(intent);
        this.finish();
    }

    public static VideoMenu getInstance() {
        return instance;
    }

    public void fullScreenToggle()
    {
        if(!fullscreen)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreen = true;
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullscreen = false;
        }

        // After 5 seconds the screen rotation is unlocked
        timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }.start();
    }

    public boolean isVerticalOnly()
    {
        return verticalOnly;
    }

    public boolean isFullscreen()
    {
        return fullscreen;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            fullscreen = true;
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            fullscreen = false;
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

        for(Prediction prediction : predictions)
        {
            if(prediction.score > 2.0)
            {
                switch (prediction.name)
                {
                    case ("Restart"):
                        restart();
                        return;
                    case ("Forward"):
                        forward();
                        return;
                    case ("Backward"):
                        backward();
                        return;
                    case ("Stop"):
                        stop();
                        return;
                    case ("Play"):
                        video.start();
                        return;
                    case ("Loop"):
                        toggleLooping();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public void toggleLooping()
    {
        looping = !looping;

        if(looping)
            Toast.makeText(this, "Looping: ON", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Looping: OFF", Toast.LENGTH_SHORT).show();
    }

    public void restart()
    {
        video.seekTo(0);
    }

    public void stop()
    {
        video.pause();
    }

    public void forward()
    {
        video.seekTo(video.getCurrentPosition() + 15000);
    }

    public void backward()
    {
        video.seekTo(video.getCurrentPosition() - 10000);
    }

    public void raiseVolume()
    {
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    public void lowerVolume()
    {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }
}
package com.diogonobregadiogocruz.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.drawable.Drawable;
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
    ImageView fullscreenButton;
    private static Boolean fullscreen;
    private CountDownTimer timer;
    private GestureLibrary gestureLib;
    GestureOverlayView gestureOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fullscreen = false;
        video = findViewById(R.id.videoView);
        fullscreenButton = findViewById(R.id.fullscreenIcon);
        Uri uri;

        Bundle extras = getIntent().getExtras();

        if(extras.getString("Url") == null)
            uri = Uri.parse(extras.getString("Uri"));
        else
            uri = Uri.parse(extras.getString("Url"));

        video.setVideoURI(uri);
        video.start();

        MediaController mediaController = new MediaController(this);
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
                    fullscreenButton.setVisibility(View.GONE);
                }
            }
        });

        gestureOverlayView = findViewById(R.id.gestures);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if(!gestureLib.load())
            finish();

    }

    public void fullScreenToggle(View view)
    {
        if(!fullscreen)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreenButton.setBackground(getDrawable(R.drawable.fullscreen_exit_icon));
            fullscreen = true;
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullscreenButton.setBackground(getDrawable(R.drawable.fullscreen_icon));
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            fullscreenButton.setBackground(getDrawable(R.drawable.fullscreen_exit_icon));
            fullscreen = true;
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            fullscreenButton.setBackground(getDrawable(R.drawable.fullscreen_icon));
            fullscreen = false;
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

        for(Prediction prediction : predictions)
        {
            if(prediction.score > 1.0)
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
        }
    }
}

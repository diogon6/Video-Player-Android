package com.diogonobregadiogocruz.videoplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
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
    PlaybackParams myPlayBackParams = null;
    MediaPlayer myMediaPlayer;
    ProgressDialog loadingDialog;

    // How much time the forward and backward buttons/gestures do (ms)
    private final int FORWARD_TIME = 15000;
    private final int BACKWARD_TIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        // Hide the top bar which displays the battery, internet, etc.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Creates and shows a progress loadingDialog, while the video is not prepared
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading");
        loadingDialog.setCancelable(true);
        loadingDialog.setInverseBackgroundForced(true);
        loadingDialog.show();

        instance = this;
        fullscreen = false;
        video = findViewById(R.id.videoView);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Uri uri;
        // Get the extras passed on the intent
        Bundle extras = getIntent().getExtras();

        // Get the right uri (either file or url)
        if(extras.getString("Url") == null)
            uri = Uri.parse(extras.getString("Uri"));
        else
            uri = Uri.parse(extras.getString("Url"));

        // If the sensors switch was enabled, then start the service with the sensors (SensorService)
        if(extras.getBoolean("sensorsEnabled"))
        {
            _serviceIntent = new Intent(this, SensorService.class);
            startService(_serviceIntent);
        }

        // Set the video view with the right uri and start the video
        video.setVideoURI(uri);
        video.start();

        // Create a new custom media controller and "attach" it to the video view
        MediaController mediaController = new CustomMediaController(this);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);

        // Called when the video is prepared
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                //Determine if the video is horizontal or vertical
                //If it is vertical, it locks the screen rotation to PORTRAIT
                if(mp.getVideoHeight() > mp.getVideoWidth())
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    verticalOnly = true;
                }

                // If android version >= 23, get new playback parameters and set the initial speed to 1
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    myPlayBackParams = new PlaybackParams();
                    myMediaPlayer = mp;
                    myPlayBackParams.setSpeed(1);
                    mp.setPlaybackParams(myPlayBackParams);
                }

                // Dismiss the loading loadingDialog
                loadingDialog.dismiss();
            }
        });

        // Called when the video is over
        video.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                // If the looping feature is activated, start the video again
                if(looping)
                    video.start();
            }
        });

        // Set the gestures overlay in place, with the custom gestures file
        gestureOverlayView = findViewById(R.id.gestures);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if(!gestureLib.load())
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save current position of video
        currentPosition = video.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Go to the saved position of video
        video.seekTo(currentPosition);
    }

    // Called when the back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Stops the sensor service
        Intent intent = new Intent(this, SensorService.class);
        this.stopService(intent);
        // "kills" this activity
        this.finish();
    }

    // Return this activity instance
    public static VideoMenu getInstance() {
        return instance;
    }

    // When the fullscreen button is pressed
    public void fullScreenToggle()
    {
        // If it's not in fullscreen mode, it locks the screen in landscape
        if(!fullscreen)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreen = true;
        }
        // If it's not, then locks the screen in portrait
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

    // Returns if the video is vertical only
    public boolean isVerticalOnly()
    {
        return verticalOnly;
    }

    // Returns if the video is fullscreen
    public boolean isFullscreen()
    {
        return fullscreen;
    }

    // Called when the phone was rotated, putting it in landscape or portrait mode
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the new orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            fullscreen = true;
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            fullscreen = false;
        }
    }

    // Called when a custom gesture is performed
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

        for(Prediction prediction : predictions)
        {
            // If the gesture is similar to the one in the file
            if(prediction.score > 2.0)
            {
                // Check which gesture was performed
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


    // Video control methods
    //#region

    // Activates and deactivates the looping feature when the video ends
    public void toggleLooping()
    {
        looping = !looping;

        // Inform the user of the new state of the feature
        if(looping)
            Toast.makeText(this, "Looping: ON", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Looping: OFF", Toast.LENGTH_SHORT).show();
    }

    // Restarts the video from the beginning
    public void restart()
    {
        video.seekTo(0);
    }

    // Stops the video
    public void stop()
    {
        video.pause();
    }

    // Moves the video progress 15 seconds forward
    public void forward()
    {
        video.seekTo(video.getCurrentPosition() + FORWARD_TIME);
    }

    // Moves the video progress 10 seconds backward
    public void backward()
    {
        video.seekTo(video.getCurrentPosition() - BACKWARD_TIME);
    }

    // Raises the volume of the device by one level
    public void raiseVolume()
    {
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    // Lowers the volume of the device by one level
    public void lowerVolume()
    {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    // Slows the video down by one level
    public void slowDownVideo()
    {
        // Get the current video speed
        switch (Float.toString(myPlayBackParams.getSpeed()))
        {
            case "0.25":
                Toast.makeText(this, "Minimum speed reached", Toast.LENGTH_SHORT).show();
                return;
            case "0.5":
                myPlayBackParams.setSpeed(0.25f);
                break;
            case "0.75":
                myPlayBackParams.setSpeed(0.5f);
                break;
            case "1.0":
                myPlayBackParams.setSpeed(0.75f);
                break;
            case "1.5":
                myPlayBackParams.setSpeed(1);
                break;
            case "2.0":
                myPlayBackParams.setSpeed(1.5f);
                break;
            case "3.0":
                myPlayBackParams.setSpeed(2);
                break;
            default:
                myPlayBackParams.setSpeed(1);
                break;
        }

        updateVideoSpeed();
    }

    // Speeds up the video by one level
    public void speedUpVideo()
    {
        // Get the current video speed
        switch (Float.toString(myPlayBackParams.getSpeed()))
        {
            case "0.25":
                myPlayBackParams.setSpeed(0.5f);
                break;
            case "0.5":
                myPlayBackParams.setSpeed(0.75f);
                break;
            case "0.75":
                myPlayBackParams.setSpeed(1);
                break;
            case "1.0":
                myPlayBackParams.setSpeed(1.5f);
                break;
            case "1.5":
                myPlayBackParams.setSpeed(2);
                break;
            case "2.0":
                myPlayBackParams.setSpeed(3);
                break;
            case "3.0":
                Toast.makeText(this, "Maximum speed reached", Toast.LENGTH_SHORT).show();
                return;
            default:
                myPlayBackParams.setSpeed(1);
                break;
        }

        updateVideoSpeed();
    }

    // Updates the video speed with the new speed and informs the user of it
    public void updateVideoSpeed()
    {
        myMediaPlayer.setPlaybackParams(myPlayBackParams);
        Toast.makeText(this, "Video speed changed to " + myPlayBackParams.getSpeed(), Toast.LENGTH_SHORT).show();
    }
    //#endregion
}
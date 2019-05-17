package com.diogonobregadiogocruz.videoplayer;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;

public class CustomMediaController extends MediaController {

    ImageButton fullscreenButton, restartButton, slowDownButton, speedUpButton;
    Context mContext;
    VideoMenu videoMenu;
    private int screenWidth, screenHeight;

    public CustomMediaController(Context context) {
        super(context);
        mContext = context;

        // Get the video menu activity instance
        videoMenu = VideoMenu.getInstance();

        // Get the height and width of the screen having different resolutions in mind
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        FrameLayout.LayoutParams frameParamsLeft = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParamsLeft.gravity = Gravity.LEFT|Gravity.TOP;

        FrameLayout.LayoutParams frameParamsCenterLeft = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParamsCenterLeft.gravity = Gravity.LEFT|Gravity.TOP;

        FrameLayout.LayoutParams frameParamsCenterRight = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParamsCenterRight.gravity = Gravity.RIGHT|Gravity.TOP;

        FrameLayout.LayoutParams frameParamsRight = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParamsRight.gravity = Gravity.RIGHT|Gravity.TOP;

        // Create the views and add them to the media controller

        View restartView = makeRestartView();
        addView(restartView, frameParamsLeft);

        View halfSpeed = SlowDownView();
        addView(halfSpeed, frameParamsCenterLeft);

        View doubleSpeed = SpeedUpView();
        addView(doubleSpeed, frameParamsCenterRight);

        View fullscreenView = makeFullscreenView();
        addView(fullscreenView, frameParamsRight);
    }

    // Make the restart button
    private View makeRestartView() {
        restartButton = new ImageButton(mContext);
        // Adjust the position of the button
        restartButton.setY(screenHeight/50);
        restartButton.setX(15);
        restartButton.setBackgroundResource(R.drawable.restart_icon);

        // On click it, restart the video
        restartButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.restart();
            }
        });

        return restartButton;
    }

    // Make the slow down button
    private View SlowDownView()
    {
        slowDownButton = new ImageButton(mContext);
        // Adjust the position of the button
        slowDownButton.setX(screenWidth/7);
        slowDownButton.setY(screenHeight/50);
        slowDownButton.setBackgroundResource(R.drawable.slow_down_icon);

        // On click it, slow down the video
        slowDownButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.slowDownVideo();
            }
        });

        return slowDownButton;
    }

    // Make the speed up button
    private View SpeedUpView()
    {
        speedUpButton = new ImageButton(mContext);
        // Adjust the position of the button
        speedUpButton.setY(screenHeight/50);
        speedUpButton.setX(- screenWidth/7);
        speedUpButton.setBackgroundResource(R.drawable.speed_up_icon);

        // On click it, speed up the video
        speedUpButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.speedUpVideo();
            }
        });

        return speedUpButton;
    }

    // Make the fullscreen button
    private View makeFullscreenView() {

        fullscreenButton = new ImageButton(mContext);
        // Adjust the position of the button
        fullscreenButton.setY(screenHeight/60);
        fullscreenButton.setX(-15);
        fullscreenButton.setBackgroundResource(R.drawable.fullscreen_icon);

        // On click it, toggle the fullscreen if possible
        fullscreenButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {

                // If the video is vertical, it is already fullscreen and returns
                if(videoMenu.isVerticalOnly())
                {
                    Toast.makeText(mContext, "This video is already in FullScreen Mode", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make this button's icon according to the fullscreen state
                if(videoMenu.isFullscreen())
                {
                    fullscreenButton.setBackgroundResource(R.drawable.fullscreen_icon);
                }
                else
                {
                    fullscreenButton.setBackgroundResource(R.drawable.fullscreen_exit_icon);
                }

                videoMenu.fullScreenToggle();
            }
        });

        return fullscreenButton;
    }

    // Called every time the media controller is shown to the user
    @Override
    public void show()
    {
        super.show();

        // Update the fullscreen button's icon
        if(videoMenu.isFullscreen())
        {
            fullscreenButton.setBackgroundResource(R.drawable.fullscreen_exit_icon);
        }
        else
        {
            fullscreenButton.setBackgroundResource(R.drawable.fullscreen_icon);
        }
    }
}
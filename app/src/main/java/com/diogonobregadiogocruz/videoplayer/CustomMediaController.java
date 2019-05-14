package com.diogonobregadiogocruz.videoplayer;

import android.content.Context;
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

    public CustomMediaController(Context context) {
        super(context);
        mContext = context;

        videoMenu = VideoMenu.getInstance();
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

        View restartView = makeRestartView();
        addView(restartView, frameParamsLeft);

        View halfSpeed = SlowDownView();
        addView(halfSpeed, frameParamsCenterLeft);

        View doubleSpeed = SpeedUpView();
        addView(doubleSpeed, frameParamsCenterRight);

        View fullscreenView = makeFullscreenView();
        addView(fullscreenView, frameParamsRight);
    }

    private View makeRestartView() {
        restartButton = new ImageButton(mContext);
        restartButton.setY(40);
        restartButton.setX(15);
        restartButton.setBackgroundResource(R.drawable.restart_icon);

        restartButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.restart();
            }
        });

        return restartButton;
    }

    private View SlowDownView()
    {
        slowDownButton = new ImageButton(mContext);
        slowDownButton.setY(40);
        slowDownButton.setX(155);
        slowDownButton.setBackgroundResource(R.drawable.slow_down_icon);

        slowDownButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.slowDownVideo();
            }
        });

        return slowDownButton;
    }

    private View SpeedUpView()
    {
        speedUpButton = new ImageButton(mContext);
        speedUpButton.setY(40);
        speedUpButton.setX(-155);
        speedUpButton.setBackgroundResource(R.drawable.speed_up_icon);

        speedUpButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.speedUpVideo();
            }
        });

        return speedUpButton;
    }

    private View makeFullscreenView() {

        fullscreenButton = new ImageButton(mContext);
        fullscreenButton.setY(30);
        fullscreenButton.setX(-15);
        fullscreenButton.setBackgroundResource(R.drawable.fullscreen_icon);

        fullscreenButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                if(videoMenu.isVerticalOnly())
                {
                    Toast.makeText(mContext, "This video is already in FullScreen Mode", Toast.LENGTH_SHORT).show();
                    return;
                }

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

    @Override
    public void show()
    {
        super.show();

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
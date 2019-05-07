package com.diogonobregadiogocruz.videoplayer;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;

public class CustomMediaController extends MediaController {

    ImageButton fullscreenButton, restartButton;
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

        FrameLayout.LayoutParams frameParamsRight = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParamsRight.gravity = Gravity.RIGHT|Gravity.TOP;

        View restartView = makeRestartView();
        addView(restartView, frameParamsLeft);

        View fullscreenView = makeFullscreenView();
        addView(fullscreenView, frameParamsRight);
    }

    private View makeRestartView() {
        restartButton = new ImageButton(mContext);
        restartButton.setY(35);
        restartButton.setX(40);
        restartButton.setBackgroundResource(R.drawable.restart_icon);

        restartButton.setOnClickListener(new OnClickListener() {


            public void onClick(View v)
            {
                videoMenu.restart();
            }
        });

        return restartButton;
    }

    private View makeFullscreenView() {

        fullscreenButton = new ImageButton(mContext);
        fullscreenButton.setY(30);
        fullscreenButton.setX(-40);
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
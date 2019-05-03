package com.diogonobregadiogocruz.videoplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoMenu extends AppCompatActivity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        video = findViewById(R.id.videoView);
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
    }
}

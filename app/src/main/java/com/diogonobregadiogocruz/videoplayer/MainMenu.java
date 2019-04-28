package com.diogonobregadiogocruz.videoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

public class MainMenu extends AppCompatActivity {

    RadioButton rbfile, rburl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        rbfile = findViewById(R.id.fileRadioButton);
        rburl = findViewById(R.id.urlRadioButton);
    }

    public void playVideo(View view)
    {

    }

    public void selectButton(View view)
    {
        if(view.getId() == R.id.fileRadioButton)
        {
            rbfile.setChecked(true);
            rburl.setChecked(false);
        }

        if(view.getId() == R.id.urlRadioButton)
        {
            rbfile.setChecked(false);
            rburl.setChecked(true);
        }
    }
}

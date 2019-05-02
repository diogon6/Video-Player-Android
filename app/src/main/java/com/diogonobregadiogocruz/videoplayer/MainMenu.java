package com.diogonobregadiogocruz.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainMenu extends AppCompatActivity {

    EditText fileName;
    RadioButton rbfile, rburl;
    private static final int READ_REQUEST_CODE = 42;
    private String filemanagerstring, selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fileName = findViewById(R.id.fileEditText);
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

    public void chooseVideo (View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("video/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                fileName.setText(uri.toString());
            }
        }
    }
}

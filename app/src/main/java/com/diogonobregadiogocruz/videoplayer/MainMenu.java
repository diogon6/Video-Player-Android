package com.diogonobregadiogocruz.videoplayer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

public class MainMenu extends AppCompatActivity {

    EditText fileName, urlName;
    RadioButton rbfile, rburl;
    private static final int READ_REQUEST_CODE = 42;
    private String filePath, selectedVideoName;
    private Dialog helpDialogGestures, helpDialogSensors;
    private DisplayMetrics metrics;
    private int width, height;
    private Switch switchSensors;
    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mySharedPreferences = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        switchSensors = findViewById(R.id.switchSensors);
        switchSensors.setChecked(mySharedPreferences.getBoolean("switchSensors", true));

        fileName = findViewById(R.id.fileEditText);
        urlName = findViewById(R.id.urlEditText);
        rbfile = findViewById(R.id.fileRadioButton);
        rburl = findViewById(R.id.urlRadioButton);

        fileName.setInputType(InputType.TYPE_NULL);

        helpDialogGestures = new Dialog(this);
        helpDialogSensors = new Dialog(this);

        metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("switchSensors", switchSensors.isChecked());
        editor.commit();
    }

    public void selectButton(View view)
    {
        if(view.getId() == R.id.fileRadioButton)
        {
            rbfile.setChecked(true);
            rburl.setChecked(false);
        }

        if(view.getId() == R.id.urlRadioButton || view.getId() == R.id.urlEditText)
        {
            rbfile.setChecked(false);
            rburl.setChecked(true);
        }
    }

    public void chooseVideo (View view)
    {
        if(!isStoragePermissionGranted())
            return;

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

                filePath = uri.toString();

                selectedVideoName = getFileName(uri);

                fileName.setText(selectedVideoName);
            }

            selectButton(findViewById(R.id.fileRadioButton));
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chooseVideo(findViewById(R.id.fileChoser));
        }
    }

    public void playVideo(View view)
    {
        Intent intent = new Intent(this, VideoMenu.class);

        if(rbfile.isChecked())
        {
            if(fileName.getText().toString().equals(""))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please select a video from your files.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return;
            }
            else
            {
                intent.putExtra("Uri", filePath);
            }
        }
        else
        {
            if(urlName.getText().toString().equals(""))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please insert the URL source of the video.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return;
            }
            else
            {
                intent.putExtra("Url", urlName.getText().toString());
            }
        }

        intent.putExtra("sensorsEnabled", switchSensors.isChecked());
        startActivity(intent);
    }

    public void showHelpDialogGestures(View view)
    {
        helpDialogGestures.setContentView(R.layout.help_dialog_gestures);

        Button nextButton = (Button) helpDialogGestures.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialogSensors();
                //helpDialogGestures.dismiss();
            }
        });

        helpDialogGestures.getWindow().setLayout(width, height);
        helpDialogGestures.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        helpDialogGestures.show();
    }

    public void showHelpDialogSensors()
    {
        helpDialogSensors.setContentView(R.layout.help_dialog_sensors);

        Button okButton = (Button) helpDialogSensors.findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialogGestures.dismiss();
                helpDialogSensors.dismiss();
            }
        });

        helpDialogSensors.getWindow().setLayout(width, height);
        helpDialogSensors.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        helpDialogSensors.show();
    }
}

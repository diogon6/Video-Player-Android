package com.diogonobregadiogocruz.videoplayer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    EditText fileName, urlName;
    RadioButton rbfile, rburl;
    private static final int READ_REQUEST_CODE = 42;
    private String filePath, selectedVideoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fileName = findViewById(R.id.fileEditText);
        urlName = findViewById(R.id.urlEditText);
        rbfile = findViewById(R.id.fileRadioButton);
        rburl = findViewById(R.id.urlRadioButton);

        fileName.setInputType(InputType.TYPE_NULL);
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

                filePath = uri.getPath();

                selectedVideoName = getFileName(uri);

                fileName.setText(selectedVideoName);
            }
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

        startActivity(intent);
    }
}

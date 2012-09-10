package com.idctdo.android;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EQForm_ModifiedEMS_Camera extends EQForm {

	final static int CAMERA_RESULT = 0;

	SharedPreferences mAppSettings;

	Uri FilenameUri;
	TextView Continue;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		final String FILENAME;
		final String Filename;
		final File ImageFile;
		final Uri FilenameUri;
		final Button CameraButton;

		mAppSettings = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.modified_ems98_camera);

		FILENAME = (mAppSettings.getString(APP_SETTINGS_FILE_NAME, ""));
		Filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FILENAME +".jpg";
		ImageFile = new File(Filename);
		FilenameUri = Uri.fromFile(ImageFile);

		CameraButton = (Button) findViewById(R.id.take_image);

		CameraButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {

				Continue = (TextView) findViewById(R.id.image_file_name);

				Intent takePic = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				takePic.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, FilenameUri);
				startActivityForResult(takePic, CAMERA_RESULT);

				Continue.setVisibility(View.VISIBLE);
				Continue.setText(Filename);		
			};

		});
	}


}



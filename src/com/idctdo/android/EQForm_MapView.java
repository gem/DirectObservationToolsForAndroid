/*******************************************************************************
 * Copyright (c) 2010-2012, GEM Foundation.
 * IDCT Android is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IDCT Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IDCT Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.idctdo.android;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class EQForm_MapView extends Activity {

	public boolean DEBUG_LOG = true; 

	WebView mWebView;
	/** Called when the activity is first created. */
	WebView webview;        
	private static final String TAG = "IDCT";

	SharedPreferences mAppSettings;
	public GemDbAdapter mDbHelper;

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 0;

	public double prevSurveyPointLon = 0;
	public double prevSurveyPointLat = 0;

	private Context mContext; 

	final static int CAMERA_RESULT = 0;

	public boolean isFirstLoad = true;

	public Location currentLocation; 
	public LocationManager locationManager; 
	public LocationListener mlocListener = new MyLocationListener();
	public double currentLatitude;
	public double currentLongitude;
	public double currentLocationAccuracy;
	public double currentBearingFromGPS;
	public String  currentLocationProvider = "Not set";
	public boolean currentLocationSetAsCentre = true;
	MyCount drawUpdateCounter;

	public long minTimePositionUpdates = 0; //30000
	public float minDistPositionUpdates = 0; //10f



	StringBuilder sb;
	StringBuilder sb2;

	private ProgressDialog progressBar; 

	File ImageFile;
	Uri FilenameUri;
	String FILENAME;
	String Filename;

	DecimalFormat df = new DecimalFormat("#0.#####");
	DecimalFormat dfRounded = new DecimalFormat("#0");


	public boolean showGPSDetails = true;

	public boolean isEditingPoints = false;

	Button btn_locateMe;
	Button btn_takeCameraPhoto;
	Button btn_take_survey_photo;
	Button btn_startSurvey;
	Button btn_startSurveyFavourite;
	Button btn_cancelSurveyPoint;	
	Button btn_selectLayer;
	Button btn_selectVectorLayer;
	Button btn_zoomIn;
	Button btn_zoomOut;
	Button btn_refreshLayer;
	ToggleButton btn_edit_points;

	File vectorsFile;
	File mapTilesFile;
	String sdCardPath;

	TextView text_view_gpsInfo;
	TextView text_view_gpsInfo2;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG_LOG) Log.d(TAG,"ON CREATE");
		setContentView(R.layout.map_view);





		mContext = this;

		mWebView = (WebView) findViewById(R.id.map_webview);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				if (DEBUG_LOG) Log.d(TAG, cm.message() + " -- From line "
						+ cm.lineNumber() + " of "
						+ cm.sourceId() );
				return true;
			}
		});

		if (DEBUG_LOG) Log.d(TAG,"adding JS interface");
		mWebView.addJavascriptInterface(this, "webConnector"); 

		mWebView.loadUrl("file:///android_asset/idct_map.html");
		mWebView.setWebViewClient(new MapWebViewClient());



		progressBar = new ProgressDialog(EQForm_MapView.this);
		progressBar.setMessage("Loading maps...");
		progressBar.setCancelable(false);
		progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		//progressBar.show();		

		//progressBar = ProgressDialog.show(EQForm_MapView.this, "IDCT Surveyor", "Loading Maps...");


		//Create Folder
		vectorsFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/kml");
		vectorsFile.mkdirs();

		mapTilesFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/maptiles");
		mapTilesFile.mkdirs();

		File testFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/kml/PUT_KML_FILES_HERE");
		try {
			testFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File testFile2 = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/maptiles/PUT_DIRECTORIES_OF_ZYX_TILES_HERE");
		try {
			testFile2.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





		//Save the path as a string value
		String extStorageDirectory = vectorsFile.toString();
		//SingleMediaScanner scan2 = new SingleMediaScanner(this, vectorsFile);
		//SingleMediaScanner scan3 = new SingleMediaScanner(this, mapTilesFile);
		SingleMediaScanner scan2 = new SingleMediaScanner(this, testFile);
		SingleMediaScanner scan3 = new SingleMediaScanner(this, testFile2);


		sdCardPath = "file:///" +  Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		if (DEBUG_LOG) Log.d(TAG,"sdcard Path: " + sdCardPath);
		// Restore preferences
		PreferenceManager.getDefaultSharedPreferences(this);
		//SharedPreferences settings = getSharedPreferences("R.xml.prefs"), 0);




		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



		btn_locateMe = (Button)findViewById(R.id.btn_locate_me);
		btn_locateMe.setOnClickListener(locateMeListener);

		btn_takeCameraPhoto =(Button)findViewById(R.id.btn_take_photo);
		btn_takeCameraPhoto.setOnClickListener(takePhotoListener);

		//btn_take_survey_photo=(Button)findViewById(R.id.btn_take_survey_photo);
		//btn_take_survey_photo.setVisibility(View.INVISIBLE);//Poss Dodgy threading stuff using this


		btn_startSurvey =(Button)findViewById(R.id.btn_start_survey);
		btn_startSurvey.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
		btn_startSurvey.setOnClickListener(startSurveyListener);

		btn_cancelSurveyPoint =(Button)findViewById(R.id.btn_cancel_survey_point);
		btn_cancelSurveyPoint.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
		btn_cancelSurveyPoint.setOnClickListener(cancelSurveyPointListener);

		btn_selectLayer =(Button)findViewById(R.id.btn_select_layer);
		btn_selectLayer.setOnClickListener(selectLayerListener);

		btn_selectVectorLayer =(Button)findViewById(R.id.btn_select_vector_layer);
		btn_selectVectorLayer.setOnClickListener(selectVectorLayerListener);


		btn_zoomIn =(Button)findViewById(R.id.btn_zoom_in);
		btn_zoomIn.setOnClickListener(zoomInListener);
		btn_zoomOut =(Button)findViewById(R.id.btn_zoom_out);
		btn_zoomOut.setOnClickListener(zoomOutListener);

		btn_edit_points =(ToggleButton)findViewById(R.id.btn_edit_points); 
		btn_edit_points.setOnClickListener(editPointsListener);


		text_view_gpsInfo = (TextView)findViewById(R.id.text_view_gpsInfo);
		text_view_gpsInfo2 = (TextView)findViewById(R.id.text_view_gpsInfo2);


		/*
		tabActivity = (TabActivity) getParent();
		tabHost = tabActivity.getTabHost();
		//tabHost.setEnabled(false);

		 */

		//mWebView.loadUrl("javascript:clearMyPositions()");

		loadPrevSurveyPoints();
	}


	@Override
	public void onResume(){
		super.onResume();
		if (DEBUG_LOG) Log.d("IDCT","ON RESUME");

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		//SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);		
		showGPSDetails= (settings.getBoolean("showPositionDetailsCheckBox", false));


		if (showGPSDetails) { 
			text_view_gpsInfo.setVisibility(View.VISIBLE);//
			text_view_gpsInfo2.setVisibility(View.VISIBLE);//
		} else {
			text_view_gpsInfo.setVisibility(View.INVISIBLE);//
			text_view_gpsInfo2.setVisibility(View.INVISIBLE);//
		}




		/*
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, mlocListener);
		locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

		// Register the listener with the Location Manager to receive location
		// updates
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
		}
		 */



		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
		}else{
			showGPSDisabledAlertToUser();
		}


		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);

		//currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		//drawUpdateCounter = new MyCount(100000000,1000);
		//drawUpdateCounter.start();



		mWebView.loadUrl("javascript:clearMyPositions()");
		loadPrevSurveyPoints();

		GEMSurveyObject g = (GEMSurveyObject)getApplication();
		//g.putData("OBJ_UID", id.toString());
		if (DEBUG_LOG) Log.d(TAG,"RESUMING MAP, global vars " + g.getLon()+ " lat: " + g.getLat());
		mWebView.loadUrl("javascript:locateMe("+ g.getLat()+","+g.getLon()+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");


		if (g.unsavedEdits) {

			//Draw a candidate survey point
			if (DEBUG_LOG) Log.d(TAG,"RESUMING EDITS. DRAWING MOVEABLE POINT, " + g.getLon()+ " lat: " + g.getLat());
			mWebView.loadUrl("javascript:drawCandidateSurveyPoint("+ g.getLon()+","+g.getLat()+")");

		} else {
			if (DEBUG_LOG) Log.d(TAG,"NO UNSAVED EDITS. Hide the survey button. , " + g.getLon()+ " lat: " + g.getLat());

			btn_edit_points.setChecked(false);
			isEditingPoints = false;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btn_startSurvey.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
					//btn_take_survey_photo.setVisibility(View.INVISIBLE);//Poss Dodgy threading stuff using this
					btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera);
				}
			});		       

		}

	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (DEBUG_LOG) Log.d(TAG, "On Start .....");

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("IDCT", "On Pause .....");

		locationManager.removeUpdates(mlocListener);

		//drawUpdateCounter.cancel();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (DEBUG_LOG) Log.d(TAG, "On Restart .....");
	}


	private OnClickListener zoomInListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mWebView.loadUrl("javascript:map.zoomIn()");
			//loadSurveyPoints();
		}
	};


	private OnClickListener zoomOutListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mWebView.loadUrl("javascript:map.zoomOut()");
		}
	};

	private OnClickListener refreshLayerListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mWebView.loadUrl("javascript:map.layers[0].redraw()");
		}
	};


	private OnClickListener locateMeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//addPoint();
			if (DEBUG_LOG) Log.d(TAG,"locate me button clicked");
			//mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");
			locateMe(true);
		}
	};


	private OnClickListener editPointsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//addPoint();



			if (DEBUG_LOG) Log.d(TAG,"Edit points button clicked");
			if (isEditingPoints) {
				mWebView.loadUrl("javascript:startEditingMode(false)");
				isEditingPoints = false;
			} else {
				mWebView.loadUrl("javascript:startEditingMode(true)");
				isEditingPoints = true;

			}


			//locateMe(true);
			/*
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setMessage("Editing of items is disabled at the moment").setCancelable(false).setPositiveButton("OK",
					new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){

				}
			});

			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
			 */
		}
	};

	//Press the tick button- start or modify the attribute / survey data  
	private OnClickListener startSurveyListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (DEBUG_LOG) Log.d(TAG,"next survey form");

			//Stop any geometry editing			
			mWebView.loadUrl("javascript:startEditingMode(false)");
			isEditingPoints = false;

			//Get the most recent point geometry
			getSurveyPoint();

			//Start the tabs view
			Intent ModifiedEMS98 = new Intent (EQForm_MapView.this, MainTabActivity.class);
			startActivity(ModifiedEMS98);
		}
	};




	private OnClickListener cancelSurveyPointListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (DEBUG_LOG) Log.d(TAG,"Cancel survey point");

			//Stop any geometry editing			
			mWebView.loadUrl("javascript:startEditingMode(false)");
			isEditingPoints = false;
			mWebView.loadUrl("javascript:clearMyPositions()");

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btn_startSurvey.setVisibility(View.INVISIBLE);//This might be causing issues
					btn_cancelSurveyPoint.setVisibility(View.INVISIBLE);//This might be causing issues
					btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera);
					//btn_take_survey_photo.setVisibility(View.VISIBLE);//Poss Dodgy threading stuff using this
				}
			});

		}
	};

	private OnClickListener takePhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//addPoint();
			if (DEBUG_LOG) Log.d(TAG,"camera class");

			getSurveyPoint();



			GEMSurveyObject g = (GEMSurveyObject)getApplication();
			UUID mediaId = UUID.randomUUID();
			FILENAME = "" + mediaId.toString();	
			//Button CameraButton;
			//mAppSettings = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);
			//FILENAME = (mAppSettings.getString(APP_SETTINGS_FILE_NAME, ""));				
			Filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/idctdo/" + FILENAME +".jpg";


			if (DEBUG_LOG) Log.d(TAG,"CAMERA IMAGE FILENAME: " + Filename.toString());
			ImageFile = new File(Filename);
			FilenameUri = Uri.fromFile(ImageFile);		
			Intent takePic = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			takePic.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, FilenameUri);
			takePic.putExtra("return-data", true);				
			startActivityForResult(takePic, CAMERA_RESULT);

			/*1
			Intent PreviousPage = new Intent (EQForm_MapView.this, EQForm_ModifiedEMS_Camera.class);
			startActivity(PreviousPage);*/
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_RESULT) {
			//ShowMessage(outputFileUri.toString());
			if (resultCode == Activity.RESULT_OK) {
				GEMSurveyObject g = (GEMSurveyObject)getApplication();
				if (g.unsavedEdits) {					
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("Photo Comment");
					alert.setMessage("Add a comment to this photo");

					// Set an EditText view to get user input 
					final EditText input = new EditText(this);
					alert.setView(input);

					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							//Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show();
							GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();				
							UUID mediaUid = UUID.randomUUID();
							surveyDataObject.putMediaData(
									"MEDIA_UID", FILENAME,
									"MEDIA_TYPE", "PHOTOGRAPH",
									"COMMENTS", value,
									"FILENAME", FILENAME + ".jpg"
							);				

						}
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Canceled.
							//Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show();
							GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();				
							UUID mediaUid = UUID.randomUUID();
							surveyDataObject.putMediaData(
									"MEDIA_UID", FILENAME,
									"MEDIA_TYPE", "PHOTOGRAPH",
									"COMMENTS", "no comment entered",
									"FILENAME", FILENAME + ".jpg"
							);						


						}
					});

					alert.show();
				}
			} else {
				Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
			}
		}
	}

	//Called from JS with geoJson of Openlayers features
	public boolean loadLayerNames(final String layerNamesJson) {
		if (DEBUG_LOG) Log.d(TAG,"loading layer names");
		if (DEBUG_LOG) Log.d(TAG,"layers are: " + layerNamesJson);
		return false; 	
	}






	//Called from JS with point location of survey
	//This point forms the survey point and should be saved in the db
	//It could be a new survey point or a new geom of an exisiting one 
	//Can then mark the map tab as complete
	public boolean loadSurveyPoint(final double lon, final double lat,final String gemId) {
		if (DEBUG_LOG) Log.d(TAG,"edited point location from Openlayers. Lon:" + lon + " lat: " + lat+ " gemId: " + gemId);



		//mWebView.loadUrl("javascript:locateMe("+ lat+","+lon+","+currentLocationAccuracy+","+true+")");
		GEMSurveyObject g = (GEMSurveyObject)getApplication();

		if (DEBUG_LOG) Log.d(TAG,"Currently got unsaved edits? " + g.unsavedEdits);
		g.setLon(lon);
		g.setLat(lat);
		if (g.unsavedEdits) {
			g.isExistingRecord = true;			
		}  else {
			if (gemId.equals("0")) {
				//Generate a uid for this survey point
				UUID id = UUID.randomUUID();
				g.setUid(id.toString());
				g.isExistingRecord = false; 
			} else {
				g.setUid(gemId);			
				g.isExistingRecord = true;
				g.unsavedEdits = true;
			}
		}


		


		//g.putData("OBJ_UID", id.toString());
		if (DEBUG_LOG) Log.d(TAG,"GLOBAL VARS UID " + g.getUid());	
		if (DEBUG_LOG) Log.d(TAG,"GLOBAL VARS " + g.getLon()+ " lat: " + g.getLat());
		if (DEBUG_LOG) Log.d(TAG,"GLOBAL VARS ISEXISTINGRECORD: " + g.isExistingRecord);
		g.setData(1);

		prevSurveyPointLon = lon; 
		prevSurveyPointLat = lat;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btn_startSurvey.setVisibility(View.VISIBLE);//This might be causing issues
				btn_cancelSurveyPoint.setVisibility(View.VISIBLE);//This might be causing issues
				btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera_green);
				//btn_take_survey_photo.setVisibility(View.VISIBLE);//Poss Dodgy threading stuff using this
			}
		});

		return false;
	}	





	public boolean getSurveyPoint() {
		if (DEBUG_LOG) Log.d(TAG,"getting point location from Openlayers");
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		int data=surveyDataObject.getData();
		if (DEBUG_LOG) Log.d(TAG,"TEST GLOBALS: " + data);

		mWebView.loadUrl("javascript:updateSurveyPointPositionFromMap("+ isEditingPoints+")");

		return false; 		


	}


	/*
	private void loadSurveyPoints(double lon, double lat) {
		if (DEBUG_LOG) Log.d(TAG,"loading survey points");
		if (DEBUG_LOG) Log.d(TAG,"loading survey points into Java" + lon + " " + lat);

		mWebView.loadUrl("javascript:loadSurveyPointsOnMap(-1.1662567,52.9470582)");
	}
	 */


	private void loadPrevSurveyPoints() {
		if (DEBUG_LOG) Log.d(TAG,"loading PREVIOUS survey points");

		
		mDbHelper = new GemDbAdapter(getBaseContext());      
		mDbHelper.createDatabase();      
		mDbHelper.open();		
		Cursor mCursor = mDbHelper.getGemObjectsForMap();
		mDbHelper.close();

		if (DEBUG_LOG) Log.d("IDCT","Gem Map Objects cursor " + DatabaseUtils.dumpCursorToString(mCursor));
		//ArrayList<DBRecord> gemObjectsList = GemUtilities.cursorToArrayList(gemObjects);
		//Log.d("IDCT","Gem Map Objects List " + gemObjectsList.get(gemObjectsList.size()-1));
		mCursor.moveToFirst();

		mWebView.loadUrl("javascript:clearMySurveyPoints()");//Inefficient	

		while(!mCursor.isAfterLast()) {
			if (DEBUG_LOG) Log.d("IDCT","Gem Map Objects cursor " + mCursor.getDouble(1) + " , " + mCursor.getDouble(2) + " , " + mCursor.getString(0));

			mWebView.loadUrl("javascript:loadSurveyPointsOnMap("+ mCursor.getDouble(1)+","+mCursor.getDouble(2)+", \"" + mCursor.getString(0) +"\")");
			mCursor.moveToNext();			
		}
		mCursor.close();



		/*
		if (prevSurveyPointLat != 0) { 
			mWebView.loadUrl("javascript:loadSurveyPointsOnMap("+ prevSurveyPointLon+","+prevSurveyPointLat+")");
		}
		 */
	}

	/*
	private void drawPreviousSurveyPoints() {
		if (DEBUG_LOG) Log.d(TAG,"loading survey points");
		if (DEBUG_LOG) Log.d(TAG,"loading survey points into Java" + lon + " " + lat);

		mWebView.loadUrl("javascript:loadSurveyPointsOnMap(-1.1662567,52.9470582)");
	}*/



	//Called from JS with geoJson of Openlayers features
	public JSONObject getCurrentLocation() {
		JSONObject object=new JSONObject();
		try {
			object.put("latitude",currentLocation.getLatitude());
			object.put("longitude",currentLocation.getLongitude());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		}
		if (DEBUG_LOG) Log.d(TAG,"returning current location: " + object);
		return object; 		

	}


	private void locateMe(boolean setAsCentre) {
		if (DEBUG_LOG) Log.d(TAG,"locateMe. SetAsCentre " + setAsCentre);
		mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+"," + setAsCentre + ")");
	}



	private void showGPSDisabledAlertToUser(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false).setPositiveButton("Goto Settings Page To Enable GPS",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	/*

	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		//MainTabActivity a = (MainTabActivity)getParent();
		//a.backButtonPressed();
	}
	 */

	public void completeThis() {
		//MainTabActivity a = (MainTabActivity)getParent();
		//a.completeTab(tabIndex);
	}






	public String readFileToString() {

		String fileName = "file:////android_asset/kml/sundials.kml";
		StringBuilder ReturnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try {
			fIn = EQForm_MapView.this.getBaseContext().getResources().getAssets()
			.open(fileName, EQForm_MapView.this.getBaseContext().MODE_WORLD_READABLE);
			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null) {
				ReturnString.append(line);
			}
		} catch (Exception e) {
			e.getMessage();
		} finally {
			try {
				if (isr != null)
					isr.close();
				if (fIn != null)
					fIn.close();
				if (input != null)
					input.close();
			} catch (Exception e2) {
				e2.getMessage();
			}
		}
		Log.d("JFR", "KML is: " + ReturnString.toString());
		return ReturnString.toString();

	}

	public String convertXMLFileToString(InputStream inputStream) 
	{ 
		try{ 
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); 
			org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream); 
			StringWriter stw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
			return stw.toString(); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return null; 

	}


	private String readTxt() throws IOException{

		AssetManager am = EQForm_MapView.this.getBaseContext().getAssets();
		InputStream inputStream = null;
		try {
			inputStream = am.open("kml/prevSurveyPointsSmall.kml");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d("JFR", "PROBLEM READING SUNDIALS");
			e1.printStackTrace();			
		}

		//InputStream inputStream = getResources().openRawResource(R.raw.);
		//	     InputStream inputStream = getResources().openRawResource(R.raw.internals);
		System.out.println(inputStream);


		String kml = convertXMLFileToString(inputStream);
		Log.d("JFR", "KML INPUTSTREAM is: " + kml.toString());
		return kml;
		/*

		InputStreamReader is = new InputStreamReader(inputStream);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while(read != null) {
			//System.out.println(read);
			sb.append(read);
			read =br.readLine();

		}
		Log.d("JFR", "KML is: " + sb.toString());

		return sb.toString();
		 */


		/*
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("JFR", "KML is: " + byteArrayOutputStream.toString());
		return byteArrayOutputStream.toString();
		 */
	}



	private OnClickListener selectLayerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "show Dialog ButtonClick");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Select Base Map");			

			final CharSequence[] baseMaps = {"OpenStreetMap","Bing Hybrid","Bing Roads","Bing Aerial"};
			final CharSequence[] localBaseMaps = getLocalBaseMapLayers();
			if (DEBUG_LOG) Log.d(TAG,"LOCAL BASEMAPS " + localBaseMaps.toString());

			final CharSequence[] choiceList = new CharSequence[baseMaps.length + localBaseMaps.length];
			System.arraycopy(baseMaps, 0, choiceList, 0, baseMaps.length);
			System.arraycopy(localBaseMaps, 0, choiceList, baseMaps.length, localBaseMaps.length);
			int selected = -1; // does not select anything			

			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//dismiss the dialog  
				}
			});

			builder.setSingleChoiceItems(
					choiceList, 
					selected, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,	int which) {
							if (DEBUG_LOG) Log.d(TAG,"selected "+choiceList[which]);
							int index = 1;
							if (which > 3) {
								//String tileLocationPath = "file:////mnt/sdcard/idctdo/maptiles/laquila_mapquest/";
								String tileLocationPath = sdCardPath +  "idctdo/maptiles/" + choiceList[which] +"/";
								String zoomLevel = "17";						

								File extStore = Environment.getExternalStorageDirectory();
								File xmlFile = new File(extStore.getAbsolutePath() + "/idctdo/maptiles/" + choiceList[which] +"/tilemapresource.xml");


								if (DEBUG_LOG) Log.d(TAG,"possible tms xml path:" + xmlFile.getPath());
								if (xmlFile.isFile()) {		
									if (DEBUG_LOG) Log.d(TAG,"Tile resource File is there");
									mWebView.loadUrl("javascript:addOfflineTMSMap(\""+ tileLocationPath + "\" , \"" + zoomLevel +"\")");
								} else {
									if (DEBUG_LOG) Log.d(TAG,"No TMS Resource file. Try loading zxy tiles");
									mWebView.loadUrl("javascript:addOfflineBaseMap(\""+ tileLocationPath + "\" , \"" + zoomLevel +"\")");
								}
							} else {
								mWebView.loadUrl("javascript:setMapLayer("+ which +")");
							}
						}
					});
			AlertDialog alert = builder.create();

			alert.show();
		}
	};
	private CharSequence[] getLocalBaseMapLayers() {
		mapTilesFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/maptiles");
		String file;

		File[] listOfFiles = mapTilesFile.listFiles(); 
		ArrayList<CharSequence> choiceList = new ArrayList();
		int j = 0;
		for (int i = 0; i < listOfFiles.length; i++) 	{
			if (listOfFiles[i].isDirectory()) {
				file = listOfFiles[i].getName();		
				choiceList.add(file);
			}
		}
		final CharSequence[] choiceListFinal = choiceList.toArray(new CharSequence[choiceList.size()]);

		return choiceListFinal;
	}


	private CharSequence[] getVectorLayers() {
		vectorsFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/kml");
		vectorsFile.mkdirs();
		String files;
		File[] listOfFiles = vectorsFile.listFiles(); 
		ArrayList<CharSequence> choiceList = new ArrayList();
		int j = 0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) 	{
				files = listOfFiles[i].getName();
				if (files.endsWith(".kml") || files.endsWith(".KML"))	{
					System.out.println(files);
					choiceList.add(files);
				}
			}
		}
		final CharSequence[] choiceListFinal = choiceList.toArray(new CharSequence[choiceList.size()]);
		return choiceListFinal;
	}
	private OnClickListener selectVectorLayerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "show Dialog ButtonClick");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Select Vector Layer To Show");		


			//String egg = readFileToString();
			String egg = null;
			try {
				egg = readTxt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			mWebView.loadUrl("javascript:addKmlStringToMap("+ egg +")");


			int selected = -1; // does not select anything
			final CharSequence[] choiceList = getVectorLayers();
			builder.setSingleChoiceItems(
					choiceList, 
					selected, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,	int which) {
							String kmlPath = sdCardPath +  "idctdo/kml/" + choiceList[which];
							if (DEBUG_LOG) Log.d(TAG,"selected " + kmlPath);
							int index = 1;
							mWebView.loadUrl("javascript:addLocalKmlLayer("+ kmlPath +")");
						}
					});
			AlertDialog alert = builder.create();
			alert.show();


		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"Refresh Map");
		menu.add(0,1,0,"Settings");
		menu.add(0,2,0,"Export DB Snapshot to SDCard");
		menu.add(0,3,0,"Export CSV to SDCard");

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()){

		case 0: //Refresh / redraw map
			mWebView.loadUrl("javascript:map.layers[0].redraw()");
			break;
		case 1: 

			Intent intent = new Intent(EQForm_MapView.this,PrefsActivity.class);
			startActivity(intent);
			break;
		case 2: //Export data 

			mDbHelper = new GemDbAdapter(getBaseContext());    
			mDbHelper.open();	
			Toast.makeText(this, "Exporting Database to SDCard", Toast.LENGTH_SHORT).show();
			mDbHelper.copyDataBaseToSdCard();
			mDbHelper.close();
			break;
		case 3: //Export data to csv 

			mDbHelper = new GemDbAdapter(getBaseContext());    
			mDbHelper.open();	
			Toast.makeText(this, "Exporting Survey Data to CSV SDCard", Toast.LENGTH_SHORT).show();
			mDbHelper.exportGemTableToCsv();
			mDbHelper.close();

			break;
		default:
			break;
		}

		return false;
	}


	public void HandleButton (View v){
		if (DEBUG_LOG) Log.d(TAG,"pressed button");

		if (v.getId()==R.id.btn_locate_me){
			/*
			Toast.makeText(this, "Launching new earthquake survey form", Toast.LENGTH_SHORT).show();
			Intent ModifiedEMS98 = new Intent (EQForm_MapView.this, EQForm_ModifiedEMS_1.class);
			startActivity(ModifiedEMS98);*/

		}   else if (v.getId()==R.id.settings){
			//Intent Settings = new Intent (EQForm_MapView.this, EQForm_Settings.class);
			//startActivity(Settings);

		}  else if (v.getId()==R.id.btn_take_photo){ 
			Toast.makeText(this, "Launching camera", Toast.LENGTH_SHORT).show();
			//Intent PreviousPage = new Intent (EQForm_MapView.this, EQForm_ModifiedEMS_Camera.class);
			//startActivity(PreviousPage);
		} 

		else if (v.getId()==R.id.btn_select_layer){ 
			Toast.makeText(this, "selecting layer", Toast.LENGTH_SHORT).show();
			//showDialogButtonClick();
		}

	}

	//countdowntimer is an abstract class, so extend it and fill in methods
	public class MyCount extends CountDownTimer{
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			if (DEBUG_LOG) Log.d(TAG,"Timer finished");

		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (DEBUG_LOG) Log.d(TAG,"seconds left: " +millisUntilFinished/1000);
			if (DEBUG_LOG) Log.d(TAG,"currentLat:" + currentLatitude + " currentLon: " + currentLongitude);

			if (showGPSDetails) {
				//text_view_gpsInfo.setText("GPS Information:\nLat: " + df.format(currentLatitude) + "\nLon: " + df.format(currentLongitude) + "\nAccuracy (Metres): " + dfRounded.format(currentLocationAccuracy) + "\nProvider: " +currentLocationProvider );
			}
			//drawGuidePoint2();
			locateMe(false);
			//mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");
		}
	}



	private class MapWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}



		@Override
		public void onPageFinished(WebView view, String url) {
			if (DEBUG_LOG) Log.d(TAG,"WebView has finished loading");
			if (progressBar.isShowing()) {
				progressBar.dismiss();
			}
			if (isFirstLoad) {
				//mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");

				locateMe(false);
				loadPrevSurveyPoints();
			}
			isFirstLoad = false;

		}
	}

	public class MyLocationListener implements LocationListener	{
		@Override
		public void onLocationChanged(Location loc)	{


			if (isBetterLocation(loc,currentLocation)) {
				if (DEBUG_LOG) Log.d(TAG,"New location is better. Updating it");
				currentLocation = loc;
				currentLatitude = currentLocation.getLatitude();
				currentLongitude = currentLocation.getLongitude();

				currentLocationAccuracy = currentLocation.getAccuracy();
				currentBearingFromGPS = currentLocation.getBearing();
				currentLocationProvider = currentLocation.getProvider();
				locateMe(false);


				sb = new StringBuilder(512);
				/* display some of the data in the TextView */
				sb.append("Best Location:\n");

				sb.append("Lon: ");
				sb.append(loc.getLongitude());
				sb.append('\n');

				sb.append("Lat: ");
				sb.append(loc.getLatitude());
				sb.append('\n');

				sb.append("Alt: ");
				sb.append(loc.getAltitude());
				sb.append('\n');

				sb.append("Acc: ");
				sb.append(loc.getAccuracy());
				sb.append('\n');


				sb.append("Prov: ");
				sb.append(loc.getProvider());
				sb.append('\n');

				sb.append("Time: ");
				sb.append(DateFormat.format("hh:mm:ssaa", loc.getTime()));
				sb.append('\n');				

				text_view_gpsInfo.setText(sb.toString());
			} else {
				sb2 = new StringBuilder(512);
				/* display some of the data in the TextView */
				sb2.append("Discarded location:\n");
				sb2.append("Lon: ");
				sb2.append(loc.getLongitude());
				sb2.append('\n');

				sb2.append("Lat: ");
				sb2.append(loc.getLatitude());
				sb2.append('\n');

				sb2.append("Alt: ");
				sb2.append(loc.getAltitude());
				sb2.append('\n');

				sb2.append("Acc: ");
				sb2.append(loc.getAccuracy());
				sb2.append('\n');

				sb2.append("Prov: ");
				sb2.append(loc.getProvider());
				sb2.append('\n');

				sb2.append("Time: ");
				sb2.append(DateFormat.format("hh:mm:ssaa", loc.getTime()));				
				sb2.append('\n');				



				text_view_gpsInfo2.setText(sb2.toString());


			}

			//textViewLocationProvider.setText("Loc. Provider: " + currentLocationProvider);
			//textViewLocationAccuracy.setText("Loc. Accuracy: " + currentLocationAccuracy);

			if (DEBUG_LOG) Log.d(TAG,"lat: "+loc.getLatitude() + "lng: " + loc.getLongitude() );
			//mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");

			//textViewLatitude.setText(Double.toString(loc.getLatitude()));
			//textViewLongitude.setText(Double.toString(loc.getLongitude()));
		}


		@Override

		public void onProviderDisabled(String provider)

		{
			if (DEBUG_LOG) Log.d(TAG,"Provider disabled");

			Toast.makeText( getApplicationContext(),

					provider + " location provider disabled",

					Toast.LENGTH_SHORT ).show();


			/*
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimePositionUpdates, minDistPositionUpdates,  mlocListener );
			} else {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener );
			} 
			 */
		}


		@Override

		public void onProviderEnabled(String provider)

		{
			if (DEBUG_LOG) Log.d(TAG,"Provider enabled");

			Toast.makeText( getApplicationContext(),

					provider + " location provider disabled",

					Toast.LENGTH_SHORT).show();

			/*
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);   
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);
			} else {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);
			}
			 */
		}




		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			/* This is called when the GPS status alters */
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Out of Service");
				//Toast.makeText(getApplicationContext(), "Status Changed: Out of Service",	Toast.LENGTH_SHORT).show();
				//textGpsStatus.setText("Provider:" + provider + " status: " + status);
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Temporarily Unavailable");
				//textGpsStatus.setText("Provider:" + provider + " status: " + status);
				//Toast.makeText(getApplicationContext(), "Status Changed: Temporarily Unavailable",Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:



				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Available");

				//textGpsStatus.setText("Provider:" + provider + " status: " + status);



				//Toast.makeText(getApplicationContext(), "Status Changed: Available",Toast.LENGTH_SHORT).show();
				break;
			}
		}


		private static final int TWO_MINUTES = 1000 * 60 * 3;

		/** Determines whether one Location reading is better than the current Location fix
		 * @param location  The new Location that you want to evaluate
		 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
		 */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			/*
			if (location.getProvider() == "GPS_PROVIDER") {
				return true;
			} */


			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				//If the new location is more than two minutes older, it must be worse
				//Not necessarily, can be different times between networkprovidedLocation.getTime()
				//and gpsProvidedLocation.getTime();
			} else if (isSignificantlyOlder) {
				//return false;
			}


			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;


			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(),
					currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return true;
			}
			return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}


	}/* End of Class MyLocationListener */


}

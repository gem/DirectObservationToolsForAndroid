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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class EQForm_MapView extends Activity {

	public boolean DEBUG_LOG = false; 

	protected Dialog mSplashDialog;
	
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
	public LocationListener mlocListener;
	public double currentLatitude;
	public double currentLongitude;
	public double currentLocationAccuracy;
	public double currentBearingFromGPS;
	public String  currentLocationProvider = "Not set";
	public boolean currentLocationSetAsCentre = true;
	MyCount drawUpdateCounter;

	public long minTimePositionUpdates = 0; //30000
	public float minDistPositionUpdates = 0; //10f

	private int mProgressStatus = 0;
	private ProgressDialog mProgressDialog;
	private ProgressBarAsync mProgressbarAsync;

	StringBuilder sb;
	StringBuilder sb2;

	private ProgressDialog progressBar; 

	File ImageFile;
	Uri FilenameUri;
	String FILENAME;
	String Filename;

	DecimalFormat df = new DecimalFormat("#0.#####");
	DecimalFormat dfRounded = new DecimalFormat("#0");


	public boolean showGPSDetails;

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

	File mediaFile;
	File vectorsFile;
	File mapTilesFile;
	String sdCardPath;

	boolean[] vectorsListDisplayState = null; 


	TextView text_view_gpsInfo;
	TextView text_view_gpsInfo2;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG_LOG) Log.d(TAG,"ON CREATE");
		setContentView(R.layout.map_view);
		mContext = this;

		 MyStateSaver data = (MyStateSaver) getLastNonConfigurationInstance();
		    if (data != null) {
		        // Show splash screen if still loading
		        if (data.showSplashScreen) {
		            showSplashScreen();
		        }
		        setContentView(R.layout.map_view);      

		        // Rebuild your UI with your saved state here
		    } else {
		        showSplashScreen();
		        setContentView(R.layout.map_view);   
		        // Do your heavy loading here on a background thread



		    }
		    
		//showSplashScreen();
		
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

		//Create Folder
		mediaFile = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/gemmedia");
		mediaFile.mkdirs();		

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


	     /** Creating a progress dialog window */
        mProgressDialog = new ProgressDialog(this);
 
        /** Close the dialog window on pressing back button */
        mProgressDialog.setCancelable(true);
 
        /** Setting a horizontal style progress bar */
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
 
        /** Setting a message for this progress dialog
        * Use the method setTitle(), for setting a title
        * for the dialog window
        *  */
        mProgressDialog.setMessage("Loading map data...");
        
                
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

		mlocListener = new MyLocationListener();
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

		btn_startSurveyFavourite =(Button)findViewById(R.id.btn_start_survey_with_favourite);
		btn_startSurveyFavourite.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
		btn_startSurveyFavourite.setOnClickListener(selectFavouriteListener);

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


		//Load any of the previous survey points
		loadPrevSurveyPoints();
	}


	@Override
	public void onBackPressed() {
		// do something on back.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle("Exit IDCT?");

		// set dialog message
		alertDialogBuilder
		.setMessage("Are you sure you want to close this application?")
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				EQForm_MapView.this.finish();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();

		return;
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


		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
		}else{
			//showGPSDisabledAlertToUser();
		}
		

		if (DEBUG_LOG) Log.d("IDCT","Requesting location updates for network");

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);
		if (DEBUG_LOG) Log.d("IDCT","Requesting location updates for GPS");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimePositionUpdates, minDistPositionUpdates, mlocListener);

		mWebView.loadUrl("javascript:clearMyPositions()");
		loadPrevSurveyPoints();

		GEMSurveyObject g = (GEMSurveyObject)getApplication();
		if (DEBUG_LOG) Log.d(TAG,"RESUMING MAP, global vars " + g.getLon()+ " lat: " + g.getLat());
		if (g.getLat() == 0 && g.getLon() == 0) {  
			Toast.makeText(this, "Waiting for location", Toast.LENGTH_SHORT).show();
		}else {
			mWebView.loadUrl("javascript:locateMe("+ g.getLat()+","+g.getLon()+","+currentLocationAccuracy+","+currentLocationSetAsCentre+")");			
		}

		//If there is unsaved edits draw the current candidate survey point
		if (g.unsavedEdits) {
			//Draw a candidate survey point
			if (DEBUG_LOG) Log.d(TAG,"RESUMING EDITS. DRAWING MOVEABLE POINT, " + g.getLon()+ " lat: " + g.getLat());
			mWebView.loadUrl("javascript:drawCandidateSurveyPoint("+ g.getLon()+","+g.getLat()+")");

		} else {
			//Set the screen up to be ready for user drawing a new survey point
			if (DEBUG_LOG) Log.d(TAG,"NO UNSAVED EDITS. Hide the survey button. , " + g.getLon()+ " lat: " + g.getLat());
			btn_edit_points.setChecked(false);
			isEditingPoints = false;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btn_startSurvey.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
					btn_cancelSurveyPoint.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
					btn_startSurveyFavourite.setVisibility(View.INVISIBLE);//Dodgy threading stuff using this
					//btn_take_survey_photo.setVisibility(View.INVISIBLE);//Poss Dodgy threading stuff using this
					btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera);
					btn_edit_points.setEnabled(true);
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
		//Stop receiving location updates to save battery when app pauses
		locationManager.removeUpdates(mlocListener);
		//drawUpdateCounter.cancel();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (DEBUG_LOG) Log.d(TAG, "On Restart .....");
	}






	/***********************************************************
	 * MAP UI BUTTON CLICK LISTENERS
	 * ********************************************************/

	private OnClickListener zoomInListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mWebView.loadUrl("javascript:map.zoomIn()");
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
			if (DEBUG_LOG) Log.d(TAG,"Edit points button clicked");
			/*
			if (isEditingPoints) {
				if (DEBUG_LOG) Log.d(TAG,"Starting Editing mode in js");
				mWebView.loadUrl("javascript:startEditingMode(false)");
				isEditingPoints = false;
			} else {
				if (DEBUG_LOG) Log.d(TAG,"stoppint editing mode in js");
				mWebView.loadUrl("javascript:startEditingMode(true)");
				isEditingPoints = true;
			}
			*/
			if (btn_edit_points.isChecked()) {
				if (DEBUG_LOG) Log.d(TAG,"Starting Editing mode in js");
				mWebView.loadUrl("javascript:startEditingMode(true)");
				isEditingPoints = true;
			} else {
				if (DEBUG_LOG) Log.d(TAG,"stoppint editing mode in js");
				mWebView.loadUrl("javascript:startEditingMode(false)");
				isEditingPoints = false;
			}
			
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

	//Cancel adding a new survey point
	private OnClickListener cancelSurveyPointListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (DEBUG_LOG) Log.d(TAG,"Cancel survey point");

			//If we're not editing points i.e. we're in the adding a new survey point state
			if (!btn_edit_points.isChecked()) {
				//Stop any geometry editing			
				mWebView.loadUrl("javascript:startEditingMode(false)");
				isEditingPoints = false;

				//Clear the temporary/proposal survey points
				mWebView.loadUrl("javascript:clearMyPositions()");

				//Reset the buttons / icons to initial graphics/states
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						btn_startSurvey.setVisibility(View.INVISIBLE);
						btn_cancelSurveyPoint.setVisibility(View.INVISIBLE);
						btn_startSurveyFavourite.setVisibility(View.INVISIBLE);
						btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera);
						btn_edit_points.setEnabled(true);
						btn_edit_points.setChecked(false);
					}
				});

				//Is in editing state, could prompt to save these changes	
			} else {
				//promptForSavingEdits();
			}
		}
	};

	//User wants to add this point and load a favourite/template into the forms
	private OnClickListener selectFavouriteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (DEBUG_LOG) Log.d(TAG,"Favourite survey point");

			Log.i(TAG, "show Dialog ButtonClick");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Select a Favourite");		

			//Get the favourite / template list from the db
			mDbHelper = new GemDbAdapter(getBaseContext());       
			mDbHelper.createDatabase();      
			mDbHelper.open();
			Cursor favouritesCursor = mDbHelper.getGemFavourites();
			mDbHelper.close();	


			int selected = -1; // does not select anything			
			ArrayList<DBRecord> buildingPositionAttributesList = GemUtilities.cursorToArrayList(favouritesCursor);
			final ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,buildingPositionAttributesList );

			builder.setAdapter(spinnerArrayAdapter , new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,	int which) {
					DBRecord r = (DBRecord) spinnerArrayAdapter.getItem(which);
					if (DEBUG_LOG) Log.d(TAG,"selected favourite "+r.getAttributeValue());
					GEMSurveyObject g = (GEMSurveyObject)getApplication();
					g.isExistingRecord = false;		
					g.favouriteRecord = r.getAttributeValue();
					//g.setUid(r.getAttributeValue());			
					g.unsavedEdits = true;

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
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			AlertDialog alert = builder.create();

			alert.show();
			favouritesCursor.close();

			//Stop any geometry editing			
			mWebView.loadUrl("javascript:startEditingMode(false)");
			isEditingPoints = false;
			mWebView.loadUrl("javascript:clearMyPositions()");

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btn_startSurvey.setVisibility(View.INVISIBLE);//This might be causing issues
					btn_cancelSurveyPoint.setVisibility(View.INVISIBLE);//This might be causing issues
					btn_startSurveyFavourite.setVisibility(View.INVISIBLE);//This might be causing issues
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
			Filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/idctdo/gemmedia/" + FILENAME +".jpg";


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
							if (which > 3) { //If not one of the standard OSM or Bing web access layers								
								//String tileLocationPath = "file:////mnt/sdcard/idctdo/maptiles/laquila_mapquest/";
								String tileLocationPath = sdCardPath +  "idctdo/maptiles/" + choiceList[which] +"/";
								String zoomLevel = "18";			


								File extStore = Environment.getExternalStorageDirectory();
								File xmlFile = new File(extStore.getAbsolutePath() + "/idctdo/maptiles/" + choiceList[which] +"/tilemapresource.xml");
								String layerNameString = choiceList[which].toString();
								if (DEBUG_LOG) Log.d(TAG,"possible tms xml path:" + xmlFile.getPath());
								if (xmlFile.isFile()) {		
									if (DEBUG_LOG) Log.d(TAG,"Tile resource File is there");
									mWebView.loadUrl("javascript:addOfflineTMSMap(\""+ layerNameString +  "\" , \"" +  tileLocationPath + "\" , \"" + zoomLevel +"\")");
								} else {
									if (DEBUG_LOG) Log.d(TAG,"No TMS Resource file. Try loading zxy tiles");
									mWebView.loadUrl("javascript:addOfflineBaseMap(\""+ layerNameString + "\" , \"" + tileLocationPath + "\" , \"" + zoomLevel +"\")");
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


	private OnClickListener selectVectorLayerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "show Dialog ButtonClick");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Select Vector Layer To Show");	

			int selected = -1; // does not select anything
			final CharSequence[] choiceList = getVectorLayers();
			if (vectorsListDisplayState == null) { 
				vectorsListDisplayState = new boolean[choiceList.length];
			}

			builder.setMultiChoiceItems(
					choiceList, 
					vectorsListDisplayState, 
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							String layerFileName = choiceList[which].toString();
							if (isChecked) { //add the layer
								
								
	
								
								
								int index = 1;
								File extStore = Environment.getExternalStorageDirectory();
								String kmlPath = extStore.getAbsolutePath() + "/idctdo/kml/" + choiceList[which];
								
								//File xmlFile = new File(extStore.getAbsolutePath() + "/idctdo/kml/" + choiceList[which]);
								if (DEBUG_LOG) Log.d(TAG,"selected " + kmlPath);

						         /** Show the progress dialog window */
				                mProgressDialog.show();				 
				                /** Creating an instance of ProgressBarAsync */
				                mProgressbarAsync = new ProgressBarAsync();				 
				                /** ProgressBar starts its execution */
				                mProgressbarAsync.execute(kmlPath, layerFileName);
				                
				                
				                
								
							} else {//remove the layer
								Toast.makeText(getBaseContext(), "Removing KML", Toast.LENGTH_SHORT).show();
								int packingVar1= 1;
								int packingVar2= 1;
								mWebView.loadUrl("javascript:removeOverlay("+packingVar1 +","+ packingVar2 +", \"" +  layerFileName +"\")");

							}
						}
					});
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//dismiss the dialog  
				}
			});


			AlertDialog alert = builder.create();
			alert.show();
		}
	};



	/***********************************************************
	 * END OF UI BUTTON CLICK LISTENERS
	 * ********************************************************/
	

	/***********************************************************
	 * JAVA / JAVASCRIPT INTERFACE HELPER FUNCTION
	 * Contains various general functions for calling high level functionality
	 * in the map view (javascript) for map drawing, editing, getting locations
	 * ********************************************************/

	public boolean getSurveyPoint() {
		if (DEBUG_LOG) Log.d(TAG,"getting point location from Openlayer. isEditingPoints: s + isEditingPoints");
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		int data=surveyDataObject.getData();
		if (DEBUG_LOG) Log.d(TAG,"TEST GLOBALS: " + data);
		mWebView.loadUrl("javascript:updateSurveyPointPositionFromMap("+ isEditingPoints+")");
		return false; 		
	}	
	protected void removeSplashScreen() {
	    if (mSplashDialog != null) {
	        mSplashDialog.dismiss();
	        mSplashDialog = null;
	    }
	}
	protected void showSplashScreen() {
	    mSplashDialog = new Dialog(this, R.style.SplashScreen);
	    mSplashDialog.setContentView(R.layout.splash);
	    mSplashDialog.setCancelable(false);
	    mSplashDialog.show();
	     
	    // Set Runnable to remove splash screen just in case
	    final Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
	        removeSplashScreen();
	      }
	    }, 3000);
	}
	
	private void loadPrevSurveyPoints() {
		if (DEBUG_LOG) Log.d(TAG,"loading PREVIOUS survey points");
		mDbHelper = new GemDbAdapter(getBaseContext());      
		mDbHelper.createDatabase();      
		mDbHelper.open();		
		Cursor mCursor = mDbHelper.getGemObjectsForMap();
		mDbHelper.close();

		//if (DEBUG_LOG) Log.d("IDCT","Gem Map Objects cursor " + DatabaseUtils.dumpCursorToString(mCursor));
		//ArrayList<DBRecord> gemObjectsList = GemUtilities.cursorToArrayList(gemObjects);
		//Log.d("IDCT","Gem Map Objects List " + gemObjectsList.get(gemObjectsList.size()-1));
		mCursor.moveToFirst();

		mWebView.loadUrl("javascript:clearMySurveyPoints()");//Inefficient	

		while(!mCursor.isAfterLast()) {
			//if (DEBUG_LOG) Log.d("IDCT","Gem Map Objects cursor " + mCursor.getDouble(1) + " , " + mCursor.getDouble(2) + " , " + mCursor.getString(0));

			mWebView.loadUrl("javascript:loadSurveyPointsOnMap("+ mCursor.getDouble(1)+","+mCursor.getDouble(2)+", \"" + mCursor.getString(0) +"\")");
			mCursor.moveToNext();			
		}
		mCursor.close();

	}

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
			if (DEBUG_LOG) Log.d(TAG,"Got unsaved edits");	
			//Check if we're editing a point i.e. we've clicked it and got its id. If we are then keep the uid. Else generate new one			
			//if (gemId.equals("0")) {
			if (!g.isExistingRecord) {
				if (DEBUG_LOG) Log.d(TAG,"Got unsaved edits but generate a new UID");	
				//Generate a uid for this survey point
				UUID id = UUID.randomUUID();
				g.setUid(id.toString());
				g.isExistingRecord = false; 
			} else {
				if (DEBUG_LOG) Log.d(TAG,"Unsaved edits. Setting existing record = true");
				g.isExistingRecord = true;
			}
		}  else {		
			if (DEBUG_LOG) Log.d(TAG,"Not got unsaved edits");			
			if (gemId.equals("0")) {
				if (DEBUG_LOG) Log.d(TAG,"gemId from map is 0. Generate a new UID");	
				//Generate a uid for this survey point
				UUID id = UUID.randomUUID();
				g.setUid(id.toString());
				g.isExistingRecord = false; 

			} else { //Then we've tapped existing point. Set it's id, flag as existing record 
				if (DEBUG_LOG) Log.d(TAG,"Using gemId from map");
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
				btn_startSurvey.setVisibility(View.VISIBLE);

				//Only allow cancel and favouriting if not editing a point
				if (!btn_edit_points.isChecked()) {
					btn_cancelSurveyPoint.setVisibility(View.VISIBLE);	
					btn_startSurveyFavourite.setVisibility(View.VISIBLE);
				}

				//btn_takeCameraPhoto.setBackgroundResource(R.drawable.camera_green); //Disabled due to linking photo problem 21/02/13 
				btn_edit_points.setEnabled(false);

				//btn_take_survey_photo.setVisibility(View.VISIBLE);//Poss Dodgy threading stuff using this
			}
		});
		return false;
	}	


	private void locateMe(boolean setAsCentre) {
		if (DEBUG_LOG) Log.d(TAG,"locateMe. SetAsCentre " + setAsCentre);
		if (currentLatitude == 0 && currentLongitude== 0) {  
			Toast.makeText(this, "Waiting for location", Toast.LENGTH_SHORT).show();
		}else {
			mWebView.loadUrl("javascript:locateMe("+ currentLatitude+","+currentLongitude+","+currentLocationAccuracy+"," + setAsCentre + ")");
		}
	}


	/***********************************************************
	 * END OF JAVA / JAVASCRIPT INTERFACE HELPER FUNCTIONS
	 * ********************************************************/



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_RESULT) {
			//ShowMessage(outputFileUri.toString());
			if (resultCode == Activity.RESULT_OK) {
				GEMSurveyObject g = (GEMSurveyObject)getApplication();
				if (g.unsavedEdits) {					
					AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

					alert.setTitle("Photo Comment");
					alert.setMessage("Add a comment to this photo");
					// Set an EditText view to get user input 
					final EditText input = new EditText(mContext);
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
									"MEDIA_TYPE", "PHOTO",
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
									"MEDIA_TYPE", "PHOTO",
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




	public void promptForSavingEdits() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Unsaved Survey Observation");

		// set dialog message
		alertDialogBuilder
		.setMessage("Do you want to save this observation?")
		.setCancelable(false)
		.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				//MainActivity.this.finish();
				dialog.cancel();
			}
		})
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				//This is needed to trigger the focus changed events of EditText fields
				//tabHost.setCurrentTab(0);
				saveData();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				/*
				GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
				surveyDataObject.clearGemSurveyObject();
				surveyDataObject.unsavedEdits = false;
				MainTabActivity.this.finish();
				 */
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();

	}


	//Duplicated in MainTabAcitivty, should be moved elsewhere really	
	public boolean saveData() {
		if (DEBUG_LOG) Log.d(TAG, "Saving data");		
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();

		mDbHelper = new GemDbAdapter(getBaseContext());      
		mDbHelper.createDatabase();      
		mDbHelper.open();		

		mDbHelper.insertOrUpdateGemData(surveyDataObject);
		//Should really try / catch this

		mDbHelper.close();
		//Toast.makeText(getApplicationContext(), "Survey data saved", Toast.LENGTH_SHORT).show();
		surveyDataObject.clearGemSurveyObject();
		surveyDataObject.unsavedEdits = false;
		return false;
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

	private String readTxt(String filePath) throws IOException{
		//AssetManager am = EQForm_MapView.this.getBaseContext().getAssets();
		InputStream inputStream = null;		
		try {
			inputStream = new FileInputStream( filePath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			if (DEBUG_LOG) Log.d("JFR", "PROBLEM READING KML");
			e1.printStackTrace();			
		}
		String kml = convertXMLFileToString(inputStream);
		if (DEBUG_LOG) Log.d(TAG, "KML INPUTSTREAM is: " + kml.toString());
		return kml;
	}





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



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"Refresh Map");
		menu.add(0,1,0,"Settings");
		menu.add(0,2,0,"Export DB Snapshot to SDCard");
		menu.add(0,3,0,"Export CSV to SDCard");
		menu.add(0,4,0,"Project / Survey Set Up");
		menu.add(0,5,0,"Clear Database");
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
		case 4: //Export data 

			Intent intent2 = new Intent(EQForm_MapView.this,Project_Settings.class);
			startActivity(intent2);
			break;

		case 5: //Delete record

			deleteRecords();
			break;
		default:
			break;
		}

		return false;
	}

	
	@Override
	public Object onRetainNonConfigurationInstance() {
		MyStateSaver data = new MyStateSaver();
		// Save your important data here


		if (mSplashDialog != null) {
			data.showSplashScreen = true;
			removeSplashScreen();
		}
		return data;
	}
	
	
	/**
	 * Simple class for storing important data across config changes
	 */
	private class MyStateSaver {
		public boolean showSplashScreen = false;
		// Your other important fields here
	}


	
	public void deleteRecords(){
		if (DEBUG_LOG) Log.d(TAG,"Deleting records");
		// do something on back.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Clear database");
		sb = new StringBuilder(512);
		/* display some of the data in the TextView */
		sb.append("This will clear the database of all survey data");
		sb.append("\n\nProject details, favourites, building survey information, exposure, consequences and photograph links will be deleted.");
		sb.append("\n\nOffline map tiles, photographs, database exports will be kept.");
		sb.append("\n\nIf you want to proceed enter the word 'delete' in the text box and hit yes");
		// set dialog message
		alertDialogBuilder.setMessage(sb.toString());
		// Use an EditText view to get user input.
		final EditText input = new EditText(this);
		//input.setId(TEXT_ID);
		alertDialogBuilder.setView(input);


		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				//This is needed to trigger the focus changed events of EditText fields
				String value = input.getText().toString();
				if (value.equals("delete")) {

					mDbHelper = new GemDbAdapter(getBaseContext());    
					mDbHelper.open();	
					//Toast.makeText(this, "Deleting Records", Toast.LENGTH_SHORT).show();
					mDbHelper.deleteRecords();
					mDbHelper.close();
					// Refresh main activity upon close of dialog box
					Intent refresh = new Intent(EQForm_MapView.this, EQForm_MapView.class);
					startActivity(refresh);
					EQForm_MapView.this.finish();
				} else {
					Toast.makeText(getBaseContext(), "Entered wrong check value. Database not cleared.", Toast.LENGTH_SHORT).show();
				}

			}
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
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

	private class ProgressBarAsync extends AsyncTask<String, String, Void> {
		 //private ProgressDialog dialog = new ProgressDialog(EQForm_MapView.this);
		 
		/** This callback method is invoked, before starting the background process */
		@Override
		protected void onPreExecute() {

			// this.dialog.setMessage("Please wait");
		     //   this.dialog.show();
		}

		/** This callback method is invoked on calling execute() method
		 * on an instance of this class */
		@Override
		protected Void doInBackground(String... params) {	
					String kmlPath2 = params[0];
					String layerFileName = params[1];
					String kmlString = null;
					try {
						kmlString = readTxt(kmlPath2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						if (DEBUG_LOG) Log.d(TAG,"problem getting kml file");
						e.printStackTrace();
					} 			

					//Escape the string
					String escaped = StringEscapeUtils.escapeJava(kmlString);
					int packingVar1= 1;
					int packingVar2= 1;
					mWebView.loadUrl("javascript:addKmlStringToMap2("+packingVar1 +", \"" +   layerFileName +"\", \"" +  escaped+ "\")");


					/** Sleeps this thread for 100ms */
					//Thread.sleep(100);

			
			return null;
		}



		/** This callback method is invoked when the background function
		 * doInBackground() is executed completely */
		@Override
		protected void onPostExecute(Void result) {
			   //if (dialog.isShowing()) {
		       //     dialog.dismiss();		            
		       // }
			   if (mProgressDialog.isShowing()) {
			   		mProgressDialog.dismiss();
			   }
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
			if (DEBUG_LOG) Log.d(TAG,"lat: "+loc.getLatitude() + "lng: " + loc.getLongitude() );

		}


		@Override
		public void onProviderDisabled(String provider)

		{
			if (DEBUG_LOG) Log.d(TAG,"Provider disabled: " + provider );

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
					provider + " location provider enabled",
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
			if (DEBUG_LOG) Log.d(TAG, "GPS status changed");
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Out of Service");
				Toast.makeText(getApplicationContext(), provider + " location provider status Changed: Out of Service",	Toast.LENGTH_SHORT).show();
				//textGpsStatus.setText("Provider:" + provider + " status: " + status);
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Temporarily Unavailable");
				//textGpsStatus.setText("Provider:" + provider + " status: " + status);
				Toast.makeText(getApplicationContext(), provider + " location provider status Changed: TEMPORARILY_UNAVAILABLE",	Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:

				if (DEBUG_LOG) Log.d(TAG, "Status Changed: Available");

				//textGpsStatus.setText("Provider:" + provider + " status: " + status);

				Toast.makeText(getApplicationContext(), provider + " location provider status Changed: AVAILABLE",	Toast.LENGTH_SHORT).show();

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

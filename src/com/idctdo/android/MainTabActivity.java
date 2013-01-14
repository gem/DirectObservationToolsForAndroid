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






import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabActivity extends TabActivity {
	private static final String TAG = "IDCT";
	public boolean DEBUG_LOG = false; 

	public TabHost tabHost;

	public GemDbAdapter mDbHelper;

	public Resources ressources;

	public int unselectedTabColour = Color.parseColor("#cccccc");
	public int unselectedTabColour2 = Color.parseColor("#00ff00");
	public int selectedTabColour = Color.parseColor("#eeeeee");

	boolean[] completedTabs;

	final static int CAMERA_RESULT = 0;
	File ImageFile;
	Uri FilenameUri;
	String FILENAME;
	String Filename;



	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_activity);



		ressources = getResources(); 
		tabHost = getTabHost(); 

		/*

		Intent intentMapView = new Intent().setClass(this, EQForm_MapView.class);
		TabSpec tabMapView = tabHost
		.newTabSpec("MapView")
		.setIndicator("Map", ressources.getDrawable(R.drawable.tab_icon))

		.setContent(intentMapView );


		// add all tabs 
		tabHost.addTab(tabMapView);
		 */

		generateTabs();
		/*
		tabHost.addTab(tabSpecPageOne);
		tabHost.addTab(tabSpecPageTwo);
		tabHost.addTab(tabSpecPageThree);
		tabHost.addTab(tabSpecPageFour);
		tabHost.addTab(tabSpecPageFive);
		tabHost.addTab(tabSpecPageSix);
		tabHost.addTab(tabSpecPageSeven);
		tabHost.addTab(tabSpecPageEight);
		 */
		//set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);

		//tabHost.getTabWidget().setEnabled(false);
		//tabHost.setEnabled(false);


		//Set the globe icon for the map icon view
		//ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.icon);
		//iv.setImageDrawable(getResources().getDrawable(R.drawable.globe));

		/*
		for(int i=1;i<tabHost.getTabWidget().getChildCount();i++)			{
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#660000")); 			
		}
		 */


		//tabHost.getTabWidget().setVisibility(View.GONE);






		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
				if (DEBUG_LOG) Log.d(TAG, "tab change " + tabId);

				setTabColor();
			}
		});



	}


	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.d(TAG, "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
	}

	public void generateTabs() {

		Intent intentPageOne = new Intent().setClass(this,  Material_Selection_Longitudinal_Form.class);
		TabSpec tabSpecPageOne = tabHost
		.newTabSpec("Page 1.1")
		.setIndicator("Mater L", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageOne);

		Intent intentPageOne2 = new Intent().setClass(this,  Material_Selection_Transverse_Form.class);
		TabSpec tabSpecPageOne2 = tabHost
		.newTabSpec("Page 1.2")
		.setIndicator("Mater T", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageOne2);		

		Intent intentPageTwo = new Intent().setClass(this,  LLRS_Selection_Longitudinal_Transverse_Form.class);
		TabSpec tabSpecPageTwo = tabHost
		.newTabSpec("Page 2.1")	
		.setIndicator("LLRS", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageTwo);


		Intent intentPageThree = new Intent().setClass(this,  Roof_Selection_Form.class);
		TabSpec tabSpecPageThree = tabHost
		.newTabSpec("Page 3")
		.setIndicator("Roof", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageThree);

		Intent intentPageFour = new Intent().setClass(this, Floor_Selection_Form.class);
		TabSpec tabSpecPageFour = tabHost
		.newTabSpec("Page 4")
		.setIndicator("Floor", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageFour);


		Intent intentPageFive = new Intent().setClass(this, Irregularity_Selection_Form.class);
		TabSpec tabSpecPageFive = tabHost
		.newTabSpec("Page 5")
		.setIndicator("Irreg", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageFive);




		Intent intentPageSix = new Intent().setClass(this, Occupancy_Selection_Form.class);
		TabSpec tabSpecPageSix= tabHost
		.newTabSpec("Page 6")
		.setIndicator("Occu", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageSix);



		Intent intentPageSeven = new Intent().setClass(this, Age_Selection_Form.class);
		TabSpec tabSpecPageSeven = tabHost
		.newTabSpec("Page 7")
		.setIndicator("AgeHght", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageSeven);


		Intent intentPageEight = new Intent().setClass(this, Details_Selection.class);
		TabSpec tabSpecPageEight = tabHost
		.newTabSpec("Page 8")
		.setIndicator("Comm.", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageEight);

		Intent intentPageNine = new Intent().setClass(this, Structure_Selection_Form.class);
		TabSpec tabSpecPageNine = tabHost
		.newTabSpec("Page 9")
		.setIndicator("Struc", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageNine);

		Intent intentPageTen = new Intent().setClass(this, Height_Selection_Form.class);
		TabSpec tabSpecPageTen = tabHost
		.newTabSpec("Page 10")
		.setIndicator("Height", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageTen);

		Intent intentPageEleven = new Intent().setClass(this, Exposure_Form.class);
		TabSpec tabSpecPageEleven = tabHost
		.newTabSpec("Page 11")
		.setIndicator("Exp", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageEleven);

		Intent intentPageTwelve = new Intent().setClass(this, Consequences_Form.class);
		TabSpec tabSpecPageTwelve = tabHost
		.newTabSpec("Page 11")
		.setIndicator("Conseq", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageTwelve);

		tabHost.addTab(tabSpecPageOne);
		tabHost.addTab(tabSpecPageOne2);
		tabHost.addTab(tabSpecPageFive);
		tabHost.addTab(tabSpecPageEight);
		tabHost.addTab(tabSpecPageTwo);


		tabHost.addTab(tabSpecPageThree);
		tabHost.addTab(tabSpecPageFour);

		tabHost.addTab(tabSpecPageSix);
		tabHost.addTab(tabSpecPageSeven);
		//tabHost.addTab(tabSpecPageTen);

		//tabHost.addTab(tabSpecPageNine);
		tabHost.addTab(tabSpecPageEleven);
		tabHost.addTab(tabSpecPageTwelve);

		initTabIcons(tabHost);
		setTabColor();
		completedTabs = new boolean[20];
		unlockTabIcons(tabHost);
	}

	public void removeTabs() {
		/*
		Intent intent = getIntent();
		finish();
		startActivity(intent);
		 */

		if (DEBUG_LOG) Log.d(TAG,"removing tabs " + tabHost.getTabWidget().getChildCount());
		//tabHost.clearAllTabs();

		Activity currentActivity = getLocalActivityManager().getActivity("Material");
		if (currentActivity instanceof Material_Selection_Longitudinal_Form) {
			((Material_Selection_Longitudinal_Form) currentActivity).clearThis();
		}


		int count = tabHost.getTabWidget().getChildCount();
		int i = tabHost.getTabWidget().getChildCount();
		while(tabHost.getTabWidget().getChildCount()>1)
		{
			//String currentActivityId = getLocalActivityManager().getCurrentId();
			//if (DEBUG_LOG) Log.d(TAG,"REMOVING SOMETHING CALLED: " + currentActivityId);
			//Activity currentActivity = getLocalActivityManager().getActivity(currentActivityId)

			tabHost.getTabWidget().removeView(tabHost.getTabWidget().getChildTabViewAt(i));

			i--;

		}
		generateTabs();


	}


	public void setTabColor() {

		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(unselectedTabColour); //unselected
		}
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedTabColour); // selected
	}


	public void initTabIcons(TabHost tabHost) {	

		tabHost.getTabWidget().setEnabled(true);
		tabHost.setEnabled(true);
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)			{
			//tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));

			ImageView iv2 = (ImageView)tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);
			iv2.setImageDrawable(getResources().getDrawable(R.drawable.tick_grey));

		}		 
	}


	public void next() {
		if (tabHost.getCurrentTab()<10){
			tabHost.setCurrentTab(tabHost.getCurrentTab()+1);
		}
	}

	public void back() {
		if (tabHost.getCurrentTab()>0){
			tabHost.setCurrentTab(tabHost.getCurrentTab()-1);
		}
	}

	public void restart() {
		lockTabIcons();
		tabHost.setCurrentTab(0);
		tabHost.getTabWidget().setEnabled(false);
		tabHost.setEnabled(false);

		initTabIcons(tabHost);
		setTabColor();
		removeTabs();
		HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.ScrollView);
		hsv.scrollTo(0,0);
		hsv.refreshDrawableState();
		if (DEBUG_LOG) Log.d(TAG, "scrolling to");

	}



	public void unlockTabIcons(TabHost tabHost){

		if (tabHost.getTabWidget().isEnabled() == false) {
			tabHost.getTabWidget().setEnabled(true);
			tabHost.setEnabled(true);

			unselectedTabColour = Color.parseColor("#000000");
			//selectedTabColour = Color.parseColor(colorString);

			tabHost.getTabWidget().setEnabled(true);
			for(int i=1;i<tabHost.getTabWidget().getChildCount();i++)			{
				//tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));
				ImageView iv2 = (ImageView)tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);

				iv2.setImageDrawable(getResources().getDrawable(R.drawable.tick_grey));
			}		
			setTabColor();
		}
	}

	public void lockTabIcons(){

		unselectedTabColour = Color.parseColor("#cccccc");
		//selectedTabColour = Color.parseColor(colorString);


		for(int i=1;i<tabHost.getTabWidget().getChildCount();i++)			{
			//tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));
			ImageView iv2 = (ImageView)tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);

			iv2.setImageDrawable(getResources().getDrawable(R.drawable.layers_icon));
		}		
		setTabColor();

	}




	public void completeTab(int tabIndex) {
		//tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));
		ImageView iv2 = (ImageView)tabHost.getTabWidget().getChildAt(tabIndex).findViewById(android.R.id.icon);
		iv2.setImageDrawable(getResources().getDrawable(R.drawable.finish_small));
		completedTabs[tabIndex] = true;
		if (DEBUG_LOG) Log.d(TAG, "completd tab " + completedTabs[tabIndex]);
	}

	public boolean isTabCompleted(int tabIndex) {
		return completedTabs[tabIndex];
	}

	public boolean saveData() {
		if (DEBUG_LOG) Log.d(TAG, "Saving data");		
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();


		/*
		EditText date1 = (EditText)findViewById(R.id.editTextDateVal1);
		EditText date2 = (EditText)findViewById(R.id.editTextDateVal2);
		String dateString1 = date1.getText().toString();
		String dateString2 = date2.getText().toString();


		//surveyDataObject.putData("D1", dateString1);
		//surveyDataObject.putData("D2",dateString2);


		EditText surveyComment = (EditText)findViewById(R.id.editTextSurveyComment);
		String surveyCommentString = surveyComment.getText().toString();
		 */

		mDbHelper = new GemDbAdapter(getBaseContext());      
		mDbHelper.createDatabase();      
		mDbHelper.open();		

		mDbHelper.insertGemData(surveyDataObject);
		//Should really try / catch this

		mDbHelper.close();
		//Toast.makeText(getApplicationContext(), "Survey data saved", Toast.LENGTH_SHORT).show();
		surveyDataObject.clearGemSurveyObject();
		surveyDataObject.unsavedEdits = false;

		return false;
	}





	public void backButtonPressed() {
		// do something on back.

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
				tabHost.setCurrentTab(0);

				saveData();
				MainTabActivity.this.finish();

			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
				surveyDataObject.clearGemSurveyObject();
				surveyDataObject.unsavedEdits = false;

				MainTabActivity.this.finish();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();

		return;
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"Take Picture");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()){

		case 0: //Take Picture
			if (DEBUG_LOG) Log.d(TAG,"camera class");

			//getSurveyPoint();

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


			break;
		default:
			break;
		}

		return false;
	}
	
	

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

}

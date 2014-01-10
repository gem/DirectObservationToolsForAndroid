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
package org.globalquakemodel.org.idctdo;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.globalquakemodel.org.idctdo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
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

	private static final int TEXT_ID_FOR_DIALOG = 0;


	public void onCreate(Bundle savedInstanceState) {		
		if (DEBUG_LOG) Log.d(TAG, "On Create of the  MainTab");		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_activity);


		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		boolean isFavourite=false;
		if (GemUtilities.isBlank(surveyDataObject.favouriteRecord)) {
			isFavourite = false;
		} else { 
			isFavourite = true;
		}

		//Ensure that 
		surveyDataObject.unsavedEdits = true;

		if (surveyDataObject.isExistingRecord || isFavourite) {
			if (DEBUG_LOG) Log.d(TAG, "EDITING EXISTING RECORD OR USING FAVOURITE");
			String idToLoad =  "";
			if (surveyDataObject.isExistingRecord) { 
				idToLoad = surveyDataObject.getUid();
				if (DEBUG_LOG) Log.d(TAG, "EDITING EXISTING RECORD");
			} else {
				idToLoad = surveyDataObject.favouriteRecord;
				if (DEBUG_LOG) Log.d(TAG, "SING FAVOURITE AS TEMPLATE");

			}
			mDbHelper = new GemDbAdapter(getBaseContext());
			mDbHelper.open();	
			if (DEBUG_LOG) Log.d(TAG, "Id of record to load data from: " + idToLoad);		
			Cursor surveyDataCursor = mDbHelper.getObjectByUid(idToLoad);
			Cursor gedDataCursor = mDbHelper.getGedObjectByUid(idToLoad);
			Cursor consequencesDataCursor = mDbHelper.getConsequencesObjectByUid(idToLoad);
			mDbHelper.close();
			//if (DEBUG_LOG) Log.d(TAG, "Existing data: " + surveyDataCursor.getColumnCount());
			if (DEBUG_LOG) Log.d(TAG,"colCount: " + surveyDataCursor.getColumnCount());
			for (int i = 0; i < surveyDataCursor.getColumnCount(); i++) {
				String colName = surveyDataCursor.getColumnName(i);
				//Check not change the locations back to the old points
				if(colName.equals("X")  || colName.equals("Y") || colName.equals("OBJ_UID")) {
				} else {
					//if (DEBUG_LOG) Log.d(TAG,"colName : " + colName);
					String colVal = surveyDataCursor.getString(i);
					//if (DEBUG_LOG) Log.d(TAG,"colVal : " + colVal);
					if (colVal != null) {						
						surveyDataObject.putData(colName, colVal);
					}
				}
			}			
			surveyDataCursor.close();

			//Load in the exposure data
			if (DEBUG_LOG) Log.d(TAG,"Ged colCount: " + gedDataCursor.getColumnCount());
			for (int i = 0; i < gedDataCursor.getColumnCount(); i++) {
				String colName = gedDataCursor.getColumnName(i);
				//Check not change the locations back to the old points
				if (DEBUG_LOG) Log.d(TAG,"GED values");
				if(isFavourite) {
					if(colName.equals("GEMOBJ_UID")) {
						surveyDataObject.putGedData(colName, surveyDataObject.getUid());
					} else if (colName.equals("GED_UID") ) {
						//Do nothing, we'll get a new uid for GED later
					} else {
						//Put the favourite / template attributes into the object
						if (DEBUG_LOG) Log.d(TAG,"ged colName : " + gedDataCursor.getColumnCount());
						String colVal = gedDataCursor.getString(i);
						//if (DEBUG_LOG) Log.d(TAG,"ged colVal : " + colVal);
						if (colVal != null) {						
							surveyDataObject.putGedData(colName, colVal);
						}
					}
				} else {				
					if (DEBUG_LOG) Log.d(TAG,"ged colName : " + gedDataCursor.getColumnCount());
					String colVal = gedDataCursor.getString(i);
					//if (DEBUG_LOG) Log.d(TAG,"ged colVal : " + colVal);
					if (colVal != null) {						
						surveyDataObject.putGedData(colName, colVal);
					}
				}
			}			
			gedDataCursor.close();



			if (DEBUG_LOG) Log.d(TAG,"consequences colCount: " + consequencesDataCursor.getColumnCount());

			//Consequences data
			if (DEBUG_LOG) Log.d(TAG,"consequences colCount: " + consequencesDataCursor.getColumnCount());
			for (int i = 0; i < consequencesDataCursor.getColumnCount(); i++) {
				String colName = consequencesDataCursor.getColumnName(i);
				//Check not change the locations back to the old points
				if(isFavourite) {
					if(colName.equals("GEMOBJ_UID")) {
						surveyDataObject.putConsequencesData(colName, surveyDataObject.getUid());
					} else if (colName.equals("CONSEQ_UID")) {	

					} else {
						if (DEBUG_LOG) Log.d(TAG,"ged colName : " + gedDataCursor.getColumnCount());
						String colVal = consequencesDataCursor.getString(i);
						//if (DEBUG_LOG) Log.d(TAG,"ged colVal : " + colVal);
						if (colVal != null) {						
							surveyDataObject.putConsequencesData(colName, colVal);
						}
					}
				} else {				
					if (DEBUG_LOG) Log.d(TAG,"CONSEQ colName : " + consequencesDataCursor.getColumnCount());
					String colVal = consequencesDataCursor.getString(i);
					//if (DEBUG_LOG) Log.d(TAG,"ged colVal : " + colVal);
					if (colVal != null) {						
						surveyDataObject.putConsequencesData(colName, colVal);
					}
				}
			}			
			consequencesDataCursor.close();

		} else {
			if (DEBUG_LOG) Log.d(TAG,"STARTING FRESH SURVEY FORMS");
		}

		ressources = getResources(); 
		tabHost = getTabHost(); 



		generateTabs();


		//set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);


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

	//Sets up the screens that go into the tabs
	//Also colours the tab with any values that have been filled out  
	public void generateTabs() {

		Intent intentPageOne=new Intent(this, SecondTabsActivity.class);
		TabSpec tabSpecPageOne = tabHost
		.newTabSpec("Page 1.1")
		.setIndicator("Material", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageOne);

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
		tabHost
		.newTabSpec("Page 9")
		.setIndicator("Struc", ressources.getDrawable(R.drawable.tab_icon))
		.setContent(intentPageNine);

		Intent intentPageTen = new Intent().setClass(this, Height_Selection_Form.class);
		tabHost
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
		//tabHost.addTab(tabSpecPageOne2);
		tabHost.addTab(tabSpecPageTwo);
		tabHost.addTab(tabSpecPageFive);
		tabHost.addTab(tabSpecPageEight);

		tabHost.addTab(tabSpecPageThree);
		tabHost.addTab(tabSpecPageFour);

		tabHost.addTab(tabSpecPageSix);
		tabHost.addTab(tabSpecPageSeven);
		//tabHost.addTab(tabSpecPageTen);

		//tabHost.addTab(tabSpecPageNine);
		tabHost.addTab(tabSpecPageEleven);
		tabHost.addTab(tabSpecPageTwelve);


		//tabHost.addTab(tabHost.newTabSpec("tab_test1").setIndicator("TAB 1").setContent(tabs2));

		initTabIcons(tabHost);
		setTabColor();
		completedTabs = new boolean[20];
		unlockTabIcons(tabHost);


		//Chuck all the survey data that is going to be edited together
		HashMap<String,String> keyVals = new HashMap();		
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		HashMap<String,String> gemObjects = surveyDataObject.getKeyValuePairsMap();
		HashMap<String,String> gedObjects = surveyDataObject.getGedKeyValuePairsMap();
		HashMap<String,String> consequencesObjects = surveyDataObject.getConsequencesKeyValuePairsMap();

		keyVals.putAll(gemObjects);
		keyVals.putAll(gedObjects);
		keyVals.putAll(consequencesObjects);

		//Check which of the tabs should be coloured as completed
		for (Map.Entry<String, String> entry : keyVals.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (DEBUG_LOG) Log.d(TAG,"Check key " + key);
			if (!GemUtilities.isBlank(value)) {
				int tabIdToComplete = findFormWithThisElement(key);
				if (DEBUG_LOG) Log.d(TAG,"Should complete tab " + tabIdToComplete);
				//Mark the tab colour as completed.
				if (tabIdToComplete > -1) {
					ImageView iv2 = (ImageView)tabHost.getTabWidget().getChildAt(tabIdToComplete).findViewById(android.R.id.icon);
					iv2.setImageDrawable(getResources().getDrawable(R.drawable.finish_small));
				}
			}
		}
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


	//TODO: User feedback on saving
	//Duplicated in MapActivity, should be moved elsewhere really
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

		return true;
	}

	public void saveSelectedAdapterData(String attributeKey,SelectedAdapter selectedAdapter) {
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		Log.d("IDCT", "selected adapter position " + selectedAdapter.getSelectedPosition());
		if (selectedAdapter.getSelectedPosition() > -1){
			Log.d("IDCT", "selected adapter is not empty");
			surveyDataObject.putData(attributeKey, selectedAdapter.getItem(selectedAdapter.getSelectedPosition()).getAttributeValue());
		} else {
			Log.d("IDCT", "selected adapter is empty. Save a null to " + attributeKey);
			surveyDataObject.putData(attributeKey, null);
			Log.d("IDCT", "surveyDataObject is " +surveyDataObject.getSurveyDataValue(attributeKey));
		}
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
				if (DEBUG_LOG) Log.d(TAG, "SAVE DATA FINISHED. Finish the activity");
				MainTabActivity.this.finish();
				if (DEBUG_LOG) Log.d(TAG, "Acitivity should be finished");					
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


	//Checks the arrays.xml to determine which forms hold which attributes
	public int findFormWithThisElement(String attributeKey) {
		Resources res = getResources();
		String[] attribKeys0a = res.getStringArray(R.array.formAttributeKeys0a);
		String[] attribKeys0b = res.getStringArray(R.array.formAttributeKeys0b);
		String[] attribKeys1 = res.getStringArray(R.array.formAttributeKeys1);
		String[] attribKeys2 = res.getStringArray(R.array.formAttributeKeys2);
		String[] attribKeys3 = res.getStringArray(R.array.formAttributeKeys3);
		String[] attribKeys4 = res.getStringArray(R.array.formAttributeKeys4);
		String[] attribKeys5 = res.getStringArray(R.array.formAttributeKeys5);
		String[] attribKeys6 = res.getStringArray(R.array.formAttributeKeys6);
		String[] attribKeys7 = res.getStringArray(R.array.formAttributeKeys7);
		String[] attribKeys8 = res.getStringArray(R.array.formAttributeKeys8);
		String[] attribKeys9 = res.getStringArray(R.array.formAttributeKeys9);
		int a = -1;

		a = Arrays.asList(attribKeys0a).contains(attributeKey)? 0 :
			Arrays.asList(attribKeys0b).contains(attributeKey)? 0:
				Arrays.asList(attribKeys1).contains(attributeKey)? 1:
					Arrays.asList(attribKeys2).contains(attributeKey)? 2:
						Arrays.asList(attribKeys3).contains(attributeKey)? 3:
							Arrays.asList(attribKeys4).contains(attributeKey)? 4:
								Arrays.asList(attribKeys5).contains(attributeKey)? 5:
									Arrays.asList(attribKeys6).contains(attributeKey)? 6:
										Arrays.asList(attribKeys7).contains(attributeKey)? 7:
											Arrays.asList(attribKeys8).contains(attributeKey)? 8:
												Arrays.asList(attribKeys9).contains(attributeKey)? 9:
													-1;				

		if (DEBUG_LOG) Log.d(TAG,"finding form with attribute: " + attributeKey + " in form attribs: " + a);

		return a;

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"Take Picture");
		menu.add(0,4,0,"View linked pictures");
		menu.add(0,1,0,"Save changes and close");
		menu.add(0,2,0,"Save changes and favourite");
		menu.add(0,5,0,"Delete record");
		menu.add(0,3,0,"Help");
		//menu.add(0,6,0,"Basic/Advanced");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()){

		case 0: //Take Picture
			if (DEBUG_LOG) Log.d(TAG,"camera class");

			//getSurveyPoint();

			getApplication();
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


			break;
		case 1: //Save button
			if (DEBUG_LOG) Log.d(TAG,"Save button class");	
			backButtonPressed();
			break;
		case 2: //Add as favourite
			if (DEBUG_LOG) Log.d(TAG,"Add this as a favourite");
			addAsFavourite();
			break;
		case 3: //Help
			if (DEBUG_LOG) Log.d(TAG,"Get help");	
			showHelp();
			break;
		case 4: //Add as favourite
			if (DEBUG_LOG) Log.d(TAG,"View linked pictures");
			viewLinkedPictures();
			break;
		case 5: //Delete this point
			if (DEBUG_LOG) Log.d(TAG,"Delete this point");		

			deleteThisRecord();
			break;
		case 6: //Basic /advanced switch mode
			basicAdvancedModeSwitch();
		default:
			break;
		}
		return false;
	}

	public void basicAdvancedModeSwitch() {
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();

		if (DEBUG_LOG) Log.d(TAG, "surveyDataObject.advancedView: " + surveyDataObject.advancedView);
		if (surveyDataObject.isShowingAdvancedView) { 	
			((SecondTabsActivity)getLocalActivityManager().getCurrentActivity()).hideAdvancedView(true);
			//surveyDataObject.advancedView = false;
		} else {
			((SecondTabsActivity)getLocalActivityManager().getCurrentActivity()).hideAdvancedView(false);
			//surveyDataObject.advancedView = true;
		}
	}



	public void deleteThisRecord() {		
		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();

		if (surveyDataObject.isExistingRecord) { 	
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Delete this record");
			builder.setMessage("Are you sure you want to delete this record?\n\nIf this record has been defined as a favourite template record it won't be available to create new records in future.");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {				
					GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
					String uidToDelete= surveyDataObject.getUid();				
					mDbHelper = new GemDbAdapter(getBaseContext());    				   
					mDbHelper.open();		
					mDbHelper.deleteRecordByUid(uidToDelete);
					//Should really try / catch this
					mDbHelper.close();				

					tabHost.setCurrentTab(0);
					surveyDataObject.clearGemSurveyObject();
					surveyDataObject.unsavedEdits = false;


					if (DEBUG_LOG) Log.d(TAG, "DELETE FINISHED. Finish the activity");
					MainTabActivity.this.finish();
					if (DEBUG_LOG) Log.d(TAG, "Acitivity should be finished");

					return;
				}
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});

			builder.show();

		} else {
			Toast.makeText(getApplicationContext(), "Can't delete this as it's not saved!", Toast.LENGTH_SHORT).show();
		}



	}

	//Checks the arrays.xml to determine which forms hold which attributes
	public void viewLinkedPictures() {
		if (DEBUG_LOG) Log.d(TAG,"Viewing linked pictures");

		GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
		String uid = surveyDataObject.getUid();

		mDbHelper = new GemDbAdapter(getBaseContext());      
		mDbHelper.createDatabase();      
		mDbHelper.open();
		Cursor mCursor = mDbHelper.getAllMediaByRecord(uid);
		//Should really try / catch this
		mDbHelper.close();		
		ArrayList<DBRecord> buildingPositionAttributesList = GemUtilities.cursorToArrayList(mCursor);	

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Linked images. Note: the list will only show images once the survey point is saved.");	

		final ArrayAdapter<String> modeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,  buildingPositionAttributesList);
		builder.setAdapter(modeAdapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
			}

		});
		builder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//dismiss the dialog  
			}
		});
		builder.show();


	}
	//Checks the arrays.xml to determine which forms hold which attributes
	public void addAsFavourite() {
		if (DEBUG_LOG) Log.d(TAG,"ADDING AS FAVOURITE");


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Name this favourite");
		builder.setMessage("Enter a name for this favourite");
		// Use an EditText view to get user input.
		final EditText input = new EditText(this);
		input.setId(TEXT_ID_FOR_DIALOG);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				Log.d(TAG, "Favourite name: " + value);

				GEMSurveyObject surveyDataObject = (GEMSurveyObject)getApplication();
				String uidToFavourite = surveyDataObject.getUid();
				if (DEBUG_LOG) Log.d(TAG,"Uid is for favouriting: " + uidToFavourite);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
				Date currentDate = new Date(System.currentTimeMillis());
				String currentDateandTime = sdf.format(currentDate);
				String name = value + "_" + currentDateandTime.toString();

				mDbHelper = new GemDbAdapter(getBaseContext());      
				mDbHelper.createDatabase();      
				mDbHelper.open();
				mDbHelper.insertFavourite(name,uidToFavourite);
				//Should really try / catch this
				mDbHelper.close();		

				tabHost.setCurrentTab(0);
				saveData();
				if (DEBUG_LOG) Log.d(TAG, "SAVE DATA FINISHED. Finish the activity");
				MainTabActivity.this.finish();
				if (DEBUG_LOG) Log.d(TAG, "Acitivity should be finished");	


				return;
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		builder.show();


	}




	public String loadHelpFileNames(String strToCheck) {
		//File folder = new File("file:///android_asset/glossary");
		if (DEBUG_LOG) Log.d(TAG,"help loading: " + strToCheck);
		String pageToLoad = "";
		try {
			String[] elements = listFiles("glossary/glossary_cleaned","nothing");
			if (DEBUG_LOG) Log.d(TAG,"starting match loop for elements. " + elements.length);
			for( int i = 0; i < elements.length; i++)
			{				
				String element = elements[i].substring(0, elements[i].lastIndexOf('.'));    
				String[] parts = element.split("--");
				if (parts.length > 1) {
					if (strToCheck.toLowerCase().equals(parts[1].toLowerCase())) {
						pageToLoad = element; 
						//if (DEBUG_LOG) Log.d(TAG,"str: " +  strToCheck + " element.toString(): "+element.toString() );
						break;
					}
				}
				/*
				d = GemUtilities.getLevenshteinDistance(strToCheck.toLowerCase(), element.toLowerCase());			   

				if (DEBUG_LOG) Log.d(TAG,"LevShtein: " + d + " i: "+ i  );
				if (DEBUG_LOG) Log.d(TAG,"str: " +  strToCheck + " element.toString(): "+element.toString() );

				if (d < lowestD) {
					lowestD = d;
					lowestIndex = i;
					matched = element;
				}*/
			}
			/*
			if (DEBUG_LOG) Log.d(TAG,"Matched index: " + elements[lowestIndex]);
			if (DEBUG_LOG) Log.d(TAG,"Match: " + lowestIndex);
			if (DEBUG_LOG) Log.d(TAG,"Match Val: " + lowestD);
			if (DEBUG_LOG) Log.d(TAG,"Query string: " + strToCheck);


			if (lowestD < 10) {
				pageToLoad = elements[0];
			}*/

			if (DEBUG_LOG) Log.d(TAG,"Page to Load: " + pageToLoad);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageToLoad + ".html";
	}


	private String[] listFiles(String dirFrom, String dirTo) throws IOException {
		Resources res = getResources(); //if you are in an activity
		AssetManager am = res.getAssets();
		String fileList[] = am.list(dirFrom);
		if (DEBUG_LOG) Log.d(TAG,"fileList: " + fileList.toString());
		if (fileList != null)	{   
			for ( int i = 0;i<fileList.length;i++)			{
				//if (DEBUG_LOG) Log.d(TAG,fileList[i]);
			}
		} else {
			if (DEBUG_LOG) Log.d(TAG,"fileList empty");
		}
		return fileList;
	}


	public void fileCopy(String srcStr, String dstStr) throws IOException {
		File src =  new File(srcStr );
		File dst = new File(dstStr);
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Check if an asset exists. This will fail if the asset has a size < 1 byte.
	 * @param context
	 * @param path
	 * @return TRUE if the asset exists and FALSE otherwise
	 */
	public static boolean assetExists(Context context, String pathInAssets) {
		boolean bAssetOk = false;
		AssetManager mg = context.getAssets();

		try {
			mg.open(pathInAssets);
			bAssetOk = true;

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bAssetOk;
	}

	public void showHelp() {
		GEMSurveyObject g = (GEMSurveyObject)getApplication();
		if (!GemUtilities.isBlank(g.lastEditedAttribute)) { 
			String pageToLoad = g.lastEditedAttribute + ".html";
			if (DEBUG_LOG) Log.d("IDCT","Going to try help file for: " + g.lastEditedAttribute );
			pageToLoad = loadHelpFileNames(g.lastEditedAttribute);
			//File file = new File("file:///android_asset/glossary/glossary_cleaned/" + pageToLoad);
			if (assetExists(getBaseContext(),"glossary/glossary_cleaned/" + pageToLoad)) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				WebView wv = new WebView(this);			
				wv.loadUrl("file:///android_asset/glossary/glossary_cleaned/" + pageToLoad);
				wv.setWebViewClient(new WebViewClient()
				{
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url)
					{
						view.loadUrl(url);
						return true;
					}
				});

				alert.setView(wv);
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
					}
				});
				alert.show();
			} else {
				Toast.makeText(this, "There is no help associated with the item", Toast.LENGTH_SHORT).show();				
			}
		} else {
			Toast.makeText(this, "There is no help associated with the item", Toast.LENGTH_SHORT).show();			
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (DEBUG_LOG) Log.d("IDCT","CAMERA callback. requestCode:" +  requestCode + " resultCode: " + resultCode);

		if (requestCode == CAMERA_RESULT) {
			if (DEBUG_LOG) Log.d("IDCT","CAMERA callback. requestCode is CAMERA_RESULT");
			//ShowMessage(outputFileUri.toString());
			if (resultCode == Activity.RESULT_OK) {
				if (DEBUG_LOG) Log.d("IDCT","CAMERA callback. Result is ok");

				/* Disabled linking photos in map view 21 Feb 13
				 * Doesn't seem to work on Samsung Galaxy S2*/
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
							UUID.randomUUID();
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
							UUID.randomUUID();
							surveyDataObject.putMediaData(
									"MEDIA_UID", FILENAME,
									"MEDIA_TYPE", "PHOTO",
									"COMMENTS", "no comment entered",
									"FILENAME", FILENAME + ".jpg"
							);			
						}
					});

					alert.show();
				} else {
					if (DEBUG_LOG) Log.d("IDCT","CAMERA callback. No unsaved edits");
				}


			} else {
				Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
			}						
		}
	}

}

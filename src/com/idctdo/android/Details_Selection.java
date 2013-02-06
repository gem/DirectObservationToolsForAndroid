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

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Details_Selection extends Activity {

	public boolean DEBUG_LOG = true; 
	
	private ArrayList list;
	public ArrayList<DBRecord> lLrsd;


	ListView listview;
	ListView listview2;

	Button btn_saveObservation;
	Button btn_cancelObservation;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 3;

	public EditText editTextSurveyComment;

	
	private String buildingPositionAttributeDictionary = "DIC_POSITION";
	private String buildingPositionAttributeKey = "POSITION";
	
	private String buildingShapeAttributeDictionary = "DIC_PLAN_SHAPE";
	private String buildingShapeAttributeKey = "PLAN_SHAPE";
	
	public Spinner spinnerBuildingPosition;
	public Spinner spinnerBuildingShape;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_selectable_list);
		btn_saveObservation =(Button)findViewById(R.id.btn_save_observation);
		btn_saveObservation.setOnClickListener(saveObservationListener);

		btn_cancelObservation =(Button)findViewById(R.id.btn_cancel_observation);
		btn_cancelObservation.setOnClickListener(cancelObservationListener);

	}

	
	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity a = (MainTabActivity)getParent();
		surveyDataObject = (GEMSurveyObject)getApplication();



		if (a.isTabCompleted(tabIndex)) {

		} else {
			
			mDbHelper = new GemDbAdapter(getBaseContext()); 

			mDbHelper.createDatabase();      
			mDbHelper.open();
			
			editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
			editTextSurveyComment.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
						surveyDataObject.putData("COMMENTS", editTextSurveyComment.getText().toString());
						completeThis();
					}
				}
			});
			
			
			
			spinnerBuildingPosition = (Spinner)  findViewById(R.id.spinnerBuildingPosition);
			final Cursor roofShapeAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(buildingPositionAttributeDictionary);
			ArrayList<DBRecord> roofShapeAttributesList = GemUtilities.cursorToArrayList(roofShapeAttributeDictionaryCursor);
			ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,roofShapeAttributesList );
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerBuildingPosition.setAdapter(spinnerArrayAdapter);
			spinnerBuildingPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerBuildingPosition.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					
					//Temporarily disabled 7/1/13					
					//allAttributeTypesTopLevelCursor.moveToPosition(pos);
					//Log.d("IDCT","spinner selected pos val: " + allAttributeTypesTopLevelCursor.getString(1));							
					//surveyDataObject.putGedData(topLevelAttributeKey,  allAttributeTypesTopLevelCursor.getString(1).toString());
					DBRecord selected = (DBRecord) spinnerBuildingPosition.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putGedData(buildingPositionAttributeKey, selected.getAttributeValue());		
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
					
			
			spinnerBuildingShape = (Spinner)  findViewById(R.id.spinnerBuildingShape);
			final Cursor buildingShapeAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(buildingShapeAttributeDictionary);
			ArrayList<DBRecord> buildingShapeAttributesList = GemUtilities.cursorToArrayList(buildingShapeAttributeDictionaryCursor);
			ArrayAdapter spinnerArrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,buildingShapeAttributesList);
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerBuildingShape.setAdapter(spinnerArrayAdapter2);
			spinnerBuildingShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerBuildingShape.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					
					//Temporarily disabled 7/1/13					
					//allAttributeTypesTopLevelCursor.moveToPosition(pos);
					//Log.d("IDCT","spinner selected pos val: " + allAttributeTypesTopLevelCursor.getString(1));							
					//surveyDataObject.putGedData(topLevelAttributeKey,  allAttributeTypesTopLevelCursor.getString(1).toString());
					DBRecord selected = (DBRecord) spinnerBuildingShape.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putGedData(buildingShapeAttributeKey, selected.getAttributeValue());		
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			
			
			
			
			
			roofShapeAttributeDictionaryCursor.close();
			mDbHelper.close();
		}
	}



	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}

	private OnClickListener saveObservationListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//mWebView.loadUrl("javascript:map.zoomOut()");
			Log.d("IDCT", "Save observation");

			MainTabActivity a = (MainTabActivity)getParent();	
			a.saveData();
			//a.restart();

			//Intent ModifiedEMS98 = new Intent (Details_Selection.this, EQForm_MapView.class);
			//startActivity(ModifiedEMS98);
			a.finish();			
		}

	};

	private OnClickListener cancelObservationListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//mWebView.loadUrl("javascript:map.zoomOut()");
			Log.d("IDCT", "Cancel observation");

			MainTabActivity a = (MainTabActivity)getParent();	
			a.finish();			
		}

	};


	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


}

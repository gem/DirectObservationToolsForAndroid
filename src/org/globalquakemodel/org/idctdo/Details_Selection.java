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
 * GNU General Public License r more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IDCT Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.globalquakemodel.org.idctdo;


import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;

import java.util.ArrayList;
import org.globalquakemodel.org.idctdo.R;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;

public class Details_Selection extends Activity {

	
	public boolean DEBUG_LOG = false; 
	
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
	private String commentsAttributeKey = "COMMENTS";
	
	public EditText editTextSlope;
	private String slopeAttributeKey = "SLOPE";

	public EditText editTextDirectionX;
	private String directionXKey = "DIRECT_1";
	
	public EditText editTextDirectionY;
	private String directionYKey = "DIRECT_2";
	
	
	private String buildingPositionAttributeDictionary = "DIC_POSITION";
	private String buildingPositionAttributeKey = "POSITION";
	
	private String buildingShapeAttributeDictionary = "DIC_PLAN_SHAPE";
	private String buildingShapeAttributeKey = "PLAN_SHAPE";
	
	private String nonStructuralExteriorWallsDictionary = "DIC_NONSTRUCTURAL_EXTERIOR_WALLS";
	private String nonStructuralExteriorWallsAttributeKey = "NONSTRCEXW";
	
	public Spinner spinnerBuildingPosition;
	public Spinner spinnerBuildingShape;
	public Spinner spinnerNonStructuralExteriorWalls;
	
	
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
					//if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
						surveyDataObject.putData(commentsAttributeKey, editTextSurveyComment.getText().toString());
						surveyDataObject.lastEditedAttribute = commentsAttributeKey;
						completeThis();
					//}
				}
			});
			
			editTextSlope = (EditText) findViewById(R.id.editTextSlope);
			editTextSlope.setOnFocusChangeListener(new OnFocusChangeListener() {				
				public void onFocusChange(View v, boolean hasFocus) {
					editTextSlope = (EditText) findViewById(R.id.editTextSlope);
					//if(!hasFocus) {
						Log.d("IDCT", "FOCUS OF EDIT TEXT");						
						surveyDataObject.putData(slopeAttributeKey , editTextSlope.getText().toString());
						surveyDataObject.lastEditedAttribute = slopeAttributeKey;
						completeThis();
				}
			});
			
			
			editTextDirectionX= (EditText) findViewById(R.id.editTextDirectionX);
			editTextDirectionX.setOnFocusChangeListener(new OnFocusChangeListener() {				
				public void onFocusChange(View v, boolean hasFocus) {
					//if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextDirectionX = (EditText) findViewById(R.id.editTextDirectionX);
						surveyDataObject.putData(directionXKey, editTextDirectionX.getText().toString());
						surveyDataObject.lastEditedAttribute = directionXKey;
						completeThis();
					//}
				}
			});
			editTextDirectionY= (EditText) findViewById(R.id.editTextDirectionY);
			editTextDirectionY.setOnFocusChangeListener(new OnFocusChangeListener() {				
				public void onFocusChange(View v, boolean hasFocus) {
					//if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextDirectionY = (EditText) findViewById(R.id.editTextDirectionY);
						surveyDataObject.putData(directionYKey, editTextDirectionY.getText().toString());
						surveyDataObject.lastEditedAttribute = directionYKey;
						completeThis();
					//}
				}
			});
									
			spinnerBuildingPosition = (Spinner)  findViewById(R.id.spinnerBuildingPosition);
			final Cursor buildingPositionAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(buildingPositionAttributeDictionary);
			ArrayList<DBRecord> buildingPositionAttributesList = GemUtilities.cursorToArrayList(buildingPositionAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,buildingPositionAttributesList );
			CustomAdapter spinnerArrayAdapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, buildingPositionAttributesList , 0);			
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerBuildingPosition.setAdapter(spinnerArrayAdapter);
			spinnerBuildingPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerBuildingPosition.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);					
	
					DBRecord selected = (DBRecord) spinnerBuildingPosition.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(buildingPositionAttributeKey, selected.getAttributeValue());
					completeThis();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			
			
			buildingPositionAttributeDictionaryCursor.close();
			
			spinnerBuildingShape = (Spinner)  findViewById(R.id.spinnerBuildingShape);
			final Cursor buildingShapeAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(buildingShapeAttributeDictionary);
			ArrayList<DBRecord> buildingShapeAttributesList = GemUtilities.cursorToArrayList(buildingShapeAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,buildingShapeAttributesList);
			CustomAdapter spinnerArrayAdapter2 = new CustomAdapter(this, android.R.layout.simple_spinner_item, buildingShapeAttributesList , 0);
			spinnerArrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerBuildingShape.setAdapter(spinnerArrayAdapter2);
			spinnerBuildingShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerBuildingShape.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerBuildingShape.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(buildingShapeAttributeKey, selected.getAttributeValue());
					completeThis();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			buildingShapeAttributeDictionaryCursor.close();
			
			spinnerNonStructuralExteriorWalls = (Spinner)  findViewById(R.id.spinnerNonStructuralExteriorWalls);
			final Cursor nonStructuralExteriorWallsDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(nonStructuralExteriorWallsDictionary);
			ArrayList<DBRecord> nonStructuralExteriorWallsAttributesList = GemUtilities.cursorToArrayList(nonStructuralExteriorWallsDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nonStructuralExteriorWallsAttributesList);
			CustomAdapter spinnerArrayAdapter3 = new CustomAdapter(this, android.R.layout.simple_spinner_item, nonStructuralExteriorWallsAttributesList , 0);
			
			spinnerArrayAdapter3.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerNonStructuralExteriorWalls.setAdapter(spinnerArrayAdapter3);
			spinnerNonStructuralExteriorWalls.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerNonStructuralExteriorWalls.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerNonStructuralExteriorWalls.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(nonStructuralExteriorWallsAttributeKey, selected.getAttributeValue());
					completeThis();					
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});					
			nonStructuralExteriorWallsDictionaryCursor.close();		
						
			
			mDbHelper.close();
						
			
			//Resume any previous values if editing existing attribute 
			editTextSurveyComment.setText(surveyDataObject.getSurveyDataValue(commentsAttributeKey));
			editTextSlope.setText(surveyDataObject.getSurveyDataValue(slopeAttributeKey));
			editTextDirectionX.setText(surveyDataObject.getSurveyDataValue(directionXKey));
			editTextDirectionY.setText(surveyDataObject.getSurveyDataValue(directionYKey));
							
			boolean result = false;	
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerBuildingPosition, buildingPositionAttributesList, buildingPositionAttributeKey,surveyDataObject.getSurveyDataValue(buildingPositionAttributeKey));
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerBuildingShape, buildingShapeAttributesList, buildingShapeAttributeKey,surveyDataObject.getSurveyDataValue(buildingShapeAttributeKey));
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerNonStructuralExteriorWalls, nonStructuralExteriorWallsAttributesList, nonStructuralExteriorWallsAttributeKey,surveyDataObject.getSurveyDataValue(nonStructuralExteriorWallsAttributeKey));				
		}//end tab is completed check
		
		surveyDataObject.lastEditedAttribute = "";
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

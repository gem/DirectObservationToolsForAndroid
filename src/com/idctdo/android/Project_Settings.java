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

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Project_Settings extends Activity {

	public boolean DEBUG_LOG = true; 

	private ArrayList list;
	public ArrayList<DBRecord> lLrsd;


	ListView listview;
	ListView listview2;

	Button btn_saveObservation;



	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public EditText editTextSurveyComment;
	private String commentsAttributeKey = "COMMENTS";

	public EditText projectName;
	public EditText projectSummary;
	public EditText surveyorName;
	public Date surveyDate;
	
	private String projectNameAttributeKey = "PROJ_NAME";

	private String buildingPositionAttributeDictionary = "DIC_POSITION";
	private String buildingPositionAttributeKey = "POSITION";

	private String buildingShapeAttributeDictionary = "DIC_PLAN_SHAPE";
	private String buildingShapeAttributeKey = "PLAN_SHAPE";

	private String nonStructuralExteriorWallsDictionary = "DIC_NONSTRUCTURAL_EXTERIOR_WALLS";
	private String nonStructuralExteriorWallsAttributeKey = "NONSTRCEXW";

	public Spinner spinnerProjectSelection;
	public Spinner spinnerBuildingShape;
	public Spinner spinnerNonStructuralExteriorWalls;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_settings);
		btn_saveObservation =(Button)findViewById(R.id.btn_save_observation);
		btn_saveObservation.setOnClickListener(saveObservationListener);


	}


	@Override
	protected void onResume() {
		super.onResume();

		surveyDataObject = (GEMSurveyObject)getApplication();
		mDbHelper = new GemDbAdapter(getBaseContext()); 

		mDbHelper.createDatabase();      
		mDbHelper.open();

		projectName = (EditText) findViewById(R.id.projectName);
		projectName.setOnFocusChangeListener(new OnFocusChangeListener() {				
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");

				}
			}
		});


		projectSummary= (EditText) findViewById(R.id.projectSummary);
		projectSummary.setOnFocusChangeListener(new OnFocusChangeListener() {				
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");

				}
			}
		});


		surveyorName= (EditText) findViewById(R.id.surveyorName);
		surveyorName.setOnFocusChangeListener(new OnFocusChangeListener() {				
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");

				}
			}
		});

		setUpPreferenceList();

		mDbHelper.close();

	}



	private void setUpPreferenceList() {
		mDbHelper = new GemDbAdapter(getBaseContext()); 

		mDbHelper.createDatabase();      
		mDbHelper.open();
		spinnerNonStructuralExteriorWalls = (Spinner)  findViewById(R.id.spinnerNonStructuralExteriorWalls);
		final Cursor nonStructuralExteriorWallsDictionaryCursor = mDbHelper.getGemProjects();
		ArrayList<DBRecord> nonStructuralExteriorWallsAttributesList = GemUtilities.cursorToArrayList(nonStructuralExteriorWallsDictionaryCursor);
		ArrayAdapter spinnerArrayAdapter3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nonStructuralExteriorWallsAttributesList);
		
		mDbHelper.close();
		nonStructuralExteriorWallsDictionaryCursor.close();
		
		spinnerArrayAdapter3.setDropDownViewResource(R.layout.simple_spinner_item);
		spinnerNonStructuralExteriorWalls.setAdapter(spinnerArrayAdapter3);
		
		spinnerNonStructuralExteriorWalls.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				//Object item = parent.getItemAtPosition(pos);
				if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerNonStructuralExteriorWalls.getSelectedItem().toString());
				if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);

				DBRecord selected = (DBRecord) spinnerNonStructuralExteriorWalls.getSelectedItem();
				if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());

				
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("projectIdTextPref", selected.getAttributeValue());
				editor.commit();

				String feedbackMsg = "Using project id\n " + selected.getAttributeValue();
				Toast.makeText(getBaseContext().getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();

				
/*

				mDbHelper = new GemDbAdapter(getBaseContext()); 
				mDbHelper.open();
				Cursor mCursor = mDbHelper.getGemProjectById(selected.getAttributeValue());

				if (mCursor != null) {
					mCursor.moveToFirst();

					projectName.setText(mCursor.getString(mCursor.getColumnIndex("PROJ_NAME")));
					surveyorName.setText(mCursor.getString(mCursor.getColumnIndex("USER_MADE")));
					projectSummary.setText(mCursor.getString(mCursor.getColumnIndex("PROJ_SUMRY")));

				}

				mCursor.close();
				mDbHelper.close();    		          
*/

			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});			
		
		
		
	}


	private OnClickListener saveObservationListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//mWebView.loadUrl("javascript:map.zoomOut()");
			Log.d("IDCT", "Save observation");

			mDbHelper = new GemDbAdapter(getBaseContext()); 
			mDbHelper.createDatabase();      
			mDbHelper.open();
			
			DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
			int day = datePicker.getDayOfMonth();
			int month = datePicker.getMonth() + 1;
			int year = datePicker.getYear();
			
			Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());
			
			mDbHelper.insertProject(projectName.getText().toString(), surveyorName.getText().toString(), projectSummary.getText().toString(),date);
			mDbHelper.close();
			
			
			setUpPreferenceList();
			
		}

	};

	/*
	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
	}

	 */
}

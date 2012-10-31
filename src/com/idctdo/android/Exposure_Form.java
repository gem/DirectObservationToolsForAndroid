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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Exposure_Form extends EQForm {


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
	public int tabIndex = 9;

	public EditText editTextNumberOfDayOccupants;
	public EditText editTextNumberOfNightOccupants;	
	public EditText editTextNumberOfTransitOccupants;	
	public EditText editTextNumberOfDwellings;
	public EditText editTextPlanArea;
	public EditText editTextReplacementCost;
	public EditText editTextExposureComments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exposure);
	}



	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity a = (MainTabActivity)getParent();
		surveyDataObject = (GEMSurveyObject)getApplication();
		if (a.isTabCompleted(tabIndex)) {

		} else {
			editTextNumberOfDayOccupants = (EditText) findViewById(R.id.editTextNumberOfDayOccupants);
			editTextNumberOfDayOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextNumberOfDayOccupants = (EditText) findViewById(R.id.editTextNumberOfDayOccupants);
						surveyDataObject.putData("COMMENTS", editTextNumberOfDayOccupants.getText().toString());
						completeThis();
					}
				}
			});
			editTextNumberOfNightOccupants = (EditText) findViewById(R.id.editTextNumberOfNightOccupants);
			editTextNumberOfNightOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextNumberOfNightOccupants = (EditText) findViewById(R.id.editTextNumberOfNightOccupants );
						surveyDataObject.putData("COMMENTS", editTextNumberOfNightOccupants.getText().toString());
						completeThis();
					}
				}
			});
			editTextNumberOfTransitOccupants = (EditText) findViewById(R.id.editTextNumberOfTransitOccupants );
			editTextNumberOfTransitOccupants.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextNumberOfTransitOccupants  = (EditText) findViewById(R.id.editTextNumberOfTransitOccupants );
						surveyDataObject.putData("COMMENTS", editTextNumberOfNightOccupants.getText().toString());
						completeThis();
					}
				}
			});
			
			editTextNumberOfDwellings= (EditText) findViewById(R.id.editTextNumberOfDwellings);
			editTextNumberOfDwellings.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextNumberOfDwellings = (EditText) findViewById(R.id.editTextNumberOfDwellings);
						surveyDataObject.putData("COMMENTS", editTextNumberOfDwellings.getText().toString());
						completeThis();
					}
				}
			});
			
			editTextPlanArea= (EditText) findViewById(R.id.editTextPlanArea);
			editTextPlanArea.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextPlanArea = (EditText) findViewById(R.id.editTextPlanArea);
						surveyDataObject.putData("COMMENTS", editTextPlanArea.getText().toString());
						completeThis();
					}
				}
			});
			
		}
	}		
	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}
	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


}
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

public class Consequences_Form extends EQForm {
	public boolean DEBUG_LOG = false; 
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 10;

	
	

	public EditText editTextNumberOfFatalities;
	public EditText editTextNumberOfInjured;	
	public EditText editTextNumberOfMissing;	
	public EditText editTextConsequencesComments;
	

	
	private String topLevelAttributeDictionary = "DIC_DAMAGE";
	private String topLevelAttributeKey = "DAMAGE";
	
	private String attributeKey1 = "FATALITIES";
	private String attributeKey2 = "INJURED";
	private String attributeKey3 = "MISSING";
	private String attributeKey4 = "COMMENTS";
	
	ListView listview;
	ListView listview2;
	private SelectedAdapter selectedAdapter;
	
	
	Button btn_saveObservation;
	Button btn_cancelObservation;
	
	public GemDbAdapter mDbHelper;

	public GEMSurveyObject surveyDataObject;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consequences);
		
		editTextNumberOfFatalities = (EditText) findViewById(R.id.editTextNumberOfFatalities);
		editTextNumberOfInjured = (EditText) findViewById(R.id.editTextNumberOfInjured);
		editTextNumberOfMissing = (EditText) findViewById(R.id.editTextNumberOfMissing );
		editTextConsequencesComments= (EditText) findViewById(R.id.editTextConsequencesComments);
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

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());
			mDbHelper.close();
			

			editTextNumberOfFatalities.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putConsequencesData(attributeKey1, editTextNumberOfFatalities.getText().toString());
						completeThis();
					}
				}
			});

			editTextNumberOfInjured.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putConsequencesData(attributeKey2, editTextNumberOfInjured.getText().toString());
						completeThis();
					}
				}
			});
			editTextNumberOfMissing.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");

						surveyDataObject.putConsequencesData(attributeKey3, editTextNumberOfInjured.getText().toString());
						completeThis();
					}
				}
			});
			

			editTextConsequencesComments.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");

						surveyDataObject.putConsequencesData(attributeKey4, editTextConsequencesComments.getText().toString());
						completeThis();
					}
				}
			});
			
			
			
			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);
			listview = (ListView) findViewById(R.id.listDamageGrade);
			listview.setAdapter(selectedAdapter);        

			
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					selectedAdapter.setSelectedPosition(position);
					surveyDataObject.putConsequencesData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());
					
				}
			});
				
			
		}
	}
	public void completeThis() {
		//MainTabActivity a = (MainTabActivity)getParent();
		//a.completeTab(tabIndex);
	}
	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


}
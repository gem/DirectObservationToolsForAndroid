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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Consequences_Form extends Activity {
	public boolean DEBUG_LOG = false; 
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 9;
	

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
			allAttributeTypesTopLevelCursor.close();
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
				
			
			//Resume any attributes
			editTextNumberOfFatalities.setText(surveyDataObject.getConsequencesDataValue(attributeKey1));
			editTextNumberOfInjured.setText(surveyDataObject.getConsequencesDataValue(attributeKey2));
			editTextNumberOfMissing.setText(surveyDataObject.getConsequencesDataValue(attributeKey3));
			editTextConsequencesComments.setText(surveyDataObject.getConsequencesDataValue(attributeKey4));

			boolean result = false;	
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));

			
			
		}//end tab completed check
		
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

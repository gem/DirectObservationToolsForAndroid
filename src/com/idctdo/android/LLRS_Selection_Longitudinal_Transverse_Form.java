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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class LLRS_Selection_Longitudinal_Transverse_Form extends Activity {

	private static final String TAG = "IDCT";
	public boolean DEBUG_LOG = false; 
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 1;


	private String topLevelAttributeDictionary = "DIC_LLRS";
	private String secondLevelAttributeDictionary = "DIC_LLRS_DUCTILITY";

	private String topLevelAttributeKeyLongitudinal = "LLRS_L";
	private String topLevelAttributeKeyTransverse = "LLRS_T";
	private String secondLevelAttributeKeyLongitudinal = "LLRS_DCT_L";	
	private String secondLevelAttributeKeyTransverse = "LLRS_DCT_T";

	private String llrsQualifierAttributeDictionary = "DIC_LLRS_QUAL";
	private String llrsQualifierAttributeKey = "LLRS_QUAL";
	
	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;
	private SelectedAdapter selectedAdapter4;
	private SelectedAdapter selectedAdapter5;
	
	public Spinner spinnerLlrsQualifier;

	private ArrayList list;

	public ArrayList<DBRecord> lLrs;
	public ArrayList<DBRecord> lLrsd;


	ListView listview;
	ListView listview2;
	ListView listview3;
	ListView listview4;
	ListView listview5;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.llrs_selectable_list_longitudinal_transverse);
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
			
			Cursor allLLRSCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);        
			lLrs = GemUtilities.cursorToArrayList(allLLRSCursor);        
			if (DEBUG_LOG) Log.d(TAG,"lLrs: " + lLrs.toString());

			Cursor allLLRSDCursor = mDbHelper.getAttributeValuesByDictionaryTable(secondLevelAttributeDictionary); 
			lLrsd = GemUtilities.cursorToArrayList(allLLRSDCursor);
			if (DEBUG_LOG) Log.d(TAG,"lLrs: " + lLrsd.toString());

			allLLRSCursor.close(); 
			allLLRSDCursor.close();

						
			selectedAdapter2 = new SelectedAdapter(this,0,lLrs);    		
			selectedAdapter2.setNotifyOnChange(true);		
			listview2 = (ListView) findViewById(R.id.listLLRSLongitudinal);
			listview2.setAdapter(selectedAdapter2);        


			selectedAdapter3 = new SelectedAdapter(this,0,lLrsd);    		
			selectedAdapter3.setNotifyOnChange(true);		
			listview3 = (ListView) findViewById(R.id.listLLRSLongitudinalDuctility);
			listview3.setAdapter(selectedAdapter3);        

			selectedAdapter4 = new SelectedAdapter(this,0,lLrs);    		
			selectedAdapter4.setNotifyOnChange(true);		
			listview4 = (ListView) findViewById(R.id.listLLRSTransverse);
			listview4.setAdapter(selectedAdapter4);        


			selectedAdapter5 = new SelectedAdapter(this,0,lLrsd);    		
			selectedAdapter5.setNotifyOnChange(true);		
			listview5 = (ListView) findViewById(R.id.listLLRSTransverseDuctility);
			listview5.setAdapter(selectedAdapter5);        		

			spinnerLlrsQualifier = (Spinner)  findViewById(R.id.spinnerLlrsQualifier);
			final Cursor llrsQualifierAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(llrsQualifierAttributeDictionary);
			ArrayList<DBRecord> llrsQualifierAttributesList = GemUtilities.cursorToArrayList(llrsQualifierAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,llrsQualifierAttributesList );
			CustomAdapter spinnerArrayAdapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, llrsQualifierAttributesList, 0);
		
			
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerLlrsQualifier.setAdapter(spinnerArrayAdapter);
			spinnerLlrsQualifier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerLlrsQualifier.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerLlrsQualifier.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(llrsQualifierAttributeKey, selected.getAttributeValue());
					completeThis();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			llrsQualifierAttributeDictionaryCursor.close();
			
						
			
			mDbHelper.close();			

			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					if (DEBUG_LOG) Log.d(TAG,"Description " + a.getItem(position).getAttributeDescription())	;
					surveyDataObject.putData(topLevelAttributeKeyLongitudinal, selectedAdapter2.getItem(position).getAttributeValue());					
					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					completeThis();		

				}
			});
					
			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter3.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					surveyDataObject.putData(secondLevelAttributeKeyLongitudinal, selectedAdapter3.getItem(position).getAttributeValue());					
					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					completeThis();							
				}
			});
			listview4.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter4.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					surveyDataObject.putData(topLevelAttributeKeyTransverse, selectedAdapter4.getItem(position).getAttributeValue());					
					

				}
			});
			listview5.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter5.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					surveyDataObject.putData(secondLevelAttributeKeyTransverse, selectedAdapter5.getItem(position).getAttributeValue());					
					completeThis();		

				}
			});
	
			
			boolean result = false;			
			result= selectedAdapter2.loadPreviousAtttributes(listview2, topLevelAttributeKeyLongitudinal,surveyDataObject.getSurveyDataValue(topLevelAttributeKeyLongitudinal));
			result= selectedAdapter3.loadPreviousAtttributes(listview3, secondLevelAttributeKeyLongitudinal,surveyDataObject.getSurveyDataValue(secondLevelAttributeKeyLongitudinal));
			result= selectedAdapter4.loadPreviousAtttributes(listview4, topLevelAttributeKeyTransverse,surveyDataObject.getSurveyDataValue(topLevelAttributeKeyTransverse));
			result= selectedAdapter5.loadPreviousAtttributes(listview5, secondLevelAttributeKeyTransverse,surveyDataObject.getSurveyDataValue(secondLevelAttributeKeyTransverse));
			
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerLlrsQualifier, llrsQualifierAttributesList , llrsQualifierAttributeKey,surveyDataObject.getSurveyDataValue(llrsQualifierAttributeKey));

			
		}//End of tab completed check
	}

	
	
	public void clearThis() {
		if (DEBUG_LOG) Log.d(TAG, "clearing stuff");
		//selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
	}


	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}

	@Override
	public void onBackPressed() {
		if (DEBUG_LOG) Log.d(TAG,"back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}



}

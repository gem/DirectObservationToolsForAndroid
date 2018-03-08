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


import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;

import java.util.ArrayList;
import org.globalquakemodel.org.idctdo.R;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;


public class Floor_Selection_Form extends Activity {

	public boolean DEBUG_LOG = false; 

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 5;

	private String topLevelAttributeDictionary = "DIC_FLOOR_MATERIAL";
	private String topLevelAttributeKey = "FLOOR_MAT";
	
	private String secondLevelAttributeDictionary = "DIC_FLOOR_TYPE";
	private String secondLevelAttributeKey = "FLOOR_TYPE";
		
	private String foundationSystemAttributeDictionary = "DIC_FOUNDATION_SYSTEM";
	private String foundationSystemAttributeKey = "FOUNDN_SYS";

	private String floorConnectionAttributeDictionary = "DIC_FLOOR_CONNECTION";
	private String floorConnectionAttributeKey = "FLOOR_CONN";
	

	public Spinner spinnerFoundationSystem;
	public Spinner spinnerFloorConnection;
	
	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;


	
	public ArrayList list;
	public ArrayList<DBRecord> secondLevelAttributesList;


	ListView listview;
	ListView listview2;		 

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floor_selectable_list);


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
			
			spinnerFoundationSystem = (Spinner)  findViewById(R.id.spinnerFoundationSystem);
			final Cursor foundationSystemAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(foundationSystemAttributeDictionary);
			ArrayList<DBRecord> foundationSystemAttributesList = GemUtilities.cursorToArrayList(foundationSystemAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,foundationSystemAttributesList );
			CustomAdapter spinnerArrayAdapter2 = new CustomAdapter(this, android.R.layout.simple_spinner_item, foundationSystemAttributesList , 0);
			spinnerArrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerFoundationSystem.setAdapter(spinnerArrayAdapter2);				
			
			spinnerFoundationSystem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					Log.d("IDCT","spinner selected: " + spinnerFoundationSystem.getSelectedItem().toString());
					Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerFoundationSystem.getSelectedItem();
					Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(foundationSystemAttributeKey, selected.getAttributeValue());		
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			foundationSystemAttributeDictionaryCursor.close();
			
			
			
			
			
			//Floor connection data loading
			spinnerFloorConnection = (Spinner)  findViewById(R.id.spinnerFloorConnection);
			final Cursor floorConnectionAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(floorConnectionAttributeDictionary);
			ArrayList<DBRecord> floorConnectionAttributesList = GemUtilities.cursorToArrayList(floorConnectionAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,floorConnectionAttributesList);
			CustomAdapter spinnerArrayAdapter3 = new CustomAdapter(this, android.R.layout.simple_spinner_item, floorConnectionAttributesList , 0);
			spinnerArrayAdapter3.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerFloorConnection.setAdapter(spinnerArrayAdapter3);				
			spinnerFloorConnection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					Log.d("IDCT","spinner selected: " + spinnerFloorConnection.getSelectedItem().toString());
					Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerFloorConnection.getSelectedItem();
					Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(floorConnectionAttributeKey, selected.getAttributeValue());		
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			floorConnectionAttributeDictionaryCursor.close();
			
			
			
			
			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());

			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(secondLevelAttributeDictionary);
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

			allAttributeTypesTopLevelCursor.close();
			allAttributeTypesSecondLevelCursor.close();			
			mDbHelper.close();

			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);

			listview = (ListView) findViewById(R.id.listExample);
			listview.setAdapter(selectedAdapter);      


			selectedAdapter2 = new SelectedAdapter(this,0,secondLevelAttributesList);    		
			selectedAdapter2.setNotifyOnChange(true);		
			listview2 = (ListView) findViewById(R.id.listExample2);
			listview2.setAdapter(selectedAdapter2);        

			listview2.setVisibility(View.INVISIBLE);
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
			relativeLayout.setVisibility(View.INVISIBLE);
			
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter.setSelectedPosition(position);
					selectedAdapter2.setSelectedPosition(-1);		
					
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());
					secondLevelAttributesList.clear();				
					
					mDbHelper.open();				
					//Cursor mCursor = mDbHelper.getAllMaterialTechnologies(selectedAdapter.getItem(position).getJson());
					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(secondLevelAttributeDictionary,selectedAdapter.getItem(position).getJson());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();	
						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));
						o1.setAttributeDescription(mCursor.getString(0));
						o1.setAttributeValue(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						secondLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}
											  				

					mCursor.close();
					mDbHelper.close();    
					
					listview2.setVisibility(View.VISIBLE);
					RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
					relativeLayout.setVisibility(View.VISIBLE);		
					
					selectedAdapter2.notifyDataSetChanged();       
					completeThis();
				}
			});        

			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					Log.d("IDCT","selected adapter pos: " + selectedAdapter2.getItem(position).getAttributeValue());
					
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());
					
			
				}
			});

			boolean result = false;	
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));
			result= selectedAdapter2.loadPreviousAtttributes(listview2, secondLevelAttributeKey,surveyDataObject.getSurveyDataValue(secondLevelAttributeKey));
			if (result)  {
				listview2.setVisibility(View.VISIBLE);
				findViewById(R.id.rel2).setVisibility(View.VISIBLE);
			}
			
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerFoundationSystem, foundationSystemAttributesList , foundationSystemAttributeKey,surveyDataObject.getSurveyDataValue(foundationSystemAttributeKey));
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerFloorConnection, floorConnectionAttributesList , foundationSystemAttributeKey,surveyDataObject.getSurveyDataValue(floorConnectionAttributeKey));
			
			surveyDataObject.lastEditedAttribute = "";
		}		//end of tab completed check
		
	}

	public void clearThis() {
		if (DEBUG_LOG) Log.d("IDCT", "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
	}

	@Override
	public void onBackPressed() {
		if (DEBUG_LOG) Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}

}

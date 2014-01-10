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


public class Roof_Selection_Form extends Activity {

	public boolean DEBUG_LOG = false; 

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 4;

	private String roofShapeAttributeDictionary = "DIC_ROOF_SHAPE";
	private String roofShapeAttributeKey = "ROOF_SHAPE";

	private String roofCoverMaterialAttributeDictionary = "DIC_ROOF_COVER_MATERIAL";
	private String roofCoverMaterialAttributeKey = "ROOFCOVMAT";


	private String topLevelAttributeDictionary = "DIC_ROOF_SYSTEM_MATERIAL";
	private String topLevelAttributeKey = "ROOFSYSMAT";

	private String secondLevelAttributeDictionary = "DIC_ROOF_SYSTEM_TYPE";
	private String secondLevelAttributeKey = "ROOFSYSTYP";

	private String roofConnectionAttributeDictionary = "DIC_ROOF_CONNECTION";
	private String roofConnectionAttributeKey = "ROOF_CONN";

	public Spinner spinnerRoofShape;
	public Spinner spinnerRoofCoverMaterial;
	public Spinner spinnerRoofConnection;


	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	public ArrayList<DBRecord> secondLevelAttributesList;


	ListView listview;
	ListView listview2;


	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roof_selectable_list);       
	}

	@Override
	public void onBackPressed() {
		if (DEBUG_LOG) Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
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
			spinnerRoofShape = (Spinner)  findViewById(R.id.spinnerRoofShape);
			final Cursor roofShapeAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(roofShapeAttributeDictionary);
			ArrayList<DBRecord> roofShapeAttributesList = GemUtilities.cursorToArrayList(roofShapeAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,roofShapeAttributesList );
			CustomAdapter spinnerArrayAdapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, roofShapeAttributesList , 0);
			spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerRoofShape.setAdapter(spinnerArrayAdapter);
			spinnerRoofShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerRoofShape.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);					
					DBRecord selected = (DBRecord) spinnerRoofShape.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(roofShapeAttributeKey, selected.getAttributeValue());		
				}
				public void onNothingSelected(AdapterView<?> parent) {
					if (DEBUG_LOG) Log.d("IDCT","NOTHING SELECTED");
					surveyDataObject.putData(roofShapeAttributeKey, "");				
				}
			});	
			roofShapeAttributeDictionaryCursor.close();

			spinnerRoofCoverMaterial = (Spinner)  findViewById(R.id.spinnerRoofCoverMaterial);
			final Cursor roofCoverMaterialAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(roofCoverMaterialAttributeDictionary);
			ArrayList<DBRecord> roofCoverMaterialAttributesList = GemUtilities.cursorToArrayList(roofCoverMaterialAttributeDictionaryCursor,true);
			//ArrayAdapter spinnerArrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,roofCoverMaterialAttributesList );
			CustomAdapter spinnerArrayAdapter2 = new CustomAdapter(this, android.R.layout.simple_spinner_item, roofCoverMaterialAttributesList, 0);
			spinnerArrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerRoofCoverMaterial.setAdapter(spinnerArrayAdapter2);			
			spinnerRoofCoverMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerRoofCoverMaterial.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerRoofCoverMaterial.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(roofCoverMaterialAttributeKey, selected.getAttributeValue());
					completeThis();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			roofCoverMaterialAttributeDictionaryCursor.close();





			spinnerRoofConnection = (Spinner)  findViewById(R.id.spinnerRoofConnection);
			final Cursor roofConnectionAttributeDictionaryCursor = mDbHelper.getAttributeValuesByDictionaryTable(roofConnectionAttributeDictionary);
			ArrayList<DBRecord> roofConnectionAttributesList = GemUtilities.cursorToArrayList(roofConnectionAttributeDictionaryCursor,true);			
			int hidingItemIndex = 0;
			//ArrayAdapter spinnerArrayAdapter4 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,roofConnectionAttributesList );			
			CustomAdapter spinnerArrayAdapter4 = new CustomAdapter(this, android.R.layout.simple_spinner_item, roofConnectionAttributesList , hidingItemIndex);			
			spinnerArrayAdapter4.setDropDownViewResource(R.layout.simple_spinner_item);
			spinnerRoofConnection.setAdapter(spinnerArrayAdapter4);
			spinnerRoofConnection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					//Object item = parent.getItemAtPosition(pos);

					if (DEBUG_LOG) Log.d("IDCT","spinner selected: " + spinnerRoofConnection.getSelectedItem().toString());
					if (DEBUG_LOG) Log.d("IDCT","spinner selected pos: " + pos);
					DBRecord selected = (DBRecord) spinnerRoofConnection.getSelectedItem();
					if (DEBUG_LOG) Log.d("IDCT","SELECTED: " + selected.getAttributeValue());
					surveyDataObject.putData(roofConnectionAttributeKey, selected.getAttributeValue());
					completeThis();

				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});	
			roofConnectionAttributeDictionaryCursor.close();








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
			RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.rel2);
			relativeLayout2.setVisibility(View.INVISIBLE);

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
					if (DEBUG_LOG) Log.d("IDCT", "Going to select by" + secondLevelAttributeKey + " and " + selectedAdapter.getItem(position).getJson());
					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(secondLevelAttributeDictionary,selectedAdapter.getItem(position).getJson());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						DBRecord o1 = new DBRecord();		
						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
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
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());
				}
			});

			//Resume any previous values if editing existing attribute 
			boolean result = false;	
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));
			result= selectedAdapter2.loadPreviousAtttributes(listview2, secondLevelAttributeKey,surveyDataObject.getSurveyDataValue(secondLevelAttributeKey));
			if (result)  {
				listview2.setVisibility(View.VISIBLE);
				findViewById(R.id.rel2).setVisibility(View.VISIBLE);
			}			

			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerRoofShape, roofShapeAttributesList, roofShapeAttributeKey,surveyDataObject.getSurveyDataValue(roofShapeAttributeKey));
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerRoofCoverMaterial, roofCoverMaterialAttributesList, roofCoverMaterialAttributeKey,surveyDataObject.getSurveyDataValue(roofCoverMaterialAttributeKey));
			result = GemUtilities.loadPreviousAtttributesSpinner(spinnerRoofConnection, roofConnectionAttributesList, roofConnectionAttributeKey,surveyDataObject.getSurveyDataValue(roofConnectionAttributeKey));

		}//End tab completed check		

		Log.d("IDCT","Setting last edited to blank");
		surveyDataObject.lastEditedAttribute = "";
	}//end of onResume

	public void clearThis() {
		if (DEBUG_LOG) Log.d("IDCT", "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
	}

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}


}

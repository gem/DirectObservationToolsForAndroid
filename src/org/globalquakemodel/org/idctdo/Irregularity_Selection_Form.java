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
import java.util.UUID;
import org.globalquakemodel.org.idctdo.R;

import android.app.Activity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;



public class Irregularity_Selection_Form extends Activity {
	public boolean DEBUG_LOG = false; 

	private String topLevelAttributeDictionary = "DIC_STRUCTURAL_IRREGULARITY";
	private String topLevelAttributeKey = "STR_IRREG";

	private String horiztonalIrregularityAttributeDictionary = "DIC_STRUCTURAL_HORIZ_IRREG";
	private String secondLevelAttributeKey = "STR_HZIR_P";
	private String secondLevelAttributeKeySecondary = "STR_HZIR_S";

	private String verticalIrregularityAttributeDictionary = "DIC_STRUCTURAL_VERT_IRREG";
	private String thirdLevelAttributeKey = "STR_VEIR_P";
	private String thirdLevelAttributeKeySecondary = "STR_VEIR_S";


	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 2;

	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;
	private SelectedAdapter selectedAdapter4;
	private SelectedAdapter selectedAdapter5;


	private ArrayList list;
	public ArrayList<DBRecord> secondLevelAttributesList;
	public ArrayList<DBRecord> secondLevelAttributesListPart2;
	public ArrayList<DBRecord> thirdLevelAttributesList;
	public ArrayList<DBRecord> thirdLevelAttributesListPart2;

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
		setContentView(R.layout.irregularity_selectable_list2);


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

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());

			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(horiztonalIrregularityAttributeDictionary,"IRIR");
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

			Cursor allAttributeTypesSecondLevelPart2Cursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(horiztonalIrregularityAttributeDictionary,"IRIR");
			secondLevelAttributesListPart2 = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelPart2Cursor);

			Cursor allAttributeTypesThirdLevelCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(verticalIrregularityAttributeDictionary,"IRIR");
			thirdLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);

			Cursor allAttributeTypesThirdLevelPart2Cursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(verticalIrregularityAttributeDictionary,"IRIR");
			thirdLevelAttributesListPart2 =  GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelPart2Cursor);

			allAttributeTypesTopLevelCursor.close();
			allAttributeTypesSecondLevelCursor.close();
			allAttributeTypesSecondLevelPart2Cursor.close();
			allAttributeTypesThirdLevelCursor.close();
			allAttributeTypesThirdLevelPart2Cursor.close();

			mDbHelper.close();

			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);
			listview = (ListView) findViewById(R.id.listStructuralIrregularity);
			listview.setAdapter(selectedAdapter);        

			selectedAdapter2 = new SelectedAdapter(this,0,secondLevelAttributesList);    		
			selectedAdapter2.setNotifyOnChange(true);		
			listview2 = (ListView) findViewById(R.id.listHorizontalIrregularityPrimary);
			listview2.setAdapter(selectedAdapter2);                      
			listview2.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
			relativeLayout.setVisibility(View.INVISIBLE);


			selectedAdapter3 = new SelectedAdapter(this,0,secondLevelAttributesListPart2);    		
			selectedAdapter3.setNotifyOnChange(true);		
			listview3 = (ListView) findViewById(R.id.listHorizontalIrregularitySecondary);
			listview3.setAdapter(selectedAdapter3);               
			listview3.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
			relativeLayout3.setVisibility(View.INVISIBLE);

			selectedAdapter4 = new SelectedAdapter(this,0,thirdLevelAttributesList);    		
			selectedAdapter4.setNotifyOnChange(true);		
			listview4 = (ListView) findViewById(R.id.listVerticalIrregularityPrimary);
			listview4.setAdapter(selectedAdapter4);                      
			listview4.setVisibility(View.VISIBLE);

			selectedAdapter5 = new SelectedAdapter(this,0,thirdLevelAttributesListPart2);    		
			selectedAdapter5.setNotifyOnChange(true);		
			listview5 = (ListView) findViewById(R.id.listVerticalIrregularitySecondary);
			listview5.setAdapter(selectedAdapter5);                      
			listview5.setVisibility(View.VISIBLE);

			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter.setSelectedPosition(position);
					selectedAdapter2.setSelectedPosition(-1);
					selectedAdapter3.setSelectedPosition(-1);
					selectedAdapter4.setSelectedPosition(-1);
					selectedAdapter5.setSelectedPosition(-1);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());

					DBRecord selectedItem = selectedAdapter.getItem(position);
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getJson());
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getCode());
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getAttributeDescription());

					completeThis();

					// Causing some fail having this inter-list dependency
					secondLevelAttributesList.clear();
					thirdLevelAttributesList.clear();
					secondLevelAttributesListPart2.clear();
					thirdLevelAttributesListPart2.clear();	
					mDbHelper.open();			
					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(horiztonalIrregularityAttributeDictionary, selectedAdapter.getItem(position).getAttributeValue());
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

					

					mCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(horiztonalIrregularityAttributeDictionary, selectedAdapter.getItem(position).getAttributeValue());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						DBRecord o1 = new DBRecord();		
						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						o1.setAttributeDescription(mCursor.getString(0));
						o1.setAttributeValue(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						secondLevelAttributesListPart2.add(o1);
						mCursor.moveToNext();
					}		     


					mCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(verticalIrregularityAttributeDictionary, selectedAdapter.getItem(position).getAttributeValue());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						DBRecord o1 = new DBRecord();		
						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						o1.setAttributeDescription(mCursor.getString(0));
						o1.setAttributeValue(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						thirdLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     					


					mCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(verticalIrregularityAttributeDictionary, selectedAdapter.getItem(position).getAttributeValue());
					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						DBRecord o1 = new DBRecord();
						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						o1.setAttributeDescription(mCursor.getString(0));
						o1.setAttributeValue(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						thirdLevelAttributesListPart2.add(o1);
						mCursor.moveToNext();
					}		     							
					mDbHelper.close();    		       
					
					if (mCursor.getCount() > 0) { 
						listview2.setVisibility(View.VISIBLE);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
						relativeLayout.setVisibility(View.VISIBLE);
						
						RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
						relativeLayout3.setVisibility(View.VISIBLE);
					} else {
						//listview2.setVisibility(View.INVISIBLE);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
						relativeLayout.setVisibility(View.INVISIBLE);
						
						RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
						relativeLayout3.setVisibility(View.INVISIBLE);
					}
					selectedAdapter2.notifyDataSetChanged();
					selectedAdapter3.notifyDataSetChanged();
					selectedAdapter4.notifyDataSetChanged();
					selectedAdapter5.notifyDataSetChanged();
					
					storeSurveyVariables();
				}
			});        


			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());
				}
			});

			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter3.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
		
					surveyDataObject.putData(secondLevelAttributeKeySecondary, selectedAdapter3.getItem(position).getAttributeValue());
				}
			});


			listview4.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter4.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					//Broken as the column name doesn't map, columen should be strhvi not strvi
					surveyDataObject.putData(thirdLevelAttributeKey, selectedAdapter4.getItem(position).getAttributeValue());
				}
			});

			listview5.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 	        
					selectedAdapter5.setSelectedPosition(position);
					SelectedAdapter a = (SelectedAdapter) arg0.getAdapter();
					surveyDataObject.lastEditedAttribute = a.getItem(position).getAttributeDescription();
					//Broken as the column name doesn't map, columen should be strhvi not strvi
					surveyDataObject.putData(thirdLevelAttributeKeySecondary, selectedAdapter5.getItem(position).getAttributeValue());
				}
			});

			boolean result = false;		
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));			
			result= selectedAdapter2.loadPreviousAtttributes(listview2, secondLevelAttributeKey,surveyDataObject.getSurveyDataValue(secondLevelAttributeKey));
			
			if (result)  {
				listview2.setVisibility(View.VISIBLE);
				findViewById(R.id.rel2).setVisibility(View.VISIBLE);
			}		
			result= selectedAdapter3.loadPreviousAtttributes(listview3, secondLevelAttributeKeySecondary,surveyDataObject.getSurveyDataValue(secondLevelAttributeKeySecondary));			
			result= selectedAdapter4.loadPreviousAtttributes(listview4, thirdLevelAttributeKey,surveyDataObject.getSurveyDataValue(thirdLevelAttributeKey));
			
			if (result)  {
				//listview5.setVisibility(View.VISIBLE);
				findViewById(R.id.rel3).setVisibility(View.VISIBLE);
			}
			result= selectedAdapter5.loadPreviousAtttributes(listview5, thirdLevelAttributeKeySecondary,surveyDataObject.getSurveyDataValue(thirdLevelAttributeKeySecondary));			
		}	//end of tab completed check	
		
		
		
		surveyDataObject.lastEditedAttribute = "";
	}

	public void clearThis() {
		if (DEBUG_LOG) Log.d("IDCT", "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
		selectedAdapter3.setSelectedPosition(-1);	

	}

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}
	
	public void storeSurveyVariables() {	
		MainTabActivity mainTab = (MainTabActivity)getParent(); 
		mainTab.saveSelectedAdapterData(topLevelAttributeKey, selectedAdapter);
		mainTab.saveSelectedAdapterData(secondLevelAttributeKey, selectedAdapter2);
		mainTab.saveSelectedAdapterData(secondLevelAttributeKeySecondary, selectedAdapter3);
		mainTab.saveSelectedAdapterData(thirdLevelAttributeKey, selectedAdapter4);
		mainTab.saveSelectedAdapterData(thirdLevelAttributeKeySecondary, selectedAdapter5);
	}

}

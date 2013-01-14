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
	public ArrayList<DBRecord> thirdLevelAttributesList;

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

			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(horiztonalIrregularityAttributeDictionary,"IR");
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

			Cursor allAttributeTypesThirdLevelCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(verticalIrregularityAttributeDictionary,"IR");
			thirdLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);

			allAttributeTypesTopLevelCursor.close();
			allAttributeTypesSecondLevelCursor.close();
			allAttributeTypesThirdLevelCursor.close();
			
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
			relativeLayout.setVisibility(View.VISIBLE);

			selectedAdapter3 = new SelectedAdapter(this,0,secondLevelAttributesList);    		
			selectedAdapter3.setNotifyOnChange(true);		
			listview3 = (ListView) findViewById(R.id.listHorizontalIrregularitySecondary);
			listview3.setAdapter(selectedAdapter3);               
			listview3.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
			relativeLayout3.setVisibility(View.VISIBLE);

			
			
			selectedAdapter4 = new SelectedAdapter(this,0,thirdLevelAttributesList);    		
			selectedAdapter4.setNotifyOnChange(true);		
			listview4 = (ListView) findViewById(R.id.listVerticalIrregularityPrimary);
			listview4.setAdapter(selectedAdapter4);                      
			listview4.setVisibility(View.VISIBLE);

			selectedAdapter5 = new SelectedAdapter(this,0,thirdLevelAttributesList);    		
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
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());
					
					
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();				

					DBRecord selectedItem = selectedAdapter.getItem(position);
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getJson());
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getCode());
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED ITEM: " + selectedItem.getAttributeDescription());
					//GEMSurveyObject g = (GEMSurveyObject)getApplication();
					//g.putData(key, value);

					completeThis();

					/* Causing some fail having this inter-list dependency
					secondLevelAttributesList.clear();
					thirdLevelAttributesList.clear();	

					mDbHelper.open();			

					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(horiztonalIrregularityAttributeDictionary, selectedAdapter.getItem(position).getJson());

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
					mDbHelper.close();    		          



					if (mCursor.getCount() > 0) { 
						listview2.setVisibility(View.VISIBLE);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
						relativeLayout.setVisibility(View.VISIBLE);
					}


					selectedAdapter2.notifyDataSetChanged();
					selectedAdapter3.notifyDataSetChanged(); 
					*/  
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

			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter3.setSelectedPosition(position);
					
					//Broken as the column name doesn't map, columen should be strhvi not strvi
					surveyDataObject.putData(secondLevelAttributeKeySecondary, selectedAdapter3.getItem(position).getAttributeValue());
					
					//Toast.makeText(getApplicationContext(), "LV3 click: " + selectedAdapter3.getItem(position).getOrderName() + " " + selectedAdapter3.getItem(position).getOrderStatus() + " " +selectedAdapter3.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

				}
			});

			
			listview4.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter4.setSelectedPosition(position);
					
					//Broken as the column name doesn't map, columen should be strhvi not strvi
					surveyDataObject.putData(thirdLevelAttributeKey, selectedAdapter4.getItem(position).getAttributeValue());
				}
			});
			
			listview5.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter5.setSelectedPosition(position);
					
					
					//Broken as the column name doesn't map, columen should be strhvi not strvi
					surveyDataObject.putData(thirdLevelAttributeKeySecondary, selectedAdapter5.getItem(position).getAttributeValue());
				}
			});
		}		
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

}

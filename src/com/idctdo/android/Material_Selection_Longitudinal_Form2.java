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
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class Material_Selection_Longitudinal_Form2 extends Activity {

	public boolean DEBUG_LOG = false; 
	private static final String TAG = "IDCT";

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 0;


	private String topLevelAttributeType = "DIC_MATERIAL_TYPE";
	private String secondLevelAttributeType = "DIC_MATERIAL_TECHNOLOGY";
	private String thirdLevelAttributeType = "DIC_MASONARY_MORTAR_TYPE";
	private String fourthLevelAttributeType = "DIC_MASONRY_REINFORCEMENT";
	private String fifthLevelAttributeType = "DIC_STEEL_CONNECTION_TYPE";

	private String topLevelAttributeKey;
	private String secondLevelAttributeKey;
	private String thirdLevelAttributeKey;
	private String fourthLevelAttributeKey;
	private String fifthLevelAttributeKey;

	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;
	private SelectedAdapter selectedAdapter4;
	private SelectedAdapter selectedAdapter5;


	//private ArrayList list;
	public ArrayList<DBRecord> secondLevelAttributesList;
	public ArrayList<DBRecord> thirdLevelAttributesList;
	public ArrayList<DBRecord> fourthLevelAttributesList;
	public ArrayList<DBRecord> fifthLevelAttributesList;


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
		setContentView(R.layout.material_selectable_list2);

		Resources res = getResources();
		String[] attribKeys = res.getStringArray(R.array.formAttributeKeys0a);
		topLevelAttributeKey = attribKeys[0];
		secondLevelAttributeKey = attribKeys[1];
		thirdLevelAttributeKey =  attribKeys[2];
		fourthLevelAttributeKey =  attribKeys[3];
		fifthLevelAttributeKey =  attribKeys[4];



		tabActivity = (TabActivity) getParent();
		tabHost = tabActivity.getTabHost();

	}

	@Override
	public void onBackPressed() {
		if (DEBUG_LOG) Log.d("IDCT","back button pressed");
		SecondTabsActivity a = (SecondTabsActivity)getParent();
		a.backButtonPressed();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
		Log.d("IDCT", "Tab Pausing .....");
	}



	@Override
	protected void onResume() {
		super.onResume();

		SecondTabsActivity a = (SecondTabsActivity)getParent();		
		surveyDataObject = (GEMSurveyObject)getApplication();	

		//if (a.isSecondaryTabCompleted()) {
		//	if (DEBUG_LOG) Log.d("JFR","Tab is complete");
		//} else {

			if (DEBUG_LOG) Log.d("JFR","creating material");
			mDbHelper = new GemDbAdapter(getBaseContext());        

			mDbHelper.createDatabase();      
			mDbHelper.open();

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeType);     
			ArrayList<DBRecord> firstLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + firstLevelAttributesList.toString());

			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(secondLevelAttributeType);
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

			//Cursor allAttributeTypesThirdLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(thirdLevelAttributeType);
			Cursor allAttributeTypesThirdLevelCursor = mDbHelper.getAttributeValuesByDictionaryTableUsingRule(thirdLevelAttributeType,"MR");
			thirdLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);

			Cursor allAttributeTypesFourthLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(fourthLevelAttributeType);
			fourthLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesFourthLevelCursor);

			Cursor allAttributeTypesFifthLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(fifthLevelAttributeType);
			fifthLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesFifthLevelCursor);

			allAttributeTypesTopLevelCursor.close();
			allAttributeTypesSecondLevelCursor.close();
			allAttributeTypesThirdLevelCursor.close();
			allAttributeTypesFourthLevelCursor.close();
			allAttributeTypesFifthLevelCursor.close();

			mDbHelper.close();

			selectedAdapter = new SelectedAdapter(this,0,firstLevelAttributesList);
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

			selectedAdapter3 = new SelectedAdapter(this,0,thirdLevelAttributesList);    		
			selectedAdapter3.setNotifyOnChange(true);		
			listview3 = (ListView) findViewById(R.id.listExample3);
			listview3.setAdapter(selectedAdapter3);               
			listview3.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
			relativeLayout3.setVisibility(View.INVISIBLE);

			selectedAdapter4 = new SelectedAdapter(this,0,fourthLevelAttributesList);    		
			selectedAdapter4.setNotifyOnChange(true);		
			listview4 = (ListView) findViewById(R.id.listExample4);
			listview4.setAdapter(selectedAdapter4);               
			listview4.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.rel4);
			relativeLayout4.setVisibility(View.INVISIBLE);


			selectedAdapter5 = new SelectedAdapter(this,0,fifthLevelAttributesList);    		
			selectedAdapter5.setNotifyOnChange(true);		
			listview5 = (ListView) findViewById(R.id.listExample5);
			listview5.setAdapter(selectedAdapter5);               
			listview5.setVisibility(View.VISIBLE);
			RelativeLayout relativeLayout5 = (RelativeLayout) findViewById(R.id.rel5);
			relativeLayout5.setVisibility(View.INVISIBLE);

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
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();				

					secondLevelAttributesList.clear();
					thirdLevelAttributesList.clear();
					fourthLevelAttributesList.clear();	
					fifthLevelAttributesList.clear();	

					mDbHelper.open();

					if (DEBUG_LOG) Log.d("IDCT", "MATERIAL TYPE SELECING BY " + secondLevelAttributeType + " and " + selectedAdapter.getItem(position).getJson());				
					if (DEBUG_LOG) Log.d("IDCT", "SELECTED DB RECORD QUADRUPLET: "  + selectedAdapter.getItem(position).toString());
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());

					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(secondLevelAttributeType,selectedAdapter.getItem(position).getJson());
					if (mCursor != null) {
						mCursor.moveToFirst();
						while(!mCursor.isAfterLast()) {
							DBRecord o1 = new DBRecord();		
							o1.setAttributeDescription(mCursor.getString(0));
							o1.setAttributeValue(mCursor.getString(1));
							o1.setJson(mCursor.getString(2));
							secondLevelAttributesList.add(o1);
							mCursor.moveToNext();
						}			
					}
					mCursor.close();
					mDbHelper.close();    		          


					listview2.setVisibility(View.VISIBLE);
					RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
					relativeLayout.setVisibility(View.VISIBLE);

					
					completeThisSecondaryTab();							

					selectedAdapter2.notifyDataSetChanged();
					selectedAdapter3.notifyDataSetChanged();
					selectedAdapter4.notifyDataSetChanged();   

					storeSurveyVariables();
				}
			});        


			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					selectedAdapter3.setSelectedPosition(-1);			
					selectedAdapter4.setSelectedPosition(-1);			
					selectedAdapter5.setSelectedPosition(-1);	
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());

					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

					thirdLevelAttributesList.clear();
					fourthLevelAttributesList.clear();	
					fifthLevelAttributesList.clear();	

					mDbHelper.open();
					if (DEBUG_LOG) Log.d("IDCT", "MATERIAL TECH ON CLICK" + thirdLevelAttributeType + " and " + selectedAdapter2.getItem(position).getJson());

					Cursor mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(thirdLevelAttributeType,selectedAdapter2.getItem(position).getJson());
					if (mCursor != null) {
						if (mCursor.getCount() > 0) {						
							mCursor.moveToFirst();
							while(!mCursor.isAfterLast()) {
								DBRecord o1 = new DBRecord();		

								if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
								//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

								o1.setAttributeDescription(mCursor.getString(0));
								o1.setAttributeValue(mCursor.getString(1));
								o1.setJson(mCursor.getString(2));
								thirdLevelAttributesList.add(o1);
								mCursor.moveToNext();
							}		     
							RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
							relativeLayout3.setVisibility(View.VISIBLE);
							listview3.setVisibility(View.VISIBLE);
						}
					}
					mCursor.close();
					mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(fourthLevelAttributeType,selectedAdapter2.getItem(position).getJson());
					if (mCursor != null) {
						if (mCursor.getCount() > 0) {		
							mCursor.moveToFirst();
							while(!mCursor.isAfterLast()) {
								//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
								DBRecord o1 = new DBRecord();		
								if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
								//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

								o1.setAttributeDescription(mCursor.getString(0));
								o1.setAttributeValue(mCursor.getString(1));
								o1.setJson(mCursor.getString(2));
								fourthLevelAttributesList.add(o1);
								mCursor.moveToNext();
							}		

							listview4.setVisibility(View.VISIBLE);
							RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.rel4);
							relativeLayout4.setVisibility(View.VISIBLE);
						}

					}
					mCursor.close();
					mCursor = mDbHelper.getAttributeValuesByDictionaryTableAndScope(fifthLevelAttributeType,selectedAdapter2.getItem(position).getJson());
					if (mCursor != null) {
						if (mCursor.getCount() > 0) {	
							mCursor.moveToFirst();
							while(!mCursor.isAfterLast()) {
								//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
								DBRecord o1 = new DBRecord();		
								if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
								//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));
								o1.setAttributeDescription(mCursor.getString(0));
								o1.setAttributeValue(mCursor.getString(1));
								o1.setJson(mCursor.getString(2));
								fifthLevelAttributesList.add(o1);
								mCursor.moveToNext();
							}		 

							RelativeLayout relativeLayout5 = (RelativeLayout) findViewById(R.id.rel5);
							relativeLayout5.setVisibility(View.VISIBLE);
							listview5.setVisibility(View.VISIBLE);
						}

					}
					mCursor.close();
					mDbHelper.close();    

					updateListViewHeights(2);					

					selectedAdapter3.notifyDataSetChanged();
					selectedAdapter4.notifyDataSetChanged();
					selectedAdapter5.notifyDataSetChanged();						

					storeSurveyVariables();
				}
			});

			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter3.setSelectedPosition(position);						
					surveyDataObject.putData(thirdLevelAttributeKey, selectedAdapter3.getItem(position).getAttributeValue());
					storeSurveyVariables();
				}
			});


			listview4.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter4.setSelectedPosition(position);			
					surveyDataObject.putData(fourthLevelAttributeKey, selectedAdapter4.getItem(position).getAttributeValue());
					storeSurveyVariables();
				}
			}); 


			listview5.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter5.setSelectedPosition(position);				
					//Toast.makeText(getApplicationContext(), "LV3 click: " + selectedAdapter3.getItem(position).getOrderName() + " " + selectedAdapter3.getItem(position).getOrderStatus() + " " +selectedAdapter3.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					surveyDataObject.putData(fifthLevelAttributeKey, selectedAdapter5.getItem(position).getAttributeValue());

					storeSurveyVariables();
				}
			});            



			boolean result = false;			
			result= selectedAdapter.loadPreviousAtttributes(listview, topLevelAttributeKey,surveyDataObject.getSurveyDataValue(topLevelAttributeKey));
			if (result)  {
				listview.setVisibility(View.VISIBLE);
				findViewById(R.id.rel1).setVisibility(View.VISIBLE);
				//completeThisSecondaryTab();
			}
			result= selectedAdapter2.loadPreviousAtttributes(listview2, secondLevelAttributeKey,surveyDataObject.getSurveyDataValue(secondLevelAttributeKey));
			if (result)  {
				listview2.setVisibility(View.VISIBLE);
				findViewById(R.id.rel2).setVisibility(View.VISIBLE);
			}
			result= selectedAdapter3.loadPreviousAtttributes(listview3, thirdLevelAttributeKey,surveyDataObject.getSurveyDataValue(thirdLevelAttributeKey));
			if (result)  {
				listview3.setVisibility(View.VISIBLE);
				findViewById(R.id.rel3).setVisibility(View.VISIBLE);
			}	

			result= selectedAdapter4.loadPreviousAtttributes(listview4, fourthLevelAttributeKey,surveyDataObject.getSurveyDataValue(fourthLevelAttributeKey));
			if (result)  {
				listview4.setVisibility(View.VISIBLE);
				findViewById(R.id.rel4).setVisibility(View.VISIBLE);
			}	

			result= selectedAdapter5.loadPreviousAtttributes(listview5, fifthLevelAttributeKey,surveyDataObject.getSurveyDataValue(fifthLevelAttributeKey));
			if (result)  {
				listview5.setVisibility(View.VISIBLE);
				findViewById(R.id.rel5).setVisibility(View.VISIBLE);
			}	
		//}//End of tab completed check



		//updateListViewHeights(1);




	}

	public void updateListViewHeights(int layoutCode) {		
		int measuredWidth = 0;  
		int measuredHeight = 0;  
		Point size = new Point();
		WindowManager w = getWindowManager();		
		Display d = w.getDefaultDisplay(); 
		measuredWidth = d.getWidth(); 
		measuredHeight = d.getHeight(); 		  
		if (DEBUG_LOG) Log.d("IDCT","SCREEN DIMENSIONS: measuredWidth "+  measuredWidth  + " MeasuredHeight: " + measuredHeight);

		int h1 = (int)(measuredHeight * 0.2);
		int h2  = (int)(measuredHeight * 0.18);
		int h3 = (int)(measuredHeight * 0.13);
		int h4 = (int)(measuredHeight * 0.13);
		int h5 = (int)(measuredHeight * 0.15);

		/*
		if (layoutCode == 1) {
			h1 = (int)(measuredHeight * 0.3);
			h2  = (int)(measuredHeight * 0.3);
			h3 = (int)(measuredHeight * 0.1);
			h4 = (int)(measuredHeight * 0.1);
			h5 = (int)(measuredHeight * 0.1);	
		} else if (layoutCode == 2){
			h1 = (int)(measuredHeight * 0.18);
			h2  = (int)(measuredHeight * 0.18);
			h3 = (int)(measuredHeight * 0.2);
			h4 = (int)(measuredHeight * 0.2);
			h5 = (int)(measuredHeight * 0.01);	
		}
		 */


		LayoutParams lp = listview.getLayoutParams();
		lp.height = h1;	       
		listview.setLayoutParams(lp);
		listview.requestLayout();

		LayoutParams lp2 = listview2.getLayoutParams();
		lp2.height = h2;	       
		listview2.setLayoutParams(lp2);
		listview2.requestLayout();

		LayoutParams lp3 = listview3.getLayoutParams();
		lp3.height = h3;	       
		listview3.setLayoutParams(lp3);
		listview3.requestLayout();

		LayoutParams lp4 = listview4.getLayoutParams();
		lp4.height = h4;	       
		listview4.setLayoutParams(lp4);
		listview4.requestLayout();

		LayoutParams lp5 = listview5.getLayoutParams();
		lp5.height = h5;	       
		listview5.setLayoutParams(lp5);
		listview5.requestLayout();   

	}



	public void clearThis() {
		if (DEBUG_LOG) Log.d("IDCT", "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
		selectedAdapter3.setSelectedPosition(-1);
		selectedAdapter4.setSelectedPosition(-1);
	}

	public void completeThisSecondaryTab() {		
		SecondTabsActivity a = (SecondTabsActivity)getParent();
		a.completeSecondaryTab();		 
	}


	public void storeSurveyVariables() {
		SecondTabsActivity a = (SecondTabsActivity)getParent();		
		MainTabActivity mainTab =a.getMainTab(); 
		mainTab.saveSelectedAdapterData(topLevelAttributeKey, selectedAdapter);
		mainTab.saveSelectedAdapterData(secondLevelAttributeKey, selectedAdapter2);
		mainTab.saveSelectedAdapterData(thirdLevelAttributeKey, selectedAdapter3);
		mainTab.saveSelectedAdapterData(fourthLevelAttributeKey, selectedAdapter4);
	}


}

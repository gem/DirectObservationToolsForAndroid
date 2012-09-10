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


public class Floor_Selection_Form extends EQForm {

	public boolean DEBUG_LOG = false; 


	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 3;

	private String topLevelAttributeType = "FMAT";
	private String secondLevelAttributeType = "FTYPE";


	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;

	private ArrayList list;
	public ArrayList<DBRecord> secondLevelAttributesList;


	ListView listview;
	ListView listview2;		 

	public GemDbAdapter mDbHelper;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floor_selectable_list);


	}

	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity a = (MainTabActivity)getParent();
		if (a.isTabCompleted(tabIndex)) {

		} else {
			mDbHelper = new GemDbAdapter(getBaseContext());        

			mDbHelper.createDatabase();      
			mDbHelper.open();

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByType(topLevelAttributeType);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());


			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,"X");
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

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

					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

					secondLevelAttributesList.clear();					

					mDbHelper.open();				

					//Cursor mCursor = mDbHelper.getAllMaterialTechnologies(selectedAdapter.getItem(position).getJson());

					Cursor mCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,selectedAdapter.getItem(position).getJson());

					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();		

						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setOrderName(mCursor.getString(0));
						o1.setOrderStatus(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						secondLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}

					mDbHelper.insertTestData();


					mDbHelper.close();    		  				

					if (mCursor.getCount() > 0) { 
						listview2.setVisibility(View.VISIBLE);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
						relativeLayout.setVisibility(View.VISIBLE);
					}
					selectedAdapter2.notifyDataSetChanged();        

					completeThis();


				}
			});        

			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);		

					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();


				}
			});

		}		
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


	/*
	// Move selected item "up" in the ViewList.
	private void moveUp(){
    	int selectedPos = selectedAdapter.getSelectedPosition();
    	if (selectedPos > 0 ){
    		String str = (String) list.remove(selectedPos);
    		list.add(selectedPos-1, str);
    		// set selected position in the adapter
    		selectedAdapter.setSelectedPosition(selectedPos-1);
    	}
	}

	// Move selected item "down" in the ViewList.
	private void moveDown(){
    	int selectedPos = selectedAdapter.getSelectedPosition();
    	if (selectedPos < list.size()-1 ){
    		String str = (String) list.remove(selectedPos);
    		list.add(selectedPos+1, str);
    		// set selected position in the adapter
    		selectedAdapter.setSelectedPosition(selectedPos+1);
    	}
	}
	 */

}
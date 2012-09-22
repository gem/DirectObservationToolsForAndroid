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


public class Material_Selection_Form extends EQForm {
	
	public boolean DEBUG_LOG = true; 

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 0;


	private String topLevelAttributeType = "MTYPE";
	private String secondLevelAttributeType = "MTECH";
	private String thirdLevelAttributeType = "MORT";
	private String fourthLevelAttributeType = "MREIN";
	private String fifthLevelAttributeType = "SCONN";


	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;
	private SelectedAdapter selectedAdapter4;
	private SelectedAdapter selectedAdapter5;

	private ArrayList list;
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



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_selectable_list);


		tabActivity = (TabActivity) getParent();
		tabHost = tabActivity.getTabHost();


		

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
		if (a.isTabCompleted(tabIndex)) {

		} else {
			//clearThis();


			if (DEBUG_LOG) Log.d("JFR","creating material");
			mDbHelper = new GemDbAdapter(getBaseContext());        

			mDbHelper.createDatabase();      
			mDbHelper.open();



			Cursor testdata = mDbHelper.getTestData();
			if (DEBUG_LOG) Log.d("IDCT","getTestData-GetColumnName " + DatabaseUtils.dumpCursorToString(testdata));


			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByType(topLevelAttributeType);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			if (DEBUG_LOG) Log.d("IDCT","TYPES: " + topLevelAttributesList.toString());


			Cursor allAttributeTypesSecondLevelCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,"X");
			secondLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesSecondLevelCursor);

			Cursor allAttributeTypesThirdLevelCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,"X");
			thirdLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);

			Cursor allAttributeTypesFourthLevelCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,"X");
			fourthLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);

			Cursor allAttributeTypesFifthLevelCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,"X");
			fifthLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesThirdLevelCursor);
			
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

					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();				

					secondLevelAttributesList.clear();
					thirdLevelAttributesList.clear();
					fourthLevelAttributesList.clear();	
					fifthLevelAttributesList.clear();	

					mDbHelper.open();

					if (DEBUG_LOG) Log.d("IDCT", "MATERIAL TYPE SELECING BY " + secondLevelAttributeType + " and " + selectedAdapter.getItem(position).getJson());

					Cursor mCursor = mDbHelper.getAttributeValuesByTypeAndScope(secondLevelAttributeType,selectedAdapter.getItem(position).getJson());

					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();		

						//if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setOrderName(mCursor.getString(0));
						o1.setOrderStatus(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						secondLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     
					mDbHelper.close();    		          


					listview2.setVisibility(View.VISIBLE);
					RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel2);
					relativeLayout.setVisibility(View.VISIBLE);

					saveGlobals();
					
					completeThis();

					selectedAdapter2.notifyDataSetChanged();
					selectedAdapter3.notifyDataSetChanged();
					selectedAdapter4.notifyDataSetChanged();   
				}
			});        


			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);

					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

					thirdLevelAttributesList.clear();
					fourthLevelAttributesList.clear();	
					fifthLevelAttributesList.clear();	

					mDbHelper.open();			


					if (DEBUG_LOG) Log.d("IDCT", "MATERIAL TECH ON CLICK" + thirdLevelAttributeType + " and " + selectedAdapter2.getItem(position).getJson());

					Cursor mCursor = mDbHelper.getAttributeValuesByTypeAndScope(thirdLevelAttributeType,selectedAdapter2.getItem(position).getJson());

					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();		

						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setOrderName(mCursor.getString(0));
						o1.setOrderStatus(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						thirdLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     

					mCursor = mDbHelper.getAttributeValuesByTypeAndScope(fourthLevelAttributeType,selectedAdapter2.getItem(position).getJson());

					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();		

						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setOrderName(mCursor.getString(0));
						o1.setOrderStatus(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						fourthLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     


					mCursor = mDbHelper.getAttributeValuesByTypeAndScope(fifthLevelAttributeType,selectedAdapter2.getItem(position).getJson());

					mCursor.moveToFirst();
					while(!mCursor.isAfterLast()) {
						//mArrayList.add(mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));

						DBRecord o1 = new DBRecord();		

						if (DEBUG_LOG) Log.d("IDCT", "CURSOR TO ARRAY LIST" + mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1))));
						//String mTitleRaw = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(1)));

						o1.setOrderName(mCursor.getString(0));
						o1.setOrderStatus(mCursor.getString(1));
						o1.setJson(mCursor.getString(2));
						fifthLevelAttributesList.add(o1);
						mCursor.moveToNext();
					}		     

					mDbHelper.close();    
					
					listview3.setVisibility(View.VISIBLE);
					listview4.setVisibility(View.VISIBLE);
					listview5.setVisibility(View.VISIBLE);
					
					RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rel3);
					relativeLayout3.setVisibility(View.VISIBLE);
					RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.rel4);
					relativeLayout4.setVisibility(View.VISIBLE);
					RelativeLayout relativeLayout5 = (RelativeLayout) findViewById(R.id.rel5);
					relativeLayout5.setVisibility(View.VISIBLE);


					selectedAdapter3.notifyDataSetChanged();
					selectedAdapter4.notifyDataSetChanged();
					selectedAdapter5.notifyDataSetChanged();		

				}
			});

			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter3.setSelectedPosition(position);				
					//Toast.makeText(getApplicationContext(), "LV3 click: " + selectedAdapter3.getItem(position).getOrderName() + " " + selectedAdapter3.getItem(position).getOrderStatus() + " " +selectedAdapter3.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

				}
			});


			listview4.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter4.setSelectedPosition(position);				
					//Toast.makeText(getApplicationContext(), "LV3 click: " + selectedAdapter3.getItem(position).getOrderName() + " " + selectedAdapter3.getItem(position).getOrderStatus() + " " +selectedAdapter3.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

				}
			}); 
			
			listview5.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter5.setSelectedPosition(position);				
					//Toast.makeText(getApplicationContext(), "LV3 click: " + selectedAdapter3.getItem(position).getOrderName() + " " + selectedAdapter3.getItem(position).getOrderStatus() + " " +selectedAdapter3.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

				}
			});            
		}		
	}

	public void clearThis() {
		if (DEBUG_LOG) Log.d("IDCT", "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
		selectedAdapter2.setSelectedPosition(-1);
		selectedAdapter3.setSelectedPosition(-1);
		selectedAdapter4.setSelectedPosition(-1);
	}

	public void completeThis() {


		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);

		


	}
	

	public void saveGlobals(String key, String value) {
		GEMSurveyObject g = (GEMSurveyObject)getApplication();
		g.putData(key, value)
		//Log.d(TAG,"GLOBAL VARS " + g.getLon()+ " lat: " + g.getLat());
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
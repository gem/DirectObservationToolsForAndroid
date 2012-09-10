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
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class LLRS_Selection_FORM extends EQForm {
	
	private static final String TAG = "IDCT";
	public boolean DEBUG_LOG = false; 
	
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 1;
	

	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;

	private ArrayList list;
	public ArrayList<DBRecord> lLrsd;



	ListView listview;
	ListView listview2;


	public GemDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.llrs_selectable_list);
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

			Cursor testdata = mDbHelper.getTestData();

			if (DEBUG_LOG) Log.d(TAG,"getTestData-GetColumnName " + DatabaseUtils.dumpCursorToString(testdata));


			mDbHelper.insertTestData();

			if (DEBUG_LOG) Log.d(TAG,"insertTestData ");

			Cursor testdata2 = mDbHelper.getTestData();

			if (DEBUG_LOG) Log.d(TAG,"GETTESTDATA AGAIN" + DatabaseUtils.dumpCursorToString(testdata2));

			UUID uid = UUID.fromString("08991839-dff5-4d24-b39a-9303d1f34dae");
			Cursor ojbByUid= mDbHelper.getObjectByUid(uid);
			if (DEBUG_LOG) Log.d(TAG,"GETTESTDATA ojbByUid" + DatabaseUtils.dumpCursorToString(ojbByUid));


			Cursor allLLRSCursor = mDbHelper.getAllLLRS();        
			ArrayList<DBRecord> lLrs = GemUtilities.cursorToArrayList(allLLRSCursor);        
			if (DEBUG_LOG) Log.d(TAG,"lLrs: " + lLrs.toString());


			Cursor allLLRSDCursor = mDbHelper.getAllLLRSD();
			lLrsd = GemUtilities.cursorToArrayList(allLLRSDCursor);



			mDbHelper.close();

			
			selectedAdapter = new SelectedAdapter(this,0,lLrs);
			selectedAdapter.setNotifyOnChange(true);

			listview = (ListView) findViewById(R.id.listExample);
			listview.setAdapter(selectedAdapter);        


			selectedAdapter2 = new SelectedAdapter(this,0,lLrsd);    		
			selectedAdapter2.setNotifyOnChange(true);		
			listview2 = (ListView) findViewById(R.id.listExample2);
			listview2.setAdapter(selectedAdapter2);        


			//listview2.setVisibility(View.INVISIBLE);



			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter.setSelectedPosition(position);


					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

				}
			});        


			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);

					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();

					completeThis();		
					
				}
			});

			
		}		
    }
	
	public void clearThis() {
		if (DEBUG_LOG) Log.d(TAG, "clearing stuff");
		selectedAdapter.setSelectedPosition(-1);
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
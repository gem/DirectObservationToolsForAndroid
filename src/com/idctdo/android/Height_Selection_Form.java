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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;




public class Height_Selection_Form extends EQForm {
	public boolean DEBUG_LOG = false; 
	
	private String heightTopLevelAttributeDictionary = "DIC_HEIGHT_QUALIFIER";
	private String topLevelAttributeKey = "STORY_AG_Q";
	
	private String heightTopLevelAttributeDictionary2 = "DIC_HEIGHT_QUALIFIER";
	private String topLevelAttributeKey2 = "STORY_AG_Q";	
	
	private String heightTopLevelAttributeDictionary3 = "DIC_HEIGHT_QUALIFIER";
	private String topLevelAttributeKey3 = "STORY_AG_Q";	
	
	
	/*	
	private String topLevelAttributeType = "DATE";
	private String heightLevelAttributeType = "HEIGHT";
*/
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 6;
	
	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	private SelectedAdapter selectedAdapter3;
	
	private ArrayList list;
	private ArrayList list2;
	private ArrayList list3;
	
	ListView listview;
	ListView listview2;
	ListView listview3;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public EditText date1;
	public EditText date2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.height);
	
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

			Cursor heightAllAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(heightTopLevelAttributeDictionary);     
			ArrayList<DBRecord> heightTopLevelAttributesList = GemUtilities.cursorToArrayList(heightAllAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + heightTopLevelAttributesList.toString());

			Cursor allHeightAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(heightTopLevelAttributeDictionary2);     
			ArrayList<DBRecord> heightLevelAttributesList = GemUtilities.cursorToArrayList(allHeightAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + heightLevelAttributesList.toString());
									
			mDbHelper.close();
			
			selectedAdapter = new SelectedAdapter(this,0,heightTopLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);
			listview = (ListView) findViewById(R.id.listAboveGroundQualifier);
			listview.setAdapter(selectedAdapter);        

			selectedAdapter2 = new SelectedAdapter(this,0,heightLevelAttributesList);
			selectedAdapter2.setNotifyOnChange(true);
			listview2 = (ListView) findViewById(R.id.listBelowGroundQualifier);
			listview2.setAdapter(selectedAdapter2);        

			selectedAdapter3 = new SelectedAdapter(this,0,heightLevelAttributesList);
			selectedAdapter3.setNotifyOnChange(true);
			listview3 = (ListView) findViewById(R.id.listAboveGradeQualifier);
			listview3.setAdapter(selectedAdapter3);        

			
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					if (DEBUG_LOG) Log.d("IDCT","Selected item");
					selectedAdapter.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					
					if (DEBUG_LOG) Log.d("IDCT","DATE SELECT: " + selectedAdapter.getItem(position).toString());
					if (DEBUG_LOG) Log.d("IDCT","DATE Code: " + selectedAdapter.getItem(position).getAttributeValue());			
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());					
				}
			});    
			
			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter2.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();				
					completeThis();

				}
			});
			
			
			listview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter3.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();				
					completeThis();

				}
			});
		}
	}	

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}
}
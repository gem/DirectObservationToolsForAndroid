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




public class Structure_Selection_Form extends Activity {
	public boolean DEBUG_LOG = false; 
	
	private String topLevelAttributeType = "DATE";
	private String heightLevelAttributeType = "HEIGHT";

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 6;

	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	
	private ArrayList list;
	private ArrayList list2;
	
	ListView listview;
	ListView listview2;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public EditText date1;
	public EditText date2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.structure);
		
		date1 = (EditText)findViewById(R.id.editTextDateVal1);
		date2 = (EditText)findViewById(R.id.editTextDateVal2);
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

			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getQualifierValuesByQualifierType(topLevelAttributeType);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + topLevelAttributesList.toString());

			Cursor allHeightAttributeTypesTopLevelCursor = mDbHelper.getQualifierValuesByQualifierType2(heightLevelAttributeType);     
			ArrayList<DBRecord> heightLevelAttributesList = GemUtilities.cursorToArrayList(allHeightAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + heightLevelAttributesList.toString());
			
					
			allAttributeTypesTopLevelCursor.close();
			allHeightAttributeTypesTopLevelCursor.close();
			mDbHelper.close();

			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);

			listview = (ListView) findViewById(R.id.listExample);
			listview.setAdapter(selectedAdapter);        


			selectedAdapter2 = new SelectedAdapter(this,0,heightLevelAttributesList);
			selectedAdapter2.setNotifyOnChange(true);

			listview2 = (ListView) findViewById(R.id.listExample2);
			listview2.setAdapter(selectedAdapter2);        





			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					selectedAdapter.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					if (DEBUG_LOG) Log.d("IDCT","back button pressed");
					if (DEBUG_LOG) Log.d("IDCT","DATE SELECT: " + selectedAdapter.getItem(position).toString());
					if (DEBUG_LOG) Log.d("IDCT","DATE Code: " + selectedAdapter.getItem(position).getAttributeValue());
					if (Integer.parseInt(selectedAdapter.getItem(position).getAttributeValue()) == 4 ) {
						//Make second date val visible
						if (DEBUG_LOG) Log.d("IDCT","enter date 2");
						date2.setVisibility(View.VISIBLE);
						date1.setVisibility(View.VISIBLE);
					} else {
						date2.setVisibility(View.INVISIBLE);
						date1.setVisibility(View.VISIBLE);
					}
					
					surveyDataObject.putData("D", selectedAdapter.getItem(position).getAttributeValue());
					
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

		}

	}
	

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}


}
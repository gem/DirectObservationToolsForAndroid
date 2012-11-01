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
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;




public class Age_Selection_Form extends EQForm {
	public boolean DEBUG_LOG = false; 
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 8;
	
	private String topLevelAttributeDictionary = "DIC_YEAR_BUILT_QUAL";
	private String topLevelAttributeKey = "YR_BUILT_Q";
	private String topLevelAttributeKey3 = "YR_BUILT_1";
	private String topLevelAttributeKey2 = "YR_BUILT_2";
	
	private String heightTopLevelAttributeDictionary = "DIC_HEIGHT_QUALIFIER";
	private String heightStoreysAboveGroundQualifierAttributeKey = "STORY_AG_Q";	
	private String heightStoreysAboveGroundAttributeKey1 = "STORY_AG_1";		
	private String heightStoreysAboveGroundAttributeKey2 = "STORY_AG_2";	
	
	private String heightStoreysBelowGroundQualifierAttributeKey = "STORY_BG_Q";	
	private String heightStoreysBelowGroundAttributeKey1 = "STORY_BG_1";		
	private String heightStoreysBelowGroundAttributeKey2 = "STORY_BG_2";		
	
	private String heightStoreysAboveGradeQualifierAttributeKey = "HT_GR_GF_Q";	
	private String heightStoreysAboveGradeAttributeKey1 = "HT_GR_GF_1";		
	private String heightStoreysAboveGradeAttributeKey2 = "HT_GR_GF_2";		
	
	private String slope = "SLOPE";		
	
	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;
	
	private ArrayList list;
	private ArrayList list2;
	
	ListView listview;
	ListView listview2;

	private SelectedAdapter heightSelectedAdapter;
	private SelectedAdapter heightSelectedAdapter2;
	private SelectedAdapter heightSelectedAdapter3;
	
	ListView heightListview;
	ListView heightListview2;
	ListView heightListview3;
	
	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public EditText date1;
	public EditText date2;
	
	public EditText editTextAboveGround1;
	public EditText editTextAboveGround2;
	
	public EditText editTextBelowGround1;
	public EditText editTextBelowGround2;
	
	public EditText editTextAboveGrade1;
	public EditText editTextAboveGrade2;
	
	public EditText editTextSlope;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.age_and_height);
		
		date1 = (EditText)findViewById(R.id.editTextDateVal1);
		date2 = (EditText)findViewById(R.id.editTextDateVal2);
		
		editTextAboveGround1 = (EditText)findViewById(R.id.editTextAboveGround1);
		editTextAboveGround2 = (EditText)findViewById(R.id.editTextAboveGround2);
		
		editTextBelowGround1 = (EditText)findViewById(R.id.editTextBelowGround1);
		editTextBelowGround2 = (EditText)findViewById(R.id.editTextBelowGround2);
		
		editTextAboveGrade1 = (EditText)findViewById(R.id.editTextAboveGrade1);
		editTextAboveGrade2 = (EditText)findViewById(R.id.editTextAboveGrade2);
		
		editTextSlope = (EditText)findViewById(R.id.editTextSlope);
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

			//AGE STUFF
			Cursor allAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);     
			ArrayList<DBRecord> topLevelAttributesList = GemUtilities.cursorToArrayList(allAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + topLevelAttributesList.toString());
			mDbHelper.close();			
			selectedAdapter = new SelectedAdapter(this,0,topLevelAttributesList);
			selectedAdapter.setNotifyOnChange(true);
			listview = (ListView) findViewById(R.id.listExample);
			listview.setAdapter(selectedAdapter);      
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
					if (selectedAdapter.getItem(position).getAttributeValue().toString().equals("BETW")) {
						//Make second date val visible
						if (DEBUG_LOG) Log.d("IDCT","enter date 2");
						date2.setVisibility(View.VISIBLE);
						date1.setVisibility(View.VISIBLE);
					} else {
						date2.setVisibility(View.INVISIBLE);
						date1.setVisibility(View.VISIBLE);
					}					
					surveyDataObject.putData(topLevelAttributeKey, selectedAdapter.getItem(position).getAttributeValue());				
				}
			});    
			

			date1.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(topLevelAttributeKey2, date1.getText().toString());
					}
				}
			});
			date2.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(topLevelAttributeKey3, date2.getText().toString());
					}
				}
			});

			
			
			
			
			
			//Height Stuff			
			mDbHelper.open();

			Cursor heightAllAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(heightTopLevelAttributeDictionary);     
			ArrayList<DBRecord> heightTopLevelAttributesList = GemUtilities.cursorToArrayList(heightAllAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + heightTopLevelAttributesList.toString());

			/*
			Cursor allHeightAttributeTypesTopLevelCursor = mDbHelper.getAttributeValuesByDictionaryTable(heightTopLevelAttributeDictionary2);     
			ArrayList<DBRecord> heightLevelAttributesList = GemUtilities.cursorToArrayList(allHeightAttributeTypesTopLevelCursor);        
			Log.d("IDCT","QUALIFIER TYPES: " + heightLevelAttributesList.toString());
				*/
			mDbHelper.close();
			
			heightSelectedAdapter = new SelectedAdapter(this,0,heightTopLevelAttributesList);
			heightSelectedAdapter.setNotifyOnChange(true);
			heightListview = (ListView) findViewById(R.id.listAboveGroundQualifier);
			heightListview.setAdapter(heightSelectedAdapter);        

			heightSelectedAdapter2 = new SelectedAdapter(this,0,heightTopLevelAttributesList);
			heightSelectedAdapter2.setNotifyOnChange(true);
			heightListview2 = (ListView) findViewById(R.id.listBelowGroundQualifier);
			heightListview2.setAdapter(heightSelectedAdapter2);        

			heightSelectedAdapter3 = new SelectedAdapter(this,0,heightTopLevelAttributesList);
			heightSelectedAdapter3.setNotifyOnChange(true);
			heightListview3 = (ListView) findViewById(R.id.listAboveGradeQualifier);
			heightListview3.setAdapter(heightSelectedAdapter3);        
			
			heightListview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					if (DEBUG_LOG) Log.d("IDCT","Selected item");
					heightSelectedAdapter.setSelectedPosition(position);			
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();					
					if (DEBUG_LOG) Log.d("IDCT","Height SELECT: " + heightSelectedAdapter.getItem(position).toString());
					if (DEBUG_LOG) Log.d("IDCT","Height Code: " + heightSelectedAdapter.getItem(position).getAttributeValue());			
					surveyDataObject.putData(heightStoreysAboveGroundQualifierAttributeKey, heightSelectedAdapter.getItem(position).getAttributeValue());					
				}
			});    		
			editTextAboveGround1.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysAboveGroundAttributeKey1, editTextAboveGround1.getText().toString());
					}
				}
			});
			editTextAboveGround2.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysAboveGroundAttributeKey2, editTextAboveGround2.getText().toString());
					}
				}
			});
			
			
			heightListview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					heightSelectedAdapter2.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
				
					if (DEBUG_LOG) Log.d("IDCT","height val: " + heightSelectedAdapter2.getItem(position).toString());
					surveyDataObject.putData(heightStoreysBelowGroundQualifierAttributeKey, heightSelectedAdapter2.getItem(position).getAttributeValue());					
					
					completeThis();

				}
			});
			editTextBelowGround1.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysBelowGroundAttributeKey1, editTextBelowGround1.getText().toString());
					}
				}
			});
			editTextBelowGround2.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysBelowGroundAttributeKey2,editTextBelowGround2.getText().toString());
					}
				}
			});			
			
			
			heightListview3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,
						int position, long id) {
					// user clicked a list item, make it "selected"
					heightSelectedAdapter3.setSelectedPosition(position);
					//Toast.makeText(getApplicationContext(), "Item clicked: " + selectedAdapter.getItem(position).getOrderName() + " " + selectedAdapter.getItem(position).getOrderStatus() + " " +selectedAdapter.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					surveyDataObject.putData(heightStoreysAboveGradeQualifierAttributeKey, heightSelectedAdapter3.getItem(position).getAttributeValue());					
					completeThis();

				}
			});
			editTextAboveGrade1.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysAboveGradeAttributeKey1, editTextAboveGrade1.getText().toString());
					}
				}
			});
			editTextAboveGrade2.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(heightStoreysAboveGradeAttributeKey2,editTextAboveGrade2.getText().toString());
					}
				}
			});
			editTextSlope.setOnFocusChangeListener(new OnFocusChangeListener() { 				
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						surveyDataObject.putData(slope,editTextSlope.getText().toString());
					}
				}
			});
		}
	}	

	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}
}
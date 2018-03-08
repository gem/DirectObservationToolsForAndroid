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
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

public class LLRS_Selection_Longitudinal_Form extends Activity {
	
	private static final String TAG = "IDCT";
	public boolean DEBUG_LOG = false; 
	
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 2;
	

	private String topLevelAttributeDictionary = "DIC_LLRS";
	private String topLevelAttributeKey = "LLRS_L";
	
	private String secondLevelAttributeDictionary = "DIC_LLRS_DUCTILITY";
	private String secondLevelAttributeKey = "LLRS_DUCT_L";
	
	
	private SelectedAdapter selectedAdapter;
	private SelectedAdapter selectedAdapter2;

	public ArrayList<DBRecord> lLrsd;



	ListView listview;
	ListView listview2;


	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.llrs_selectable_list_longitudinal);
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

			Cursor allLLRSCursor = mDbHelper.getAttributeValuesByDictionaryTable(topLevelAttributeDictionary);        
			ArrayList<DBRecord> lLrs = GemUtilities.cursorToArrayList(allLLRSCursor);        
			if (DEBUG_LOG) Log.d(TAG,"lLrs: " + lLrs.toString());

			Cursor allLLRSDCursor = mDbHelper.getAttributeValuesByDictionaryTable(secondLevelAttributeDictionary); 
			lLrsd = GemUtilities.cursorToArrayList(allLLRSDCursor);
			
			allLLRSCursor.close();
			allLLRSDCursor.close(); 

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
					surveyDataObject.putData(topLevelAttributeDictionary, selectedAdapter.getItem(position).getAttributeValue());		
				}
			});        


			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView arg0, View view,int position, long id) {
					// user clicked a list item, make it "selected" 		        
					selectedAdapter2.setSelectedPosition(position);
					surveyDataObject.putData(secondLevelAttributeKey, selectedAdapter2.getItem(position).getAttributeValue());					
					//Toast.makeText(getApplicationContext(), "LV2 click: " + selectedAdapter2.getItem(position).getOrderName() + " " + selectedAdapter2.getItem(position).getOrderStatus() + " " +selectedAdapter2.getItem(position).getJson(), Toast.LENGTH_SHORT).show();
					completeThis();		
					
				}
			});

			
		}
		
		if (surveyDataObject.isExistingRecord) {
			if (DEBUG_LOG) Log.d(TAG, "Resuming attributes of existing record");
				
			String v = surveyDataObject.getSurveyDataValue(topLevelAttributeKey);
			if (DEBUG_LOG) Log.d(TAG, "v: " + v);
		}
		
		surveyDataObject.lastEditedAttribute = "";
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

package com.idctdo.android;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class SecondTabsActivity extends TabActivity {
	private static final String TAG = "IDCT";
	public boolean DEBUG_LOG = false; 
	
	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 0;

	
	public int unselectedTabColour = Color.parseColor("#000000");
	public int selectedTabColour = Color.parseColor("#eeeeee");
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level2tabs);

		tabHost = getTabHost();


		//Intent intentPageOne = new Intent().setClass(this,  Material_Selection_Transverse_Form.class);
		//Intent intentPageTwo = new Intent().setClass(this,  LLRS_Selection_Longitudinal_Transverse_Form.class);


		Intent tabIntent1=new Intent(this, Material_Selection_Longitudinal_Form2.class);
		TabSpec tabSpec1 = tabHost
		.newTabSpec("Longitudinal")
		.setIndicator("Longitudinal")
		.setContent(tabIntent1);

		tabHost.addTab(tabSpec1);
		
		Intent tabIntent2=new Intent(this, Material_Selection_Transverse_Form2.class);
		TabSpec tabSpec2 = tabHost
		.newTabSpec("Transverse")
		.setIndicator("Transverse")
		.setContent(tabIntent2);
		tabHost.addTab(tabSpec2);


		//Set tab height
		for (int i = 0; i <tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height /=2;
		}
		
		setTabColor();
		
		tabHost.setCurrentTab(0);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
				if (DEBUG_LOG) Log.d(TAG, "tab change " + tabId);
				setTabColor();
			}
		});
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void completeThis() {
		
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
		 
	}
	
	public void completeSecondaryTab() {
		
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
		 
	}

	public void setTabColor() {
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(unselectedTabColour); //unselected
		}
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedTabColour); // selected
	}
	
	
	public boolean isSecondaryTabCompleted() {
		MainTabActivity a = (MainTabActivity)getParent();		
		return a.isTabCompleted(tabIndex);
	}

}
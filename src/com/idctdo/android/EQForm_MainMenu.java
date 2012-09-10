package com.idctdo.android;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


public class EQForm_MainMenu extends Activity {
	
	SharedPreferences mAppSettings;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 


	}
	public void HandleButton (View v){
		if (v.getId()==R.id.ems98_button){

			//Toast.makeText(this, "Launching new earthquake survey form", Toast.LENGTH_SHORT).show();
			Intent ModifiedEMS98 = new Intent (EQForm_MainMenu.this, MainTabActivity.class);
			startActivity(ModifiedEMS98);

		}   else if (v.getId()==R.id.settings){
			/*temporary disabled*/
			Toast.makeText(this, "Android prefs and config to come", Toast.LENGTH_SHORT).show();
			/*
			Intent Settings = new Intent (EQForm_MainMenu.this, EQForm_Settings.class);
			startActivity(Settings);
			*/
		}   else if (v.getId()==R.id.help){
			
			Intent Help = new Intent (EQForm_MainMenu.this, EQForm_Help.class);
			startActivity(Help);
			

		}	else if (v.getId()==R.id.about){
			Intent About = new Intent (EQForm_MainMenu.this, EQForm_About.class);
			startActivity(About);

		}	/*else if (v.getId()==R.id.btn_map_view){

			Intent MapView = new Intent (EQForm_MainMenu.this, EQForm_MapView.class);
			startActivity(MapView);
		}*/
		
		else if (v.getId()==R.id.btn_sync_data){
			/*temporary disabled*/
			Toast.makeText(this, "Syncing to come", Toast.LENGTH_SHORT).show();

		}	
	}
}




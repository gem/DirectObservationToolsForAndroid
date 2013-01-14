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




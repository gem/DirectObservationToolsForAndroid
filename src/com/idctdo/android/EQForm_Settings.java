package com.idctdo.android;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class EQForm_Settings extends EQForm{

	SharedPreferences mAppSettings;

	private TextView mDateDisplay;
	private int mYear;
	private int mMonth;
	private int mDay;


	static final int DATE_DIALOG_ID = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		mAppSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Earthquake();
		Surveyor();
		Date();
		SurveyName();
	}

	private void Earthquake(){
		final EditText EQText = (EditText)findViewById(R.id.settings_earthquake); 

		if(mAppSettings.contains(APP_SETTINGS_EARTHQUAKE)){   

			EQText.setText(mAppSettings.getString(APP_SETTINGS_EARTHQUAKE, ""));
		}

		EQText.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction()== KeyEvent.ACTION_DOWN)&&(keyCode ==KeyEvent.KEYCODE_ENTER)){
					String EQString = EQText.getText().toString();
					Editor editor = mAppSettings.edit();
					editor.putString(APP_SETTINGS_EARTHQUAKE, EQString);


					editor.commit();

					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(EQText.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					return true;
				}
				return false;
			}
		});

	}

	private void Surveyor(){
		final EditText SurveyorText = (EditText)findViewById(R.id.settings_surveyor); 

		if(mAppSettings.contains(APP_SETTINGS_SURVEYOR)){   

			SurveyorText.setText(mAppSettings.getString(APP_SETTINGS_SURVEYOR, ""));
		}

		SurveyorText.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction()== KeyEvent.ACTION_DOWN)&&(keyCode ==KeyEvent.KEYCODE_ENTER)){
					String SurveyorString = SurveyorText.getText().toString();
					Editor editor = mAppSettings.edit();
					editor.putString(APP_SETTINGS_SURVEYOR, SurveyorString);
					editor.commit();

					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(SurveyorText.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					return true;
				}
				return false;
			}
		});

	}

	//capture date elements
	private void Date(){  
		mDateDisplay = (TextView)findViewById(R.id.date);


		/*get current date*/ final Calendar c =Calendar.getInstance();
		mYear  = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay   = c.get(Calendar.DAY_OF_MONTH);

		updateDisplay();
	}											
	private void updateDisplay(){
		mDateDisplay.setText(
				new StringBuilder()
				.append(mDay).append(" ")
				//month is 0 so add one
				.append(mMonth + 1).append(" ")
				.append(mYear).append(""));

	}




	private void SurveyName(){
		final EditText Name = (EditText)findViewById(R.id.settings_survey_name); 

		if(mAppSettings.contains(APP_SETTINGS_SURVEY_NAME)){   

			Name.setText(mAppSettings.getString(APP_SETTINGS_SURVEY_NAME, ""));
		}

		Name.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction()== KeyEvent.ACTION_DOWN)&&(keyCode ==KeyEvent.KEYCODE_ENTER)){
					String NameString = Name.getText().toString();
					Editor editor = mAppSettings.edit();
					editor.putString(APP_SETTINGS_SURVEY_NAME, NameString);
					editor.commit();

					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(Name.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					return true;
				}
				return false;
			}
		});

	}
	public void HandleButton (View v){
		if (v.getId()==(R.id.back_settings)){


			Intent Back = new Intent (EQForm_Settings.this, EQForm_MainMenu.class);
			startActivity(Back);    		  
		}
		else if (v.getId()==R.id.clear_settings){
			Editor e = mAppSettings.edit();
			e.clear();
			e.commit();

			Toast.makeText(this, "All settings cleared", Toast.LENGTH_SHORT).show();
			Intent Refresh = new Intent (EQForm_Settings.this, EQForm_Settings.class);
			startActivity(Refresh);
		}
		/*
        else if (v.getId()==R.id.next_settings){
        	final EditText  EQText = (EditText) findViewById(R.id.settings_earthquake);
        	final EditText  SurveyorText = (EditText) findViewById(R.id.settings_surveyor);
        	final EditText  SurveyName = (EditText) findViewById(R.id.settings_survey_name);

    		if ((EQText.length() == 0 )^(SurveyorText.length()== 0)^(SurveyName.length()==0)){
    			Toast.makeText(this, "Please complete settings options before starting survey", Toast.LENGTH_SHORT).show();
    		}else if (EQText.length() > 0){

        	Toast.makeText(this, "Launching new earthquake survey form", Toast.LENGTH_SHORT).show();

		   Intent Next = new Intent (EQForm_Settings.this, EQForm_ModifiedEMS_1.class);
		   startActivity(Next);       	


        }
       }

		 */
	}
}










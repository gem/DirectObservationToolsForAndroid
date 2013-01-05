package com.idctdo.android;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Details_Selection extends Activity {


	private ArrayList list;
	public ArrayList<DBRecord> lLrsd;


	ListView listview;
	ListView listview2;

	Button btn_saveObservation;
	Button btn_cancelObservation;

	public GemDbAdapter mDbHelper;
	public GEMSurveyObject surveyDataObject;

	public TabActivity tabActivity;
	public TabHost tabHost;
	public int tabIndex = 3;

	public EditText editTextSurveyComment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_selectable_list);
		btn_saveObservation =(Button)findViewById(R.id.btn_save_observation);
		btn_saveObservation.setOnClickListener(saveObservationListener);

		btn_cancelObservation =(Button)findViewById(R.id.btn_cancel_observation);
		btn_cancelObservation.setOnClickListener(cancelObservationListener);

	}

	
	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity a = (MainTabActivity)getParent();
		surveyDataObject = (GEMSurveyObject)getApplication();



		if (a.isTabCompleted(tabIndex)) {

		} else {
			editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
			editTextSurveyComment.setOnFocusChangeListener(new OnFocusChangeListener() { 				

				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
						editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
						surveyDataObject.putData("COMMENTS", editTextSurveyComment.getText().toString());
						completeThis();
					}
				}
			});
		}
	}



	public void completeThis() {
		MainTabActivity a = (MainTabActivity)getParent();
		a.completeTab(tabIndex);
	}

	private OnClickListener saveObservationListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//mWebView.loadUrl("javascript:map.zoomOut()");
			Log.d("IDCT", "Save observation");

			MainTabActivity a = (MainTabActivity)getParent();	
			a.saveData();
			//a.restart();

			//Intent ModifiedEMS98 = new Intent (Details_Selection.this, EQForm_MapView.class);
			//startActivity(ModifiedEMS98);
			a.finish();			
		}

	};

	private OnClickListener cancelObservationListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//mWebView.loadUrl("javascript:map.zoomOut()");
			Log.d("IDCT", "Cancel observation");

			MainTabActivity a = (MainTabActivity)getParent();	
			a.finish();			
		}

	};


	@Override
	public void onBackPressed() {
		Log.d("IDCT","back button pressed");
		MainTabActivity a = (MainTabActivity)getParent();
		a.backButtonPressed();
	}


}
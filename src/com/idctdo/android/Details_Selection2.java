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

public class Details_Selection2 extends Activity {


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
	}


	@Override
	protected void onResume() {
		super.onResume();

		surveyDataObject = (GEMSurveyObject)getApplication();



		editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
		editTextSurveyComment.setOnFocusChangeListener(new OnFocusChangeListener() { 				

			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					Log.d("IDCT", "CHANGED FOCUS OF EDIT TEXT");
					editTextSurveyComment = (EditText) findViewById(R.id.surveyComment);
					surveyDataObject.putData("COMMENTS", editTextSurveyComment.getText().toString());

				}
			}
		});

	}




}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.Application;
import android.util.Log;

public class GEMSurveyObject extends Application {
	private int data=0;
	private String uid;
	public boolean unsavedEdits = false;
	
	public boolean isExistingRecord = false;
	
	
	private double currentSurveyPointLon = 0;
	private double currentSurveyPointLat = 0;	
	
	public HashMap<String, String> surveyData = new HashMap<String, String>();
	
	public HashMap<String, String> gedData = new HashMap<String, String>();
	public HashMap<String, String> consequencesData = new HashMap<String, String>();	
	
	public ArrayList<HashMap<String, String>> mediaDetailDataList = new ArrayList<HashMap<String, String>>();
	
	
	public int getData(){
		return this.data;
	}

	public void setData(int d){
		this.data=d;
	}
	
	public void setUid(String uid){
		this.uid=uid;
	}
	
	public String getUid(){
		return this.uid;
	}

	public void setLon(double d){
		this.currentSurveyPointLon=d;
	}

	public void setLat(double d){
		this.currentSurveyPointLat=d;
	}

	public double getLon(){
		return this.currentSurveyPointLon;

	}

	public double getLat(){
		return this.currentSurveyPointLat;
	}
	
	public String getComment(){
		
		return null;
	}
	
	

	public boolean putData(String key, String value) {		
		surveyData.put(key, value);
		//Log.d("IDCT","HASHMAP : " + surveyData.toString());
		return true;
	}
	
	public String getSurveyDataValue(String key) {			
		//Log.d("IDCT","HASHMAP : " + surveyData.toString());
		return surveyData.get(key);
	}
	
	public boolean putGedData(String key, String value) {		
		gedData.put(key, value);
		//Log.d("IDCT","HASHMAP : " + surveyData.toString());
		return true;
	}
	public boolean putConsequencesData(String key, String value) {		
		consequencesData.put(key, value);
		//Log.d("IDCT","HASHMAP : " + surveyData.toString());
		return true;
	}
	
	public boolean putMediaData(String key, String value,String key2, String value2,String key3, String value3,String key4, String value4) {		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		map.put(key2, value2);
		map.put(key3, value3);
		map.put(key4, value4);
		mediaDetailDataList.add(map);
		//Log.d("IDCT","HASHMAP : " + surveyData.toString());
		return true;
	}
	

	public ArrayList getMediaDetailKeyValuePairsMap() {		
		return mediaDetailDataList;
	}
	
	public HashMap getKeyValuePairsMap() {		
		return surveyData;
	}
	
	public HashMap getGedKeyValuePairsMap() {		
		return gedData;
	}
	public HashMap getConsequencesKeyValuePairsMap() {		
		return consequencesData;
	}
	
	
	public void clearGemSurveyObject() {
		surveyData = new HashMap<String, String>();
		mediaDetailDataList = new ArrayList<HashMap<String, String>>();
		gedData = new HashMap<String, String>();
		consequencesData = new HashMap<String, String>();
		setData(0);
	}
	
	
}


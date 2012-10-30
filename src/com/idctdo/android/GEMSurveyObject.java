package com.idctdo.android;

import java.util.HashMap;

import android.app.Application;
import android.util.Log;

public class GEMSurveyObject extends Application {
	private int data=200;

	private double currentSurveyPointLon = 0;
	private double currentSurveyPointLat = 0;	
	
	public HashMap<String, String> surveyData = new HashMap<String, String>();
	
	
	public int getData(){
		return this.data;
	}

	public void setData(int d){
		this.data=d;
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
	
	public HashMap getKeyValuePairsMap() {		
		return surveyData;
	}
	
	public void clearGemSurveyObject() {
		surveyData = new HashMap<String, String>();
	}
}


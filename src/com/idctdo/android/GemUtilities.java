package com.idctdo.android;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

//Static functions for data processing and the like
public class GemUtilities {

	public static ArrayList cursorToArrayList(Cursor mCursor) {
		ArrayList<DBRecord> mArrayList = new ArrayList<DBRecord>();
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast()) {
			DBRecord o1 = new DBRecord();		
	        o1.setOrderName(mCursor.getString(0));
	        o1.setOrderStatus(mCursor.getString(1));
	        o1.setJson(mCursor.getString(2));
	        mArrayList.add(o1);
		    mCursor.moveToNext();
		}
		
		return mArrayList;
	}
	
	
}

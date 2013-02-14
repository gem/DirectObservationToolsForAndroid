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

import android.database.Cursor;
import android.util.Log;
import android.widget.Spinner;

//Static functions for data processing and the like
public class GemUtilities {

	public static ArrayList cursorToArrayList(Cursor mCursor) {
		ArrayList<DBRecord> mArrayList = new ArrayList<DBRecord>();
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast()) {
			DBRecord o1 = new DBRecord();		
	        o1.setAttributeDescription(mCursor.getString(0));
	        o1.setAttributeValue(mCursor.getString(1));
	        o1.setJson(mCursor.getString(2));
	        mArrayList.add(o1);
		    mCursor.moveToNext();
		}
		
		return mArrayList;
	}
	
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0)
            return true;

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isWhitespace(string.codePointAt(i)))
                return false;
        }
        return true;
    }
	
    
    
    
	public static boolean loadPreviousAtttributesSpinner(Spinner lv,ArrayList<DBRecord> listOfTheseObjects, String attributeKey,String attributeValue) {
		Log.d("IDCT","About to resume some values for " + attributeKey);
		if (!GemUtilities.isBlank(attributeValue)) {
			Log.d("IDCT", attributeValue + " is not null. attributeValue: " + attributeValue);
			int i = 0;
			for(DBRecord d : listOfTheseObjects){
				Log.d("IDCT", "Looping thring arraylist of selectedAdapter " + i);
				Log.d("IDCT", "val" + d.getAttributeValue());
				if(d.getAttributeValue().contains(attributeValue)) {
					Log.d("IDCT", "MATCH!" );					
					//selectedAdapterToPopulate.setSelectedPosition(i);
					//this.setSelectedPosition(i);
					
					lv.setSelection(i,true);
					return true;
				}
				i++;
			}
		}
		return false;
	}
	
	
}

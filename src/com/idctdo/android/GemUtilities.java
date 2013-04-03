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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Spinner;

//Static functions for data processing and the like
public class GemUtilities {

	public static boolean DEBUG_LOG = false; 
	protected static final String TAG = "GemUtilities";

	public static ArrayList cursorToArrayList(Cursor mCursor) {
		ArrayList<DBRecord> mArrayList = new ArrayList<DBRecord>();
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast()) {
			DBRecord o1 = new DBRecord();		
			o1.setAttributeDescription(mCursor.getString(0));
			o1.setAttributeValue(mCursor.getString(1));
			if (mCursor.getColumnCount() > 2) {
				o1.setJson(mCursor.getString(2));
			}
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
				Log.d("IDCT", "Comparing val: " + d.getAttributeValue() + " with: " + attributeValue);
				
				//if(d.getAttributeValue().contains(attributeValue)) {
				if(d.getAttributeValue().equals(attributeValue.toString())) {
					Log.d("IDCT", "MATCH!" );		
	
					lv.setSelection(i,true);
					return true;
				}
				i++;
			}
		} else {
			Log.d("IDCT","Attribute value is null");
		}
		return false;
	}


	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
	         The difference between this impl. and the previous is that, rather 
	         than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
	         we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
	         is the 'current working' distance array that maintains the newest distance cost
	         counts as we iterate through the characters of String s.  Each time we increment
	         the index of String t we are comparing, d is copied to p, the second int[].  Doing so
	         allows us to retain the previous cost counts as required by the algorithm (taking 
	         the minimum of the cost count to the left, up one, and diagonally up and to the left
	         of the current cost count being calculated).  (Note that the arrays aren't really 
	         copied anymore, just switched...this is clearly much better than cloning an array 
	         or doing a System.arraycopy() each time  through the outer loop.)

	         Effectively, the difference between the two implementations is this one does not 
	         cause an out of memory condition when calculating the LD over two very large strings.
		 */

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}

		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}



	private static File[] listFiles(String dirFrom, String dirTo) throws IOException {
		String res = Environment.getExternalStorageDirectory().toString()+ "/idctdo/glossary/";
		File folder = new File(res);
		//String fileList[] = am.list(dirFrom);

		File[] fileList = folder.listFiles();		
		if (fileList != null)
		{   
			for ( int i = 0;i<fileList.length;i++)
			{

				//if (DEBUG_LOG) Log.d(TAG,fileList[i]);
			}
		}
		return fileList;
	}


	public static void fileCopy(String srcStr, String dstStr) throws IOException {
		File src =  new File(srcStr );
		File dst = new File(dstStr);
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}



	public static String loadHelpFileNames(String strToCheck) {
		File folder = new File("file:///android_asset/glossary");
		//if (DEBUG_LOG) Log.d(TAG,"help loading");
		String pageToLoad = "";
		try {
			File[] files = listFiles("glossary","nothing");
			int d = 0;
			int lowestD = 1000;
			int lowestIndex = -1;
			String matched = "";
			for( int i = 0; i < files.length - 1; i++) {				
				String filename = files[i].getName();
				String element = filename.substring(0, filename.lastIndexOf('.'));    

				d = GemUtilities.getLevenshteinDistance(strToCheck.toLowerCase(), element.toLowerCase());			   

				//if (DEBUG_LOG) Log.d(TAG,"LevShtein: " + d + " i: "+ i  );
				//if (DEBUG_LOG) Log.d(TAG,"str: " +  strToCheck + " element.toString(): "+element.toString() );

				if (d < lowestD) {
					lowestD = d;
					lowestIndex = i;
					matched = element;
				}
			}


			//if (DEBUG_LOG) Log.d(TAG,"Matched index: " + files[lowestIndex].getName());
			// if (DEBUG_LOG) Log.d(TAG,"Match: " + lowestIndex);
			//if (DEBUG_LOG) Log.d(TAG,"Match Val: " + lowestD);
			//if (DEBUG_LOG) Log.d(TAG,"Query string: " + strToCheck);
			if (lowestD < 5) {
				if (DEBUG_LOG) Log.d(TAG,"Successful matching of: " + strToCheck + "," +files[lowestIndex].getName() + ", "+ lowestD);
				pageToLoad = files[lowestIndex].getName();
			} else {
				if (DEBUG_LOG) Log.d(TAG,"Match failed: " + strToCheck);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageToLoad;
	}


	public static void generateCleanGlossary(String queryDesc , String newName) {
		String pageToLoad = loadHelpFileNames(queryDesc);
		//String pageToLoad =  mCur.getString(0);		
		//tabHost.getCurrentTab();
		if (!isBlank(pageToLoad)) {
			try {		

			
				String sdcardpath = Environment.getExternalStorageDirectory().toString()+ "/idctdo/glossary/";
				String dst= Environment.getExternalStorageDirectory().toString()+ "/idctdo/glossary_cleaned/";

				String dst2 = dst  + newName + ".html";
				fileCopy(sdcardpath + pageToLoad, dst2);
				//if (DEBUG_LOG) Log.d(TAG,"Sucess copying " + newName + ".html");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

	}
}

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
package org.globalquakemodel.org.idctdo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;



public class GemDbAdapter 
{
	public boolean DEBUG_LOG = false; 

	protected static final String TAG = "IDCT";


	private final Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	private String appVer;
	public GemDbAdapter(Context context) 
	{
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);

		try
		{
			appVer = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			Log.v(TAG, e.getMessage());
		}   
	}

	public GemDbAdapter createDatabase() throws SQLException 
	{
		try 
		{
			mDbHelper.createDataBase();
		} 
		catch (IOException mIOException) 
		{
			Log.d(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public GemDbAdapter open() throws SQLException 
	{
		try 
		{
			Log.d(TAG, "trying to open db >>");
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} 
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "open >>"+ mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() 
	{
		mDbHelper.close();
	}





	public Cursor getGemObjects()
	{
		try
		{
			String sql ="SELECT * FROM GEM_OBJECT";
			Cursor mCur = mDb.rawQuery(sql, null);

			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getGemFavourites()
	{
		try
		{
			String sql ="SELECT * FROM SETTINGS where KEY like '%_FAV';";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getFavouritesData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getGemProjects()
	{
		try
		{
			String sql ="SELECT PROJ_NAME, PROJ_UID FROM GEM_PROJECT";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getFavouritesData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getGemProjectById(String uid)
	{
		try
		{
			String sql ="SELECT * FROM GEM_PROJECT WHERE PROJ_UID = '"+ uid + "'";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getFavouritesData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public void deleteRecords()
	{
		mDbHelper.openDataBase();
		mDbHelper.deleteRecords();
		mDbHelper.close();
	}


	public boolean deleteRecordByUid(String uid)
	{

		try
		{
			String sql ="DELETE FROM GEM_OBJECT WHERE OBJ_UID = '" + uid + "'";
			Cursor mCur = mDb.rawQuery(sql, null);	
			if (mCur!=null)
			{
				mCur.moveToNext();
			}

		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "deleting record >>"+ mSQLException.toString());
			throw mSQLException;
		}


		try
		{
			String sql ="DELETE FROM CONSEQUENCES WHERE GEMOBJ_UID = '" + uid + "'";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "deleting record from CONSEQUENCES >>"+ mSQLException.toString());
			throw mSQLException;
		}

		try
		{
			String sql ="DELETE FROM GED WHERE GEMOBJ_UID = '" + uid + "'";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "deleting record from GED >>"+ mSQLException.toString());
			throw mSQLException;
		}

		try
		{
			String sql ="DELETE FROM MEDIA_DETAIL WHERE GEMOBJ_UID = '" + uid + "'";
			Cursor mCur = mDb.rawQuery(sql, null);	
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "deleting record from MEDIA_DETAIL >>"+ mSQLException.toString());
			throw mSQLException;
		}

		return true;
	}


	public void exportGemTableToCsv(){

		File sd = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/db_snapshots");
		sd.mkdirs();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date currentDate = new Date(System.currentTimeMillis());
		String currentDateandTime = sdf.format(currentDate);
		String backupDBPath = null;
		backupDBPath = "IDCTDO_survey_points_" + currentDateandTime.toString() + ".csv";

		//File dbFile=getDatabasePath("yourDBname.sqlite");
		/*
         File exportDir = new File(sd, backupDBPath);

        if (!exportDir.exists()) 
        {
            exportDir.mkdirs();
        }
		 */
		File file = new File(sd, backupDBPath);
		try       {
			file.createNewFile();                
			CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
			//SQLiteDatabase db = dbhelper.getReadableDatabase();
			Cursor curCSV = getGemObjects();

			Log.d("IDCT","IDCT DB Export cursor count: " + curCSV.getCount());
			if (curCSV != null) {
				csvWrite.writeNext(curCSV.getColumnNames());
				curCSV.moveToFirst();				
				if (curCSV.getCount() > 0 ){
					//Write the first record in the cursor as .writeNext will end up skipping a record
					String[] arrStr = new String[curCSV.getColumnCount()];
					for (int i = 0; i < curCSV.getColumnCount(); i = i + 1) {
						arrStr[i] = curCSV.getString(i);
					}
					csvWrite.writeNext(arrStr);

					//Now loop over the rest of the cursor
					curCSV.moveToFirst();	
					while(curCSV.moveToNext())
					{

						String[] arrStr2 = new String[curCSV.getColumnCount()];
						for (int i = 0; i < curCSV.getColumnCount(); i = i + 1) {
							arrStr2[i] = curCSV.getString(i);
						}
						csvWrite.writeNext(arrStr2);
						Log.d("IDCT","IDCT DB Export" + Arrays.toString(arrStr2));
					}
				}

			}
			csvWrite.close();
			curCSV.close();
//			Toast.makeText(this.mContext.getApplicationContext(), "CSV export created. Export is located at: \n" + sd + "/" + backupDBPath , Toast.LENGTH_LONG).show();
			Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterCSVExport) + "\n" + sd + "/" + backupDBPath , Toast.LENGTH_LONG).show();

		}
		catch(Exception sqlEx)
		{
			Log.d("IDCT DB Export", sqlEx.getMessage(), sqlEx);
		}

	}


	public Cursor getGemObjectsForMap()
	{
		try
		{
			String sql ="SELECT OBJ_UID, X, Y FROM GEM_OBJECT";

			Cursor mCur = mDb.rawQuery(sql, null);

			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getGemObjects >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getObjectByUid(String uid)
	{
		try
		{
			String sql ="select * FROM GEM_OBJECT WHERE OBJ_UID = '"+ uid + "'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getGedObjectByUid(String uid)
	{
		try
		{
			String sql ="select * FROM GED WHERE GEMOBJ_UID = '"+ uid + "'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getConsequencesObjectByUid(String uid)
	{
		try
		{
			String sql ="select * FROM CONSEQUENCES WHERE GEMOBJ_UID = '"+ uid + "'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}
	public Cursor getAllAttributeTypes()
	{
		try
		{
			String sql ="select CODE, SHORT_DESCRIPTION from DIC_ATTRIBUTE_TYPE";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	public Cursor getAllMaterialTypes()
	{
		try
		{
			String sql ="select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'MTYPE'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getAllMaterialTechnologies(String attributeScope)
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'MTECH' and ATTRIBUTE_SCOPE = '"+ attributeScope + "'";

			Log.d(TAG, "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getAllMaterialMortars(String attributeScope)
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'MORT'and ATTRIBUTE_SCOPE = '"+ attributeScope + "'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getAllLLRS()
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'LLRS'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllLLRS >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getAllLLRSD()
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'LLRSD'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllLLRSD >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getAttributeValuesByType(String attributeType)
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = '"+ attributeType + "'";


			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllLLRS >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	//Functions like this will be deprected as they use a superdictionary
	public Cursor getAttributeValuesByTypeAndScope(String attributeType, String attributeScope)
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, 	TRIM(ATTRIBUTE_SCOPE) from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = '"+ attributeType + "' and ATTRIBUTE_SCOPE = '"+ attributeScope + "'";

			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getAllMediaByRecord(String uid) {
		try
		{
			String sql = "		select * from media_detail where GEMOBJ_UID = '"+ uid+ "'";
			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getMediaRecords >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	public Cursor getAttributeValuesByDictionaryTable(String dictionaryTable)
	{
		try
		{
			String sql = "select DESCRIPTION, CODE, TRIM(SCOPE) from '"+ dictionaryTable + "'";

			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());

				//generateCleanGlossary(mCur.getString(0),mCur.getString(1));

			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getAttributeValuesByDictionaryTableUsingRule(String dictionaryTable, String parentCode)
	{
		try
		{
			String sql = "select distinct DESCRIPTION, CODE, SCOPE from GEM_RULES INNER JOIN '"+ dictionaryTable + "' ON GEM_RULES.CHILD_CODE = '"+ dictionaryTable + "'.CODE where PARENT_CODE = '"+ parentCode+ "'";
			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
				//generateCleanGlossary(mCur.getString(0),mCur.getString(1));
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public Cursor getAttributeValuesByDictionaryTableAndScope(String dictionaryTable, String attributeScope)
	{
		try
		{
			String sql = "select DESCRIPTION, CODE, SCOPE from '"+ dictionaryTable + "' where SCOPE = '"+ attributeScope + "'";
			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}




	public Cursor getQualifierValuesByQualifierType(String qualifierType)
	{
		try
		{
			String sql = "select DESCRIPTION, CODE, QUALIFIER_VALUE from DIC_QUALIFIER WHERE QUALIFIER_TYPE = '"+ qualifierType + "'";

			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getQualifierValuesByQualifierType2(String qualifierType)
	{
		try
		{
			String sql = "select QUALIFIER_VALUE, DESCRIPTION, CODE from DIC_QUALIFIER WHERE QUALIFIER_TYPE = '"+ qualifierType + "'";

			if (DEBUG_LOG) Log.d("IDCT", "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.d(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public void insertTestData()
	{		
		try
		{						
			ContentValues cv = new ContentValues();
			UUID id = UUID.randomUUID();
			cv.put("OBJ_UID", id.toString());
			cv.put("PROJ_UID", "dummy proj string");
			cv.put("OBJ_SCOPE", "BUILD");
			cv.put("X", "1234");
			cv.put("Y", "4567");	
			cv.put("SOURCE", "FIELD");	
			mDb.insert("GEM_OBJECT", null, cv);
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insertTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	public void insertFavourite(String favouriteName, String uidString)
	{		
		try
		{					
			ContentValues cv = new ContentValues();
			UUID.randomUUID();
			cv.put("KEY", favouriteName.toString() + "_FAV");
			cv.put("VALUE", uidString.toString());
			mDb.insert("SETTINGS", null, cv);
//			String feedbackMsg = "Favourite saved\n " + favouriteName + "_FAV";
			String feedbackMsg = this.mContext.getString(R.string.titleDbAdapterFavouriteSaved) + "\n " + favouriteName + "_FAV";
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insertFavourite >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	public void insertProject(String projectName, String surveyorName, String projectSummary, Date date)
	{		
		try
		{		
			ContentValues cv = new ContentValues();
			UUID id = UUID.randomUUID();
			cv.put("PROJ_UID", id.toString());			
			if (!TextUtils.isEmpty(projectName.toString())) {
				cv.put("PROJ_NAME", projectName.toString());
			} else{
				cv.putNull("PROJ_NAME");
			}
			if (!TextUtils.isEmpty(projectSummary.toString())) {
				cv.put("PROJ_SUMRY", projectSummary.toString());
			} else{
				cv.putNull("PROJ_SUMRY");
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cv.put("PROJ_DATE", dateFormat.format(date));
			
			mDb.insert("GEM_PROJECT", null, cv);
//			String feedbackMsg = "Project saved\n " + projectName;
			String feedbackMsg = this.mContext.getString(R.string.titleDbAdapterProjectSaved) + "\n " + projectName;
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();

		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insertProject >>"+ mSQLException.toString());
			throw mSQLException;
		}

		try
		{					
			ContentValues cv = new ContentValues();
			UUID.randomUUID();
			cv.put("KEY", "CURRENT_USER");
			cv.put("VALUE",surveyorName.toString());
			mDb.insert("SETTINGS", null, cv);
//			String feedbackMsg = "Current User:\n " + surveyorName.toString();
			String feedbackMsg = this.mContext.getString(R.string.titleDbAdapterUser) + "\n " + surveyorName.toString();
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "There was a problem inserting the current user into the database"+ mSQLException.toString());
			throw mSQLException;
		}
		try
		{					
			ContentValues cv = new ContentValues();
			UUID.randomUUID();
			cv.put("KEY", "GEM_VERSION");
			cv.put("VALUE",appVer);
			mDb.insert("SETTINGS", null, cv);
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "There was a problem inserting the tool version into the database >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public void insertOrUpdateGemData(GEMSurveyObject gemGlobalVariables)
	{		
		Log.d(TAG, "TRYING TO INSERT OR UPDATE GEM DATA");
		if (DEBUG_LOG) Log.d(TAG,"gemGlobalVariables.isExistingRecord: " + gemGlobalVariables.isExistingRecord);	
		try
		{								
			ContentValues cv = new ContentValues();
			cv.put("OBJ_UID", gemGlobalVariables.getUid());	

			String projId = UUID.randomUUID().toString();
			try {
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.mContext.getApplicationContext());
				projId = settings.getString("projectIdTextPref", "0");
			} catch (SQLException mSQLException) {
//				Toast.makeText(this.mContext.getApplicationContext(), "There was a problem getting a project id. Ensure a project ID is defined in the settings.", Toast.LENGTH_LONG).show();
				Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterProjectIDProblem), Toast.LENGTH_LONG).show();
			}
			cv.put("PROJ_UID", projId.toString());			
			cv.put("X", Double.toString(gemGlobalVariables.getLon()));
			cv.put("Y",  Double.toString(gemGlobalVariables.getLat()));
			cv.put("SOURCE", "FIELD");			

			//Transfer the main survey variables into the ContentValues set			
			HashMap<String,String> keyVals = gemGlobalVariables.getKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					cv.put(key, value);
				} else{
					cv.putNull(key);
				}
			}

			Log.d(TAG, "GEM ContentValues: " + cv.toString());

			if (gemGlobalVariables.isExistingRecord) { 
				if (DEBUG_LOG) Log.d(TAG,"updating record in db " + gemGlobalVariables.getUid());	
				String columnName= "OBJ_UID";
				mDb.update("GEM_OBJECT", cv, columnName+"=?", new String[] {gemGlobalVariables.getUid()});
			}  else {				
				if (DEBUG_LOG) Log.d(TAG,"inserting record in db " + gemGlobalVariables.getUid());
				if (DEBUG_LOG) Log.d(TAG,"favourite template id: " + gemGlobalVariables.favouriteRecord);
				mDb.insert("GEM_OBJECT", null, cv);
			}

//			String feedbackMsg = "Survey Data saved\n " + "Lat: " + Double.toString(gemGlobalVariables.getLat()) + "\nLon: " + Double.toString(gemGlobalVariables.getLon()) + "\nID:" + gemGlobalVariables.getUid().toString();
			String feedbackMsg = this.mContext.getString(R.string.titleDbAdapterSurveySaved) + "\n" + this.mContext.getString(R.string.titleMapViewBestLocationLat) + Double.toString(gemGlobalVariables.getLat()) + "\n" + this.mContext.getString(R.string.titleMapViewBestLocationLon) +  Double.toString(gemGlobalVariables.getLon()) + "\nID:" + gemGlobalVariables.getUid().toString();
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insert / Update GEM Object>>"+ mSQLException.toString());
//			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterProblemSaving), Toast.LENGTH_LONG).show();
			throw mSQLException;
		}		

		ArrayList mediaList = gemGlobalVariables.getMediaDetailKeyValuePairsMap();
		Log.d(TAG, "MEDIA DETAIL LIST: " + mediaList.size());

		for (int i = 0; i < mediaList.size(); i++) {
			Log.d(TAG, "Trying to insert Media detail data");
			try
			{
				HashMap map = (HashMap) mediaList.get(i);
				ContentValues cv = new ContentValues();		
				cv.put("GEMOBJ_UID", gemGlobalVariables.getUid());

				HashMap<String,String> keyVals = map;
				for (Map.Entry<String, String> entry : keyVals.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					Log.d(TAG, "GEM Media key: " + key.toString());
					Log.d(TAG, "GEM Media val: " + value.toString());					
					if (!TextUtils.isEmpty(value)) {
						cv.put(key, value);
					} else{
						cv.putNull(key);
					}									
				}
				Log.d(TAG, "GEM Media DetailValues: " + cv.toString());
				mDb.insert("MEDIA_DETAIL", null, cv);	
//				Toast.makeText(this.mContext.getApplicationContext(), "Photos were sucessfully linked in the database", Toast.LENGTH_SHORT).show();
				Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterPhotosLinked), Toast.LENGTH_SHORT).show();

			}
			catch (SQLException mSQLException) 
			{
				Log.d(TAG, "insertMediaDetails >>"+ mSQLException.toString());
//				Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the Media Detail", Toast.LENGTH_LONG).show();
				Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterProblemSavingMedia), Toast.LENGTH_LONG).show();
				throw mSQLException;
			}
		}


		Log.d(TAG, "Trying to insert/update GED data");

		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();
			UUID gedId = UUID.randomUUID();			

			cv.put("GEMOBJ_UID", gemGlobalVariables.getUid());
			cv.put("GED_UID", gedId.toString()); 

			HashMap<String,String> keyVals = gemGlobalVariables.getGedKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					cv.put(key, value);
				} else{
					cv.putNull(key);
				}
			}

			Log.d(TAG, "GED ContentValues: " + cv.toString());
			if (gemGlobalVariables.isExistingRecord) { 
				if (DEBUG_LOG) Log.d(TAG,"updating GED record in db " + gemGlobalVariables.getUid());	
				String columnName= "GEMOBJ_UID";
				mDb.update("GED", cv, columnName+"=?", new String[] {gemGlobalVariables.getUid()});
			}  else {				
				if (DEBUG_LOG) Log.d(TAG,"inserting record in db " + gemGlobalVariables.getUid());	
				mDb.insert("GED", null, cv);
			}
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insertGEDData >>"+ mSQLException.toString());
//			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterProblemSaving), Toast.LENGTH_LONG).show();
			throw mSQLException;
		}


		Log.d(TAG, "Trying to insert/update CONSEQUENCES data");

		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();
			UUID consId = UUID.randomUUID();			

			cv.put("GEMOBJ_UID", gemGlobalVariables.getUid());
			cv.put("CONSEQ_UID", consId.toString()); 

			HashMap<String,String> keyVals = gemGlobalVariables.getConsequencesKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					cv.put(key, value);
				} else{
					cv.putNull(key);
				}
			}


			Log.d(TAG, "CONSQ ContentValues: " + cv.toString());
			if (gemGlobalVariables.isExistingRecord) { 
				if (DEBUG_LOG) Log.d(TAG,"updating CONSEQUENCES record in db " + gemGlobalVariables.getUid());	
				String columnName= "GEMOBJ_UID";
				mDb.update("CONSEQUENCES", cv, columnName+"=?", new String[] {gemGlobalVariables.getUid()});
			}  else {				
				if (DEBUG_LOG) Log.d(TAG,"inserting record in db " + gemGlobalVariables.getUid());	
				mDb.insert("CONSEQUENCES", null, cv);
			}
		}
		catch (SQLException mSQLException) 
		{
			Log.d(TAG, "insertConseqData >>"+ mSQLException.toString());
//			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDbAdapterProblemSaving), Toast.LENGTH_LONG).show();
			throw mSQLException;
		}
		gemGlobalVariables.getKeyValuePairsMap().clear();
	}


	public void copyDataBaseToSdCard()
	{		
		Log.d(TAG, "copying to sdcard");

		try {
			mDbHelper.copyDataBaseToSdCard();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "copying to sd card problem >>"+ e.toString());
			e.printStackTrace();
		}
	}
}

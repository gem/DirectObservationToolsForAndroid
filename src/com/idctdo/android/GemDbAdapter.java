package com.idctdo.android;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


public class GemDbAdapter 
{
	public boolean DEBUG_LOG = true; 

	protected static final String TAG = "DataAdapter";

	private final Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	public GemDbAdapter(Context context) 
	{
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);
	}

	public GemDbAdapter createDatabase() throws SQLException 
	{
		try 
		{
			mDbHelper.createDataBase();
		} 
		catch (IOException mIOException) 
		{
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public GemDbAdapter open() throws SQLException 
	{
		try 
		{
			Log.e(TAG, "trying to open db >>");
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} 
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "open >>"+ mSQLException.toString());
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
			Log.e(TAG, "getTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
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
			csvWrite.writeNext(curCSV.getColumnNames());
			while(curCSV.moveToNext())
			{
				//Which column you want to exprort
				//String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4), curCSV.getString(5)};
				int x  = 0;
				String[] arrStr = new String[curCSV.getColumnCount()];
				//String[] arrStr; // elements are Strings
				//arrStr = new String[]
				//while( x < curCSV.getColumnCount()-2 ){
				//	arrStr[x] =  curCSV.getString(x);
				// }

				for (int i = 0; i < curCSV.getColumnCount(); i = i + 1) {

					arrStr[i] = curCSV.getString(i);
				}
				csvWrite.writeNext(arrStr);
				Log.d("IDCT","IDCT DB Export" + Arrays.toString(arrStr));
			}
			csvWrite.close();
			curCSV.close();
			Toast.makeText(this.mContext.getApplicationContext(), "CSV export created. Export is located at: \n" + sd + "/" + backupDBPath , Toast.LENGTH_LONG).show();

		}
		catch(Exception sqlEx)
		{
			Log.e("IDCT DB Export", sqlEx.getMessage(), sqlEx);
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
			Log.e(TAG, "getGemObjects >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	public Cursor getObjectByUid(UUID uid)
	{
		try
		{
			String sql ="select OBJ_UID, X, Y FROM GEM_OBJECT WHERE OBJ_UID == '08991839-dff5-4d24-b39a-9303d1f34dae'";

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getTestData >>"+ mSQLException.toString());
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
			Log.e(TAG, "getTestData >>"+ mSQLException.toString());
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
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}

	public Cursor getAllMaterialTechnologies(String attributeScope)
	{
		try
		{
			String sql = "select DESCRIPTION, ATTRIBUTE_VALUE, ATTRIBUTE_SCOPE from DIC_ATTRIBUTE_VALUE WHERE ATTRIBUTE_TYPE = 'MTECH' and ATTRIBUTE_SCOPE = '"+ attributeScope + "'";

			Log.e(TAG, "running sql " + sql);

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur!=null)
			{
				mCur.moveToNext();
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
			Log.e(TAG, "getAllLLRS >>"+ mSQLException.toString());
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
			Log.e(TAG, "getAllLLRSD >>"+ mSQLException.toString());
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
			Log.e(TAG, "getAllLLRS >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}


	//Functiones like this will be deprected as they use a superdictionary
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
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
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
				Log.e(TAG, "mCur not null " + mCur.getColumnCount());
			}
			return mCur;
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "getAllMaterialTypes >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}



	public void insertTestData()
	{		
		try
		{					

			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();
			UUID id = UUID.randomUUID();
			cv.put("OBJ_UID", id.toString());
			cv.put("PROJ_UID", "dummy proj string");
			cv.put("OBJ_SCOPE", "BUILD");
			cv.put("X", "1234");
			cv.put("Y", "4567");
			cv.put("EPSG_CODE", "4326");	
			cv.put("SOURCE", "FIELD");	
			mDb.insert("GEM_OBJECT", null, cv);

		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
	}




	public void insertGemData(GEMSurveyObject gemGlobalVariables)
	{		
		Log.d(TAG, "Trying to insert Gem data");
		
		
		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();

			UUID id = UUID.randomUUID();			
			
			cv.put("OBJ_UID", gemGlobalVariables.getUid());
			 
			cv.put("PROJ_UID", id.toString()); //This should be a proj uid, define in a preferences thing
			cv.put("X", Double.toString(gemGlobalVariables.getLon()));
			cv.put("Y",  Double.toString(gemGlobalVariables.getLat()));
			//cv.put("EPSG_CODE", "4326"); //Should get this from the db
			cv.put("SOURCE", "FIELD");			

			HashMap<String,String> keyVals = gemGlobalVariables.getKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				cv.put(key, value);
			}

			
			Log.d(TAG, "GEM ContentValues: " + cv.toString());
			mDb.insert("GEM_OBJECT", null, cv);
			String feedbackMsg = "Survey Data saved\n " + "LatLon: " + Double.toString(gemGlobalVariables.getLat()) + ", " + Double.toString(gemGlobalVariables.getLon());
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			throw mSQLException;
		}

		
		ArrayList mediaList = gemGlobalVariables.getMediaDetailKeyValuePairsMap();
		Log.d(TAG, "MEDIA DETAIL LIST: " + mediaList.size());
		
		for (int i = 0; i < mediaList.size(); i++) {
			Log.d(TAG, "Trying to insert Media detail data");
			try
			{
				
				HashMap map = (HashMap) mediaList.get(i);
				//Cursor mCur = mDb.execSQL(sql, null);
				
				ContentValues cv = new ContentValues();		
				cv.put("GEMOBJ_UID", gemGlobalVariables.getUid());
				
				HashMap<String,String> keyVals = map;
				for (Map.Entry<String, String> entry : keyVals.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					Log.d(TAG, "GEM Media key: " + key.toString());
					Log.d(TAG, "GEM Media val: " + value.toString());
					cv.put(key, value);
				}
				Log.d(TAG, "GEM Media DetailValues: " + cv.toString());
				Toast.makeText(this.mContext.getApplicationContext(), "Photos were sucessfully linked in the database", Toast.LENGTH_LONG).show();
				mDb.insert("MEDIA_DETAIL", null, cv);	
							
			}
			catch (SQLException mSQLException) 
			{
				Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
				Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the Media Detail", Toast.LENGTH_LONG).show();
				throw mSQLException;
			}
		}
		
		
		Log.d(TAG, "Trying to insert GED data");
		
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
				cv.put(key, value);
			}

			
			Log.d(TAG, "GED ContentValues: " + cv.toString());
			mDb.insert("GED", null, cv);
			String feedbackMsg = "GED Data saved\n ";
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			throw mSQLException;
		}
		
		
		Log.d(TAG, "Trying to insert CONSEQUENCES data");
		
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
				cv.put(key, value);
			}

			
			Log.d(TAG, "CONSQ ContentValues: " + cv.toString());
			mDb.insert("CONSEQUENCES", null, cv);
			String feedbackMsg = "CONSEQUENCES Data saved\n";
			Toast.makeText(this.mContext.getApplicationContext(), feedbackMsg , Toast.LENGTH_LONG).show();
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			Toast.makeText(this.mContext.getApplicationContext(), "There was a problem saving the GEM survey data", Toast.LENGTH_LONG).show();
			throw mSQLException;
		}
		
		
		
		

		gemGlobalVariables.getKeyValuePairsMap().clear();
		
		/*
		Log.d(TAG, "Trying to insert Media detail data");
		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();					
			//UUID id = UUID.randomUUID();					
			//cv.put("MEDIA_UID", "uid-val"); //This should be a proj uid, define in a preferences thing
			cv.put("GEMOBJ_UID", "gemobjid-val");
			cv.put("MEDIA_TYPE",  "PHOTO");
			//cv.put("EPSG_CODE", "4326"); //Should get this from the db
			cv.put("FILENAME", "filenameOfPhoto");			
			cv.put("COMMENTS", "Dummy media comments");	

			HashMap<String,String> keyVals = gemGlobalVariables.getMediaDetailKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				cv.put(key, value);
			}
			Log.d(TAG, "GEM Media DetailValues: " + cv.toString());
			mDb.insert("MEDIA_DETAIL", null, cv);					
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}
*/

	}

	/*

	public void insertMediaDetail(GEMSurveyObject gemGlobalVariables)
	{		
		Log.d(TAG, "Trying to insert Media detail data");
		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();					
			//UUID id = UUID.randomUUID();					
			//cv.put("MEDIA_UID", "uid-val"); //This should be a proj uid, define in a preferences thing
			cv.put("GEMOBJ_UID", "gemobjid-val");
			cv.put("MEDIA_TYPE",  "PHOTO");
			//cv.put("EPSG_CODE", "4326"); //Should get this from the db
			cv.put("FILENAME", "filenameOfPhoto");			
			cv.put("COMMENTS", "Dummy comment information");	

			HashMap<String,String> keyVals = gemGlobalVariables.getMediaDetailKeyValuePairsMap();
			for (Map.Entry<String, String> entry : keyVals.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				cv.put(key, value);
			}
			Log.d(TAG, "GEM Media DetailValues: " + cv.toString());
			mDb.insert("MEDIA_DETAIL", null, cv);					
		}
		catch (SQLException mSQLException) 
		{
			Log.e(TAG, "insertTestData >>"+ mSQLException.toString());
			throw mSQLException;
		}

	}

*/


	public void copyDataBaseToSdCard()
	{		
		Log.e(TAG, "copying to sdcard");

		try {
			mDbHelper.copyDataBaseToSdCard();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "copying to sd card problem >>"+ e.toString());
			e.printStackTrace();
		}
	}

}
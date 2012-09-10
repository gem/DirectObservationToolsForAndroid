package com.idctdo.android;
import java.io.IOException;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class GemDbAdapter 
{
	public boolean DEBUG_LOG = false; 

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





	public Cursor getTestData()
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



	public Cursor getGemObjectsForMap()
	{
		try
		{
			String sql ="SELECT OBJECT_UID, X, Y FROM GEM_OBJECT";

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
			String sql ="select OBJECT_UID, X, Y FROM GEM_OBJECT WHERE OBJECT_UID == '08991839-dff5-4d24-b39a-9303d1f34dae'";

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
			cv.put("OBJECT_UID", id.toString());
			cv.put("PROJECT_UID", id.toString());
			cv.put("OBJECT_SCOPE", "BUILD");
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
		Log.e(TAG, "Trying to insert Gem data");
		try
		{								
			//Cursor mCur = mDb.execSQL(sql, null);
			ContentValues cv = new ContentValues();
			UUID id = UUID.randomUUID();
			cv.put("OBJECT_UID", id.toString());
			cv.put("PROJECT_UID", id.toString());
			cv.put("OBJECT_SCOPE", "BUILD");
			cv.put("X", Double.toString(gemGlobalVariables.getLon()));
			cv.put("Y",  Double.toString(gemGlobalVariables.getLat()));
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
}
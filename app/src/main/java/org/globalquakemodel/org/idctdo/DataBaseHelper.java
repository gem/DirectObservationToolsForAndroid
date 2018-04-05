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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;




public class DataBaseHelper extends SQLiteOpenHelper
{	
	private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
	//destination path (location) of our database on device
	private static String DB_PATH = "/data/data/com.dbhelper/databases/"; 
	//private static String DB_NAME ="GEM_V001.s3db";// Database name
	//	private static String DB_NAME ="gem.db3";
	private static String DB_NAME;
	private SQLiteDatabase mDataBase;
	private final Context mContext;

	
	public DataBaseHelper(Context context) 
	{
		super(context, context.getString(R.string.db_name), null, 1);// 1? its Database Version
		DB_NAME = context.getString(R.string.db_name);
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		this.mContext = context;
	}   

	public void createDataBase() throws IOException
	{		
		//If database not exists copy it from the assets
		boolean mDataBaseExist = checkDataBase();
		if(!mDataBaseExist)
		{
			this.getReadableDatabase();
			this.close();
			try 
			{
				//Copy the database from assests
				copyDataBase();
				Log.d(TAG, "createDatabase database created");
			} 
			catch (IOException mIOException) 
			{
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}
	//Check that the database exists here: /data/data/your package/databases/Da Name
	private boolean checkDataBase()
	{
		File dbFile = new File(DB_PATH + DB_NAME);
		//Log.v("dbFile", dbFile + "   "+ dbFile.exists());
		return dbFile.exists();
	}

	//Copy the database from assets
	private void copyDataBase() throws IOException
	{
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer))>0)
		{
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	//Copy the database to sdcard
	//Pre-condition that the database in the package area
	public void copyDataBaseToSdCard() throws IOException
	{
		String backupDBPath = null;
		Log.d(TAG, "TRYING TO BACK DB TO SDCARD");
		try {
			//File sd = Environment.getExternalStorageDirectory();
			File sd = new File(Environment.getExternalStorageDirectory().toString()+"/idctdo/db_snapshots");
			sd.mkdirs();
			File data = Environment.getDataDirectory();
			Log.d(TAG, "BACKING UP FROM: " + data);
			Log.d(TAG, "BACKING UP TO: " + sd);
			if (sd.canWrite()) {
				Log.d(TAG, "CAN WRITE TO SD");
				String currentDBPath = DB_PATH + DB_NAME;
				Log.d(TAG, "currentDBPath: " + currentDBPath);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
				Date currentDate = new Date(System.currentTimeMillis());
				String currentDateandTime = sdf.format(currentDate);
				backupDBPath = "IDCTDO_db_snapshot_" + currentDateandTime.toString() + ".db3";
				File currentDB = new File(currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					Log.d(TAG, "CURRENTDB EXISTS");
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Log.d(TAG, "FINISHED BACKING UP DB");
//					Toast.makeText(this.mContext.getApplicationContext(), "Database snapshot created. Snapshot is located at: \n" + sd + "/" + backupDBPath , Toast.LENGTH_LONG).show();
					Toast.makeText(this.mContext.getApplicationContext(),  this.mContext.getString(R.string.titleDatabaseHelperDBShapshot) + " \n" + sd + "/" + backupDBPath , Toast.LENGTH_LONG).show();

				}
			}
		} catch (Exception e) {
			Log.d(TAG, "PROBLEM BACKING UP DB");
		}

	}


	//Open the database, so we can query it
	public boolean openDataBase() throws SQLException
	{
		String mPath = DB_PATH + DB_NAME;
		Log.d(TAG, "opening db: " + mPath);
		mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		//mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		Log.d(TAG, "opened db");
		return mDataBase != null;

	}
	
	public boolean deleteRecords() throws SQLException
	{
		String mPath = DB_PATH + DB_NAME;
		Log.d(TAG, "deleting records");
		mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		mDataBase.delete("GEM_OBJECT", null, null);
		mDataBase.delete("GEM_PROJECT", null, null);
		mDataBase.delete("GED", null, null);
		mDataBase.delete("CONSEQUENCES", null, null);
		mDataBase.delete("MEDIA_DETAIL", null, null);
		mDataBase.delete("SETTINGS", null, null);
		//mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
//		Toast.makeText(this.mContext.getApplicationContext(), "Deleting records", Toast.LENGTH_LONG).show();
		Toast.makeText(this.mContext.getApplicationContext(), this.mContext.getString(R.string.titleDatabaseHelperDeleting), Toast.LENGTH_LONG).show();
		mDataBase.close();
		return true;

	}

	@Override
	public synchronized void close() 
	{
		if(mDataBase != null)
			mDataBase.close();
		super.close();
	}
	

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}

package com.idctdo.android;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class EQForm_About extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        // Read raw file into string and populate TextView
        InputStream iFile = getResources().openRawResource(R.raw.about);
        try {
            TextView aboutText = (TextView) findViewById(R.id.about_text);
            String strFile = inputStreamToString(iFile);
            aboutText.setText(strFile);
        } catch (Exception e) {
        }
    }

    /**
     * Converts an input stream to a string
     * 
     * @param is
     *            The {@code InputStream} object to read from
     * @return A {@code String} object representing the string for of the input
     * @throws IOException
     *             Thrown on read failure from the input
     */
    public String inputStreamToString(InputStream is) throws IOException {
        StringBuffer sBuffer = new StringBuffer();
        DataInputStream dataIO = new DataInputStream(is);
        String strLine = null;
        while ((strLine = dataIO.readLine()) != null) {
            sBuffer.append(strLine + "\n");
        }
        dataIO.close();
        is.close();
        return sBuffer.toString();
    }
    public void HandleButton(View v){
    	if (v.getId() == R.id.back_about){
    		Intent Back = new Intent (EQForm_About.this, EQForm_MainMenu.class);
    		startActivity(Back);
    	}
    }
}
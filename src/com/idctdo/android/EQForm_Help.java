package com.idctdo.android;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EQForm_Help extends EQForm {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        // Read raw file into string and populate TextView
        InputStream iFile = getResources().openRawResource(R.raw.help_text_new);
        try {
            TextView helpText = (TextView) findViewById(R.id.help_text);
            String strFile = inputStreamToString(iFile);
            helpText.setText(strFile);

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
    	if (v.getId() == R.id.back_help){
    		Intent Back = new Intent (EQForm_Help.this, EQForm_MainMenu.class);
    		startActivity(Back);
    	}
    }
}
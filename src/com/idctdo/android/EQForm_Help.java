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



import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EQForm_Help extends Activity {
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

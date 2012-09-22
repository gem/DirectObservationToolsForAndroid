package com.idctdo.android;



import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SelectedAdapter extends ArrayAdapter<DBRecord>{

	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	private int highlightColour;
	public SelectedAdapter(Context context, int textViewResourceId,ArrayList<DBRecord> objects) {

		super(context, textViewResourceId, objects);
		int highlightColour = context.getResources().getColor(R.color.gem_blue);
		Log.d("IDCT", "highlight colour: " + highlightColour);
	}

	public void setSelectedPosition(int pos){
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition(){
		return selectedPos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		// only inflate the view if it's null
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.selected_row, null);
		}

		// get text view
		TextView label = (TextView)v.findViewById(R.id.txtExample);

		//TextView label2 = (TextView)v.findViewById(R.id.txtExample);		//parent.addView(label2);	
		
		
		
		
		// change the row color based on selected state
		if(selectedPos != -1){ 
			if(selectedPos == position){
				//label.setBackgroundColor(label.getContext().getResources().getColor(R.color.gem_blue));
				label.setBackgroundColor(Color.CYAN);

			}else {
				//label.setBackgroundColor(label.getContext().getResources().getColor(R.color.light_green));
				label.setBackgroundColor(Color.WHITE);
			}
		} else {
			label.setBackgroundColor(Color.WHITE);
		}

		// label.setText(this.getItem(position).toString());

		// to use something other than .toString()

		DBRecord myObj = (DBRecord)this.getItem(position);

		label.setText(myObj.getAttributeDescription());

		return(v);
	}
}
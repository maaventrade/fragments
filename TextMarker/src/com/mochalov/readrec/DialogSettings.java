package com.mochalov.readrec;

import java.util.ArrayList;

import alex.xolo.readrec.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogSettings {
	ActivityMain context;
	int textSize = 0;
	boolean showNums = false;
	boolean showLines = false;
	boolean showBGColors = false;
	
	public DialogSettings(ActivityMain context, 
			boolean showNums) {
		super();
		this.context = context;
		this.textSize = Vars.textSize;
		this.showNums = showNums;
		this.showLines = Vars.showLines;
		this.showBGColors = Vars.showBGColors;
	}

	public void execute(){
		 final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		 
		 dialog.setTitle(context.getResources().getString(R.string.dialog_settings));

		 LinearLayout layout = new LinearLayout(context);
		 layout.setOrientation(LinearLayout.VERTICAL);

		 CheckBox checkBox = new CheckBox(context);
		 checkBox.setText(context.getResources().getString(R.string.dialog_cut_fragment_num));
		 checkBox.setChecked(showNums);
		 checkBox.setOnClickListener(new CheckBox.OnClickListener() {                  
				@Override
				public void onClick(android.view.View v) {
					showNums = ! showNums;
				}})
		;
		 layout.addView(checkBox);

		 checkBox = new CheckBox(context);
		 checkBox.setText(context.getResources().getString(R.string.dialog_settings_show_lines));
		 checkBox.setChecked(showLines);
		 checkBox.setOnClickListener(new CheckBox.OnClickListener() {                  
				@Override
				public void onClick(android.view.View v) {
					showLines = ! showLines;
				}})
		;
		 layout.addView(checkBox);

		 checkBox = new CheckBox(context);
		 checkBox.setText(context.getResources().getString(R.string.dialog_settings_show_bgcolors));
		 checkBox.setChecked(showBGColors);
		 checkBox.setOnClickListener(new CheckBox.OnClickListener() {                  
				@Override
				public void onClick(android.view.View v) {
					showBGColors = ! showBGColors;
				}})
		;
		 layout.addView(checkBox);
		 
		 TextView txt = new TextView(context);
		 txt.setText(context.getResources().getString(R.string.dialog_settings_text_size));
		 layout.addView(txt);
		 
		 final Spinner spinner = new Spinner(context);
		 
		 String sizes[] = {"16","17","18","19","20", "21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40"};

		 ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item, sizes);
		 spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		 spinner.setAdapter(spinnerArrayAdapter);
		 int spinnerPosition = spinnerArrayAdapter.getPosition(""+textSize);
		 spinner.setSelection(spinnerPosition);		 
		 
		 layout.addView(spinner);

		 dialog.setView(layout);
		 
		 dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {

			    	   int textSizeNew = Integer.parseInt(spinner.getSelectedItem().toString());
			    		
				       context.setShowNums(showNums);
				       context.setShowLines(showLines);
				       context.setShowBGColors(showBGColors);
				       
				       if (textSizeNew != textSize)
				    	   context.setTextSize(textSizeNew);
				    	   
			    }
			});
		 dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});

		 dialog.show();		 
		 }
	}

package com.mochalov.readrec;

import alex.xolo.readrec.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogCutFragment {
	ActivityMain context;
	
	public DialogCutFragment(ActivityMain context){
		super();
		this.context = context;
	}	
	
	public void execute(int cutTo){
		 final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		 final int cutFrom = 0;
		 
        dialog.setTitle(context.getResources().getString(R.string.dialog_cut_fragment));

		 LinearLayout layout = new LinearLayout(context);
		 layout.setOrientation(LinearLayout.VERTICAL);

		 TextView txt = new TextView(context);
		 txt.setText(context.getResources().getString(R.string.dialog_cut_fragment_from));
		 layout.addView(txt);
		 
		 final EditText editTextFrom = new EditText(context);
		 editTextFrom.setText(""+cutFrom);
		 layout.addView(editTextFrom);

		 txt = new TextView(context);
		 txt.setText(context.getResources().getString(R.string.dialog_cut_fragment_to));
		 layout.addView(txt);
		 
		 final EditText editTextTo = new EditText(context);
		 editTextTo.setText(""+cutTo);
		 layout.addView(editTextTo);
		 
		 dialog.setView(layout);
		 
		 dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				       int cutFrom = Integer.parseInt(editTextFrom.getText().toString());
				       int cutTo = Integer.parseInt(editTextTo.getText().toString());
				       context.cutFragment(cutFrom, cutTo);
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

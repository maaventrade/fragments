package com.mochalov.readrec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

public class DialogFileSave {
	ActivityMain context;

	public DialogFileSave(ActivityMain context){
		super();
		this.context = context;
	}	
	
	public void execute(String name){
   	 if (name.endsWith(".zip"))
	    	 	name = name.substring(0,(name.lastIndexOf(".")));
   	 name = name.substring(0,(name.lastIndexOf(".")))+".xml";

   	 if (name.lastIndexOf("/") >= 0)
   		 name = name.substring(name.lastIndexOf("/")+1);
   	 
	 AlertDialog.Builder builder = new AlertDialog.Builder(context);
	 builder.setTitle("ָלÿ פאיכא");

	 final EditText input = new EditText(context);
	 input.setText(name);
	 input.setInputType(InputType.TYPE_CLASS_TEXT);
	 builder.setView(input);

	 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
	     @Override
	     public void onClick(DialogInterface dialog, int which) {
	    	 context.saveFile(input.getText().toString()); 
	     }
	 });
	 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	     @Override
	     public void onClick(DialogInterface dialog, int which) {
	         dialog.cancel();
	     }
	 });

	 builder.show();	    	 
   	 
		
	}
	
}

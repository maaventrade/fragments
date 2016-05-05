package com.mochalov.readrec;

import alex.xolo.readrec.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Setup extends Activity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        
        Intent myIntent = getIntent(); 
        
    	Spinner spinner = (Spinner)findViewById(R.id.spinner1);
    	ArrayAdapter adapter = (ArrayAdapter)spinner.getAdapter(); 
    	int spinnerPosition = adapter.getPosition(myIntent.getStringExtra("textSize"));
    	spinner.setSelection(spinnerPosition);
    	
		Button btn = (Button)findViewById(R.id.dialogButtonOK);
	    btn.setOnClickListener(new Button.OnClickListener() {                  
			@Override
			public void onClick(android.view.View v) {
		    	Spinner spinner = (Spinner)findViewById(R.id.spinner1);
		    	
            	Intent intent = new Intent();
            	
            	//Log.d("TS", msg)
            	intent.putExtra("textSize", Integer.parseInt(spinner.getSelectedItem().toString()));
                setResult(RESULT_OK, intent);
                finish();
		    	
			}})
			;
        
	}
	
}


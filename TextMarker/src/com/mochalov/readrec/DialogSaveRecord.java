package com.mochalov.readrec;

import alex.xolo.readrec.R;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import com.mochalov.files.*;

import java.io.*;
//import com.mochalov.readrec.*;

public class DialogSaveRecord extends Dialog
 {
	Context context;
	String filePath = "";
	Dialog dialog;
	String mFileName;
	int mIndex;
	Rec currentRec = null;

	public DialogSaveRecord(Context context) {
		super(context);
		this.context = context;
		dialog = this;
	}

	public void execute(String fileName){
	
		setTitle(context.getResources().getString(R.string.action_save_audio));

 		setContentView(R.layout.edit_rec);

	  	TextView d = (TextView)findViewById(R.id.editRecName);
		d.setText(fileName);

		d = (TextView)findViewById(R.id.editRecTime);
		d.setText(Vars.msToDateTime(Media.getTimeStarting()));
		
		d = (TextView)findViewById(R.id.editRecDuration);
		d.setText(Media.msToString(Media.getDuration()));
		
		d = (TextView)findViewById(R.id.editRecInfo1);
		currentRec = RecsArray.getCurrent();
		if (currentRec ==  null){
			mIndex = getIndex(Vars.APP_FOLDER, fileName);
			d.setText(context.getResources().getString(R.string.info_new_audio));
		} else {
			mIndex = currentRec.mIndex;
			d.setText(context.getResources().getString(R.string.info_set_info_audio));
		}
		
		filePath = Vars.APP_FOLDER + "/" + fileName + mIndex + ".mp3";
		mFileName = fileName;
		
		d = (TextView)findViewById(R.id.editRecIndex);
		d.setText("" + mIndex);
		
		Button b = (Button)findViewById(R.id.editRecButtonOk);
		b.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					if (Media.getDuration() == 0){
						Vars.showMessage(context, R.string.error_empty_record);
						return;
					}
					try {
						copy(Media.getTempMediaFileName(), 
							filePath);

						EditText t1 = (EditText)findViewById(R.id.editRecNote);
						EditText t2 = (EditText)findViewById(R.id.editRecLabel);
						
						RecsArray.addRec(mFileName, 
							mIndex,
							Media.getTimeStarting(),
							Media.getDuration(),
							t1.getText().toString(),
							t2.getText().toString()
							);
						
						dialog.cancel();
					} catch (IOException e) {
						Toast.makeText(context,
									   context.getResources().getString(R.string.error_saving_audio)+" "+ e.toString(), Toast.LENGTH_LONG)
							.show();
						e.printStackTrace();
					}
				}
			});
		b = (Button)findViewById(R.id.editRecButtonCancel);
		b.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					dialog.cancel();
				}
			});
		show();
	   
	}


	private static void copy(String src, String dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	private int getIndex(String APP_FOLDER, String fileName)
	{
		for (int i = 1; i < 999; i++){
			File file = new File(APP_FOLDER + "/" + fileName + i + ".mp3");
			if(!file.exists())                        
				return i;      
		}
		return 1;
	}
}

package com.mochalov.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import com.mochalov.data.Data;

import alex.xolo.readrec.R;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 *
 * @author d920maal
 * FileSaver saves array of strings and array of marks to xml
 *
 */
public class FileSaver {
	private static Context mContext;
	
	public FileSaver(Context context){
        super();
        mContext = context;
	}

	/**
	 * Save strings and marks to file. 
	 * 
	 * @param name - name of the saved file
	 * @param APP_FOLDER - path to saved file
	 */
	public static void save(String name, String APP_FOLDER){
		try {
			
			File file = new File(APP_FOLDER);
			if(!file.exists()){                          
				file.mkdirs();                  
				}
			
			file = new File(APP_FOLDER, name);
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n");
			writer.write("<body>"+"\n");
			writer.write("<section>"+"\n");
			
			String S = "<p>";
			// Save strings and marks 
			S = Data.stringsToString(writer, S);
			// Save the rest
			if (!S.equals(""))
				writer.write(S+"</p>"+"\n");
			
			writer.write("</section>"+"\n");
			writer.write("</body>"+"\n");
					
			writer.close();
		} catch (IOException e) {
		     Toast.makeText(mContext, mContext.getResources().getString(R.string.error_save_file)+" "+e.toString() , Toast.LENGTH_LONG).show();
		}
	}	
	
}

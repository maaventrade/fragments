package com.mochalov.readrec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import alex.xolo.readrec.R;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Params {

public String loadParams(ActivityMain context, String EXTERNAL_STORAGE_DIRECTORY, String PROGRAMM_FOLDER, int[] params, boolean[] params1) {
	String fileName = "";
	
	File file = new File(EXTERNAL_STORAGE_DIRECTORY, PROGRAMM_FOLDER);
	if(!file.exists()){                          
		file.mkdirs();                  
		}
	
	String pathTmpFile = file.getAbsolutePath() + "/readrec.ini";
	try {
		file = new File(pathTmpFile);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		line = reader.readLine();
		while (line != null){
			if (line.contains("file:")){
				fileName = line.substring(line.indexOf(':')+1);
			}
			else if (line.contains("textSize:")){
				params[0] = Integer.parseInt(line.substring(line.indexOf(':')+1));
			}
			else if (line.contains("showLines:")){
				params1[0] = Boolean.parseBoolean(line.substring(line.indexOf(':')+1));
			}
			else if (line.contains("showBGColors:")){
				params1[1] = Boolean.parseBoolean(line.substring(line.indexOf(':')+1));
			}
			
			line = reader.readLine();
		}
		reader.close();
	} catch (IOException e) {
	     Toast.makeText(context, context.getResources().getString(R.string.error_load_ini) , Toast.LENGTH_LONG).show();
	}
	return fileName;
}

public void saveParams(Context context, String fileName, String EXTERNAL_STORAGE_DIRECTORY, String PROGRAMM_FOLDER, int textSize, boolean showLines, boolean showBGColors) {
	File file = new File(EXTERNAL_STORAGE_DIRECTORY, PROGRAMM_FOLDER);

	if(!file.exists()){                          
		file.mkdirs();                  
		}
	String pathTmpFile = file.getAbsolutePath() + "/readrec.ini";
	try {
		file = new File(pathTmpFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write("file:"+fileName+"\r\n"); // путь к файлу
		writer.write("textSize:"+textSize+"\r\n"); 
		writer.write("showLines:"+showLines+"\r\n"); 
		writer.write("showBGColors:"+showBGColors+"\r\n"); 
		writer.close();
	} catch (IOException e) {
	     Toast.makeText(context, context.getResources().getString(R.string.error_save_ini) , Toast.LENGTH_LONG).show();
	}
}
}
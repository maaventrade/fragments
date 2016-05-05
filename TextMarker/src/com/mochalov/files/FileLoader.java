package com.mochalov.files;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mochalov.data.Data;
import com.mochalov.data.Data.MarkTypes;
import com.mochalov.readrec.ActivityMain;
import com.mochalov.readrec.Vars;

import alex.xolo.readrec.R;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
/**
* Class FileLoader has methods to read text files (txt, zip, xml, fb2) 
* to the string array. Class extracts the marks from the xml file to
* the array Marks.
*
**/
public class FileLoader {
	private static Context mContext;
	public static OnEventListener listener = null;
	public interface OnEventListener {
		// This method links the Elements of the Marks with lines
		void callbackCalcPolyLines(); 
	} 
	/**
	* name - is the name(path) of the file to load
	* strings - destination for the file strings
	* text - if text is not empty, strings array must be loaded from the text, not from the file
	* context is context
	*
	**/
	public static void load(String name, ArrayList<String> strings, String text, Context context){
        mContext = context;
        
		strings.clear();
		Data.clearMarks();
		
		try{
			// Select the loading method
			if (text != null)
				loadStr(strings, text);
			else if (name.endsWith(".txt")) loadTXT(name, strings);
			else if (name.endsWith(".fb2.zip")) loadZIP(name, strings);
			else if (name.endsWith(".fb2") || name.endsWith(".xml")) loadXML(name, strings);
		} catch (Exception e) {
			Toast.makeText(mContext,
						   mContext.getResources().getString(R.string.error_loading)+" "+ e.toString(), Toast.LENGTH_LONG)
				.show();
		}
		if (listener != null)
			listener.callbackCalcPolyLines();
	}

	/**
	* Load from text to the strings array
	* strings - destination for the file strings
	**/
	private static void loadStr(ArrayList<String> strings, String text)
	{
		String lines[] = text.split("\\r?\\n");
		for (String s: lines)
			strings.add(s);
	}
	
	/**
	*  Extract file from zip to the temporary file
	* and call the method loadXML to read file.
	* Usually files *.fb2.zip
	*
	* name - is the name(path) of the file to load
	* strings - destination for the file strings
	**/
	public static void loadZIP(String name,  ArrayList<String> strings){		
		try {
			int BUFFER_SIZE = 1024;
			byte[] buffer = new byte[BUFFER_SIZE];
			int size;
			// Name of the temporary file
			String nameUnzipped = Vars.APP_FOLDER+"/tempUnzipped.xml";
			
			FileInputStream fin = new FileInputStream(name);
			ZipInputStream zin = new ZipInputStream(fin);
			
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					// No process directoried
				} else {
					FileOutputStream fout = new FileOutputStream(nameUnzipped);
					BufferedOutputStream bout = new BufferedOutputStream(fout, BUFFER_SIZE);
					
					while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                        fout.write(buffer, 0, size);
                    }
					
					zin.closeEntry();
					bout.flush();
					bout.close(); 
					break; // Only first file from zip is processed
				}
			}
			zin.close();
			// Load from the temporary file to the string array
			loadXML(nameUnzipped, strings);
		} catch (Exception t) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.error_load_zip)+" "+ t.toString(), Toast.LENGTH_LONG)
					.show();
		}
	}	
	
	/**
	 * Load from the text file to the strings array
	 * strings - destination for the file strings
	 * name - is the name(path) of the file to load
	 **/
	public static void loadTXT(String name,  ArrayList<String> strings){
		BufferedReader reader;
		
		try {
			int c[] = {0,0,0};
			
			FileInputStream fis = new FileInputStream(name);
			// Get file encoding
			if (fis.available() >= 2){
				c[0] = fis.read();
				c[1] = fis.read();
			}
			if (fis.available() >= 3)
				c[2] = fis.read();
			
			if (c[0] == 255 && c[1] == 254 )
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "UTF-16"));
			else if (c[0] == 239 && c[1] == 187 && c[2] == 191 )
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "UTF-8"));
			else
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "windows-1251"));
			
			String line = reader.readLine();
			while (line != null){
				strings.add(line);
				line = reader.readLine();
			}
			
			reader.close();
		} catch (IOException t) {
			Toast.makeText(mContext,
						   mContext.getResources().getString(R.string.error_loading)+" "+ t.toString(), Toast.LENGTH_LONG)
				.show();
		}
	}	

	/**
	* Load xml file to the strings array and extract marks to the Marks array
	* strings - destination for the file strings
	* name - is the name(path) of the file to load
	**/
	public static void loadXML(String name,  ArrayList<String> strings){
		Log.d("XML", "START");
		try {
			BufferedReader reader;
			BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
			
			String line = rd.readLine();
			
			rd.close();
			// Get file encoding
			if (line.toLowerCase().contains("windows-1251"))
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "windows-1251")); //Cp1252
			else if (line.toLowerCase().contains("utf-8"))
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "UTF-8")); 
			else if (line.toLowerCase().contains("utf-16"))
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "utf-16"));
			else
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			factory.setNamespaceAware(true);         
			XmlPullParser parser = factory.newPullParser();
			
			parser.setInput(reader);
			
			boolean title = false;
			boolean section = false;
			boolean body = false;
			boolean a = false;
			boolean aref = false;
			boolean aUp = false; // The mark of starting voice up
			boolean aDn = false; // The mark of starting voice down
			boolean aUpStop = false; // The mark of finish voice up
			boolean aDnStop = false; // The mark of finish voice down
			
			int eventType = parser.getEventType();         
			while (eventType != XmlPullParser.END_DOCUMENT) {          
				if(eventType == XmlPullParser.START_DOCUMENT) {              
					} 
				else if(eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("section")) section = true;
					if (parser.getName().equals("title")) title = true;
					if (parser.getName().equals("up")){
						aUp = true;
					}
					if (parser.getName().equals("dn")){
						aDn = true;
					}
					if (parser.getName().equals("up_stop")) aUpStop = true;
					if (parser.getName().equals("dn_stop")) aDnStop = true;
					if (parser.getName().equals("body")){
						body = true;
						if (parser.getAttributeValue(null, "name") != null)
							if (parser.getAttributeValue(null, "name").equals("notes"))
								body = false;
					}
					if (parser.getName().equals("a")) a = true;
					} 
				else if(eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("section")) section = false;
					if (parser.getName().equals("title")) title = false;
					if (parser.getName().equals("body")) body = false;
					if (parser.getName().equals("fv") ){
					};
					if (parser.getName().equals("a")){
						a = false;
						aref = true;
					}
				}	
				else if(eventType == XmlPullParser.TEXT) {
					if (body && section && !title && !a){
						String S = parser.getText();
						if (! S.equals("\n")){
							if (aref){
								if (strings.size() > 0) 
									strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
								else strings.add(S);
								aref = false;
							}	
							else if (aUp){
								Data.addMark(0,0,strings.size()-1, strings.get(strings.size()-1).length(), MarkTypes.Up, false);
								strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
								aUp = false;
							}	
							else if (aDn){
								Data.addMark(0,0,strings.size()-1, strings.get(strings.size()-1).length(), MarkTypes.Dn, false);
								strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
								aDn = false;
							} else if (aUpStop){
								Data.addMark(0,0,strings.size()-1, strings.get(strings.size()-1).length(), MarkTypes.Up, true);
								strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
								aUpStop = false;
							}	
							else if (aDnStop){
								Data.addMark(0,0,strings.size()-1, strings.get(strings.size()-1).length(), MarkTypes.Dn, true);
								strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
								aDnStop = false;
							}
							else
								strings.add(S);
						} else strings.add("");
					}
				}          
				eventType = parser.next();         
			}         
		} catch (Throwable t) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.error_load_xml)+" "+ t.toString(), Toast.LENGTH_LONG)
					.show();
		}
		Log.d("XML", "END");
	}

	

}

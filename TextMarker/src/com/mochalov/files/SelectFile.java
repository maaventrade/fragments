package com.mochalov.files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import alex.xolo.readrec.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

public class SelectFile extends Activity {
	ListView list_files; // отображаемый список файлов ФС

	ArrayList<String> last_files;
	
	ArrayList<String> listItems=new ArrayList<String>(); 
	ArrayList<File> listFiles=new ArrayList<File>(); 
	MyArrayAdapter<String> adapter;
	File dir;
	String fileExt[] = {};
	Activity a;
	
	class FileNameComparator implements Comparator<File> {   
		public int compare(File fileA, File fileB) {
			if ((fileA.isDirectory())&&(!fileB.isDirectory()))
				return -1;
			else if ((!fileA.isDirectory())&&(fileB.isDirectory()))
				return 1;
			else
				return fileA.getName().compareToIgnoreCase(fileB.getName());
			}
	}
	
	private class MyArrayAdapter<String> extends ArrayAdapter{      
		MyArrayAdapter() {      
			super(SelectFile.this, R.layout.raw,R.id.weekofday,listItems);  
		}    
		public View getView(int position, View convertView, ViewGroup parent) {      
			View row = super.getView(position, convertView, parent);      
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			if (listItems.get(position).equals(".."))
				icon.setImageResource(R.drawable.folder);
			else if (listFiles.get(position).isDirectory())
				icon.setImageResource(R.drawable.folder);
			else
				icon.setImageResource(R.drawable.voidf);
			return (row);   
			} 	
		}	
	
	void readDir(String path){
		listItems.clear();
		listFiles.clear();
        dir = new File(path); 
        File[] files = dir.listFiles();
        if (files != null )
            for (int i=0; i<files.length; i++){
            	boolean addFile = false;
            	if (files[i].isDirectory()) addFile = true;
            	else if (fileExt.length == 0) addFile = true;
            	else for (int j=0; j<fileExt.length; j++)
            		if (files[i].getName().endsWith(fileExt[j])){
            			addFile = true;
            			break;
            		}
            	if (addFile) listFiles.add(files[i]);
        	}	
        FileNameComparator fnc = new FileNameComparator();
        Collections.sort(listFiles, fnc);

        for (int i=0; i<listFiles.size(); i++)
    		listItems.add(listFiles.get(i).getName());
		
		listFiles.add(0,null);  
		listItems.add(0,"..");  

        this.setTitle(dir.toString());
        
        adapter = new MyArrayAdapter<String>(); 
        list_files.setAdapter(adapter);
	}
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectfile);
    
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        a = this;
        
        Intent myIntent = getIntent(); 
        
        // В первой закладке - выбор файла из файловой системы 
        list_files=(ListView)findViewById(R.id.FileList);
        String initPath = myIntent.getStringExtra("initPath"); 
        fileExt = myIntent.getStringArrayExtra("fileExt");
        
		File file = new File(initPath);                                    
		if (file.exists()) readDir(initPath);
		else readDir("/");
        
        list_files.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
            	String selectedFile = listItems.get(position);
            	String currentdDir = dir.toString();
            	
            	if (selectedFile.equals("..")){
                	int slashposition= currentdDir.lastIndexOf("/");
                	if (slashposition == 0)
                		readDir("/");
                	else if (slashposition > 0)
                        readDir(currentdDir.substring(0,slashposition));
            	}
            	else if (listFiles.get(position).isDirectory()){
                    readDir(listFiles.get(position).getAbsolutePath());
            	}
            	else{
                	Intent intent = new Intent();
                	intent.putExtra("returnedData", listFiles.get(position).getAbsolutePath()); //
                    setResult(RESULT_OK, intent);
                    finish();
            	}
            } 
        }); 
	}
}

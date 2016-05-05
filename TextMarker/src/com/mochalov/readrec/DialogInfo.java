package com.mochalov.readrec;

import java.util.ArrayList;

import alex.xolo.readrec.R;
import alex.xolo.readrec.R.color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogInfo {
	ActivityMain context;
	String fileName = "";
	
	public DialogInfo(ActivityMain context, String fileName) {
		super();
		this.context = context;
		this.fileName = fileName;
	}

	public void execute(){
		 final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		 
		 dialog.setTitle(context.getResources().getString(R.string.dialog_info));

		 LinearLayout layout = new LinearLayout(context);
		 layout.setOrientation(LinearLayout.VERTICAL);

		 String text = "Автор: <u>Алексей Мочалов</u><p>";
		 text = text + "Сайт приложения: <a href=http://maaventrade.livejournal.com/2091.html>maaventrade.livejournal.com</a><p>";
		 
		 PackageInfo pInfo;
		 try {
			 pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			 text = text + "Версия приложения: <u>"+pInfo.versionName+"</u><p>";
		 } catch (NameNotFoundException e) {
			 text = text + "Версия приложения: <u>1</u><p>";
		 }
		 
		 text = text + "В приложении использована идея разметки текста с сайта <a href=http://metapractice.livejournal.com>metapractice.livejournal.com</a>";
		 
		 Spanned sp = Html.fromHtml(text);
		 
		 TextView txt = new TextView(context);
		 txt.setText(sp);
		 txt.setClickable(true);
		 txt.setMovementMethod (LinkMovementMethod.getInstance());

		 layout.addView(txt);

		 dialog.setView(layout);
		 
		 dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    	   
			    }
			});
		 dialog.show();		 
		 }
	}

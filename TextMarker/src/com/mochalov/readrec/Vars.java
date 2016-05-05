package com.mochalov.readrec;
import android.content.*;
import android.os.*;
import android.widget.*;

import java.text.*;
import java.util.*;

public class Vars
{
	public static final String PROGRAMM_FOLDER = "xolosoft";
	public static final String RESULTS_FOLDER = "results";
	public static final String APP_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+PROGRAMM_FOLDER+"/readrec";
	public static final String REC_LIST = "list.data";

	public static int textSize = 24;
	public static boolean showLines = true;
	public static boolean showBGColors = true;
	
	
	public static String msToTime(long l)
	{
		long second = (l / 1000) % 60;
        long minute = (l / (1000 * 60)) % 60;
        long hour = (l / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, l-
							 second*1000-
							 minute*1000*60-
							 hour*1000 * 60 * 60);
	}
	
	public static void showMessage(Context context, int id){
		Toast.makeText(context,
			context.getResources().getString(id),
			Toast.LENGTH_LONG).show();
	}
	
	public static String msToDateTime(long ms){
		SimpleDateFormat formatter = new SimpleDateFormat(
			"dd.MM.yyyy hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ms);
		return formatter.format(calendar.getTime());
	}

	public static boolean getShowBGColors() {
		return showBGColors;
	}

	public static boolean getShowLines() {
		return showLines;
	}
}

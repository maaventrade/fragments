package com.mochalov.readrec;

import java.util.*;
import java.io.*;

import com.mochalov.files.*;

import android.widget.*;
import android.util.*;

import org.xmlpull.v1.*;

import android.content.*;
import alex.xolo.readrec.*;

public class RecsArray {
	static ArrayList<Rec> list = new ArrayList<Rec>();
	static Context mContext;
	private static Rec mCurrent = null;

	public static Rec getCurrent() {
		return mCurrent;
	}

	public static void setCurent(int index) {
		if (index < 0) mCurrent = null;
		else mCurrent = list.get(index);
	}

	public static void setContext(Context c) {
		mContext = c;
	}

	public static ArrayList<Rec> getArray() {
		return list;
	}

	public static String getFileName(int index) {
		return list.get(index).mName + list.get(index).mIndex + ".mp3";
	}

	public static void load() {
		String name = Vars.APP_FOLDER + "/" + Vars.REC_LIST;
		Log.d("", "START");
		Rec rec = null;
		String tag = "";
		Log.d("", "START1");
		try {
			BufferedReader reader;
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					new FileInputStream(name)));

			String line = rd.readLine();

			rd.close();

			if (line.toLowerCase().contains("windows-1251"))
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(name), "windows-1251")); // Cp1252
			else if (line.toLowerCase().contains("utf-8"))
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(name), "UTF-8"));
			else if (line.toLowerCase().contains("utf-16"))
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(name), "utf-16"));
			else
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(name)));

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			parser.setInput(reader);
			Log.d("", "START");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					Log.d("", "start " + parser.getName());
					// Log.d("Log.d("","start
					// "+parser.getName());","start "+parser.getAttributeValue(null,
					// "name"));

					if (parser.getName().equals("item")) {
						rec = new Rec();
						rec.mName = parser.getAttributeValue(null, "name");
					} else {
						tag = parser.getName();
					}

				} else if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("item"))
						list.add(rec);
					tag = "";

				} else if (eventType == XmlPullParser.TEXT) {
					Log.d("", "yav " + tag);
					Log.d("", "text " + parser.getText());

					if (tag.equals("index"))
						rec.mIndex = Integer.parseInt(parser.getText());
					else if (tag.equals("time"))
						rec.mTimeStarting = Long.parseLong(parser.getText());
					else if (tag.equals("duration"))
						rec.mDuration = Long.parseLong(parser.getText());
					else if (tag.equals("note"))
						rec.mNote = parser.getText();
					else if (tag.equals("label"))
						rec.mLabel = parser.getText();
					name = "";

				}
				eventType = parser.next();
			}
		} catch (Throwable t) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(R.string.error_load_xml)
							+ " " + t.toString(), Toast.LENGTH_LONG).show();
		}

	}

	static void addRec(String name, int index, long timeStarting,
			long duration, String note, String label) {
		// boolean foud = false;

		Rec r = new Rec(name, index, timeStarting, duration, note, label);

		list.add(r);
		mCurrent = r;
		save();
	}

	private static void save() {
		try {
			File file = new File(Vars.APP_FOLDER);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(Vars.APP_FOLDER, Vars.REC_LIST);

			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			// BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
			writer.write("<recs>" + "\n");

			for (Rec r : list) {
				writer.write("<item name = \"" + r.mName + "\">" + "\n");
				writer.write("<index>" + r.mIndex + "</index>" + "\n");
				writer.write("<time>" + r.mTimeStarting + "</time>" + "\n");
				writer.write("<duration>" + r.mDuration + "</duration>" + "\n");
				writer.write("<note>" + r.mNote + "</note>" + "\n");
				writer.write("<label>" + r.mLabel + "</label>" + "\n");

				writer.write("</item>" + "\n");
			}

			writer.write("</recs>" + "\n");

			writer.close();
		} catch (IOException e) {
			// Vars.showMessage(context, R.string.error_save_file));
		}
	}
}

class Rec {
	String mName;
	long mTimeStarting;
	int mIndex;
	long mDuration;
	String mNote;
	String mLabel;

	Rec() {

	}

	public Rec(String name, int index, long timeStarting, long duration,
			String note, String label) {

		mName = name;
		mIndex = index;
		mTimeStarting = timeStarting;
		mDuration = duration;
		mNote = note;
		mLabel = label;
	}

	public String getLabel() {
		return mLabel;
	}

	public String getNote() {
		return mNote;
	}

	public String getDuration() {

		return Vars.msToTime(mDuration);
	}

	public String getTime() {
		return Vars.msToDateTime(mTimeStarting);
	}

	public String getName() {
		return mName + " (" + mIndex + ")";
	}
}

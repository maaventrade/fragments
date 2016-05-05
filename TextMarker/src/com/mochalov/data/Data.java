package com.mochalov.data;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mochalov.data.Mark.MarkComparator;
import com.mochalov.readrec.ActivityMain;
import com.mochalov.readrec.DialogSelectRec.MyCallback;

import android.graphics.Rect;
import android.util.Log;

public class Data {
	private static ArrayList<Mark> mMarks = new ArrayList<Mark>();
	private static ArrayList<StringToDisply> mStrings = new ArrayList<StringToDisply>();
	private static ArrayList<PolyLine> polyLines = new ArrayList<PolyLine>();

	private static Mark selectedMark = null;
	private static Mark selectedmMarksaved = null;
	private static Mark selectedMarkCopy = null;

	public static OnEventListener listener = null;
	public interface OnEventListener {
		void callbackCalcPolyLines(); 
	} 
	
	/*******************************/
	public static int getPosEnd(int j) {
		StringToDisply s = mStrings.get(j);
		return s.posEnd;
	}

	public static int getPosStart(int j) {
		StringToDisply s = mStrings.get(j);
		return s.posStart;
	}

	public static int getIndex(int j) {
		StringToDisply s = mStrings.get(j);
		return s.index;
	}

	public static void addString(String string, Boolean first, int i, int prevPos,
			int nextPos, float addToBlank) {
		mStrings.add(new StringToDisply(string, first, i, prevPos, nextPos, addToBlank));
	}

	public Object getLastLength() {
		StringToDisply s = mStrings.get(mStrings.size() - 1);
		s.getString().length();
		return null;
	}

	/*******************************/

	public static void restoreSrc() {
		for (Mark m : mMarks)
			m.restoreSrc();
	}

	// public static class MTypes{
	public enum MarkTypes {
		Up, Dn
	}

	// }

	public static void addMark(int i, int j, int sizeMinus1, int length,
			MarkTypes markType, boolean b) {
		mMarks.add(new Mark(0, 0, sizeMinus1, length, markType, b));
	}

	
	public static Mark addMark(int X, int Y, int firstLine, ArrayList<Undo> undoArray,
			ActivityMain context) {

		Mark mark = new Mark(firstLine, 0, null);

		mMarks.add(mark);
		undoArray.add(new Undo(mark));

		if (listener != null)
			listener.callbackCalcPolyLines();

		mark.setRect(X, Y, X, Y);
		selectedMark = mark;

		selectedmMarksaved = selectedMark;
		return mark;
	}

	public static void deleteSelectedMark(ArrayList<Undo> undoArray) {
		if (selectedmMarksaved != null) {
			undoArray.add(new Undo(selectedmMarksaved, 1));
			mMarks.remove(selectedmMarksaved);
			// ////////selectedmMarksaved = Math.min(selectedmMarksaved,
			// mMarks.size()-1);
		}
	}

	public static void linkMarks(ArrayList<Undo> undoArray) {
		if (selectedmMarksaved != null) {
			undoArray.add(new Undo(selectedmMarksaved, selectedmMarksaved));
			selectedmMarksaved.stop = !selectedmMarksaved.stop;
		}
	}

	public static void shiftPosses() {
		for (Mark mark : mMarks)
			for (int j = 0; j < mStrings.size(); j++) {
				if (mark.getLine() == getIndex(j)
						&& mark.getPos() >= getPosStart(j)
						&& mark.getPos() <= getPosEnd(j)) {
					mark.setLine(j);
					if (getPosStart(j) == 0)
						mark.addPos(-getPosStart(j));// ????????
					else
						mark.addPos(-getPosStart(j));
					break;
				}
			}
	}

	public static void setPosses() {
		for (int i = mMarks.size() - 1; i >= 0; i--) {
			Mark mark = mMarks.get(i);
			StringToDisply StringToDisply = mStrings.get(mark.getLine());

			if (StringToDisply.getString().length() < mark.getPos() + 1) {
				mark.setPos(StringToDisply.getString().length() - 1);
			}
		}

	}

	public static double getDistance(int x0, int y0, int x1, int y1) {
		return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
	}

	public static void setSelected(int X, int Y) {
		selectedMark = null;
		for (Mark mark : mMarks)
			if (mark.rect.contains(X, Y)) {
				if (selectedMark == null)
					selectedMark = mark;
				else if (getDistance(mark.getCenterX(), mark.getCenterY(), X, Y) < getDistance(
						mark.getCenterX(), mark.getCenterY(), X, Y))
					selectedMark = mark;
			}
		selectedmMarksaved = selectedMark;
	}


	public static String marksToString(int index, String S) {
		int plus = 0;
		for (Mark mark : mMarks)
			if (mark.getLine() == index) {
				try {
					S = S.substring(0, mark.getPos() + plus)
							+ mark.getTipeStr()
							+ S.substring(mark.getPos() + plus);
					plus = plus + mark.getTipeStr().length();
				} catch (IndexOutOfBoundsException e) {
					Log.d("ERROR", "" + (mark.getPos() + plus) + " " + S);
					// mark.pos = S.string.length()-1;
					continue;
				}
			}
		return S;
	}

	public int getLine(int i) {
		Mark m = mMarks.get(i);
		return m.getLine();
	}

	public MarkTypes getType(int i) {
		Mark m = mMarks.get(i);
		return m.getType();
	}

	public int getCenterX(int i) {
		Mark m = mMarks.get(i);
		return m.getCenterX();
	}

	public static void sort() {
		MarkComparator mMarksort = new MarkComparator();
		Collections.sort(mMarks, mMarksort);
	}

	public static StringToDisply getStringToDisply(int line) {
		return mStrings.get(line);
	}

	private static String addmMarks(int index, String S) {
		S = marksToString(index, S);
		return S;
	}

	public static String stringsToString(Writer writer, String S) {
		for (int i = 0; i < mStrings.size(); i++) {
			StringToDisply s = mStrings.get(i);
			if (s.getStart()) {
				if (i != 0)
					try {
						writer.write(S + "</p>" + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				S = "<p>" + addmMarks(i, s.getString()).trim();
			} else
				S = S + " " + addmMarks(i, s.getString()).trim();
		}
		return S;
	}

	public static void clear() {
		mMarks.clear();
		mStrings.clear();
	}

	public static void clearMarks() {
		mMarks.clear();
	}

	public static int getMarksSize() {
		return mMarks.size();
	}

	public static Mark getMark(int i) {
		return mMarks.get(i);
	}

	public static int getStringsSize() {
		return mStrings.size();
	}

	public static void removeString(int i) {
		mStrings.remove(i);
	}

	public static int getMarkLine(int i) {
		return mMarks.get(i).getLine();
	}

	public static void removeMark(int i) {
		mMarks.remove(i);
	}

	public static void clearStrings() {
		mStrings.clear();
	}

	public static int getStringLength(int line) {
		return mStrings.get(line).getString().length();
	}

	public static void removeMark(Mark mark) {
		mMarks.remove(mark);
	}

	public static void addMark(Mark mark) {
		mMarks.add(mark);
		sort();
	}

	/*************************************/
	
	public static ArrayList<PolyLine> getPolyLines() {
		return polyLines;
	}
	
	public static void calcPolyLines() {
		PolyLine.calcPolyLines(polyLines);
	}
	
	public int getPolyLineBGColor(Mark mark) {
		return PolyLine.getPolyLineBGColor(mark, polyLines);
	}
	
	public static void MoveMark(int x, int xTouch, int y, int y1) {
		if (selectedMarkCopy == null)
			selectedMarkCopy = new Mark(selectedMark);

		selectedMark.moveRect(x - xTouch, y - y1);
	}

	public static Mark getSelectedMark() {
		return selectedMark;
	}

	public static void actionUp(ArrayList<Undo> undoArray) {
		if (selectedMark != null) {
			if (selectedMarkCopy != null)
				undoArray.add(new Undo(selectedMark, selectedMarkCopy));

			MarkComparator mMarksort = new MarkComparator();
			Collections.sort(mMarks, mMarksort);
		}
		selectedMark = null;
		selectedMarkCopy = null;
	}

	public static Mark getSelectedMarkSaved() {
		return selectedmMarksaved;
	}
	
	
}

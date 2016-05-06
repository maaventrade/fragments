package com.mochalov.data;

import java.util.ArrayList;


import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
/*
* PolyLine links the Marks
* It has a list of own marks.
* PolyLines with diffrent sets of marks
* have different background colors
*
*/
public class PolyLine {
	private ArrayList<Mark> marks = new ArrayList<Mark>();
	
	public PolyLine() {
		super();
	}

	static int colorsUp[] = {
		Color.rgb(255,192,203),
		Color.rgb(220,220,220),//
		Color.rgb(240,128,128),
/*3*/	Color.rgb(152,251,152),
		Color.rgb(255,165,0)
	};

	static int colorsDn[] = {
		Color.rgb(189,183,107),
/*6*/	Color.rgb(102,205,170),
/*2*/   Color.rgb(154,205,50),
		Color.rgb(255,236,200), //Color.rgb(255,236,139)
		Color.rgb(175,238,238),
	};


	public static int getPolyLineBGColor(Mark mark, ArrayList<PolyLine> polyLines) {
		for (int i = 0; i< polyLines.size(); i++)
			for (int j = 0; j< polyLines.get(i).marks.size(); j++)
				if (polyLines.get(i).marks.get(j) == mark){
					if (mark.isUp())
						return colorsUp[polyLines.get(i).marks.size() % 5];
					else
						return colorsDn[polyLines.get(i).marks.size() % 5];
				}	
		
		return 0;
		
	}

	
	public int getLineOfStart() {
		Mark mark = marks.get(0);
		return mark.getLine();
	}
	
	public int getLineOfEnd() {
		Mark mark = marks.get(marks.size()-1);
		return mark.getLine();
	}

	
	public ArrayList<Mark> getMarks() {
		return marks;
	}
	
	public int getPolyLineBGColor() {
		Mark mark = marks.get(0);
		if (mark.isUp())
			return colorsUp[marks.size() % 5];
		else
			return colorsDn[marks.size() % 5];
	}
	
	public Point getStartEndX(int line) {
		Point point = new Point();
		point.x = 0; 
		point.y = 999999999;
		
		Mark mark0 = marks.get(0);
		Mark mark9 = marks.get(marks.size()-1);
		
		if (mark0.getLine() == mark9.getLine() && mark0.getLine() == line){
			// fit in the line
			point.x = mark0.getCenterX(); 
			point.y = mark9.getCenterX(); 
		} else if (mark0.getLine() == line) 
			// starts in the line
			point.x = mark0.getCenterX(); 
		 else if (mark9.getLine() == line)
			// ends in the line
			point.y = mark9.getCenterX(); 
		
		return point;
	}
	
	public static void calcPolyLines(ArrayList<PolyLine> polyLines) {
		polyLines.clear();
		
		int marksCount = Data.getMarksSize();
		
		if (marksCount > 0)
			polyLines.add(new PolyLine());
		
		for (int i = 0; i < marksCount; i++){
			Mark mark = Data.getMark(i);

			polyLines.get(polyLines.size()-1).marks.add(mark);
			if (mark.stop && i <marksCount-1)
				polyLines.add(new PolyLine());
			
		}
			
		
	}
	
}

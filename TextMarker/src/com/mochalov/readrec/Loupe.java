package com.mochalov.readrec;

import java.util.ArrayList;

import com.mochalov.data.StringToDisply;
import com.mochalov.data.Mark;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Loupe {
	private static final int RADIUS3 = 8;
	
	private static int mWidth;
	private static int mHeight;
	
	private static  Paint paintLoupe = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static Paint paintLoupeText = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static Paint paintLoupeTextSel = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static Paint paintLoupeLine = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static Paint paintLoupeLine1 = new Paint(Paint.ANTI_ALIAS_FLAG);

	public static void setSize(int width, int height){
		mHeight = height;
		mWidth = width;
	}

	public static void setPaint(float textSize){

		paintLoupe.setColor(Color.WHITE);
		paintLoupe.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLoupe.setAlpha(200);
		
		paintLoupeText.setColor(Color.BLACK);
		paintLoupeText.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLoupeText.setTextSize(textSize*2);
		
		paintLoupeLine.setColor(Color.BLACK);
		paintLoupeLine.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLoupeLine.setStrokeWidth(3);
		
		paintLoupeLine1.setColor(Color.GRAY);
		paintLoupeLine1.setStyle(Paint.Style.STROKE);
		paintLoupeLine1.setStrokeWidth(2);
		
		paintLoupeTextSel.setColor(Color.RED);
		paintLoupeTextSel.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLoupeTextSel.setTextSize(textSize*2);
	}
	
	public Loupe(){
		super();
//		this.line = line;
	
	}
	
	static void drawLoupe(Canvas canvas, Paint paint, Paint paintCircle, StringToDisply S, Mark mark, int x, int y, int GAP, float widthOfBlank){
		int count = S.getString().length();
		
		float[] widths = new float[count];
		
		float textWidthAll = 0;
		float textWidthLargeAll = 0;
		float k;
		
		
		paint.getTextWidths(S.getString(), 0, count, widths);
		for (int i = 0; i<mark.getPos(); i++){
			textWidthAll = textWidthAll + (int)(widths[i]);
			if (S.getString().charAt(i) == ' ')
				textWidthAll = textWidthAll + widthOfBlank;
		}
		
		paintLoupeText.getTextWidths(S.getString(), 0, count, widths);
		for (int i = 0; i<mark.getPos(); i++){
			textWidthLargeAll = textWidthLargeAll + (int)(widths[i]);
			if (S.getString().charAt(i) == ' ')
				textWidthLargeAll = textWidthLargeAll + widthOfBlank*2;
		}
		
		if (textWidthAll == 0)
			k = 2;
		else k = textWidthLargeAll/textWidthAll;

		// Fill window for text drawing
		int h = (int) paintLoupeText.getTextSize();
		canvas.drawRoundRect( new RectF(0, y-h*2 ,mWidth, y+h/2), 5, 5 , paintLoupe);
		canvas.drawRoundRect( new RectF(0, y-h*2 ,mWidth, y+h/2), 5, 5 , paintLoupeLine1);
		
		// Draw text
		showText(canvas, S.getString(), x - (x-GAP)*k, y, paintLoupeText, paintCircle, mWidth, S.getAddToBlank()*k, mark);
		
	}

	private static void showText(Canvas canvas, String S, float x, int y, Paint paint , Paint paintCircle, int scrWidth, float addToBlank, Mark mark){
		int h = (int) paintLoupeText.getTextSize();
		
		float widthOfBlank = 0;
		float[] widths = new float[1]; 
		paint.getTextWidths(" ", 0, 1, widths);
		widthOfBlank = widths[0];
		
		int count = S.length();
		widths = new float[count]; 
		paint.getTextWidths(S, 0, count, widths);
		
		int start = 0;
		float width = 0;

		float xSelectedLetter = -1;
		
		for (int i = 0; i<count; i++){
			if (i == mark.getPos()) xSelectedLetter = x + width;
			
			if (S.charAt(i) == ' '){
				showTextAndLines(canvas, S, start, i, x, y, h, paint , paintCircle, mark, xSelectedLetter);
				
				start = i+1;
				x = x + width + widthOfBlank+addToBlank;
				width = 0;
				
			} else width = width + widths[i];
		}
		
		if (start < count);
			showTextAndLines(canvas, S, start, count, x, y, h, paint , paintCircle, mark, xSelectedLetter);
		
	}
	

	private static void showTextAndLines(Canvas canvas, String S, int start, int end, float x, int y, int h, Paint paint , Paint paintCircle, Mark mark, float xSelectedLetter){
		canvas.drawText(S.substring(start, end), x, y, paint);
		if (xSelectedLetter >= 0){
			paint.setColor(Color.RED);
			canvas.drawText(S.substring(mark.getPos(), mark.getPos()+1), xSelectedLetter, y, paint);
			paint.setColor(Color.BLACK);
			
			// 
			int dy = h/3;
			canvas.drawLine(xSelectedLetter-10, y-h*2 + dy,   xSelectedLetter+30, y-h*2 + dy , paintLoupeLine1);
			canvas.drawLine(xSelectedLetter-10, y-h*2 + dy*3, xSelectedLetter+30, y-h*2 + dy*3 , paintLoupeLine1);
			
			if (mark.isUp())
				canvas.drawCircle(xSelectedLetter + 10, y-h*2 + dy, RADIUS3, paintCircle);
			else
				canvas.drawCircle(xSelectedLetter + 10, y-h*2 + dy*3, RADIUS3, paintCircle);
			xSelectedLetter = -1;
		}
	}
	
}

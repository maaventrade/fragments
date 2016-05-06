package com.mochalov.data;

import java.util.Comparator;

import android.graphics.Rect;

public class Mark {
	private int mLine; // Numberl of the string where this mark is
	private int mPos;// Position in the string where this mark is
	boolean stop; // This mark is the last in the chain

	int mLineSrc;
	int mPosSrc;

	Rect rect;

	public boolean isDn()
	{
		return type == MarkTypes.Dn;
		
	}

	public void setTypeUp()
	{
		type = MarkTypes.Up;
	}

	public void setTypeDn()
	{
		type = MarkTypes.Dn;
	}

	public boolean isUp()
	{
		return type == MarkTypes.Up;
	}
	
	public enum MarkTypes {
		Up, Dn
		}
	MarkTypes type;

	public Mark(int line, int pos, MarkTypes type) {
		super();
		mLine = line;
		mPos = pos;
		this.type = type;
		this.stop = false;

		rect = new Rect();
	}

	public Mark(Mark mark) {
		super();
		mLine = mark.mLine;
		mPos = mark.mPos;
		this.type = mark.type;
		this.stop = mark.stop;

		mLineSrc = mark.mLineSrc;
		this.mPosSrc = mark.mPosSrc;

		rect = new Rect();
		rect.set(mark.rect);
	}

	public Mark(int line, int pos, int lineSrc, int mPosSrc, MarkTypes type,
			boolean stop) {
		super();
		mLine = line;
		mPos = pos;
		this.stop = stop;

		mLineSrc = lineSrc;
		this.mPosSrc = mPosSrc;

		this.type = type;
		rect = new Rect();
	}

	public void addPos(int pos) {
		mPos = mPos + pos;
	}

	public void restoreSrc() {
		mLine = mLineSrc;
		mPos = mPosSrc;
	}

	public void setRect(int left, int top, int right, int bottom) {
		rect.left = left;
		rect.top = top;
		rect.right = right;
		rect.bottom = bottom;
	}

	public void moveRect(int dx, int dy) {
		rect.left = rect.left + dx;
		rect.top = rect.top + dy;
		rect.right = rect.right + dx;
		rect.bottom = rect.bottom + dy;
	}

	public void setType(MarkTypes type) {
		this.type = type;
	}

	public void setLine(int line) {
		mLine = line;
	}

	public int getLine() {
		return mLine;
	}

	public int getPos() {
		return mPos;
	}

	public String getTipeStr() {
		if (stop) {
			if (type == MarkTypes.Dn)
				return "<dn_stop/>";
			return "<up_stop/>";
		} else {
			if (type == MarkTypes.Dn)
				return "<dn/>";
			return "<up/>";
		}
	}

	public void setPos(int pos) {
		this.mPos = pos;
	}

	public int getCenterY() {
		return (rect.top + ((rect.bottom - rect.top) >> 1));
	}

	public int getCenterX() {
		return (rect.left + ((rect.right - rect.left) >> 1));
	}

	public void setLineAndPosSrc(StringToDisply S) {
		mLineSrc = S.mIndex;
		mPosSrc = S.mPosStart + mPos + 1;
	}

	public int getPosSrc() {
		return mPosSrc;
	}

	public void setPosSrc(int posSrc) {
		mPosSrc = posSrc;
	}

	public MarkTypes getType() {
		return type;
	}
	
	static class MarkComparator implements Comparator<Mark> {
		public int compare(Mark markA, Mark markB) {
			if (markA.mLine < markB.mLine)
				return -1;
			else if (markA.mLine > markB.mLine)
				return 1;
			else if (markA.mPos <= markB.mPos)
				return -1;
			else
				return 1;
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean b) {
		stop = b;
	}
	
}

package com.mochalov.data;


import com.mochalov.data.Mark.MarkTypes;

import android.graphics.Rect;

/**
* This class provided undo for a manipulations with marks
*
*/
public class Undo {
	Mark mark;
	Mark markOld;

	int typeUndo; // 0 add // 1 remove // 2 move
	
	int line;
	int pos;
	boolean stop;
	int lineSrc;
	int posSrc;
	Rect rect;
	MarkTypes type;		

	public Undo(Mark mark) {
		super();
		this.mark = mark;
		this.typeUndo = 0;
	}

	public Undo(Mark mark, int typeUndo) {
		super();
		this.mark = mark;
		this.typeUndo = typeUndo;
	}

	public Undo(Mark mark, Mark markOld) {
		super();
		this.mark = mark;
		this.typeUndo = 2;
		this.markOld = markOld;
	}

	public void restore() {
		mark.setLine(markOld.getLine());
		mark.setPos(markOld.getPos());
		mark.stop = markOld.stop;
		mark.mLineSrc = markOld.mLineSrc;
		
		mark.setPosSrc(markOld.getPosSrc());
		
		mark.rect.set(markOld.rect);
		mark.type = markOld.type;
	}

	public int getType() {
		return typeUndo;
	}

	public Mark getMark() {
		return mark;
	}
	
}

package com.mochalov.data;

/*
* StringToDisply is string prepared to display
*
*/
public class StringToDisply {
	private String mString; // text if string
	private Boolean mStart; // True if this string is a begining of the initial string

	int mIndex;    //index of the initial string
	int mPosStart; //start position in the initial string
	int mPosEnd;   //end position in the initial string

	float mAddToBlank; // Count of pixels to add to every bkank symbol in the string to fit page

	public StringToDisply(String string, Boolean start, int index, int posStart,
			int posEnd, float addToBlank) {
		super();
		mString = string;
		mStart = start;
		mIndex = index;
		mPosStart = posStart;
		mPosEnd = posEnd;
		mAddToBlank = Math.max(0, addToBlank);
	}

	public boolean isStart() {
		return mStart;
	}

	public boolean getStart() {
		return mStart;
	}

	public String getString() {
		return mString;
	}

	public float getAddToBlank() {
		return mAddToBlank;
	}
}

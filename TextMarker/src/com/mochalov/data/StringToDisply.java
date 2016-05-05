package com.mochalov.data;

public class StringToDisply {
	private String mString;
	private Boolean mStart;

	int index;
	int posStart;
	int posEnd;

	float addToBlank;

	public StringToDisply(String string, Boolean start, int index, int posStart,
			int posEnd, float addToBlank) {
		super();
		mString = string;
		mStart = start;
		this.index = index;
		this.posStart = posStart;
		this.posEnd = posEnd;
		this.addToBlank = Math.max(0, addToBlank);
	}

	public boolean isStart() {
		// TODO: Implement this method
		return mStart;
	}

	public boolean getStart() {
		return mStart;
	}

	public String getString() {
		return mString;
	}

	public float getAddToBlank() {
		return addToBlank;
	}
}

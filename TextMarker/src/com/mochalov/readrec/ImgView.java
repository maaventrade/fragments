package com.mochalov.readrec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.mochalov.data.StringToDisply;
import com.mochalov.data.Data;
import com.mochalov.data.Mark;
import com.mochalov.data.PolyLine;
import com.mochalov.data.Undo;
import com.mochalov.data.Data.MarkTypes;
import com.mochalov.readrec.ActivityMain.states.State;
import com.mochalov.readrec.ImgView.statesIV.States;
//import com.mochalov.readrec.Mark.MarkComparator;
//import com.mochalov.readrec.Mark.MTypes.MarkTypes;
import com.mochalov.readrec.SeekBarV.OnCustomEventListener;

import alex.xolo.readrec.R;
import alex.xolo.readrec.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImgView extends ImageView {
	private ArrayList<Undo> undoArray = new ArrayList<Undo>();

	// AsyncTask for parsing the string
	MyTask mt;

	SeekBarV seekBar;

	// Scrolling text
	ProgressBar progressBar;

	// Reference to the Activity
	private ActivityMain context;

	// Styles and colors
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLines = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintCircle0 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintCircle1Selected = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintCircle1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintMarkLine = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintMarkBG = new Paint(Paint.ANTI_ALIAS_FLAG);

	private int mWidth;

	// private boolean addNewmark = false;
	static class statesIV {
		enum States {
			add, edit, view
		}
	}

	public States state = States.add;

	// Gaps
	private int XGAP = 5; // left and right
	private int PARAG = 25; // paragraph

	private int RADIUS = 6;

	private int RADIUS0 = 40;
	private int RADIUS1 = 20;
	private int RADIUS2 = 60;

	private float widthOfBlank = 0;

	private int heightOfString;

	private int heightOfTopBorderMax = 0;
	private int heightOfTopBorder = 0;

	private int y0;
	private int y1;
	private int xTouch;

	private int firstLine;
	private int firstLinePixelShift;
	private int firstLinePixelShiftMax;

	private boolean isSlided = false;
	private Handler handler = new Handler();
	private int sladeY = 0;
	private int storedY = 0;
	private long eventTime;

	private int deltaLine;
	private int deltaLine2;

	boolean parsing = false;

	OnEventListener listener;
	public interface OnEventListener {
		void onSlide(int firstLine);
		void callbackCalcPolyLines(); 
		void callbackCall(String action);
	}

	public void setEventListener(OnEventListener onEventListener) {
		listener = onEventListener;
	}

	public int nextState() {
		if (state == States.view) {
			state = States.add;
			return R.drawable.add;
		} else if (state == States.add) {
			state = States.edit;
			return R.drawable.edit;
		} else {
			state = States.view;
			return R.drawable.more;
		}
	}

	/*
	 * public void setAddNewmark(boolean addNewmark){ this.addNewmark =
	 * addNewmark; }
	 */

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

		Loupe.setSize(parentWidth, parentHeight);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/*
	 * public boolean getAddNewmark(){ return this.addNewmark; }
	 */
	private boolean nextIsComma1(String str, int pos) {
		if (pos >= str.length())
			return false;

		char c = str.charAt(pos);

		if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';')
			return true;

		return false;
	}

	private int findBlank1(String str, int prevPos, int nextPos) {
		if (nextPos >= str.length())
			return nextPos;

		if (str.charAt(nextPos - 1) == ' ')
			return nextPos;

		char c = str.charAt(nextPos);
		if (c == ' ')
			return nextPos + 1;
		if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';')
			return nextPos;

		c = str.charAt(nextPos - 1);
		if (c == ' ')
			return nextPos + 1;
		if (c == ',' || 1 == '.' || c == '?' || c == '!' || c == ';')
			return nextPos;

		for (int i = nextPos - 1; i > prevPos; i--) {
			c = str.charAt(i);
			if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';')
				return i;
			if (c == ' ')
				return i + 1;
		}

		return nextPos;
	}

	private boolean nextIsComma(char[] chars, int pos) {
		if (pos >= chars.length)
			return false;

		char c = chars[pos];

		if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';')
			return true;

		return false;
	}

	private int findBlank(char[] chars, int prevPos, int nextPos) {
		if (prevPos + nextPos >= chars.length)
			return nextPos;

		char c = chars[prevPos + nextPos];
		if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';'
				|| c == ' ')
			return nextPos;

		char c1 = chars[prevPos + nextPos - 1];
		if (c1 == ',' || c1 == '.' || c1 == '?' || c1 == '!' || c1 == ';'
				|| c1 == ' ')
			return nextPos;

		for (int i = prevPos + nextPos - 1; i > prevPos + 1; i--)
			if (chars[i] == ',' || chars[i] == '.' || chars[i] == '?'
					|| chars[i] == '!' || chars[i] == ';' || chars[i] == ' ') {
				return i - prevPos;
			}

		return nextPos;
	}

	public void parseFileAsinc(ArrayList<String> strings, int width,
			ProgressBar progressBar, SeekBarV seekBar) {

		this.seekBar = seekBar;
		this.progressBar = progressBar;
		this.mWidth = width;

		mt = new MyTask();
		mt.execute(strings);
	}

	public void cancelTask() {
		if (mt == null)
			return;
		Log.d("LOG", "cancel result: " + mt.cancel(true));
	}

	class MyTask extends AsyncTask<ArrayList<String>, Integer, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(ArrayList<String>... values) {
			loadAsinc(values[0], mWidth);
			return null;

		}

		@Override
		protected void onCancelled(Void result) {
			super.onCancelled(result);
			progressBar.setVisibility(View.INVISIBLE);
			invalidate();
			seekBar.setMax(Data.getStringsSize() - 1);
			Toast.makeText(
					context,
					context.getResources()
							.getString(R.string.loading_cancelled),
					Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.INVISIBLE);
			invalidate();
			seekBar.setMax(Data.getStringsSize() - 1);
		}

		protected void loadAsinc(ArrayList<String> strings, int availableWidth) {
			parsing = true;
			Data.clearStrings();

			availableWidth = availableWidth - (XGAP * 2); // *3
			int availableWidthTmp;

			Data.restoreSrc();

			Calendar c = Calendar.getInstance();
			int seconds = c.get(Calendar.SECOND);
			for (int i = 0; i < strings.size(); i++) {
				Boolean first = true;
				availableWidthTmp = availableWidth - PARAG;

				String S = strings.get(i);

				int prevPos = 0;
				int nextPos = 0;
				int count = S.length();

				float[] widths = new float[count];

				paint.getTextWidths(S, 0, count, widths);

				while (nextPos < count) {
					int textWidth = 0;
					int j = 0;

					int lastBlank = 0;
					int widthForLastBlank = 0;
					int blankCount = 0;

					for (j = prevPos; (j < count)
							&& (textWidth < availableWidthTmp); j++) {
						textWidth += widths[j];
						if (S.charAt(j) == ' ') {
							lastBlank = j;
							widthForLastBlank = textWidth;
							blankCount++;
						}
					}

					if (lastBlank != 0 && j < count) {
						nextPos = lastBlank + 1;
						textWidth -= widthOfBlank;
						blankCount--;
					} else {
						nextPos = j;
						widthForLastBlank = textWidth;
					}

					float addToBlank = 0;
					if (blankCount > 0)
						addToBlank = (float) (availableWidthTmp - widthForLastBlank)
								/ blankCount;

					if (nextPos == count)
						addToBlank = Math.min(addToBlank, widthOfBlank);

					// Log.d("0", S.substring(prevPos,
					// nextPos)+" "+widthForLastBlank+"  "+availableWidthTmp+" blankCount "+blankCount+" addToBlank "+addToBlank);

					Data.addString(S.substring(prevPos, nextPos), first, i,
							prevPos, nextPos, addToBlank);

					first = false;
					availableWidthTmp = availableWidth;

					prevPos = nextPos;

					if (isCancelled())
						break;
				}
			}

			Data.shiftPosses();

			Data.setPosses();

			Data.sort();

			if (isCancelled()) {
				Data.clearMarks();
			}

			parsing = false;
			Log.d("OK", "OK");

		}

	}

	public void setPaints() {
		paint.setTextSize(Vars.textSize);
		paint.setStrokeWidth(1);

		float[] widths = new float[1];
		paint.getTextWidths(" ", 0, 1, widths);
		widthOfBlank = widths[0];

		paintGreen.setTextSize(Vars.textSize);
		paintGreen.setColor(Color.YELLOW);

		paintLines.setColor(Color.GRAY);
		paintLines.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLines.setStrokeWidth(1);

		paintCircle.setColor(Color.BLACK);
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(3);

		paintCircle1.setColor(Color.rgb(0, 50, 100));
		paintCircle1.setStrokeWidth(2);
		paintCircle1.setStyle(Paint.Style.FILL_AND_STROKE);
		paintCircle1.setAlpha(200);

		paintCircle0.setColor(Color.BLACK);
		paintCircle0.setStrokeWidth(2);
		paintCircle0.setStyle(Paint.Style.FILL_AND_STROKE);
		paintCircle0.setAlpha(50);

		paintCircle1Selected.setColor(Color.argb(100, 255, 106, 0));
		paintCircle1Selected.setStrokeWidth(2);
		paintCircle1Selected.setStyle(Paint.Style.FILL_AND_STROKE);

		paintMarkLine.setColor(Color.BLACK);
		paintMarkLine.setStyle(Paint.Style.STROKE);
		paintMarkLine.setStrokeWidth(2);
		paintMarkLine
				.setPathEffect(new DashPathEffect(new float[] { 2, 1 }, 0));

		paintMarkBG.setStyle(Paint.Style.FILL_AND_STROKE);

		heightOfString = (int) paint.getTextSize()
				+ ((int) paint.getTextSize() >> 1);
		deltaLine = heightOfString >> 2;
		deltaLine2 = heightOfString >> 3;

		heightOfTopBorderMax = heightOfString << 2;

		firstLinePixelShiftMax = heightOfString + heightOfString / 2;
		firstLinePixelShift = firstLinePixelShiftMax;

		Loupe.setPaint(paint.getTextSize());
	}

	void setParams(ActivityMain pContext) {
		this.context = (ActivityMain) pContext;
		setPaints();
	}

	public ImgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImgView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (strings.s)

		int action = event.getAction();
		int Y = (int) event.getY();
		int X = (int) event.getX();

		sladeY = 0;

		if (action == MotionEvent.ACTION_DOWN) {
			if (context.mState == State.edit) {
				if (state == States.add && Data.getStringsSize() > 0) {
					setMarkCoords(Data.addMark(X, Y, firstLine, undoArray,
							context));
				} else {
					Data.setSelected(X, Y);
				}
			}

			y0 = Y;
			y1 = Y;
			xTouch = X;
			storedY = Y;
			eventTime = event.getEventTime();

			isSlided = true;
			invalidate();
			return true;
		} else if (action == MotionEvent.ACTION_MOVE) {
			if (context.mState == State.edit && state != States.view) {
				if (Data.getSelectedMark() != null) {

					Data.MoveMark(X, xTouch, Y, y1);

					setMarkCoords(Data.getSelectedMark());

					y1 = Y;
					xTouch = X;
					invalidate();
					return true;
				}
			}

			if (isSlided = true) {
				setFirstVisible(Y - y0);
				y0 = Y;
				invalidate();
			}
			return true;
		} else if (action == MotionEvent.ACTION_UP) {
			Data.actionUp(undoArray);

			isSlided = false;

			if (X == xTouch && Y == y1 && context.mState == State.read) {
				listener.callbackCall("ShowSlideBar");
			}

			long eventTime1 = event.getEventTime();
			if (storedY > Y)
				sladeY = -Math.round((storedY - Y) / (eventTime1 - eventTime)
						* 50);
			else if (storedY < Y)
				sladeY = -Math.round((storedY - Y) / (eventTime1 - eventTime)
						* 50);
			else
				storedY = 0;

			if (sladeY != 0) {
				// Log.d("--", "sladeY "+sladeY);
				handler.postDelayed(updateTimeTask, 10);
			}

			if (listener != null)
				listener.callbackCalcPolyLines();

			invalidate();
			return true;
		}
		return false;
	}

	private void setFirstVisible(int delta) {
		if (firstLine == 0 && firstLinePixelShift == firstLinePixelShiftMax) {
			int htb = heightOfTopBorder;

			heightOfTopBorder = heightOfTopBorder + delta;
			heightOfTopBorder = Math.min(heightOfTopBorder,
					heightOfTopBorderMax);
			heightOfTopBorder = Math.max(heightOfTopBorder, 0);

			if (heightOfTopBorder > 0)
				delta = 0;
		}

		firstLinePixelShift = firstLinePixelShift + delta;
		if (firstLinePixelShift > firstLinePixelShiftMax) {
			firstLinePixelShift = 0;
			if (firstLine > 0)
				firstLine--;
			else
				firstLinePixelShift = firstLinePixelShiftMax;

		} else if (firstLinePixelShift < 0) {
			firstLinePixelShift = firstLinePixelShiftMax;
			if (firstLine < Data.getStringsSize())
				firstLine++;
		}

		// Log.d("firstLine", ""+firstLine+"  "+(StringToDisplys.size()-1));
		if (firstLine >= Data.getStringsSize() - 1) {
			// Log.d("firstLine", "*");
			firstLine = Math.max(Data.getStringsSize() - 1, 0);
			firstLinePixelShift = firstLinePixelShiftMax;
		}

		if (listener != null)
			listener.onSlide(firstLine);

	}

	private void slade() {
		if (sladeY > 0)
			sladeY--;
		else if (sladeY < 0)
			sladeY++;
		setFirstVisible(sladeY);
		this.invalidate();
	}

	private Runnable updateTimeTask = new Runnable() {
		public void run() {
			slade();
			handler.postDelayed(this, 10);
			if (sladeY == 0)
				handler.removeCallbacks(updateTimeTask);
		}
	};

	void setMarkCoords(Mark mark) {
		int yc = mark.getCenterY();
		int xc = mark.getCenterX();

		int line = (yc - firstLinePixelShift - heightOfTopBorder
				+ heightOfString + heightOfString / 4 + firstLinePixelShift / 4)
				/ (firstLinePixelShiftMax) + firstLine;

		line = Math.max(line, 0);
		line = Math.min(line, Data.getStringsSize() - 1);

		mark.setLine(line);

		if (yc >= firstLinePixelShift + heightOfTopBorder
				+ (firstLinePixelShiftMax) * (line - firstLine)
				- heightOfString + 2 + (deltaLine >> 1))
			mark.setType(MarkTypes.Dn);
		else
			mark.setType(MarkTypes.Up);

		//
		//
		StringToDisply StringToDisply = Data.getStringToDisply(line);

		int count = Data.getStringLength(line);

		float[] widths = new float[count];
		paint.getTextWidths(StringToDisply.getString(), 0, count, widths);

		float textWidth = XGAP;
		if (StringToDisply.isStart())
			textWidth = textWidth + PARAG;

		boolean posWasFound = false;
		for (int k = 0; k < count; k++) {
			textWidth = textWidth + widths[k];
			if (StringToDisply.getString().charAt(k) == ' ')
				textWidth = textWidth + StringToDisply.getAddToBlank();

			if (textWidth >= xc) {
				mark.setPos(k);
				posWasFound = true;
				break;
			}
		}
		// ////////////////////////////////////////////////////

		if (!posWasFound) { //
							//
			mark.setPos(count - 1);
		}

		mark.setLineAndPosSrc(Data.getStringToDisply(mark.getLine()));
	}

	private void showBG(int firstLine, Canvas canvas, int y) {
		ArrayList<PolyLine> polyLines = Data.getPolyLines();

		int dy0 = (int) paint.getTextSize() >> 2;
		int dy1 = (int) paint.getTextSize();

		for (int i = 0; i < polyLines.size(); i++) {
			PolyLine polyLine = polyLines.get(i);
			if (polyLine.getLineOfEnd() >= firstLine) {
				paintMarkBG.setColor(polyLine.getPolyLineBGColor());

				for (int j = polyLine.getLineOfStart(); j <= polyLine
						.getLineOfEnd(); j++) {

					Point point = polyLine.getStartEndX(j);
					point.y = Math.min(point.y, getWidth());

					canvas.drawRect(point.x, y + firstLinePixelShiftMax
							* (j - firstLine) - dy1, point.y, y
							+ firstLinePixelShiftMax * (j - firstLine) + dy0,
							paintMarkBG);

					// Toast.makeText(context,
					// ""+point.x+" "+(y+firstLinePixelShiftMax*(j-firstLine)+dy0)+" "+point.y+" "+(y+firstLinePixelShiftMax*(j-firstLine)-dy1)
					// , Toast.LENGTH_LONG).show();

					if (y + firstLinePixelShiftMax * (j - firstLine) + dy0 > getHeight())
						return;

				}
			}
		}
	}

	private void showLine(Canvas canvas, Mark startLineMark, Mark mark,
			int yMiddle, int startLineY) {
		int dStart = 0;
		int dEnd = 0;

		if (startLineMark.getLine() == mark.getLine()) {
			canvas.drawLine(startLineMark.getCenterX(),
					startLineMark.getCenterY(), mark.getCenterX(),
					mark.getCenterY(), paintMarkLine);
		} else {
			if (startLineMark.getType() == MarkTypes.Up)
				dStart = -deltaLine2 + heightOfTopBorder;
			else
				dStart = deltaLine + heightOfTopBorder;

			if (mark.getType() == MarkTypes.Up)
				dEnd = -deltaLine2 + heightOfTopBorder;
			else
				dEnd = deltaLine + heightOfTopBorder;
			canvas.drawLine(startLineMark.getCenterX(),
					startLineMark.getCenterY(), getWidth() - XGAP, startLineY
							+ dEnd, paintMarkLine);
			canvas.drawLine(XGAP, yMiddle + dStart, mark.getCenterX(),
					mark.getCenterY(), paintMarkLine);
			for (int k = startLineY + firstLinePixelShiftMax; k < yMiddle; k = k
					+ firstLinePixelShiftMax) {
				canvas.drawLine(XGAP, k + dStart, getWidth() - XGAP, k + dEnd,
						paintMarkLine);
			}
		}
	}

	private float getTextWidth(Paint paint, String S, int pos, float addToBlank) {
		// paint.getTextBounds(S.getString(), 0, mark.pos+1, bounds);
		float width = 0;

		float[] widths = new float[pos];
		paint.getTextWidths(S, 0, pos, widths);

		for (int i = 0; i < pos; i++) {
			width = width + widths[i];
			if (S.charAt(i) == ' ')
				width = (int) (width + addToBlank);
		}

		return width;
	}

	private void showText(Canvas canvas, String S, float x, int y, Paint paint,
			int scrWidth, float addToBlank, int index) {
		Rect bounds = new Rect();

		String z = "";

		int posBlank = 0;
		int posBlankNext = S.indexOf(" ", posBlank);
		while (posBlankNext >= 0) {

			z = S.substring(posBlank, posBlankNext);

			bounds = new Rect();

			canvas.drawText(z, x, y, paint);

			paint.getTextBounds(z, 0, z.length(), bounds);

			//
			x = x + (bounds.right - bounds.left) + widthOfBlank + addToBlank;

			posBlank = posBlankNext + 1;
			posBlankNext = S.indexOf(" ", posBlank);
		}

		z = S.substring(posBlank);
		canvas.drawText(z, x, y, paint);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (Data.getStringsSize() == 0)
			return;

		// //////////////////////////////////////////////
		paint.setColor(Color.WHITE);
		canvas.drawColor(paint.getColor());

		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, getWidth(), heightOfTopBorder, paint);
		paint.setColor(Color.WHITE);

		int count1 = context.mFileName.length();
		int width = 0;
		int first = 0;
		int last = 0;
		int y1 = heightOfTopBorder - heightOfTopBorderMax + heightOfString;

		float[] widths1 = new float[count1];

		paint.getTextWidths(context.mFileName, 0, count1, widths1);
		for (int i = 0; i < count1; i++) {
			width = width + (int) widths1[i];
			if (width > getWidth() - XGAP - XGAP) {
				canvas.drawText(context.mFileName.substring(first, i - 1),
						XGAP, y1, paint);
				first = i - 1;
				y1 = y1 + heightOfString;
				width = 0;
			}
		}
		if (first != count1) {
			canvas.drawText(context.mFileName.substring(first, count1), XGAP,
					y1, paint);
		}
		// //////////////////////////////////////////////

		// ������� �����
		int yMiddle = firstLinePixelShift - heightOfString + 2
				+ heightOfTopBorder;
		while (yMiddle < getHeight()) {
			// canvas.drawLine(0, yMiddle, getWidth(), yMiddle, paintLines);
			canvas.drawLine(0, yMiddle - deltaLine2, getWidth(), yMiddle
					- deltaLine2, paintLines);
			canvas.drawLine(0, yMiddle + deltaLine, getWidth(), yMiddle
					+ deltaLine, paintLines);

			yMiddle = yMiddle + (firstLinePixelShiftMax);
		}

		paint.setColor(Color.BLACK);
		int y = firstLinePixelShift + heightOfTopBorder;
		float x;
		int yc;

		Mark startLineMark = null;
		int startLineY = 0;

		boolean showNums = context.getShowNums();

		if (Vars.getShowBGColors())
			showBG(firstLine, canvas, y);

		for (int i = firstLine; i < Data.getStringsSize(); i++) {
			StringToDisply StringToDisply = Data.getStringToDisply(i);

			int scrWidth;
			if (StringToDisply.isStart()) {
				x = PARAG;
				scrWidth = getWidth() - PARAG - XGAP;
			} else {
				x = XGAP;
				scrWidth = getWidth() - XGAP - XGAP;
			}

			//
			//
			// canvas.drawText(S.getString(), x, y, paint);
			showText(canvas, StringToDisply.getString(), x, y, paint, scrWidth,
					StringToDisply.getAddToBlank(), i);

			if (showNums) {
				canvas.drawRoundRect(new RectF(0, y - heightOfString,
						heightOfString << 1, y - (heightOfString >> 1)), 5, 5,
						paintCircle1);
				canvas.drawText("" + i, 0, y - (heightOfString >> 1),
						paintGreen);
			}

			y = y + firstLinePixelShiftMax;

			if (!parsing) {
				for (int j = 0; j < Data.getMarksSize(); j++) {
					Mark mark = Data.getMark(j);
					if (mark.getLine() == i) {

						yMiddle = firstLinePixelShift
								+ (firstLinePixelShiftMax) * (i - firstLine)
								- heightOfString + 2;

						if (mark.getType() == MarkTypes.Up)
							yc = yMiddle - deltaLine2 + heightOfTopBorder;
						else if (mark.getType() == MarkTypes.Dn)
							yc = yMiddle + deltaLine + heightOfTopBorder;
						else
							yc = yMiddle + heightOfTopBorder;

						Rect bounds = new Rect();
						float[] widths;
						if (mark == Data.getSelectedMark()
								&& state != States.view) {
							yc = mark.getCenterY();
							int xc = mark.getCenterX();

							canvas.drawCircle(xc, yc, RADIUS, paintCircle);
							canvas.drawCircle(xc, yc, RADIUS1,
									paintCircle1Selected);

							if (mark.getPos() < StringToDisply.getString().length()) {
								if (StringToDisply.isStart())
									Loupe.drawLoupe(canvas, paint, paintCircle,
											StringToDisply, mark, xTouch,
											yc - RADIUS2, PARAG, widthOfBlank);
								else
									Loupe.drawLoupe(canvas, paint, paintCircle,
											StringToDisply, mark, xTouch,
											yc - RADIUS2, XGAP, widthOfBlank);
							}

						} else {

							//
							//
							//
							// paint.getTextBounds(S.getString(), 0, mark.pos+1,
							// bounds);
							//
							float right = getTextWidth(paint,
									StringToDisply.getString(), mark.getPos(),
									StringToDisply.getAddToBlank());
							float right1 = right;

							widths = new float[1];
							paint.getTextWidths(StringToDisply.getString(),
									mark.getPos(), mark.getPos() + 1, widths);

							right = right + ((int) widths[0] >> 1);
							// right = right+widths[0];

							float right2 = right;

							if (StringToDisply.isStart()) {
								right = right + PARAG;
							} else {
								right = right + XGAP;
							}

							// Log.d("", ""+right1+"  "+right2+" "+right);

							canvas.drawCircle(right, yc, RADIUS, paintCircle);

							if (context.mState == State.edit) {
								if (mark == Data.getSelectedMarkSaved())
									canvas.drawCircle(right, yc, RADIUS0,
											paintCircle1Selected);
								else
									canvas.drawCircle(right, yc, RADIUS0,
											paintCircle0);
							}
							mark.setRect((int) right - RADIUS0, yc - RADIUS0,
									(int) right + RADIUS0, yc + RADIUS0);
						}
						//
						//
						if (Vars.getShowLines()) {
							if (startLineMark != null)
								showLine(canvas, startLineMark, mark, yMiddle,
										startLineY);
							if (mark.isStop()) {
								startLineMark = null;
							} else {
								startLineMark = mark;
								startLineY = yMiddle;
							}
						}

					}
				}

			}

			if (y > getHeight() + heightOfString)
				break;
		}

		// Log.d("", msg)

		paint.setColor(Color.GRAY);
		canvas.drawRect(0, y, getWidth(), getHeight(), paint);
		/*
		 * if (selectedMark != null){ yc = selectedMark.getCenterY(); int xc =
		 * selectedMark.getCenterX();
		 * 
		 * StringToDisply S = StringToDisplys.get(selectedMark.getLine());
		 * 
		 * if (selectedMark.getPos()< S.getString().length()){ if (S.isStart)
		 * loupe.drawLoupe(canvas, paint , paintCircle, S, selectedMark, xTouch,
		 * yc-RADIUS2, PARAG, StringToDisplys); else loupe.drawLoupe(canvas, paint ,
		 * paintCircle, S, selectedMark, xTouch, yc-RADIUS2, XGAP, StringToDisplys); }
		 * }
		 */

	}

	public void setFirstLine(int progress) {
		heightOfTopBorder = 0;

		firstLine = progress;
		firstLinePixelShift = firstLinePixelShiftMax;
		invalidate();
	}

	public void reset() {
		undoArray.clear();
	}

	public void undo() {
		if (undoArray.size() > 0) {
			Undo undo = undoArray.get(undoArray.size() - 1);
			if (undo.getType()  == 0)
				Data.removeMark(undo.getMark());
			else if (undo.getType() == 1) {
				Data.addMark(undo.getMark());
				if (listener != null)
					listener.callbackCalcPolyLines();
			} else if (undo.getType() == 2) {
				undo.restore();
				Data.sort();
				if (listener != null)
					listener.callbackCalcPolyLines();
			} else if (undo.getType() == 3) {
				undo.getMark().setStop(!undo.getMark().isStop());

				Data.sort();
				if (listener != null)
					listener.callbackCalcPolyLines();
			}

			undoArray.remove(undo);
		}
		invalidate();
	}

	public void deleteSelectedMark() {
		Data.deleteSelectedMark(undoArray);
		invalidate();
	}

	public void linkMarks() {
		Data.linkMarks(undoArray);
		invalidate();
	}

}

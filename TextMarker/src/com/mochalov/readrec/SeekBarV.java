package com.mochalov.readrec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SeekBarV extends ImageView {
	OnCustomEventListener listener;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);;
	private Paint paintBold = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private int max;
	private int progress = 0;

	private int height;
	
	boolean pressed;
	
	public interface OnCustomEventListener{
		public void onChanged(int progress);
	}

	public void setCustomEventListener(OnCustomEventListener eventListener) {
		listener = eventListener;
	}
		
		
	public SeekBarV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SeekBarV(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekBarV(Context context) {
		super(context);
	}

	public void setMax(int max){
		this.max = max;
	}

	public void setProgress(int progress){
		this.progress = progress;
		invalidate();
	}
	
	private void setPaint(){
		paintBold.setStrokeWidth(3);
		paintBold.setColor(Color.BLUE);
		paintBold.setAlpha(200);
		
		paint.setStrokeWidth(1);
		paint.setColor(Color.rgb(100, 200, 255));
		paint.setAlpha(200);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int Y = (int) event.getY();
		int X = (int) event.getX();

        if (action == MotionEvent.ACTION_DOWN) {
    		paint.setAlpha(100);
        	progress = (int)Math.max(0, (float)((Y-RADIUS)*max)/(float)height);
        	
        	if(listener!=null) listener.onChanged(progress);
        	
        	pressed = true;
        	
        	invalidate();
        	return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
        	progress = (int)Math.max(0, (float)((Y-RADIUS)*max)/(float)height);
        	progress = Math.min(progress, max);
        	

        	if(listener!=null) listener.onChanged(progress);
        	
            invalidate();
        	return true;
        } else if (action == MotionEvent.ACTION_UP) {
        	pressed = false;
    		paint.setAlpha(200);
        	invalidate();
        	return true;
        }
		return false;
	}
	
	int RADIUS = 26;
	int RADIUS0 = 6;

	
	protected void onSizeChenged(int widthMeasureSpec, int heightMeasureSpec) {
    }
	
	
	@Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(RADIUS*2+1, getMeasuredWidth());
        //Log.d("", "getMeasuredHeight() "+getMeasuredHeight());
        //Log.d("", "getMeasuredWidth() "+getMeasuredWidth());
        height = getMeasuredHeight()-RADIUS*2; 
		setPaint();
    }
	
	
	@Override
	protected void onDraw(Canvas canvas) {
	    canvas.drawLine(RADIUS,0,RADIUS,getHeight(), paint);
	    
	    if (max == 0) return;

	    canvas.drawLine(RADIUS,0,RADIUS, RADIUS+progress*height/max , paintBold);
	    
	    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS, paint);

	    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS0, paintBold);

	    if (pressed){
		    paintBold.setStyle(Paint.Style.STROKE);
		    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS-3, paintBold);
		    paintBold.setStyle(Paint.Style.FILL_AND_STROKE);
	    }
	    
	}
	
		
}

package com.alexmohalov.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.*;

public class ElementFace2 extends Element{
	//private Bitmap bitmapInitial; // Initial picture
	//private Bitmap bitmap;     // Scaled picture
	
	//private Rect rectInitial;
	
	private boolean eyesArrived = false; // To synchronize eyes moving
	
	public ElementFace2(Context context, int color, int faceID){
		super(new PointF(0, 0), color);
		
		Options opts = new Options();
		opts.inScaled = false;
		
		bitmapInitial = BitmapFactory.decodeResource(context.getResources(), faceID, opts);
		rectInitial = new Rect(0, 0, bitmapInitial.getWidth(), bitmapInitial.getHeight());
		
		bitmap = Bitmap.createScaledBitmap(bitmapInitial, 
				bitmapInitial.getWidth(), 
				bitmapInitial.getHeight(), false);
	}

	public void commitOffset(float newX, float newY, double zoom) {
		bitmap = Bitmap.createScaledBitmap(bitmapInitial, 
				(int)(bitmapInitial.getWidth() * zoom), 
				(int)(bitmapInitial.getHeight() * zoom), 
				false);
		
		x = newX;
		y = newY;
		
	}	
	
	public boolean isFace()
	{
		return true;
	}
	
	public float getWidth()
	{
		return rectInitial.width();
	}
	
	public void draw(Canvas canvas, boolean moved, float newX, float newY, double zoom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		if (moved){
			RectF rect = new RectF(0, 0,  
					(int)(bitmapInitial.getWidth() * zoom), 
					(int)(bitmapInitial.getHeight() * zoom));
			
			//x = newX;
			//y = newY;
			
			Log.d("","ZOOM "+zoom);
			
			rect.offsetTo(newX, newY);
			canvas.drawBitmap(bitmapInitial, rectInitial, rect, paint);
		} else {
			canvas.drawBitmap(bitmap, x, y, paint);
		}
	}
	
	public boolean getEyesArrived(){
		return eyesArrived;
	}
	
	public void setEyesArrived(boolean p){
		eyesArrived = p;
	}
}

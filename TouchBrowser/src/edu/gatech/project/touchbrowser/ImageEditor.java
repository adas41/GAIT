package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ImageEditor extends View implements OnTouchListener, EditorGestureHandler{
	
	PhotoSortrView photoSortr;
	Img img;
	GestureDetector gestureDetector;
	Bitmap bitmap;
	float brightness, contrast = 1;
	Paint paintStroke;
	List<DragPoint> listOfCropRectPoints =  new ArrayList<DragPoint>();
	boolean cropMode = true;
	Drawable bmpDrawable;
	Paint paint = null;
	
	public ImageEditor(Context context, PhotoSortrView photoView, Img img) {
        super(context);
        setBackgroundColor(Color.rgb(47, 47, 47));
        photoSortr = photoView;
        this.img = img;
        bmpDrawable = img.getDrawable();
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        setOnTouchListener(this);
        bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
    }
	
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setColor(Color.YELLOW);
		paintStroke.setStyle(Paint.Style.FILL);
		paintStroke.setStrokeWidth(5);
		
		bmpDrawable.setBounds(0, 0, getWidth(), getHeight());
		
		bmpDrawable.draw(canvas);
		
		if(cropMode){
			drawCropRect(canvas);
		}
		
		canvas.restore();
	}

	private void drawCropRect(Canvas canvas) {
		// TODO Auto-generated method stub
		for (DragPoint dp : listOfCropRectPoints){
			canvas.drawCircle(dp.getX(), dp.getY(), 10, paintStroke);
			canvas.drawLine(dp.getX(), dp.getY(), dp.getX(), dp.getY(), paintStroke);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}


	@Override
	public void doubleTap() {
		// TODO Auto-generated method stub
		System.out.println("+++++image double tap");
		img.setDrawable(bmpDrawable);
		if(paint != null)
			img.setPaint(paint);
		photoSortr.addImgFromEditor(img);
	}


	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub
		bmpDrawable = new BitmapDrawable(getResources(), changeBrightness(true));
		invalidate();
	}


	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub
		bmpDrawable = new BitmapDrawable(getResources(), changeBrightness(false));
		invalidate();
	}
	
	private Bitmap changeBrightness(boolean increase){
		
		Bitmap alteredBitmap = Bitmap.createBitmap(
				bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);
		paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		brightness = increase? brightness + 10 : brightness - 10;
		cm.set(new float[] { contrast, 0, 0, 0, brightness, 0,
				contrast, 0, 0, brightness, 0, 0, contrast, 0,
				brightness, 0, 0, 0, 1, 0 });

		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bitmap, matrix, paint);
				
		return alteredBitmap;
	}
	
}

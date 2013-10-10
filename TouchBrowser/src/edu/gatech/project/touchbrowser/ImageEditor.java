package edu.gatech.project.touchbrowser;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageEditor extends ImageView implements OnTouchListener, EditorGestureHandler{
	
	PhotoSortrView photoSortr;
	Img img;
	GestureDetector gestureDetector;
	
	public ImageEditor(Context context, PhotoSortrView photoView, Img img) {
        super(context);
        this.setImageDrawable(img.getDrawable());
        setBackgroundColor(Color.rgb(47, 47, 47));
        photoSortr = photoView;
        this.img = img;
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        setOnTouchListener(this);
    }
	
	
    /*public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        this.setImageDrawable(img.getDrawable());
        //canvas.drawColor(Color.GRAY);
        //paint.setColor(Color.BLUE);
        //canvas.drawCircle(20, 20, 15, paint);
    }*/


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}


	@Override
	public void doubleTap() {
		// TODO Auto-generated method stub
		System.out.println("+++++image double tap");
		photoSortr.addImgFromEditor(this.getDrawable());
	}
	
}

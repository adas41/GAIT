package edu.gatech.project.touchbrowser;

import android.content.Context;
import android.text.InputType;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class GestureListener extends SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    EditorGestureHandler gestureHandler;
    
    public GestureListener(EditorGestureHandler gestureHandler){
    	super();
    	this.gestureHandler = gestureHandler;
    }

    @Override
    public boolean onDown(MotionEvent e) {
    	if(gestureHandler instanceof ImageEditor)
    		return true;
    	else
    		return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        gestureHandler.swipeRight();
                    } else {
                        gestureHandler.swipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        System.out.println("++++++swipe bottom");
                    	gestureHandler.swipeBottom();
                    } else {
                    	System.out.println("++++++swipe top");
                        gestureHandler.swipeTop();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    
    public boolean onDoubleTap(MotionEvent event){
		gestureHandler.doubleTap();
		
		return true;
    	
    }
    
    /*public boolean onSingleTapUp(MotionEvent event){
    	
    	if(gestureHandler instanceof TextEditor){
	    	TextEditor editText = (TextEditor)gestureHandler;
	    	editText.setInputType(InputType.TYPE_CLASS_TEXT);
	        editText.requestFocus();
	        InputMethodManager mgr = (InputMethodManager) editText.context.getSystemService(Context.INPUT_METHOD_SERVICE);
	        mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    	}
    	
    	return true;
    }*/


	public void onSwipeRight() {
	}
	
	public void onSwipeLeft() {
	}
	
	public void onSwipeTop() {
	}
	
	public void onSwipeBottom() {
	}

}
 
interface EditorGestureHandler{
	 
	 public void doubleTap();
	 
	 public void swipeRight();
	 
	 public void swipeLeft();
	 
	 public void swipeBottom();
	 
	 public void swipeTop();
	 
 }

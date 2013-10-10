package edu.gatech.project.touchbrowser;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class TextEditor extends EditText implements OnTouchListener, EditorGestureHandler{
	
	PhotoSortrView photoSortr;
	GestureDetector gestureDetector;
	Context context;
	
	public TextEditor(Context context, PhotoSortrView photoView, TextImg img) {
        super(context);
        this.setSingleLine(false);
        this.setMinLines(10);
        this.setText(img.getText());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        setOnTouchListener(this);
        photoSortr= photoView;
        this.context = context;
        
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		return gestureDetector.onTouchEvent(event);
		
			
	}

	@Override
	public void doubleTap() {
		// TODO Auto-generated method stub
		photoSortr.addTextImgFromEditor(this.getText().toString());
		
	}

}

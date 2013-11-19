package edu.gatech.project.touchbrowser;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputType;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testphotosortr.R;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;

public class TextEditor extends EditText implements OnTouchListener, EditorGestureHandler{
	
	PhotoSortrView photoSortr;
	GestureDetector gestureDetector;
	Context context;
	float textSize;
	SemiCircularRadialMenu radialMenu;
	SemiCircularRadialMenuItem mBold, mItalic, mUnderline, mSize, mStyle;
	MenuItemSelected selected;
	boolean radialMenuAdded;
	Typeface[] fonts = {Typeface.DEFAULT, Typeface.MONOSPACE, Typeface.SERIF, Typeface.SANS_SERIF};
	String[] fontNames = {"Default", "Monospace", "Serif","Sans Serif"};
	int currentFont;
	Toast toast;
	
	public TextEditor(Context context, PhotoSortrView photoView, TextImg img, Toast toast) {
        super(context);
        this.setMinLines(10);
        this.setText(img.getText());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        setOnTouchListener(this);
        photoSortr= photoView;
        this.context = context;
        this.setGravity(Gravity.TOP);
        textSize = getTextSize();
        createRadialMenu();
        radialMenuAdded = false;
        selected = null;
        currentFont = 0;
        setInputType(InputType.TYPE_NULL);
        this.setSingleLine(false);
        this.toast = toast;
        //InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//mgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		//((PhotoSortrActivity)context).getWindow().setSoftInputMode(
          //      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
    }
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(context != null){
		}
			
		return gestureDetector.onTouchEvent(event);
		
			
	}

	@Override
	public void doubleTap() {
		
		photoSortr.addTextImgFromEditor(this.getText().toString());
		
		
	}
	
	public void onSelectionChanged(int selStart, int selEnd) {
		if(context != null){
			if(radialMenu != null && !radialMenuAdded){
				photoSortr.addRadialMenu(radialMenu);
				radialMenuAdded = true;
			}
		}
		
	}

	@Override
	public void swipeRight() {
		
		switch(selected){
			case FONT_SIZE : 
				textSize += 3;
				setTextSize(textSize);
				((TextView) toast.getView().findViewById(R.id.text)).setText("Font Size: "+ (int)textSize);
				toast.show();
				break;
			case FONT_STYLE :
				currentFont = currentFont == 0 ? 3 : currentFont- 1;
				setTypeface(fonts[currentFont]);
				((TextView) toast.getView().findViewById(R.id.text)).setText("Font Style: "+ fontNames[currentFont]);
				toast.show();
				break;
			default:
				break;
		}
		
		
	}

	@Override
	public void swipeLeft() {
		
		switch(selected){
			case FONT_SIZE :
				textSize -= 3;
				setTextSize(textSize);
				((TextView) toast.getView().findViewById(R.id.text)).setText("Font Size: "+ (int)textSize);
				toast.show();
				break;
			case FONT_STYLE :
				currentFont = currentFont == 3 ? 0 : currentFont+1;
				setTypeface(fonts[currentFont]);
				((TextView) toast.getView().findViewById(R.id.text)).setText("Font Style: "+ fontNames[currentFont]);
				toast.show();
				break;
			default:
				break;
		}
		
		
	}
	
	public void swipeTop(){
		if(radialMenu != null && !radialMenuAdded){
			photoSortr.addRadialMenu(radialMenu);
			radialMenuAdded = true;
		}
	}
	
	public void swipeBottom(){
		
		if(radialMenuAdded && radialMenu != null){
			photoSortr.removeRadialMenu();
			radialMenuAdded = false;
		}
	}
	
	public enum MenuItemSelected{
		BOLD, ITALIC, UNDERLINE, FONT_SIZE, FONT_STYLE
	}
	public void createRadialMenu(){
		
		radialMenu = new SemiCircularRadialMenu(context);
		
		mBold = new SemiCircularRadialMenuItem("Bold", getResources().getDrawable(R.drawable.ic_action_camera), "Bold");
		mItalic = new SemiCircularRadialMenuItem("Italic", getResources().getDrawable(R.drawable.ic_action_dislike), "Italic");
		mUnderline = new SemiCircularRadialMenuItem("Underline", getResources().getDrawable(R.drawable.ic_action_info), "Underline");
		mSize = new SemiCircularRadialMenuItem("Size", getResources().getDrawable(R.drawable.ic_action_refresh), "Size");
		mStyle = new SemiCircularRadialMenuItem("Style", getResources().getDrawable(R.drawable.ic_action_search), "Style");
				
		radialMenu.addMenuItem(mBold.getMenuID(), mBold);
		radialMenu.addMenuItem(mItalic.getMenuID(), mItalic);
		radialMenu.addMenuItem(mUnderline.getMenuID(), mUnderline);
		radialMenu.addMenuItem(mSize.getMenuID(), mSize);
		radialMenu.addMenuItem(mStyle.getMenuID(), mStyle);
				
		mBold.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mBold.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.BOLD;
				toggleStyle(selected);
			}
		});
		
		mItalic.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mItalic.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.ITALIC;
				toggleStyle(selected);
			}
		});
		
		mUnderline.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mUnderline.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.UNDERLINE;
				toggleStyle(selected);
			}
		});
		
		mSize.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mSize.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.FONT_SIZE;
			}
		});
		
		mStyle.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mStyle.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.FONT_STYLE;
				//radialMenu.dismissMenu();
			}
		});
		
	}
	
	private void toggleStyle(MenuItemSelected style) {
		// Gets the current cursor position, or the starting position of the
		// selection
		int selectionStart = this.getSelectionStart();

		// Gets the current cursor position, or the end position of the
		// selection
		// Note: The end can be smaller than the start
		int selectionEnd = this.getSelectionEnd();

		// Reverse if the case is what's noted above
		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		// The selectionEnd is only greater then the selectionStart position
		// when the user selected a section of the text. Otherwise, the 2
		// variables
		// should be equal (the cursor position).
		if (selectionEnd > selectionStart) {
			Spannable str = this.getText();
			boolean exists = false;
			StyleSpan[] styleSpans;

			switch (style) {
			case BOLD:
				styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

				// If the selected text-part already has BOLD style on it, then
				// we need to disable it
				for (int i = 0; i < styleSpans.length; i++) {
					if (styleSpans[i].getStyle() == android.graphics.Typeface.BOLD) {
						str.removeSpan(styleSpans[i]);
						exists = true;
					}
				}

				// Else we set BOLD style on it
				if (!exists) {
					str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}

				this.setSelection(selectionStart, selectionEnd);
				break;
			case ITALIC:
				styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

				// If the selected text-part already has ITALIC style on it,
				// then we need to disable it
				for (int i = 0; i < styleSpans.length; i++) {
					if (styleSpans[i].getStyle() == android.graphics.Typeface.ITALIC) {
						str.removeSpan(styleSpans[i]);
						exists = true;
					}
				}

				// Else we set ITALIC style on it
				if (!exists) {
					str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}

				this.setSelection(selectionStart, selectionEnd);
				break;
			case UNDERLINE:
				UnderlineSpan[] underSpan = str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);

				// If the selected text-part already has UNDERLINE style on it,
				// then we need to disable it
				for (int i = 0; i < underSpan.length; i++) {
					str.removeSpan(underSpan[i]);
					exists = true;
				}

				// Else we set UNDERLINE style on it
				if (!exists) {
					str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}

				this.setSelection(selectionStart, selectionEnd);
				break;
			}
		}
	}


}


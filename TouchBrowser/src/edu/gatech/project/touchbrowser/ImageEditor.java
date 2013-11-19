package edu.gatech.project.touchbrowser;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testphotosortr.R;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;

public class ImageEditor extends View implements OnTouchListener, EditorGestureHandler{
	
	PhotoSortrView photoSortr;
	Img img;
	GestureDetector gestureDetector;
	Bitmap bitmap;
	float brightness = 1, contrast = 2;
	Drawable bmpDrawable;
	Paint paint = null;
	Context context;
	
	boolean cropMode = true;
	boolean beautifyCropRect = false;
	
	float maxX, maxY, minX, minY;
	
	Paint paintFill;
	Paint paintStroke;
	ArrayList<DragPoint> listOfCropRectPoints =  new ArrayList<DragPoint>();
	
	//Hitesh
	SemiCircularRadialMenu radialMenu;
	SemiCircularRadialMenuItem mBrightness, mContrast, mSharpness, mCrop, mColor;
	MenuItemSelected selected;
	boolean radialMenuAdded;
	Drawable[] colorFilters;
	String[] filterNames;
	int colorFilterIndex;
	Toast toast;
	
	public ImageEditor(Context context, PhotoSortrView photoView, Img img, Toast toast) {
        super(context);
        setBackgroundColor(Color.rgb(47, 47, 47));
        photoSortr = photoView;
        this.img = img;
        this.context = context;
        bmpDrawable = img.getDrawable();
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        setOnTouchListener(this);
        bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        createRadialMenu();
        radialMenuAdded = false;
        selected = null; 
        colorFilters = new Drawable[]{img.getDrawable(),convertToGrayscale(bitmap), convertToSepia(bitmap), convertToBnW(bitmap)};
        filterNames = new String[]{"Normal","Grayscale","Sepia","B&W"};
        colorFilterIndex = 0;
        this.toast = toast;
        
    }
	
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setColor(Color.YELLOW);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setStrokeWidth(5);
		
		paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setColor(Color.YELLOW);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setStrokeWidth(5);
		
		
		bmpDrawable.setBounds(0, 0, getWidth(), getHeight());
		
		bmpDrawable.draw(canvas);
		
		if(cropMode && listOfCropRectPoints.size() > 10){
			drawCropRect(canvas);
		}
		
		if(beautifyCropRect && listOfCropRectPoints.size() > 10){
			System.out.println("about to draw beautified");
			drawBeautifiedCropRect(canvas);
		}
		
		canvas.restore();
	}

	private void drawBeautifiedCropRect(Canvas canvas) {
		// TODO Auto-generated method stub
		listOfCropRectPoints.clear();
		//invalidate();
		canvas.drawRect(minX, minY, maxX, maxY, paintStroke);
		beautifyCropRect = false;
		cropImage();
		invalidate();
	}


	private void cropImage() {
		// TODO Auto-generated method stub
		RectF gestRect = new RectF(minX, minY, maxX, maxY);
		
		//System.out.println(bmpDrawable.getBounds().width() + " , " + bmpDrawable.getBounds().height());
		Bitmap imageBitmap = ((BitmapDrawable) bmpDrawable).getBitmap();
		
		Bitmap tempImageBitmap = Bitmap.createScaledBitmap(imageBitmap, getWidth(), getHeight(), false);
		
		
		//if((int) (gestRect.right - gestRect.left) <= imageBitmap.getWidth() && (int) (gestRect.bottom - gestRect.top) <= imageBitmap.getHeight()){
			//System.out.println(gestRect.left + "  " + gestRect.top + "  " + gestRect.width() + "  " + gestRect.height());
			//System.out.println(this.getWidth() +  "  " + this.getHeight());
			imageBitmap = Bitmap.createBitmap(tempImageBitmap,
					 (int) (gestRect.left), (int)(gestRect.top),
					(int) (gestRect.width()),
					(int) (gestRect.height()));
			bitmap = imageBitmap;
			bmpDrawable = new BitmapDrawable(getResources(), bitmap);
		//}
	}


	private void drawCropRect(Canvas canvas) {
		// TODO Auto-generated method stub
		if(listOfCropRectPoints.size() > 1){
			for (int i = 1, j = 0; i < listOfCropRectPoints.size(); i++, j++){
				//canvas.drawCircle(dp.getX(), dp.getY(), 10, paintStroke);
				canvas.drawLine(listOfCropRectPoints.get(j).getX(), listOfCropRectPoints.get(j).getY(), listOfCropRectPoints.get(i).getX(), listOfCropRectPoints.get(i).getY(), paintFill);
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(MenuItemSelected.CROP.equals(selected)){
		  if (event.getAction() == MotionEvent.ACTION_DOWN) {

		    DragPoint dPoint = new DragPoint(event.getX(), event.getY());
		    listOfCropRectPoints.add(dPoint);

		  } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			  DragPoint dPoint = new DragPoint(event.getX(), event.getY());
			  listOfCropRectPoints.add(dPoint);
			  
			  invalidate();			  
			  

		  } else if (event.getAction() == MotionEvent.ACTION_UP) {
			System.out.println("Crop rect created!");  
			//listOfCropRectPoints.clear();
			
			extractSeedPoints();
			beautifyCropRect = true;
			invalidate();
		  }
		}
		
		return gestureDetector.onTouchEvent(event);
	}
	
	private void extractSeedPoints() {
		// TODO Auto-generated method stub
		maxX = listOfCropRectPoints.get(0).x;
		minX = listOfCropRectPoints.get(0).x;
		maxY = listOfCropRectPoints.get(0).y;
		minY = listOfCropRectPoints.get(0).y;
		
		for (DragPoint dp : listOfCropRectPoints){
			if (dp.x < minX){
				minX = dp.x;
			}
			if (dp.x > maxX) {
				maxX = dp.x;
			}
			if (dp.y < minY){
				minY = dp.y;
			}
			if (dp.y > maxY) {
				maxY = dp.y;
			}
		}
		
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
		switch(selected){
			case BRIGHTNESS :
				bmpDrawable = new BitmapDrawable(getResources(), changeBrightnessOrContrast(true,true));
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Brightness: "+ (int)Math.ceil(brightness/10));
				toast.show();
				break;
			case CONTRAST :
				bmpDrawable = new BitmapDrawable(getResources(), changeBrightnessOrContrast(false,true));
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Contrast: "+ (int)Math.floor(contrast*10));
				toast.show();
				break;
			case COLORFILTER :
				colorFilterIndex = colorFilterIndex == 0 ? 3 : colorFilterIndex- 1;
				bmpDrawable = colorFilters[colorFilterIndex];
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Color Filter: "+ filterNames[colorFilterIndex]);
				toast.show();
				break;
			default:
				break;
		}
	}


	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub
		switch(selected){
			case BRIGHTNESS :
				bmpDrawable = new BitmapDrawable(getResources(), changeBrightnessOrContrast(true, false));
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Brightness: "+ (int)Math.ceil(brightness/10));
				toast.show();
				break;
			case CONTRAST :
				bmpDrawable = new BitmapDrawable(getResources(), changeBrightnessOrContrast(false,false));
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Contrast: "+ (int)Math.floor(contrast*10));
				toast.show();
				break;
			case COLORFILTER :
				colorFilterIndex = colorFilterIndex == 3 ? 0 : colorFilterIndex+ 1;
				bmpDrawable = colorFilters[colorFilterIndex];
				invalidate();
				((TextView) toast.getView().findViewById(R.id.text)).setText("Color Filter: "+ filterNames[colorFilterIndex]);
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
	
	private Bitmap changeBrightnessOrContrast(boolean changeBrightness, boolean increase){
		
		Bitmap alteredBitmap = Bitmap.createBitmap(
				bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);
		paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		if(changeBrightness)
			brightness = increase? brightness + 10 : brightness - 10;
		else{
			if(contrast >= 0 && contrast <=5 )
				contrast = increase? contrast + 0.2f : contrast - 0.2f;
			else{
				contrast = contrast < 0 ? 0 : 5;
			}
		}
		cm.set(new float[] { contrast, 0, 0, 0, brightness, 0,
				contrast, 0, 0, brightness, 0, 0, contrast, 0,
				brightness, 0, 0, 0, 1, 0 });

		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bitmap, matrix, paint);
				
		return alteredBitmap;
	}
	
	public enum MenuItemSelected{
		BRIGHTNESS, CONTRAST, SHARPNESS, CROP, COLORFILTER
	}
	public void createRadialMenu(){
		
		radialMenu = new SemiCircularRadialMenu(context);
		
		mBrightness = new SemiCircularRadialMenuItem("Brightness", getResources().getDrawable(R.drawable.ic_action_camera), "Brightness");
		mContrast = new SemiCircularRadialMenuItem("Contrast", getResources().getDrawable(R.drawable.ic_action_dislike), "Contrast");
		mSharpness = new SemiCircularRadialMenuItem("Sharpness", getResources().getDrawable(R.drawable.ic_action_info), "Sharpness");
		mCrop = new SemiCircularRadialMenuItem("Crop", getResources().getDrawable(R.drawable.ic_action_refresh), "Crop");
		mColor = new SemiCircularRadialMenuItem("Color", getResources().getDrawable(R.drawable.ic_action_search), "Color");
				
		radialMenu.addMenuItem(mBrightness.getMenuID(), mBrightness);
		radialMenu.addMenuItem(mContrast.getMenuID(), mContrast);
		radialMenu.addMenuItem(mSharpness.getMenuID(), mSharpness);
		radialMenu.addMenuItem(mCrop.getMenuID(), mCrop);
		radialMenu.addMenuItem(mColor.getMenuID(), mColor);
				
		mBrightness.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mBrightness.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.BRIGHTNESS;
			}
		});
		
		mContrast.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mContrast.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.CONTRAST;
			}
		});
		
		mSharpness.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mSharpness.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.SHARPNESS;
			}
		});
		
		mCrop.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mCrop.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.CROP;
			}
		});
		
		mColor.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				Toast.makeText(getContext(), mColor.getText(), Toast.LENGTH_SHORT).show();
				selected = MenuItemSelected.COLORFILTER;
				//radialMenu.dismissMenu();
			}
		});
		
	}
	
	private BitmapDrawable convertToSepia(Bitmap bitmap){
		
		ColorMatrix matrixA = new ColorMatrix();
		// making image B&W
		matrixA.setSaturation(0);

		ColorMatrix matrixB = new ColorMatrix();
		// applying scales for RGB color values
		matrixB.setScale(1f, .95f, .82f, 1.0f);
		matrixA.setConcat(matrixB, matrixA);

		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(
				matrixA);
		
		Bitmap bmpSepia = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), bitmap.getConfig()); 
		Canvas c = new Canvas(bmpSepia); 
		Paint paint = new Paint();
		paint.setColorFilter(filter);
		
		Matrix matrix = new Matrix(); 
		c.drawBitmap(bitmap, matrix, paint); 
				
		return new BitmapDrawable(getResources(), bmpSepia);
	}
	
	/*
	 * method to convert the bitmap to Grayscale color mode
	 */
	private BitmapDrawable convertToGrayscale(Bitmap bitmap){
			
		Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		Matrix matrix = new Matrix();
		c.drawBitmap(bitmap, matrix, paint);
				
		return new BitmapDrawable(getResources(), bmpGrayscale);
	}
	
	/*
	 * method to convert the bitmap to Black & White color mode
	 */
	private BitmapDrawable convertToBnW(Bitmap bitmap){
		
		ColorMatrix bwMatrix =new ColorMatrix();
        bwMatrix.setSaturation(0);
        final ColorMatrixColorFilter colorFilter= new ColorMatrixColorFilter(bwMatrix);
        Bitmap rBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint=new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas =new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
                
        return new BitmapDrawable(getResources(), rBitmap);
	}
	
}

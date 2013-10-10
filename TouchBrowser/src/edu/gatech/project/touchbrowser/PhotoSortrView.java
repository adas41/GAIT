/**
 * PhotoSorterView.java 
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * TODO: Add OpenGL acceleration.
 * 
 * --
 * 
 * Released under the MIT license (but please notify me if you use this code, so that I can give your project credit at
 * http://code.google.com/p/android-multitouch-controller ).
 * 
 * MIT license: http://www.opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Icon courtesy: http://www.oxygen-icons.org/
 * 
 */
package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.gatech.project.touchbrowser.MultiTouchController.MultiTouchObjectCanvas;
import edu.gatech.project.touchbrowser.MultiTouchController.PointInfo;
import edu.gatech.project.touchbrowser.MultiTouchController.PositionAndScale;

public class PhotoSortrView extends View implements MultiTouchObjectCanvas<Img> {

	// private static final int[] IMAGES = { R.drawable.m74hubble,
	// R.drawable.catarina, R.drawable.tahiti, R.drawable.sunset,
	// R.drawable.lake };

	private List<Img> mImages;

	// --

	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(
			this);

	// --

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	//arindam
	private boolean isDragPathSet = false;
	private ArrayList<DragPoint> listOfDragPoints;
	
	// Arindam
	private boolean isRotated = false;
	private double angleOfRotation = 0.0;
	
	// Arindam Apr 19
	private boolean isScaled = false;
	private double percentOfScale = 0.0;
	
	// Arindam
	private Paint mLinePaintTouchPointCircle = new Paint();
	private Paint mLinePaintDragPointLine = new Paint();
	Context context;
	
	//Arindam
	MediaPlayer mPlayer;
	
	//Hitesh
	float startX;
	float startY;
	Img currentObjInEditor = null;
	
	// Arindam
	int screenWidth = 0;
	int screenHeight = 0;

	
	// ---------------------------------------------------------------------------------------------------

	public PhotoSortrView(Context context, List<Img> resources) {
		this(context, null, resources);
	}

	public PhotoSortrView(Context context, AttributeSet attrs,
			List<Img> resources) {
		this(context, attrs, 0, resources);
	}

	public PhotoSortrView(Context context, AttributeSet attrs, int defStyle,
			List<Img> resources) {
		super(context, attrs, defStyle);
		this.context = context;
		this.mImages = resources;
		init(context);
	}

	private void init(Context context) {
		/*
		 * Resources res = context.getResources(); for (int i = 0; i <
		 * resources.size(); i++) mImages.add(new Img(context, resources.get(i),
		 * res));
		 */
		screenWidth = (int) ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		screenHeight = (int) ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();
		
		mLinePaintTouchPointCircle.setColor(Color.WHITE);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.FILL);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		mLinePaintTouchPointCircle.setAlpha(100);
		setBackgroundColor(Color.rgb(47, 47, 47));
		
		//arindam
		mLinePaintDragPointLine.setColor(Color.WHITE);
		mLinePaintDragPointLine.setStrokeWidth(5);
		mLinePaintDragPointLine.setStyle(Style.FILL);
		mLinePaintDragPointLine.setAntiAlias(true);
		mLinePaintDragPointLine.setAlpha(100);
		setBackgroundColor(Color.rgb(47, 47, 47));
		
		startX = 80;
		startY = 80;

	}

	public void loadAgain(Context context, List<Img> objects) {
		Resources res = context.getResources();
		mImages = objects;
		for (int i = 0; i < mImages.size(); i++){
			startX = startX >= 1000 ? 0 : startX + 100;
			startY = startY + 60;
			mImages.get(i).load(res,startX, startY);
		}
		invalidate();
	}

	/** Called by activity's onResume() method to load the images */
	public void loadImages(Context context) {
		Resources res = context.getResources();
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).load(res,startX >= 1000 ? 0:startX+50, startY+30);
	}

	/**
	 * Called by activity's onPause() method to free memory used for loading the
	 * images
	 */
	public void unloadImages() {
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).unload();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas, i);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
		if(isDragPathSet )
			drawDragPath(canvas);
		if(isRotated)
			drawRotationArc(canvas);
		// Arindam Apr 19
		if(isScaled)
			drawScaleArc(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			// int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			int numPoints = currTouchPoint.getNumTouchPoints();
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80,
						mLinePaintTouchPointCircle);	

		}
	}

	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/**
	 * Get the image that is under the single-touch point, or return null
	 * (canceling the drag op) if none
	 */
	public Img getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			Img im = mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be
	 * under the point (non-null is returned by getDraggableObjectAtPoint()) and
	 * a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(Img img, PointInfo touchPoint, boolean recordDrag) {
		
		//Toast.makeText(getApplicationContext(), "Tap any folder to move the images!", Toast.LENGTH_SHORT).show();
		
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selected
			mImages.remove(img);
			mImages.add(img);
			img.toggleSelected();
			if (recordDrag) {
				if (img.getStart() == null) {
					img.setStart(new DragPoint(touchPoint.getX(), touchPoint
							.getY()));
				} else {
					// if(add logic to check if selection happened in place of
					// drag)
					img.setEnd(new DragPoint(touchPoint.getX(), touchPoint
							.getY()));
				}
			}
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/**
	 * Get the current position and scale of the selected image. Called whenever
	 * a drag starts or is reset.
	 */
	@SuppressLint("NewApi")
	public void getPositionAndScale(Img img, PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part
		// requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
				img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
				img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(Img img,
			PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = img.setPos(newImgPosAndScale);
		if (ok) {

			invalidate();
		}
		return ok;
	}

	public List<Img> getSelectedresources() {
		List<Img> result = null;
		for (Img img : mImages) {
			if (img.isResSelected()) {
				if (result == null) {
					result = new ArrayList<Img>();
				}
				result.add(img);
			}
		}
		return result;
	}

	public List<Img> getAllResources() {
		return mImages;
	}
	
	//hitesh
	public void toggleAllObjects(){
		boolean allSelected = true;
		for (Img img : mImages) {
			if (img.isImgSelected() == false) {
				allSelected = false;
				break;
			}
		}
		
		for(Img img2 : mImages){
			img2.setImgSelected(allSelected ? false : true);
		}
		
		//Arindam
		/*mPlayer = MediaPlayer.create(this, R.raw.undo);
	    mPlayer.prepare();
	    mPlayer.start();
	    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // not playVideo
                            // playVideo();

                            mPlayer.release();
            }
        });*/
		
		
		// ---------------------------------------------------------------------------------
		
		invalidate();
	}
	
	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	//arindam
	private void drawDragPath(Canvas canvas){
		
		for(DragPoint dp: listOfDragPoints){
			canvas.drawCircle(dp.getX(), dp.getY(), 10, mLinePaintDragPointLine);
		}
		isDragPathSet = false;
		listOfDragPoints = null;
		invalidate();
	}
	
	//Arindam
	public void drawDragPathCaller(ArrayList<DragPoint> tempListOfDragPoints){
		
		if(tempListOfDragPoints.size()!=0){
			isDragPathSet = true;
			listOfDragPoints = tempListOfDragPoints;
			invalidate();
		}
	}
	
	//Arindam
	public void drawRotationArc(Canvas canvas){
		
		mLinePaintDragPointLine.setTextSize(40);
		int angle = (int)(angleOfRotation*180)%360;
		canvas.drawText(""+angle+(char) 0x00B0, 75, 140, mLinePaintDragPointLine);
		
		RectF rectF = new RectF(0, 20, 200, 220);
		canvas.drawOval(rectF, mLinePaintDragPointLine);
		canvas.drawArc (rectF, 90, angle , true, mLinePaintDragPointLine);
		
		isRotated = false;
		angleOfRotation = 0.0;
		invalidate();
	}
	
	
	public void drawRotationArcCaller(double angle){
		
		angleOfRotation = angle;
		isRotated = true;
		invalidate();
		
	}
	
	// Arindam Apr 19
	public void drawScaleArc(Canvas canvas){
		
		mLinePaintDragPointLine.setTextSize(40);
		int scale = (int)(percentOfScale <= 200 ? percentOfScale : 200);
		canvas.drawText(""+scale+"%", 60, 340, mLinePaintDragPointLine);
		
		RectF rectF = new RectF(0, 230, 200, 430);
		canvas.drawOval(rectF, mLinePaintDragPointLine);
		//canvas.drawArc (rectF, 90, scale , true, mLinePaintDragPointLine);
		canvas.drawCircle(100, 330, scale/2, mLinePaintDragPointLine);
		
		isScaled = false;
		percentOfScale = 0.0;
		invalidate();
	}
	
	// Arindam Apr 19
	@Override
	public void drawScaleArcCaller(double percent) {
		// TODO Auto-generated method stub
		
		percentOfScale = percent;
		isScaled = true;
		invalidate();
		
	}
	
	// Arindam Aug 27
		public void checkDragPosition(Img img, double posX, double posY){
			/*if(posX > screenWidth){
				//
			}*/
		}
		
		public void openObjInDetailedView(final Img img){
			
			System.out.println("Double tap");
			
			mImages.remove(img);
			currentObjInEditor = img;
			loadAgain(context, mImages);
			
			final ImageView animatedImg = new ImageView(context);
					
			final PhotoSortrActivity psa = ((PhotoSortrActivity)context);
			
			animatedImg.setImageDrawable(img.getDrawable());
			
			TranslateAnimation translate;
			translate = new TranslateAnimation(Animation.ABSOLUTE, img.getCenterX(), Animation.ABSOLUTE, 700, Animation.ABSOLUTE, img.getCenterY(), Animation.ABSOLUTE, 200);
			translate.setDuration(700);
			animatedImg.setAnimation(translate);
			
			psa.containerLayout.addView(animatedImg, 6);
					
			//animatedImg.startAnimation(translate);
			
			animatedImg.postDelayed(new Runnable() {
				public void run() {
					psa.containerLayout.removeView(animatedImg);
					
				    View pcc = addEditorView(img);
				    RelativeLayout.LayoutParams detailedViewParams = new RelativeLayout.LayoutParams(screenWidth * 3/4 ,screenHeight - 100);
				    detailedViewParams.leftMargin = 760;
				    detailedViewParams.topMargin = 80;
				    
				    pcc.setLayoutParams(detailedViewParams);
				    psa.containerLayout.addView(pcc,6);
				    

				    
				}
			}, 100);
		}
		
		public View addEditorView(Img img){
			
			View pcc = null;
			if(img instanceof TextImg){
				pcc = new TextEditor(context, this, (TextImg)img);
			}
			else if(img instanceof Img){
				pcc = new ImageEditor (context,this,img);
			    Bitmap result = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
			    Canvas canvas = new Canvas(result);
			    //pcc.draw(canvas);
			} 
			
			return pcc;
		}
	
	//Hitesh sept 20
	public void copyObject(Img img, PointInfo point){
		Img newImg = (Img) img.clone();
		//Img newImg = new Img(context, img.getDrawable(), context.getResources());
		newImg.setCenterX(point.getXs()[1]);
		newImg.setCenterY(point.getYs()[1]);
		newImg.load(context.getResources(), point.getXs()[1], point.getYs()[1]);
		newImg.setSelected(false);
		mImages.add(newImg);
		invalidate();
		
	}
	
	@SuppressLint("NewApi")
	public void copyObjectProperties(Img fromImg, Img toImg){
		toImg.setPos(toImg.getCenterX(), toImg.getCenterY(), fromImg.getScaleX(), fromImg.getScaleY(), fromImg.getAngle());
		invalidate();
		
	}
	
	//Hitesh Oct 6
	public void addImgFromEditor(Drawable drawable){
		((PhotoSortrActivity)context).containerLayout.removeViewAt(6);
		Img img = currentObjInEditor;
		img.setDrawable(drawable);
		mImages.add(img);
		//currentObjInEditor = null;
		invalidate();
		
	}
	
	public void addTextImgFromEditor(String text){
		TextImg img = (TextImg)currentObjInEditor;
		img.setText(text);
		mImages.add(img);
		//currentObjInEditor = null;
		invalidate();
		((PhotoSortrActivity)context).containerLayout.removeViewAt(6);
	}
	
	public Img getObjectAtTouchPoint(PointInfo pt, int touchIndex){
		if(touchIndex == 1 && pt.getXs().length < 2)
			return null;
		float x = pt.getXs()[touchIndex], y = pt.getYs()[touchIndex];
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			Img im = mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

}

// ----------------------------------------------------------------------------------------------


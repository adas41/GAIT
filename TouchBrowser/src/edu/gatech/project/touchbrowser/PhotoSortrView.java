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
import java.util.Collections;
import java.util.Date;
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
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;

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
	ArrayList<ArrayList<DragPoint>> listOfAnimePoints;

	
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
			final PhotoSortrActivity psa = ((PhotoSortrActivity)context);
			if(psa.containerLayout.getChildCount() >= 7 && psa.containerLayout.getChildAt(6) != null){
				
				if(psa.containerLayout.getChildCount() == 8 && psa.containerLayout.getChildAt(7) != null)
					psa.containerLayout.removeViewAt(7);
				
				View view = psa.containerLayout.getChildAt(6);
				if(view instanceof TextEditor)
					addTextImgFromEditor(((TextEditor)view).getText().toString());
				else if(view instanceof ImageEditor)
					addImgFromEditor(((ImageEditor)view).img);
				
				
			}
			
			mImages.remove(img);
			currentObjInEditor = img;
			loadAgain(context, mImages);
			
			final ImageView animatedImg = new ImageView(context);
					
			
			
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
			}, 700);
		}
		
		public View addEditorView(Img img){
			
			View pcc = null;
			Toast toast = ((PhotoSortrActivity)context).customToast;
			toast.setGravity(Gravity.BOTTOM, 380, 200);
	        toast.getView().setBackgroundColor(Color.GRAY);
			if(img instanceof TextImg){
				pcc = new TextEditor(context, this, (TextImg)img,toast);
			}
			else if(img instanceof Img){
				pcc = new ImageEditor (context,this,img,toast);
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
		mImages.remove(toImg);
		toImg.setPos(toImg.getCenterX(), toImg.getCenterY(), fromImg.getScaleX(), fromImg.getScaleY(), fromImg.getAngle());
		/*if(fromImg.getPaint() != null){
			Bitmap bitmap = ((BitmapDrawable) toImg.getDrawable()).getBitmap();
			Bitmap alteredBitmap = Bitmap.createBitmap(
					bitmap.getWidth(), bitmap.getHeight(),
					bitmap.getConfig());
			Canvas canvas = new Canvas(alteredBitmap);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			
			cm.set(new float[] { 8, 0, 0, 0, 8, 0,
					1, 0, 0, 8, 0, 0, 1, 0,
					8, 0, 0, 0, 1, 0 });

			paint.setColorFilter(new ColorMatrixColorFilter(cm));
			Matrix matrix = new Matrix();
			canvas.drawBitmap(bitmap, matrix, paint);
			toImg.setDrawable(new BitmapDrawable(bitmap));
			mImages.add(toImg);
		}*/
		mImages.add(toImg);
		invalidate();
		
	}
	
	//Hitesh Oct 6
	public void addImgFromEditor(Img img){
		((PhotoSortrActivity)context).containerLayout.removeViewAt(6);
		mImages.add(img);
		//currentObjInEditor = null;
		RelativeLayout layout = ((PhotoSortrActivity)context).containerLayout;
		if(layout.getChildCount() == 7 && layout.getChildAt(6) != null){
			layout.removeViewAt(6);
		}
		invalidate();
		
	}
	
	public void addTextImgFromEditor(String text){
		TextImg img = (TextImg)currentObjInEditor;
		img.setText(text);
		img.setDrawable(PhotoSortrActivity.addTextResource(getResources(), new Date(),text.getBytes().length));
		mImages.add(img);
		//currentObjInEditor = null;
		RelativeLayout layout = ((PhotoSortrActivity)context).containerLayout;
		((PhotoSortrActivity)context).containerLayout.removeViewAt(6);
		if(layout.getChildCount() == 7 && layout.getChildAt(6) != null){
			layout.removeViewAt(6);
		}
		invalidate();
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
	
	//Hitesh Oct 25
	public void addRadialMenu(SemiCircularRadialMenu menu){
		
		RelativeLayout layout = ((PhotoSortrActivity)context).containerLayout;
		if(layout.getChildCount() == 7){
			menu.dismissMenu();
			RelativeLayout.LayoutParams menuViewParams = new RelativeLayout.LayoutParams(300 ,400);
			menuViewParams.leftMargin = screenWidth/2 - 200;
		    menuViewParams.topMargin = 270;
		    
		    menu.setLayoutParams(menuViewParams);
		    //menu.setId(10);
		    //((ViewGroup)menu.getParent()).removeView(menu);
		    ((PhotoSortrActivity)context).containerLayout.addView(menu,7);
		}
	    
	}
	
	public void removeRadialMenu(){
		RelativeLayout layout = ((PhotoSortrActivity)context).containerLayout;
		if(layout.getChildCount() == 8 && layout.getChildAt(7) != null)
			layout.removeViewAt(7);

	}
	
	//Arindam
	
	@SuppressLint("NewApi")
	public void setPosImg(String dir) {

		System.out.println(dir);

		if (dir.equals("left")) {
			DragPoint endPointImage = new DragPoint(200, 150);
			DragPoint endPointText = new DragPoint(200, 500);

			saveAnimePoints(endPointText, endPointImage);
			animateImgs(endPointText, endPointImage);

		}

		if (dir.equals("right")) {
			if (listOfAnimePoints.size() > 0) {
				for (ArrayList<DragPoint> list : listOfAnimePoints) {
					System.out
							.println("XXXXXXXXXXXX List reversed!! XXXXXXXXXXXXX");
					Collections.reverse(list);
				}
				for (int i = 0; i < 11; i++) {
					for (int j = 0; j < mImages.size(); j++) {
						mImages.get(j).setPos(
								listOfAnimePoints.get(j).get(i).x,
								listOfAnimePoints.get(j).get(i).y,
								mImages.get(j).getScaleX(),
								mImages.get(j).getScaleY(),
								mImages.get(j).getAngle());
						invalidate();
					}
				}
				listOfAnimePoints.clear();
			}
		}

		if (dir.equals("down")) {
			DragPoint point = new DragPoint(150, 100);

			for (Img img : mImages) {

				if (img.originalWidth != img.width) {
					img.originalWidth = img.width;
				}
				if (img.originalHeight != img.height)
					img.originalHeight = img.height;
				if (img.originalCenterX != img.centerX)
					img.originalCenterX = img.centerX;
				if (img.originalCenterY != img.centerY)
					img.originalCenterY = img.centerY;
				if (img.originalScaleX != img.scaleX)
					img.originalScaleX = img.scaleX;
				if (img.originalScaleY != img.scaleY)
					img.originalScaleY = img.scaleY;
				if (img.originalAngle != img.angle)
					img.originalAngle = img.angle;

				img.width = 200;
				img.height = 150;

				img.setPos(point.x, point.y, 1.0f, 1.0f, 0);

				point.x += 300;
				if (point.x >= 800) {
					point.x = 150;
					point.y += 175;
				}
				invalidate();
			}
		}

		if (dir.equals("up")) {
			for (Img img : mImages) {

				System.out.println("width=" + img.originalWidth + " height="
						+ img.originalHeight);

				img.width = img.originalWidth;
				img.height = img.originalHeight;

				/*
				 * img.centerX = img.originalCenterX; img.centerY =
				 * img.originalCenterY; img.scaleX = img.originalScaleX;
				 * img.scaleY = img.originalScaleY; img.angle =
				 * img.originalAngle;
				 */

				img.setPos(img.originalCenterX, img.originalCenterY,
						img.originalScaleX, img.originalScaleY,
						img.originalAngle);

				invalidate();
			}
		}

	}

	private void saveAnimePoints(DragPoint endPointText, DragPoint endPointImage) {
		// TODO Auto-generated method stub
		listOfAnimePoints = new ArrayList<ArrayList<DragPoint>>();
		for (Img img : mImages) {
			DragPoint startPoint = new DragPoint(img.getCenterX(),
					img.getCenterY());
			if (img instanceof TextImg) {
				listOfAnimePoints.add(getIntermediatePoints(startPoint,
						endPointText, 10));
			} else
				listOfAnimePoints.add(getIntermediatePoints(startPoint,
						endPointImage, 10));
		}
	}

	@SuppressLint("NewApi")
	void animateImgs(DragPoint endPointText, DragPoint endPointImage) {
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < mImages.size(); j++) {
				mImages.get(j).setPos(listOfAnimePoints.get(j).get(i).x,
						listOfAnimePoints.get(j).get(i).y,
						mImages.get(j).getScaleX(), mImages.get(j).getScaleY(),
						mImages.get(j).getAngle());
				invalidate();
			}
		}

		for (Img img : mImages) {
			if (img instanceof TextImg) {
				img.setPos(endPointText.x, endPointText.y, img.getScaleX(),
						img.getScaleY(), img.getAngle());
				invalidate();
			} else
				img.setPos(endPointImage.x, endPointImage.y, img.getScaleX(),
						img.getScaleY(), img.getAngle());
			invalidate();
		}
	}

	ArrayList<DragPoint> getIntermediatePoints(DragPoint start, DragPoint end,
			int maxNoOfPoints) {

		ArrayList<DragPoint> listOfPoints = new ArrayList<DragPoint>();

		listOfPoints.add(new DragPoint(start.x, start.y));

		int i = maxNoOfPoints - 1;
		while (i >= 1) {
			DragPoint point = new DragPoint((start.x + end.x) * i
					/ maxNoOfPoints, (start.y + end.y) * i / maxNoOfPoints);
			// DragPoint point = new DragPoint(start.x - 50, start.y - 50);
			listOfPoints.add(point);
			i--;
		}

		listOfPoints.add(new DragPoint(end.x, end.y));

		return listOfPoints;

	}

}

// ----------------------------------------------------------------------------------------------


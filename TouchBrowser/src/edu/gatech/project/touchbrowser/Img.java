package edu.gatech.project.touchbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import edu.gatech.project.touchbrowser.MultiTouchController.PositionAndScale;

class Img extends ImageView implements Cloneable{
	
	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;
	
	int resId;

	private Drawable drawable;
	
	private boolean firstLoad;

	private int width, height, displayWidth, displayHeight;

	private float centerX, centerY, scaleX, scaleY, angle;

	private float minX, maxX, minY, maxY;

	private static final float SCREEN_MARGIN = 100;
	
	private boolean imgSelected;
	
	private DragPoint start;
	private DragPoint end;
	Paint paint;

	public Img(Context context, int resId, Resources res) {
		super(context);
		this.resId = resId;
		this.firstLoad = true;
		getMetrics(res);
		this.imgSelected = false;
		this.drawable = res.getDrawable(resId);
		this.paint = null;
	}
	
	public Img(Context context, Drawable drawable, Resources res) {
		super(context);
		this.drawable = drawable;
		this.firstLoad = true;
		getMetrics(res);
		this.imgSelected = false;
		this.paint = null;
	}
	

	private void getMetrics(Resources res) {
		DisplayMetrics metrics = res.getDisplayMetrics();
		// The DisplayMetrics don't seem to always be updated on screen rotate, so we hard code a portrait
		// screen orientation for the non-rotated screen here...
		// this.displayWidth = metrics.widthPixels;
		// this.displayHeight = metrics.heightPixels;
		this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
				metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
				metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
	}

	/** Called by activity's onResume() method to load the images */
	public void load(Resources res, float x, float y) {
		getMetrics(res);
		this.width = drawable.getIntrinsicWidth();
		this.height = drawable.getIntrinsicHeight();
		float cx, cy, sx, sy;
		if (firstLoad) {
			/*cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
			cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));*/
			cx = x;
			cy = y;
			float sc = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
			sx = sy = sc;
			firstLoad = false;
		} else {
			// Reuse position and scale information if it is available
			// FIXME this doesn't actually work because the whole activity is torn down and re-created on rotate
			cx = this.centerX;
			cy = this.centerY;
			sx = this.scaleX;
			sy = this.scaleY;
			// Make sure the image is not off the screen after a screen rotation
			/*if (this.maxX < SCREEN_MARGIN)
				cx = SCREEN_MARGIN;
			else if (this.minX > displayWidth - SCREEN_MARGIN)
				cx = displayWidth - SCREEN_MARGIN;
			if (this.maxY > SCREEN_MARGIN)
				cy = SCREEN_MARGIN;
			else if (this.minY > displayHeight - SCREEN_MARGIN)
				cy = displayHeight - SCREEN_MARGIN;*/
		}
		System.out.println("++++++++ load() method minX:"+minX+" minY:"+minY+" maxX:"+maxX+" maxY:"+maxY);
		setPos(cx, cy, sx, sy, this.angle);
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unload() {
		//this.drawable = null;
	}

	/** Set the position and scale of an image in screen coordinates */
	public boolean setPos(PositionAndScale newImgPosAndScale) {
		return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
				.getScaleX() : newImgPosAndScale.getScale(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY()
				: newImgPosAndScale.getScale(), newImgPosAndScale.getAngle());
		// FIXME: anisotropic scaling jumps when axis-snapping
		// FIXME: affine-ize
		// return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), newImgPosAndScale.getScaleAnisotropicX(),
		// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
	}

	/** Set the position and scale of an image in screen coordinates */
	boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle) {
		float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
		float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;
		if (centerX > displayWidth || centerX < 0 || centerY > 650
				|| centerY < 0 || (newMaxX > 950 && newMaxY > 420))
			return false;
		this.centerX = centerX;
		this.centerY = centerY;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.angle = angle;
		this.minX = newMinX;
		this.minY = newMinY;
		this.maxX = newMaxX;
		this.maxY = newMaxY;
		//System.out.println("++++++++ setPos() method centerY:"+centerY);
		
		return true;
	}

	/** Return whether or not the given screen coords are inside this image */
	public boolean containsPoint(float scrnX, float scrnY) {
		// FIXME: need to correctly account for image rotation
		return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
	}

	public void draw(Canvas canvas, int index) {
		canvas.save();
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.YELLOW);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;
		//System.out.println("++++++++ draw() method minX:"+minX+" minY:"+minY+" maxX:"+maxX+" maxY:"+maxY);
		drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
		canvas.translate(dx, dy);
		canvas.rotate(angle * 180.0f / (float) Math.PI);
		canvas.translate(-dx, -dy);
		drawable.draw(canvas);
		if(this.imgSelected){
			canvas.drawRect(minX, minY , maxX, maxY, paint);
		}
/*		if(this.start != null && this.end != null){
			canvas.drawCircle(start.x, start.y, 2, paint);
			canvas.drawCircle(end.x, end.y, 2, paint);
			this.start = null;
			this.end = null;
		}
*/		canvas.restore();
		
		
	}
	
	public void toggleSelected(){
		imgSelected = !imgSelected;
	}
	
	public boolean isResSelected(){
		return imgSelected;
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	public void setDrawable(Drawable drawable){
		this.drawable = drawable;
	}

	public int getXWidth() {
		return width;
	}

	public int getXHeight() {
		return height;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	@SuppressLint("Override")
	public float getScaleX() {
		return scaleX;
	}

	@SuppressLint("Override")
	public float getScaleY() {
		return scaleY;
	}

	public float getAngle() {
		return angle;
	}

	// FIXME: these need to be updated for rotation
	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}
	
	

	/**
	 * @return the start
	 */
	public DragPoint getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(DragPoint start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public DragPoint getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(DragPoint end) {
		this.end = end;
	}

	/**
	 * @return the resId
	 */
	public int getResId() {
		return resId;
	}



	/**
	 * @return the imgSelected
	 */
	public boolean isImgSelected() {
		return imgSelected;
	}



	/**
	 * @param imgSelected the imgSelected to set
	 */
	public void setImgSelected(boolean imgSelected) {
		this.imgSelected = imgSelected;
	}
	
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public void setImgScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public void setImgScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	
}

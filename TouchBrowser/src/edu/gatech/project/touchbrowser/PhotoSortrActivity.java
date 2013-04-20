/**
 * PhotoSorterActivity.java
 *  
 * (c) Luke Hutchison (luke.hutch@mit.edu)
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
 * Icon courtesy: http://www.oxygen-icons.org/
 * 
 * 
 */


package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testphotosortr.R;


public class PhotoSortrActivity extends Activity implements
OnGesturePerformedListener{
	
	PhotoSortrView photoSorter;
	HorizontalScrollView folderView;
	RelativeLayout containerLayout;
	private static final int[] IMAGES1 = { R.drawable.campus1, R.drawable.campus2, R.drawable.campus3, R.drawable.campus4}; 
	private static final int[] IMAGES2 = { R.drawable.campus5, R.drawable.campus6, R.drawable.campus7, R.drawable.campus8 };
	//hitesh
	Folder currentFolder;
	GestureLibrary gestureLib;
	ActivityHolder activityList;
	List<Folder> folders;
	int folderCount;
	ImageButton delete;

	// Arindam Apr 12
	GestureDetector gestureDetector;
	OnTouchListener gestureListener;
	private boolean isGesturePadVisible = false;
	
	// Arindam Apr 19
	ImageView image;

	// -----------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		containerLayout = new RelativeLayout(getApplicationContext());
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.instructions);
		activityList = new ActivityHolder();
		folders = new ArrayList<Folder>();
		folderCount = 0;
		
		LinearLayout flayout = new LinearLayout(this);
		LinearLayout.LayoutParams fParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,60);
		fParams.setMargins(25, 10, 25, 10);
		Folder folder1 = new Folder(getApplicationContext(),R.drawable.directory, IMAGES1,folderCount++,String.valueOf(folderCount));
		folders.add(folder1);
		// Arindam
		//folder1.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		Folder folder2 = new Folder(getApplicationContext(), R.drawable.directory, IMAGES2,folderCount++,String.valueOf(folderCount));
		folders.add(folder2);
		// Arindam
		//folder2.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		Folder folder3 = new Folder(this, R.drawable.directory,folderCount++,String.valueOf(folderCount));
		folders.add(folder3);
		// Arindam
		//folder3.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		
		Button b = new Button(getApplicationContext());
		b.setBackgroundDrawable(getResources().getDrawable(R.drawable.directory));
		b.setText("hello");
		flayout.addView(b, fParams);
		
		flayout.addView(folder1, fParams);
		flayout.addView(folder2, fParams);
		flayout.addView(folder3, fParams);
		for(int i =0; i < 20; i++){
			Folder folder = new Folder(this,R.drawable.directory, folderCount++,String.valueOf(folderCount));
			//folder.setImageDrawable(getResources().getDrawable(R.drawable.directory));
			folders.add(folder);
			flayout.addView(folder, fParams);
		}
		int width = (int) ((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		int height = (int) ((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();

		// Arindam Apr 12
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		layout.setLayoutParams(new LinearLayout.LayoutParams(500, 200));
		layout.setBackgroundColor(Color.TRANSPARENT);

		image = (ImageView) layout.findViewById(R.id.hand);
		image.setImageResource(R.drawable.hand);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Use the gesture pad to undo this move!");

		

		final Toast customToast = new Toast(getApplicationContext());
		customToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		customToast.setDuration(Toast.LENGTH_SHORT);
		customToast.setView(layout);
		
		folder1.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
							
				Folder clickedFolder = (Folder)v;
				if(v != currentFolder){
					currentFolder.setFolderResources(photoSorter.getAllResources());
					List<Img> selectedResources = photoSorter.getSelectedresources();
					
					if(selectedResources != null){
							// Arindam
						customToast.show();
						setAnimation(image);
						// ----- X -----
						clickedFolder.moveResources(selectedResources);
						currentFolder.removeResources(selectedResources);
						photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
						activityList.addActivity(new Node(ActivityType.MOVE, currentFolder.getFolderId(), clickedFolder.getFolderId(), selectedResources));
						//animation
						performAnimation(selectedResources, v);
						
					}else{
						photoSorter.loadAgain(getApplicationContext(),clickedFolder.getFolderResources());
						currentFolder = clickedFolder;
					}
				}
			}
		});
		
		folder2.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Folder clickedFolder = (Folder)v;
				if(v != currentFolder){
					List<Img> selectedResources = photoSorter.getSelectedresources();
					currentFolder.setFolderResources(photoSorter.getAllResources());
					if(selectedResources != null){
							// Arindam
						customToast.show();
						setAnimation(image);
						// ----- X -----
						clickedFolder.moveResources(selectedResources);
						currentFolder.removeResources(selectedResources);
						photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
						activityList.addActivity(new Node(ActivityType.MOVE, currentFolder.getFolderId(), clickedFolder.getFolderId(), selectedResources));
						performAnimation(selectedResources, v);
					}else{
						photoSorter.loadAgain(getApplicationContext(),clickedFolder.getFolderResources());
						currentFolder = clickedFolder;
					}
				}
			}
		});
		
		photoSorter = new PhotoSortrView(this,folder1.getFolderResources());
		currentFolder = folders.get(0);
		photoSorter.setId(3);
		folderView = new HorizontalScrollView(this);
		folderView.setId(1);
		RelativeLayout.LayoutParams folderParams = new RelativeLayout.LayoutParams(width * 7/8,LayoutParams.WRAP_CONTENT);
		folderParams.leftMargin = 0;
		folderParams.topMargin = 0;
		
		
		RelativeLayout.LayoutParams photoParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		photoParams.leftMargin = 0;
		photoParams.topMargin = 0;
		photoParams.addRule(RelativeLayout.BELOW, folderView.getId());
		
		folderView.addView(flayout);
		
		//delete icon
		delete = new ImageButton(this);
		delete.setImageDrawable(getResources().getDrawable(R.drawable.recycle_bin));
		RelativeLayout.LayoutParams delParams = new RelativeLayout.LayoutParams(width * 7/8,60);
		delParams.setMargins(width * 7/8 + 25, 10, 25, 10);
		delete.setId(2);
		//delParams.addRule(RelativeLayout.RIGHT_OF, folderView.getId());
		
		// Arindam 
		delete.setBackgroundColor(Color.TRANSPARENT);
		// ----- X ----
	
		
		delete.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				// Arindam
				customToast.show();
				setAnimation(image);
				// ----- X -----

				List<Img> selectedResources = photoSorter
						.getSelectedresources();
				if (selectedResources != null) {
					currentFolder.setFolderResources(photoSorter
							.getAllResources());
					currentFolder.removeResources(selectedResources);
					photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
					activityList.addActivity(new Node(ActivityType.DELETE, currentFolder.getFolderId(), selectedResources));
					performAnimation(selectedResources, v);
				}
				
			}
		});

		// gesture view
		final RelativeLayout.LayoutParams gestureParams = new RelativeLayout.LayoutParams(
				width * 1 / 4, 200);
		gestureParams.leftMargin = width * 3 / 4;
		gestureParams.topMargin = 475;
		gestureParams.bottomMargin = 75;
		gestureParams.rightMargin = 75;
		// gestureParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
		// photoSorter.getId());

		final GestureOverlayView gestureView = new GestureOverlayView(this);
		gestureView.setId(4);
		gestureView.addOnGesturePerformedListener(this);
		
		//Arindam
		gestureView.bringToFront();
		gestureView.setBackgroundColor(Color.rgb(71, 71, 71));
		gestureView.setGestureColor(Color.WHITE);

		// Arindam Apr 12
		int swipeViewWidth = (int) (width * 0.25 + 250);

		// swipe view
		final RelativeLayout.LayoutParams swipeViewParams = new RelativeLayout.LayoutParams(
				swipeViewWidth, 650);
		swipeViewParams.leftMargin = width * 3 / 4 - 100;
		swipeViewParams.topMargin = 400;

		final TextView swipeView = new TextView(this);
		// swipeView.setBackgroundColor(Color.BLUE);
		swipeView.setId(5);

		swipeView.setOnTouchListener(new SwipeGesture() {
			public void onSwipeTop() {
				Toast.makeText(PhotoSortrActivity.this, "top",
						Toast.LENGTH_SHORT).show();
				// add swipe and gesture view here
				//containerLayout.addView(swipeView, 3, swipeViewParams);
				containerLayout.addView(gestureView, 4, gestureParams);
				isGesturePadVisible = true;
			}

			public void onSwipeRight() {
				Toast.makeText(PhotoSortrActivity.this, "right",
						Toast.LENGTH_SHORT).show();
				// remove swipe and gesture view
				containerLayout.removeView(gestureView);
				//containerLayout.removeView(swipeView);
				isGesturePadVisible = false;
			}

			public void onSwipeLeft() {
				Toast.makeText(PhotoSortrActivity.this, "left",
						Toast.LENGTH_SHORT).show();
				//containerLayout.addView(swipeView, 3, swipeViewParams);
				containerLayout.addView(gestureView, 4, gestureParams);
				isGesturePadVisible = true;
			}

			public void onSwipeBottom() {
				Toast.makeText(PhotoSortrActivity.this, "bottom",
						Toast.LENGTH_SHORT).show();
				containerLayout.removeView(gestureView);
				//containerLayout.removeView(swipeView);
				isGesturePadVisible = false;
			}
		});

		// -----------------------------------------------------------------------------------------------
		
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load()) {
			finish();
		}
		
		
		
		containerLayout.addView(folderView, 0, folderParams);
		containerLayout.addView(delete,1,delParams);
		containerLayout.addView(photoSorter, 2, photoParams);
		
		// Arindam Apr 12
		containerLayout.addView(swipeView, 3, swipeViewParams); 
		// containerLayout.addView(gestureView,4,gestureParams);

		setContentView(containerLayout);
	}

	// Arindam Apr 19
	public void setAnimation(ImageView image) {
		TranslateAnimation translate;
		translate = new TranslateAnimation(0, 900, 0, 0);
		translate.setDuration(6000);
		image.setAnimation(translate);
	}

	@Override
	protected void onResume() {
		super.onResume();
		photoSorter.loadImages(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		photoSorter.unloadImages();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			photoSorter.trackballClicked();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		
		// sort the predictions on gestures performed based on score and retrieve the one with highest score
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		Collections.sort(predictions,new GestureComparator());
		Prediction prediction = predictions.get(0);
		if (prediction.score > 1.0) {
				//undo gesture performed
				if (prediction.name.equals("undo")) {
					Toast.makeText(getApplicationContext(), "Use the Gesture Pad to REDO!", Toast.LENGTH_SHORT).show();
					System.out.println("undo performed");
					Node node = activityList.undo();
					System.out.println("node returned:"+node);
					if(node != null){
						switch (node.type){
						case MOVE:
							List<Img> resources = node.objChanged;
							getFolder(node.folderChanged).removeResources(resources);
							currentFolder = getFolder(node.currentFolder);
							currentFolder.moveResources(resources);
							photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
							break;
						case DELETE:
							List<Img> delresources = node.objChanged;
							currentFolder = getFolder(node.currentFolder);
							currentFolder.moveResources(delresources);
							TranslateAnimation transform;
							 ScaleAnimation scale;
							 AnimationSet animation;
							 
							for(int i = 0;i < delresources.size();i++){
								Img img = delresources.get(i);
								animation = new AnimationSet(false);
								int[] location = new int[2];
								delete.getLocationOnScreen(location);
								transform = new TranslateAnimation(Animation.ABSOLUTE,location[0], Animation.ABSOLUTE,img.getCenterX(),Animation.ABSOLUTE, location[1],Animation.ABSOLUTE,img.getCenterY());
								//transform = new TranslateAnimation(location[0], img.getCenterX(), location[1],img.getCenterY());
								transform.setDuration(600);
								transform.setFillBefore(true);
								transform.setFillAfter(true);
								//transform.setInterpolator(new LinearInterpolator());
								animation.addAnimation(transform);
								/*scale = new ScaleAnimation(1, 2, 1,2);
								scale.setDuration(600);
								animation.addAnimation(scale);*/
								
								final ImageView view = new ImageView(getApplicationContext());
								view.setImageDrawable(img.getDrawable());
								//view.setAdjustViewBounds(true);
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(img.getMaxX()-img.getMinX()),(int)(img.getMaxY()-img.getMinY()));
								lp.topMargin = location[1];
								lp.leftMargin = location[0];
								view.setLayoutParams(lp);
								view.setAdjustViewBounds(true);
								containerLayout.addView(view);
								/*MyTranslateAnimation anim = new MyTranslateAnimation(view, location[0], img.getMaxX(), location[1],img.getMaxY());          
								anim.setDuration(1000);*/
					           view.startAnimation(transform);
					          
					           
					           view.postDelayed(new Runnable(){
					           	public void run(){
					           		containerLayout.removeView(view);
					           	}
					           }, 1000);
					           view.postDelayed(new Runnable(){
						           	public void run(){
						           		photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
						           	}
						           }, 2000);
					           
							}
							 //performUndoAnimation(delresources, delete);
							//photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
							break;
							
						}
					}
					
				}
				//redo gesture performed
				else if (prediction.name.equals("redo")) {
					Node node = activityList.redo();
					System.out.println("node returned:"+node);
					if(node != null){
						switch (node.type){
						case MOVE:
							List<Img> resources = node.objChanged;
							getFolder(node.folderChanged).moveResources(resources);
							currentFolder = getFolder(node.currentFolder);
							currentFolder.removeResources(resources);
							photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
							performAnimation(resources, getFolder(node.folderChanged));
							break;
						case DELETE:
							List<Img> delresources = node.objChanged;
							currentFolder = getFolder(node.currentFolder);
							currentFolder.removeResources(delresources);
							photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
							performAnimation(delresources, delete);
							break;
							
						}
					}
				}
					
		}
	}
	
	public void performAnimation(List<Img> resources, View v){
		TranslateAnimation transform;
		 ScaleAnimation scale;
		 AnimationSet animation;
		 
		for(Img img : resources){
			animation = new AnimationSet(false);
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			transform = new TranslateAnimation(Animation.ABSOLUTE,img.getCenterX(), Animation.ABSOLUTE,location[0]*1/0.4f,Animation.ABSOLUTE, img.getCenterY(),Animation.ABSOLUTE,location[1]*1/0.4f);
			transform.setDuration(1000);
			animation.addAnimation(transform);
			scale = new ScaleAnimation(1, (float)0.4, 1,(float)0.4);
			scale.setDuration(1000);
			animation.addAnimation(scale);
			
			final ImageView view = new ImageView(getApplicationContext());
			view.setImageDrawable(img.getDrawable());
			view.setAdjustViewBounds(true);
			view.setLayoutParams(new LinearLayout.LayoutParams((int)(img.getMaxX()-img.getMinX()),(int)(img.getMaxY()-img.getMinY())));
			containerLayout.addView(view);
			           
           view.startAnimation(animation);
          
           
           view.postDelayed(new Runnable(){
           	public void run(){
           		containerLayout.removeView(view);
           	}
           }, 1000);
           
		}
	}
	
	public class MyTranslateAnimation extends Animation {
        private float fromXDelta;
        private float fromYDelta;
        private float toXDelta;
        private float toYDelta;
        private View view;

        public MyTranslateAnimation(View view, float fromXDelta,
                float toXDelta, float fromYDelta, float toYDelta) {
            this.view = view;
            this.fromXDelta = fromXDelta;
            this.toXDelta = toXDelta;
            this.fromYDelta = fromYDelta;
            this.toYDelta = toYDelta;
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                Transformation t) {
            //Log.d("united", "time " + interpolatedTime);
            float newX = (toXDelta - fromXDelta);
            float newY = (toYDelta - fromYDelta);
            LayoutParams p = (LayoutParams) view.getLayoutParams();
            p.leftMargin = (int) newX;
            p.topMargin = (int) newY;
            if (interpolatedTime > 0.0 && view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
            }
            view.requestLayout();
        }
    }
	
	public void performUndoAnimation(List<Img> resources, View v){
		TranslateAnimation transform;
		 ScaleAnimation scale;
		 AnimationSet animation;
		 
		for(Img img : resources){
			animation = new AnimationSet(false);
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			transform = new TranslateAnimation(Animation.ABSOLUTE,location[0]*1/1.4f, Animation.ABSOLUTE,img.getCenterX(),Animation.ABSOLUTE, location[1]*1/1.4f,Animation.ABSOLUTE,img.getCenterY());
			transform.setDuration(600);
			transform.setInterpolator(new LinearInterpolator());
			animation.addAnimation(transform);
			scale = new ScaleAnimation(1, (float)1.4, 1,(float)1.4);
			scale.setDuration(600);
			animation.addAnimation(scale);
			
			final ImageView view = new ImageView(getApplicationContext());
			view.setImageDrawable(img.getDrawable());
			view.setAdjustViewBounds(true);
			view.setLayoutParams(new LinearLayout.LayoutParams((int)(img.getMaxX()-img.getMinX()),(int)(img.getMaxY()-img.getMinY())));
			containerLayout.addView(view);
			           
           view.startAnimation(animation);
          
           
           view.postDelayed(new Runnable(){
           	public void run(){
           		containerLayout.removeView(view);
           	}
           }, 1000);
           
		}
	}
	
	public Folder getFolder(int id){
		for(Folder folder : folders){
			if(folder.getFolderId() == id)
				return folder;
		}
		return null;
	}
}
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.EditText;
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
	FolderTouch folderTouchListener;

	// Arindam Apr 12
	Toast customToast;
	GestureDetector gestureDetector;
	OnTouchListener gestureListener;
	private boolean isGesturePadVisible = false;
	
	// Arindam Apr 19
	ImageView image;
	
	// Arindam Apr 23
	RelativeLayout.LayoutParams gestureParams;
	GestureOverlayView gestureView;
	
	// Arindam Aug 27
	View verticalRule;
	RelativeLayout.LayoutParams verticalRuleParams;
	
		
	// -----------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		int width = (int) ((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		int height = (int) ((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();
		
		containerLayout = new RelativeLayout(getApplicationContext());
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.instructions);
		activityList = new ActivityHolder();
		folders = new ArrayList<Folder>();
		folderCount = 0;
		folderTouchListener = new FolderTouch();
		
		final LinearLayout flayout = new LinearLayout(this);
		final LinearLayout.LayoutParams fParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,60);
		fParams.setMargins(25, 10, 25, 10);
		Folder folder1 = new Folder(getApplicationContext(),R.drawable.directory, IMAGES1,folderCount++,String.valueOf(folderCount));
		setFolderSelected(folder1);
		folder1.setOnClickListener(folderTouchListener);
		folder1.setOnLongClickListener(folderTouchListener);
		
		//text file drawable
		LayerDrawable layerDrawable = addTextResource(getResources());
		String content = "Hi, this is the first text file. I want to see how it looks in the application.";
		
		folder1.addTextResource(layerDrawable,content);
		folders.add(folder1);
		// Arindam
		//folder1.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		Folder folder2 = new Folder(getApplicationContext(), R.drawable.directory, IMAGES2,folderCount++,String.valueOf(folderCount));
		folder2.setOnClickListener(folderTouchListener);
		folder2.setOnLongClickListener(folderTouchListener);
		folder2.addTextResource(layerDrawable,content);
		folders.add(folder2);
		
		Button b = new Button(getApplicationContext());
		b.setBackgroundDrawable(getResources().getDrawable(R.drawable.directory));
		b.setText("hello");
		// flayout.addView(b, fParams);

		flayout.addView(folder1, fParams);
		flayout.addView(folder2, fParams);
		//flayout.addView(folder3, fParams);
		for(int i =0; i < 20; i++){
			Folder folder = new Folder(this,R.drawable.directory, folderCount++,String.valueOf(folderCount));
			folder.setOnClickListener(folderTouchListener);
			folder.setOnLongClickListener(folderTouchListener);
			folders.add(folder);
			flayout.addView(folder, fParams);
		}
		
		// Arindam Apr 12
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		layout.setLayoutParams(new LinearLayout.LayoutParams(500, 200));
		layout.setBackgroundColor(Color.TRANSPARENT);

		image = (ImageView) layout.findViewById(R.id.hand);
		image.setImageResource(R.drawable.hand);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Use gesture to undo this move!");

		customToast = new Toast(getApplicationContext());
		customToast.setGravity(Gravity.BOTTOM, 380,80);
		customToast.setDuration(Toast.LENGTH_SHORT);
		customToast.setView(layout);
		
		photoSorter = new PhotoSortrView(this,folder1.getFolderResources());
		currentFolder = folders.get(0);
		photoSorter.setId(4);
		folderView = new HorizontalScrollView(this);
		folderView.setId(2);
		RelativeLayout.LayoutParams folderParams = new RelativeLayout.LayoutParams(width * 7/9,LayoutParams.WRAP_CONTENT);
		folderParams.leftMargin = width*1/9;
		folderParams.topMargin = 0;
		
		
		RelativeLayout.LayoutParams photoParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		photoParams.leftMargin = 0;
		photoParams.topMargin = 0;
		photoParams.addRule(RelativeLayout.BELOW, folderView.getId());
		
		folderView.addView(flayout);
		
		//new folder button
		ImageButton newFolder = new ImageButton(this);
		newFolder.setImageDrawable(getResources().getDrawable(
				R.drawable.new_folder));
		RelativeLayout.LayoutParams newFolderParams = new RelativeLayout.LayoutParams(
				width * 1 / 9, 60);
		newFolderParams.setMargins(0, 10, 25, 10);
		newFolderParams.addRule(RelativeLayout.LEFT_OF, folderView.getId());
		newFolder.setId(1);
		newFolder.setBackgroundColor(Color.TRANSPARENT);
		
		newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                final AlertDialog alertDialog = new AlertDialog.Builder(PhotoSortrActivity.this).create();
                alertDialog.setTitle("Create new folder");
                final EditText folderName = new EditText(PhotoSortrActivity.this);
                alertDialog.setView(folderName);
               
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(PhotoSortrActivity.this, "New folder created: "+folderName.getText(),
                                Toast.LENGTH_SHORT).show();
                        Folder newFolder = new Folder(getApplicationContext(),R.drawable.directory, folderCount++,folderName.getText().toString());
                        newFolder.setOnClickListener(folderTouchListener);
                        newFolder.setOnLongClickListener(folderTouchListener);
            			folders.add(newFolder);
            			flayout.addView(newFolder,0, fParams);
                  } });
               
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                  } });
               
                alertDialog.show();
            }
        });
		
		//delete icon
		delete = new ImageButton(this);
		delete.setImageDrawable(getResources().getDrawable(R.drawable.recycle_bin));
		RelativeLayout.LayoutParams delParams = new RelativeLayout.LayoutParams(width * 1/9,60);
		delParams.setMargins(width * 8/9 , 10, 25, 10);
		delete.setId(3);
		//delParams.addRule(RelativeLayout.RIGHT_OF, folderView.getId());
		
		// Arindam 
		delete.setBackgroundColor(Color.TRANSPARENT);
		// ----- X ----
	
		
		delete.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				

				List<Img> selectedResources = photoSorter
						.getSelectedresources();
				if (selectedResources != null) {
					
					// Arindam
					customToast.show();
					setAnimation(image);
					// ----- X -----
					
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
		gestureParams = new RelativeLayout.LayoutParams(
				width * 1 / 4, 200);
		gestureParams.leftMargin = width * 3 / 4;
		gestureParams.topMargin = 475;
		gestureParams.bottomMargin = 75;
		gestureParams.rightMargin = 75;
		// gestureParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
		// photoSorter.getId());

		gestureView = new GestureOverlayView(this);
		gestureView.setId(5);
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
		swipeView.setId(4);

		swipeView.setOnTouchListener(new SwipeGesture() {
			public void onSwipeTop() {
				Toast.makeText(PhotoSortrActivity.this, "top",
						Toast.LENGTH_SHORT).show();
				// add swipe and gesture view here
				//containerLayout.addView(swipeView, 3, swipeViewParams);
				containerLayout.addView(gestureView, 5, gestureParams);
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
				containerLayout.addView(gestureView, 5, gestureParams);
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
		
		// Arindam Aug 27
		verticalRule = new View(this);
				
		verticalRuleParams = new RelativeLayout.LayoutParams(3,LayoutParams.FILL_PARENT);
		verticalRuleParams.leftMargin = width * 60 / 100;
		verticalRuleParams.topMargin = 80;
		verticalRule.setBackgroundColor(Color.rgb(47, 47, 47));
	    containerLayout.addView(verticalRule, 0, verticalRuleParams);
		
		containerLayout.addView(newFolder, 0,newFolderParams);
		containerLayout.addView(folderView, 1, folderParams);
		containerLayout.addView(delete,2,delParams);
		containerLayout.addView(photoSorter, 3, photoParams);
		
		// Arindam Apr 12
		containerLayout.addView(swipeView, 4, swipeViewParams); 
		// containerLayout.addView(gestureView,4,gestureParams);

		setContentView(containerLayout);
	}

	public static LayerDrawable addTextResource(Resources resources) {
		Drawable file = resources.getDrawable(R.drawable.textfile);
		Bitmap canvasBitmap = Bitmap.createBitmap(file.getIntrinsicWidth(), file.getIntrinsicHeight(), 
                Bitmap.Config.ARGB_8888);
		// Create a canvas, that will draw on to canvasBitmap.
		Canvas imageCanvas = new Canvas(canvasBitmap);
		
		// Set up the paint for use with our Canvas
		Paint imagePaint = new Paint();
		imagePaint.setTextAlign(Align.CENTER);
		imagePaint.setTypeface(Typeface.MONOSPACE);
		imagePaint.setTextSize(10f);
		
		
		// Draw the image to our canvas
		file.draw(imageCanvas); 
		
		// Draw the text on top of our image
		imageCanvas.drawText("sample.txt", 50, 20, imagePaint);
		imageCanvas.drawText("Size: 108 Kb", 50, 40, imagePaint);
		imagePaint.setTextSize(8f);
		imageCanvas.drawText("Created:", 50, 60, imagePaint);
		imageCanvas.drawText("4/23/2013 9.20PM", 45, 70, imagePaint);
		imageCanvas.drawText("Modified:", 50, 90, imagePaint);
		imageCanvas.drawText("4/23/2013 12.15AM", 45, 100, imagePaint);
		
		// Combine background and text to a LayerDrawable
		LayerDrawable layerDrawable = new LayerDrawable(
		new Drawable[]{file, new BitmapDrawable(canvasBitmap)});
		return layerDrawable;
	}

	// Arindam Apr 19
	public void setAnimation(ImageView image) {
		TranslateAnimation translate;
		translate = new TranslateAnimation(0, 900, 0, 0);
		translate.setDuration(6000);
		image.setAnimation(translate);
		
		if(containerLayout.findViewById(5) == null){
			System.out.println("Adding the view");
			containerLayout.addView(gestureView, 5, gestureParams);
		}
		
		image.postDelayed(new Runnable() {
			public void run() {
				containerLayout.removeView(gestureView);
			}
		}, 2500);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		photoSorter.loadAgain(this,currentFolder.getFolderResources());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//photoSorter.unloadImages();
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
					           view.setAnimation(transform);
					          
					           
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

	class FolderTouch implements View.OnClickListener, View.OnLongClickListener {
		
		@Override
		public boolean onLongClick(View v) {

			final Folder clickedFolder = (Folder) v;

			Toast.makeText(PhotoSortrActivity.this, "Long click",
					Toast.LENGTH_SHORT).show();

			final AlertDialog alertDialog = new AlertDialog.Builder(
					PhotoSortrActivity.this).create();
			alertDialog.setTitle("Rename folder");
			final EditText renameFolder = new EditText(PhotoSortrActivity.this);
			alertDialog.setView(renameFolder);

			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Submit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(
									PhotoSortrActivity.this,
									"Folder name changed to"
											+ renameFolder.getText(),
									Toast.LENGTH_SHORT).show();
							clickedFolder.setName(renameFolder.getText() + "");
							clickedFolder.setText(renameFolder.getText() + "");
						}
					});

			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							alertDialog.dismiss();
						}
					});

			alertDialog.show();
			return true;
		}

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
					setFolderSelected(clickedFolder);
					currentFolder.setBackgroundDrawable(getResources().getDrawable(currentFolder.background));
					photoSorter.loadAgain(getApplicationContext(),clickedFolder.getFolderResources());
					currentFolder = clickedFolder;
				}
			}
		}
	}
	
	public void setFolderSelected(Folder folder){
		
		folder.setBackgroundColor(Color.YELLOW);
		Drawable back = folder.getBackground();
		Drawable front = getResources().getDrawable(folder.background);
		Drawable[] drawableLayers = { back, front };
		LayerDrawable ld = new LayerDrawable(drawableLayers);
		folder.setBackgroundDrawable(ld);
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
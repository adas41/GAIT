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
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

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
		Folder folder1 = new Folder(this,R.drawable.directory, IMAGES1,folderCount++);
		folders.add(folder1);
		// Arindam
		folder1.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		Folder folder2 = new Folder(this, R.drawable.directory, IMAGES2,folderCount++);
		folders.add(folder2);
		// Arindam
		folder2.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		Folder folder3 = new Folder(this, R.drawable.directory,folderCount++);
		folders.add(folder3);
		// Arindam
		folder3.setBackgroundColor(Color.TRANSPARENT);
		// ----- X -----
		
		
		flayout.addView(folder1, fParams);
		flayout.addView(folder2, fParams);
		flayout.addView(folder3, fParams);
		for(int i =0; i < 20; i++){
			Folder folder = new Folder(this,R.drawable.directory, folderCount++);
			//folder.setImageDrawable(getResources().getDrawable(R.drawable.directory));
			folders.add(folder);
			flayout.addView(folder, fParams);
		}
		int width = (int)((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		int height = (int)((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		
		
		
		
		folder1.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				// Arindam
				Toast.makeText(getApplicationContext(), "Use the Gesture Pad to UNDO this MOVE!", Toast.LENGTH_SHORT).show();
				// ----- X -----
				
				Folder clickedFolder = (Folder)v;
				if(v != currentFolder){
					List<Img> selectedResources = photoSorter.getSelectedresources();
					currentFolder.setFolderResources(photoSorter.getAllResources());
					if(selectedResources != null){
						clickedFolder.moveResources(selectedResources);
						currentFolder.removeResources(selectedResources);
						photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
						activityList.addActivity(new Node(ActivityType.MOVE, currentFolder.getFolderId(), clickedFolder.getFolderId(), selectedResources));
						
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
						clickedFolder.moveResources(selectedResources);
						currentFolder.removeResources(selectedResources);
						photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
						activityList.addActivity(new Node(ActivityType.MOVE, currentFolder.getFolderId(), clickedFolder.getFolderId(), selectedResources));
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
				Toast.makeText(getApplicationContext(), "Use the Gesture Pad to UNDO this DELETE!", Toast.LENGTH_SHORT).show();
				// ---- X ----
				
				List<Img> selectedResources = photoSorter.getSelectedresources();
				if(selectedResources != null){
					currentFolder.setFolderResources(photoSorter.getAllResources());
					currentFolder.removeResources(selectedResources);
					photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
					activityList.addActivity(new Node(ActivityType.DELETE, currentFolder.getFolderId(), selectedResources));
				}
				
			}
		});
		
		//gesture view
		RelativeLayout.LayoutParams gestureParams = new RelativeLayout.LayoutParams(width*1/4,200);
		gestureParams.leftMargin = width*3/4;
		gestureParams.topMargin = 500;
		//gestureParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, photoSorter.getId());
        
		GestureOverlayView gestureView = new GestureOverlayView(this);
		gestureView.setId(4);
		gestureView.addOnGesturePerformedListener(this);
		
		//Arindam
		gestureView.bringToFront();
		gestureView.setBackgroundColor(Color.rgb(71, 71, 71));
		gestureView.setGestureColor(Color.WHITE);
		// -----------------------------------------------------------------------------------------------
		
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load()) {
			finish();
		}
		
		
		
		containerLayout.addView(folderView, 0, folderParams);
		containerLayout.addView(delete,1,delParams);
		containerLayout.addView(photoSorter, 2, photoParams);
		containerLayout.addView(gestureView,3,gestureParams);
		
		setContentView(containerLayout);
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
							photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
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
							break;
						case DELETE:
							List<Img> delresources = node.objChanged;
							currentFolder = getFolder(node.currentFolder);
							currentFolder.removeResources(delresources);
							photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
							break;
							
						}
					}
				}
					
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
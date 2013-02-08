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
 */
package edu.gatech.project.touchbrowser;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.testphotosortr.R;


public class PhotoSortrActivity extends Activity {
	
	PhotoSortrView photoSorter;
	HorizontalScrollView folderView;
	RelativeLayout containerLayout;
	private static final int[] IMAGES1 = { R.drawable.m74hubble, R.drawable.catarina, R.drawable.tahiti}; 
	private static final int[] IMAGES2 = { R.drawable.sunset, R.drawable.lake };
	Folder currentFolder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		containerLayout = new RelativeLayout(getApplicationContext());
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.instructions);
		
		LinearLayout flayout = new LinearLayout(this);
		LinearLayout.LayoutParams fParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,60);
		fParams.setMargins(25, 10, 25, 10);
		Folder folder1 = new Folder(this,R.drawable.gesture, IMAGES1);
		Folder folder2 = new Folder(this, R.drawable.gallery, IMAGES2);
		Folder folder3 = new Folder(this, R.drawable.icon);
		
		flayout.addView(folder1, fParams);
		flayout.addView(folder2, fParams);
		flayout.addView(folder3, fParams);
		for(int i =0; i < 60; i++){
			ImageButton folder = new ImageButton(this);
			folder.setImageDrawable(getResources().getDrawable(R.drawable.icon));
			flayout.addView(folder, fParams);
		}
		
		View v = new View(getApplicationContext());
		v.setBackgroundResource(R.drawable.icon);
		//flayout.addView(v,fParams);
		containerLayout.addView(v,new LinearLayout.LayoutParams(10,20));
		containerLayout.removeView(v);
		
		folder1.setOnClickListener(new View.OnClickListener() {			
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
						ImageView animView = null;
						TranslateAnimation transform = null;
						//animView = new ImageView(getApplicationContext());
						
						for(Img img : selectedResources){
							//animView.setVisibility(View.VISIBLE);
							
							animView = new ImageView(getApplicationContext());
							containerLayout.addView(animView,2);
							animView.setImageDrawable(img.getDrawable());
							transform = new TranslateAnimation(img.getCenterX(),clickedFolder.getLeft(), img.getCenterY(), clickedFolder.getTop());
							animView.startAnimation(transform);
				            transform.setDuration(2000);
				            //animView.setVisibility(View.GONE);
				            containerLayout.removeView(animView);
						}
						currentFolder.removeResources(selectedResources);
						photoSorter.loadAgain(getApplicationContext(),currentFolder.getFolderResources());
					}else{
						photoSorter.loadAgain(getApplicationContext(),clickedFolder.getFolderResources());
						currentFolder = clickedFolder;
					}
				}
			}
		});
		
		photoSorter = new PhotoSortrView(this,folder1.getFolderResources());
		currentFolder = folder1;
		photoSorter.setId(2);
		folderView = new HorizontalScrollView(this);
		folderView.setId(1);
		RelativeLayout.LayoutParams folderParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		folderParams.leftMargin = 0;
		folderParams.topMargin = 0;
		
		RelativeLayout.LayoutParams photoParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		photoParams.leftMargin = 0;
		photoParams.topMargin = 0;
		photoParams.addRule(RelativeLayout.BELOW, folderView.getId());
		
		folderView.addView(flayout);
		
		containerLayout.addView(folderView, 0, folderParams);
		containerLayout.addView(photoSorter, 1, photoParams);
		
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
}
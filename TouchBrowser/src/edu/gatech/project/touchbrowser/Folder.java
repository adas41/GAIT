package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ImageButton;

public class Folder extends ImageButton {
	
	List<Integer> folderResources;
	int background;

	public Folder(Context context, int background) {
		super(context);
		folderResources = new ArrayList<Integer>();
		this.background = background;
	}
	
	public boolean addFile(int resourceId){
		return folderResources.add(resourceId);
	}
	
	public boolean removeFile(int resourceId){
		return folderResources.remove(resourceId)!= null ? true : false;
	}
	

}

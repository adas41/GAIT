package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ImageButton;

public class Folder extends ImageButton {
	
	List<Img> folderResources;
	int background;
	Context context;

	public Folder(Context context, int background) {
		super(context);
		folderResources = new ArrayList<Img>();
		this.background = background;
		this.setImageDrawable(getResources().getDrawable(background));
		this.context = context;
	}
	
	public Folder(Context context, int background, int[] initResources) {
		super(context);
		folderResources = new ArrayList<Img>(initResources.length);
		for(int resource : initResources){
			folderResources.add(new Img(context,resource,context.getResources()));
		}
		this.background = background;
		this.setImageDrawable(getResources().getDrawable(background));
		this.context = context;
	}
	
	public boolean addResource(int resourceId){
		return folderResources.add(new Img(context,resourceId,context.getResources()));
	}
	
	public void addResources(int[] resources){
		for(int res : resources)
			folderResources.add(new Img(context,res,context.getResources()));
	}
	
	public void moveResources(List<Img> newResources){
		for(Img img : newResources){
			img.toggleSelected();
			folderResources.add(img);
		}
	}
	
	public void removeResources(List<Img> resources){
		for(Img img : resources)
			removeResource(img);
	}
	
	
	public boolean removeResource(Img resource){
		return folderResources.remove(resource) ? true : false;
	}
	
	public List<Img> getFolderResources(){
		return folderResources;
	}
	
	public List<Img> getSelectedresources(){
		List<Img> result = null;
		for(Img img : getFolderResources()){
			if(img.isResSelected()){
				if(result == null){
					result = new ArrayList<Img>();
				}
				result.add(img);
			}
		}
		return result;
	}

	/**
	 * @param folderResources the folderResources to set
	 */
	public void setFolderResources(List<Img> folderResources) {
		this.folderResources = folderResources;
	}
	

}

package edu.gatech.project.touchbrowser;

import java.util.ArrayList;
import java.util.List;

public class ActivityHolder {
	
	List<Node> activity;
	int index;
	
	public ActivityHolder(){
		this.activity = new ArrayList<Node>();
		index = -1;
	}
	
	public Node undo(){
		if(index != -1 && activity.get(index) != null){
			return activity.get(index--);
		}
		return null;
	}
	
	public Node redo(){
		if(index + 1 < activity.size()){
			return(activity.get(++index));
		}
		return null;
	}
	
	public void addActivity(Node node){
		this.activity.add(++index, node);
	}
	
	
		

}

class Node{
	
	ActivityType type;
	int currentFolder;
	int folderChanged;
	List<Img> objChanged;
	
	public Node(ActivityType type, int currFolder, int folderChanged, List<Img> objects){
		this.type = type;
		this.currentFolder = currFolder;
		this.folderChanged = folderChanged;
		this.objChanged = objects;
	}
	
	public Node(ActivityType type, int currentFolder, List<Img> objects){
		this.type = type;
		this.currentFolder = currentFolder;
		this.objChanged = objects;
	}
	
}

enum ActivityType{
	DRAG, RESIZE, MOVE, DELETE
}
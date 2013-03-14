package edu.gatech.project.touchbrowser;

import java.util.Comparator;

import android.gesture.Prediction;

/*
 * comparator class to sort the gesture Predictions in descending order based on score
 */
public class GestureComparator implements Comparator<Prediction> {
	

	@Override
	public int compare(Prediction pred0, Prediction pred1) {
		
		Double score0 = pred0.score;
		Double score1 = pred1.score;
		
		return score1.compareTo(score0); 
	}

}

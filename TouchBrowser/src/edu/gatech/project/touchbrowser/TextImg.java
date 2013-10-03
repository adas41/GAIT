package edu.gatech.project.touchbrowser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class TextImg extends Img {
	
	protected String text;
	
	public TextImg(Context context, Drawable drawable, Resources res, String text) {
		super(context, drawable, res);
		this.text = text;
	}

}

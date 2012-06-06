package com.gmail.at.faint545.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gmail.at.faint545.R.styleable;

public class StyledTextView extends TextView {
	// For debugging
	private static final String LOGTAG = StyledTextView.class.getSimpleName();
	
	public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFontFace(context, attrs);
	}

	public StyledTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFontFace(context, attrs);
	}

	public StyledTextView(Context context) {
		super(context);
	}

	private void setCustomFontFace(Context context, AttributeSet attrs) {
		if(!isInEditMode()) {
			TypedArray attrArray = context.obtainStyledAttributes(attrs, styleable.StyledTextView);
			String assetPath = attrArray.getString(styleable.StyledTextView_fontface);			
			setTypeface(Typeface.createFromAsset(getContext().getAssets(), assetPath));
		}
	}
}
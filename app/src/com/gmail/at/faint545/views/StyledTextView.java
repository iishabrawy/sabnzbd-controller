package com.gmail.at.faint545.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
import com.gmail.at.faint545.R.styleable;
import com.gmail.at.faint545.utils.AllCapsTransformationMethod;

public class StyledTextView extends TextView {

  private final boolean isICS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

	// For debugging
	private static final String LOGTAG = "StyledTextView";
	
	public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyStyle(context, attrs);
	}

	public StyledTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyStyle(context, attrs);
	}

	public StyledTextView(Context context) {
		super(context);
	}

	private void applyStyle(Context context, AttributeSet attrs) {
		if(!isInEditMode()) {
			TypedArray attrArray = context.obtainStyledAttributes(attrs, styleable.StyledTextView);

      String fontFacePath = attrArray.getString(styleable.StyledTextView_fontface);
			setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontFacePath));

      if(!isICS) {
        boolean allCaps = attrArray.getBoolean(styleable.StyledTextView_textAllCaps,false);
        setAllCapsCompat(allCaps);
      }
		}
	}

  private void setAllCapsCompat(boolean allCaps) {
    if(allCaps)
      setTransformationMethod(new AllCapsTransformationMethod(getContext()));
    else
      setTransformationMethod(null);
  }
}
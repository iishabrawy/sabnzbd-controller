package com.gmail.at.faint545.views;

import java.util.Hashtable;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.gmail.at.faint545.R.styleable;

public class StyledTextView extends TextView {
  // For debugging
  private static final String LOGTAG = StyledTextView.class.getSimpleName();

  private static final Hashtable<String,Typeface> fontCache = new Hashtable<String, Typeface>();

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
      String fontFace = attrArray.getString(styleable.StyledTextView_fontface);
      Typeface typeFace = get(context, fontFace);
      setTypeface(typeFace);
    }
  }

  /*
   * We cache fonts here to avoid some memory leaks.
   * See http://code.google.com/p/android/issues/detail?id=9904
   */
  public static Typeface get(Context context,String assetPath) {
    synchronized (fontCache) {
      if(!fontCache.contains(assetPath)) {
        try {
          Typeface typeFace = Typeface.createFromAsset(context.getAssets(), assetPath);
          fontCache.put(assetPath, typeFace);
        }
        catch (Exception e) {
          Log.e(LOGTAG, "Error creating Typeface: " + e.getMessage());
          return null;
        }
      }
      return fontCache.get(assetPath);
    }
  }
}

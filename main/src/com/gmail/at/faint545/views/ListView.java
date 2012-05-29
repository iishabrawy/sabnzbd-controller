package com.gmail.at.faint545.views;

import com.gmail.at.faint545.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class ListView extends android.widget.ListView {

  private int defaultFadeColor, fadeColor;

  public ListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setStyleable(context, attrs);
  }

  public ListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setStyleable(context, attrs);
  }

  public ListView(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  @Override
  public int getSolidColor() {
    defaultFadeColor = super.getSolidColor();
    return fadeColor;
  }

  public void setFadeColor(int color) {
    fadeColor = color;
  }

  private void setStyleable(Context context, AttributeSet attrs) {
    TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ListView);
    for(int i = 0, max = array.getIndexCount(); i < max; i++) {
      int attr = array.getIndex(i);
      switch(attr) {
      case R.styleable.ListView_fadingColor:
        fadeColor = array.getColor(attr, defaultFadeColor);
        break;
      }
    }
  }
}

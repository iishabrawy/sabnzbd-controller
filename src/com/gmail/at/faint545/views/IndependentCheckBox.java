package com.gmail.at.faint545.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

public class IndependentCheckBox extends CheckBox {

  public IndependentCheckBox(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // TODO Auto-generated constructor stub
  }

  public IndependentCheckBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub
  }

  public IndependentCheckBox(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void setPressed(boolean pressed) {
    if(pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
      return;
    }
    super.setPressed(pressed);
  }
}

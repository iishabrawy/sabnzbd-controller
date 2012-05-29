package com.gmail.at.faint545.adapters;

import android.widget.BaseAdapter;

public abstract class SabAdapter extends BaseAdapter {
  public boolean isEven(int position) {
    return position%2 == 0 ? true : false;
  }
  
  public abstract void setAllChecked(boolean isChecked);
  public abstract String getCheckedIds();
  public abstract void reset();
}

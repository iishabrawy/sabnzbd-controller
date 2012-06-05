package com.gmail.at.faint545.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class SabFragmentActivity extends SherlockFragmentActivity {
  public abstract void downloadHistory();
  public abstract void downloadQueue();
  public abstract void showErrorDialog(CharSequence message);
}

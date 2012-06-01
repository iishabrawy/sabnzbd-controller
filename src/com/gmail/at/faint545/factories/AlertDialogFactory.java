package com.gmail.at.faint545.factories;

import com.gmail.at.faint545.R;
import com.gmail.at.faint545.views.ProgressDialog;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogFactory {
  
  public static AlertDialog getErrorInstance(Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.title__error);
    builder.setPositiveButton(R.string.ok, null);
    return builder.create();
  }
  
  public static ProgressDialog getProgressInstance(Context context) {
    return new ProgressDialog(context);
  }
}

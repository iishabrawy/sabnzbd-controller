package com.gmail.at.faint545.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gmail.at.faint545.R;

public class InputDialogBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {
  private OnClickListener positiveListener, negativeListener;  
  private Context context;
  
  /* Custom view components */
  private EditText inputText;

  public interface OnClickListener {
    public void onClick(String result, DialogInterface dialog);
  }
  
  public InputDialogBuilder(Context context, int theme) {
    super(context, theme);
    this.context = context;
    setupView(context);
  }

  public InputDialogBuilder(Context context) {
    super(context);
    this.context = context;
    setupView(context);
  }

  private void setupView(Context context) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.input_dialog, null);
    inputText = (EditText) v.findViewById(R.id.input_dialog_input);
    inputText.setHorizontallyScrolling(true);
    setView(v);
  }
  
  public void setOkButton(int resId, OnClickListener listener) {
    setOkButton(context.getString(resId), listener);
  }
  
  public void setOkButton(CharSequence label, OnClickListener listener) {
    positiveListener = listener;
    setPositiveButton(label, this);
  }
  
  public void setCancelButton(CharSequence label, OnClickListener listener) {
    negativeListener = listener;
    setNegativeButton(label, this);
  }
  
  public void setInputType(int inputType) {
    inputText.setInputType(inputType);
  }
  
  public void setValue(CharSequence text) {
    inputText.setText(text);
  }

  @Override
  public Builder setSingleChoiceItems(final CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
    setPositiveButton(null, null);
    setNegativeButton(null, null);
    return super.setSingleChoiceItems(items, checkedItem, listener);
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    switch(which) {
    case DialogInterface.BUTTON_POSITIVE:
      positiveListener.onClick(inputText.getText().toString(),dialog);
      break;
    case DialogInterface.BUTTON_NEGATIVE:
      negativeListener.onClick(inputText.getText().toString(),dialog);
      break;
    }
  }

  public void setTransformationMethod(PasswordTransformationMethod method) {
    inputText.setTransformationMethod(method);
  }

  public void setError(CharSequence error) {
    inputText.setError(error);
  }
}
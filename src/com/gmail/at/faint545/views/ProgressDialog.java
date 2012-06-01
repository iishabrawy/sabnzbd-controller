package com.gmail.at.faint545.views;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gmail.at.faint545.R;

public class ProgressDialog extends android.app.ProgressDialog {
  private CharSequence message;
  private TextView messageTextView;
  
  public ProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  public ProgressDialog(Context context) {
    super(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.progress_dialog);    
    setDimAmount();    
    messageTextView = (TextView) findViewById(R.id.progress_dialog__message);    
  }

  private void setDimAmount() {
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.dimAmount = 0.8f;
    getWindow().setAttributes(lp);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }

  @Override
  public void show() {
  	super.show();
    if(messageTextView != null)
      messageTextView.setText(message);    
  }

  @Override
  public void setMessage(CharSequence message) {
    this.message = message;
  }
}

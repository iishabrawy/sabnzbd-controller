package com.gmail.at.faint545.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gmail.at.faint545.R;

public class CompoundEditTextPreference extends DialogPreference {

	private EditText mEditText;	
	private Button mButton;
	private String mText;
	
	private CharSequence mCompoundButtonText;
	private View.OnClickListener mCompoundButtonCallback;
	
	private static final String LOGTAG = "CompoundEditTextPreference";
	private static final int mDialogLayoutResource = R.layout.apikey_preference;

	public CompoundEditTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(mDialogLayoutResource);
	}

	public CompoundEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(mDialogLayoutResource);
	}
	
	@Override
	protected View onCreateDialogView() {
		View root = super.onCreateDialogView();
		mEditText = (EditText) root.findViewById(R.id.edit);
		mButton = (Button) root.findViewById(R.id.button);
		return root;
	}

	@Override
  protected void showDialog(Bundle state) {
	  super.showDialog(state);
  }

  @Override
	protected void onBindDialogView(View view) {
		mEditText.setText(mText);
		mButton.setText(mCompoundButtonText);
		mButton.setOnClickListener(mCompoundButtonCallback);
	}
	
	/**
	 * Returns the {@link EditText} widget that will be shown in the dialog.
	 * 
	 * @return The {@link EditText} widget that will be shown in the dialog.
	 */
	public EditText getEditText() {
		return mEditText;
	}

	public void setCompoundButtonText(CharSequence text) {
		mCompoundButtonText = text;
	}

	public void setCompoundButtonListener(View.OnClickListener callback) {
		mCompoundButtonCallback = callback;
	}

	/**
   * Saves the text to the {@link SharedPreferences}.
   * 
   * @param text The text to save
   */
	public void setText(String text) {
		mText = text;
	}
	
  /**
   * Gets the text from the {@link SharedPreferences}.
   * 
   * @return The current preference value.
   */
  public String getText() {
      return mText;
  }

	@Override
  public void onClick(DialogInterface dialog, int which) {
  	switch(which)	 {
  	case DialogInterface.BUTTON_POSITIVE:
  		mText = mEditText.getText().toString();
  		callChangeListener(mText);
  		break;
  	}
  	super.onClick(dialog, which);
  }	
}
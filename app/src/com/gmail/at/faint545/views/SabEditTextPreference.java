package com.gmail.at.faint545.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alexfu.holo.widgets.HoloAlertDialog;

public class SabEditTextPreference extends DialogPreference {

	private HoloAlertDialog.Builder mBuilder;

	public SabEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SabEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateDialogView() {
		int dialogLayoutResource = getDialogLayoutResource();
		if (dialogLayoutResource == 0) {
			return null;
		}

		LayoutInflater inflater = LayoutInflater.from(getContext());
		return inflater.inflate(dialogLayoutResource, null);
	}

	@Override
	protected void showDialog(Bundle state) {
		Context context = getContext();

		mBuilder = new HoloAlertDialog.Builder(context)
		.setTitle(getDialogTitle())
		.setIcon(getDialogIcon())
		.setPositiveButton(getPositiveButtonText(), this)
		.setNegativeButton(getNegativeButtonText(), this);

		View contentView = onCreateDialogView();
		if (contentView != null) {
			onBindDialogView(contentView);
			mBuilder.setView(contentView);
		}
		else {
			mBuilder.setMessage(getDialogMessage());
		}

		onPrepareDialogBuilder(mBuilder);

		// getPreferenceManager().registerOnActivityDestroyListener(this);

		// Create the dialog
		final Dialog dialog = mBuilder.create();
		if (state != null) {
			dialog.onRestoreInstanceState(state);
		}

		if (needInputMethod()) {
			requestInputMethod(dialog);
		}
		dialog.setOnDismissListener(this);
		dialog.show();
	}
	
	public void onClick(DialogInterface dialog, int which) {
		Log.i("SabEditTextPreference",which + "");
	}

	/**
	 * Sets the required flags on the dialog window to enable input method window to show up.
	 */
	private void requestInputMethod(Dialog dialog) {
		Window window = dialog.getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}	

	/**
	 * Returns whether the preference needs to display a soft input method when the dialog
	 * is displayed. Default is false. Subclasses should override this method if they need
	 * the soft input method brought up automatically.
	 * @hide
	 */
	protected boolean needInputMethod() {
		return false;
	}	
}
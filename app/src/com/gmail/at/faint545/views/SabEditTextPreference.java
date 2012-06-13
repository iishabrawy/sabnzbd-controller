package com.gmail.at.faint545.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.text.TextUtils;

import android.widget.EditText;
import android.widget.TextView;
import com.alexfu.holo.widgets.HoloAlertDialog;
import com.alexfu.holo.widgets.HoloEditText;
import com.gmail.at.faint545.R;

public class SabEditTextPreference extends EditTextPreference {

    private HoloEditText mEditText;

    public SabEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mEditText = new HoloEditText(context);
    }

    public SabEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new HoloEditText(context);
    }

    public SabEditTextPreference(Context context) {
        super(context);
        mEditText = new HoloEditText(context);
    }

    @Override
    protected View onCreateDialogView() {
        int layoutResId = getDialogLayoutResource();
        if(layoutResId == 0) {
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        return inflater.inflate(layoutResId,null);
    }

    @Override
    protected void onBindDialogView(View view) {
        View dialogMessageView = view.findViewById(android.R.id.message);
        if (dialogMessageView != null) {
            final CharSequence message = getDialogMessage();
            int newVisibility = View.GONE;

            if (!TextUtils.isEmpty(message)) {
                if (dialogMessageView instanceof TextView) {
                    ((TextView) dialogMessageView).setText(message);
                }

                newVisibility = View.VISIBLE;
            }

            if (dialogMessageView.getVisibility() != newVisibility) {
                dialogMessageView.setVisibility(newVisibility);
            }
        }
        mEditText.setText(getText());
        ViewParent oldParent = mEditText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(mEditText);
            }
            onAddEditTextToDialogView(view, mEditText);
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        HoloAlertDialog.Builder builder = new HoloAlertDialog.Builder(getContext());
        builder.setTitle(getDialogTitle());
        builder.setIcon(getDialogIcon());
        builder.setPositiveButton(getPositiveButtonText(),this);
        builder.setNegativeButton(getNegativeButtonText(),this);

        View contentView = onCreateDialogView();
        if(contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        }
        else {
            builder.setMessage(getDialogMessage());
        }

        Dialog dialog = builder.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which)	 {
            case DialogInterface.BUTTON_POSITIVE:
                String text = getEditText().getText().toString();
                setText(text);
                callChangeListener(text);
                break;
        }
        dialog.dismiss();
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }
}
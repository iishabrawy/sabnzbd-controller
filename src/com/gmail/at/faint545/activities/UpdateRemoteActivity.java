package com.gmail.at.faint545.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.database.RemoteTableManager;
import com.gmail.at.faint545.utils.StringUtils;
import com.gmail.at.faint545.views.CompoundEditTextPreference;
import com.gmail.at.faint545.zxing.IntentIntegrator;
import com.gmail.at.faint545.zxing.IntentResult;

public class UpdateRemoteActivity extends SherlockPreferenceActivity implements OnPreferenceChangeListener {
	/* Keys for Bundles or other things... */
	public static final String KEY_REMOTE = "remote";
	private static final String LOGTAG = "UpdateRemoteActivity";

	private EditTextPreference mNicknamePref,mHostPref,mPortPref,mUsernamePref,mPasswordPref;
	private CompoundEditTextPreference mAPIKeyPref;
	private ListPreference mRefreshIntervalPref;
	private Remote mRemote;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getListView().setCacheColorHint(0x00000000); // Set cache color hint to transparent.
		addPreferencesFromResource(R.xml.remote_preference); // We still use this for compatibility.
		getRemoteFromIntent();
		setupPreferences();		
	}

	/**
	 * Setup each preference here and attach a listener to
	 * each one. Deprecated methods are still used for
	 * compatibility with devices below API 11.
	 */
	@SuppressWarnings("deprecation")
	private void setupPreferences() {
		mNicknamePref = (EditTextPreference) findPreference("nickname");
		mNicknamePref.setOnPreferenceChangeListener(this);
		mNicknamePref.setText(mRemote.getPreference(Remote.NICKNAME));

		mHostPref = (EditTextPreference) findPreference("host");
		mHostPref.setOnPreferenceChangeListener(this);
		mHostPref.setText(mRemote.getPreference(Remote.HOST));

		mPortPref = (EditTextPreference) findPreference("port");
		mPortPref.setOnPreferenceChangeListener(this);	
		mPortPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		mPortPref.setText(mRemote.getPreference(Remote.PORT));

		mRefreshIntervalPref = (ListPreference) findPreference("refresh_interval");
		mRefreshIntervalPref.setOnPreferenceChangeListener(this);
		String refreshInterval = mRemote.getPreference(Remote.REFRESH_INTERVAL);
		if(refreshInterval != null)
			mRefreshIntervalPref.setValue(refreshInterval);		

		mAPIKeyPref = (CompoundEditTextPreference) findPreference("apikey");
		mAPIKeyPref.setOnPreferenceChangeListener(this);
		mAPIKeyPref.setText(mRemote.getPreference(Remote.APIKEY));
		mAPIKeyPref.setCompoundButtonText("QR");
		mAPIKeyPref.setCompoundButtonListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentIntegrator integrator = new IntentIntegrator(UpdateRemoteActivity.this);
				integrator.initiateScan();				
			}
		});

		mUsernamePref = (EditTextPreference) findPreference("username");
		mUsernamePref.setOnPreferenceChangeListener(this);
		mUsernamePref.setText(mRemote.getPreference(Remote.USERNAME));

		mPasswordPref = (EditTextPreference) findPreference("password");
		mPasswordPref.setOnPreferenceChangeListener(this);
		mPasswordPref.getEditText().setTransformationMethod(new PasswordTransformationMethod());
		mPasswordPref.setText(mRemote.getPreference(Remote.PASSWORD));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.update_remote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_save:
			if(validatePreferences()) {
				RemoteTableManager tm = new RemoteTableManager(this);
				tm.open();
				boolean result = tm.insertOrUpdate(mRemote);
				tm.close();
				onBackPressed();
			}
			else {
				Toast.makeText(this, R.string.remote_fail_save, Toast.LENGTH_LONG).show();
			}
			break;
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Verifies that the preferences match the requirements or
	 * constraints of the back-end data store. 
	 */
	private boolean validatePreferences() {
		boolean case1 = StringUtils.isEmpty(mNicknamePref.getText());
		boolean case2 = StringUtils.isEmpty(mHostPref.getText());
		boolean case3 = StringUtils.isEmpty(mPortPref.getText());

		if(case1 && case2 && case3)
			return false;
		else
			return true;
	}

	/**
	 * Retrieve the Remote from the calling Activity.
	 */
	private void getRemoteFromIntent() {
		mRemote = getIntent().getParcelableExtra(KEY_REMOTE);
		if(mRemote == null) {
			mRemote = new Remote();
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
				resultCode, data);
		mAPIKeyPref.getEditText().setText(scanResult.getContents());
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.equals(mHostPref)) {
			newValue = ((String) newValue).replaceAll("^http://", ""); // Strip Http:// if user typed it in.
		}
		mRemote.updatePreference(preference.getKey(), (String) newValue);
		return true;
	}
}
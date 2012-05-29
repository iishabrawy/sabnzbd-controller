package com.gmail.at.faint545.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.at.faint545.HistoryItem;
import com.gmail.at.faint545.NzoItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.activities.RemoteMessageHandler.RemoteMessageListener;
import com.gmail.at.faint545.adapters.ViewRemotePagerAdapter;
import com.gmail.at.faint545.factories.AlertDialogFactory;
import com.gmail.at.faint545.fragments.CheckChangedListener;
import com.gmail.at.faint545.fragments.HistoryFragment;
import com.gmail.at.faint545.fragments.QueueFragment;
import com.gmail.at.faint545.fragments.SabFragmentActivity;
import com.gmail.at.faint545.fragments.SabListFragment;
import com.gmail.at.faint545.fragments.StatusFragment;
import com.gmail.at.faint545.receiver.AlarmReceiver;
import com.gmail.at.faint545.services.DownloadService;
import com.gmail.at.faint545.utils.InputDialogBuilder;
import com.gmail.at.faint545.views.ProgressDialog;
import com.viewpagerindicator.TitlePageIndicator;

public class ViewRemoteActivity extends SabFragmentActivity implements CheckChangedListener, ServiceConnection, RemoteMessageListener {

	public static final String EXTRA = "extra";

	private ViewRemotePagerAdapter adapter;
	private ViewPager viewPager;
	private TitlePageIndicator pageIndicator;
	private QueueFragment queueFragment;
	private HistoryFragment historyFragment;
	private StatusFragment statusFragment;
	private Remote mRemote;

	// Flags to determine if an attempt to
	// get new data has been completed.
	private boolean queueDownloadCompleted, historyDownloadCompleted;

	// Objects used for Refresh action item.
	private Animation rotateClockwise;
	private ImageView refreshActionView;
	private MenuItem refreshItem;

	// AlertDialogs
	private AlertDialog errorDialog;
	private ProgressDialog progressDialog;

	private SabListFragment currentVisibleFragment;

	private PendingIntent mRecurringDownload;

	private ActionMode mActionMode;

	private Messenger mMessenger = new Messenger(new RemoteMessageHandler(this));
	private boolean mIsBoundToService = false; // Bound to a Service?
	private Messenger mServiceMessenger = null;

	public static final String LOGTAG = "ViewRemoteActivity";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);    
		mRemote = getIntent().getParcelableExtra(EXTRA); // Get data from Intent.		
		setContentView(R.layout.view_remote);
		setupActionBar();    
		errorDialog = AlertDialogFactory.getErrorInstance(this);
		progressDialog = AlertDialogFactory.getProgressInstance(this);
		setupPager();
		showDialog(progressDialog, getString(R.string.loading));
		downloadData();
	}

	private void downloadData() {
		bindToService();

    Intent downloader = new Intent();
    downloader.putExtra("url", mRemote.getUrl());
    downloader.putExtra("username", mRemote.getUsername());
    downloader.putExtra("password", mRemote.getPassword());
    downloader.putExtra("apikey", mRemote.getAPIKey());

    if(mRemote.hasRefreshInterval()) {
      downloader.setClass(getApplicationContext(),AlarmReceiver.class);
      startRecurringDownload(downloader);
    }
    else {
      downloader.setClass(getApplicationContext(),DownloadService.class);
      startService(downloader);
    }
	}

  private void startRecurringDownload(Intent downloader) {
    Calendar downloadTime = Calendar.getInstance(); // When to download
    mRecurringDownload = PendingIntent.getBroadcast(getApplicationContext(), 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);

    AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, downloadTime.getTimeInMillis(), mRemote.getRefreshInterval(), mRecurringDownload);
  }

  private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(mRemote.getPreference(Remote.NICKNAME));
	}

	private void setupPager() {
		viewPager = (ViewPager) findViewById(R.id.pager);    
		initializeFragments();
		setupAdapter();    
		viewPager.setAdapter(adapter);
		setupIndicator();
	}

	private void initializeFragments() {
		queueFragment = QueueFragment.getInstance(mRemote);
		historyFragment = HistoryFragment.getInstance(mRemote);
		statusFragment = StatusFragment.getInstance(mRemote);
		currentVisibleFragment = queueFragment;
	}

	private void setupAdapter() {
		adapter = new ViewRemotePagerAdapter(this,getSupportFragmentManager());    
		adapter.addFragments(queueFragment,historyFragment,statusFragment);
	}

	private void setupIndicator() {
		pageIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		pageIndicator.setViewPager(viewPager);
		pageIndicator.setOnPageChangeListener(onPageChanged);
	}

	private OnPageChangeListener onPageChanged = new OnPageChangeListener() {    
		@Override
		public void onPageSelected(int position) {
			currentVisibleFragment = getFragmentAt(position);
			if(mActionMode != null)
				mActionMode.finish();
		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2) {}

		@Override
		public void onPageScrollStateChanged(int position) {}
	};  

	@Override
	public boolean onCreateOptionsMenu(Menu optionMenu) {
		getSupportMenuInflater().inflate(R.menu.view_remote, optionMenu); // Inflate default menu   
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
			return true;
		case R.id.menu__refresh:
			refreshItem = item;
			onRefreshData();
			break;
		case R.id.menu__speed_limit:
			InputDialogBuilder builder = new InputDialogBuilder(this);
			builder.setTitle(R.string.title__speed);
			builder.setInputType(InputType.TYPE_CLASS_NUMBER);
			builder.setOkButton(R.string.ok, new InputDialogBuilder.OnClickListener() {        
				@Override
				public void onClick(String result, DialogInterface dialog) {

				}
			});
			builder.setNegativeButton(R.string.cancel, null);
			builder.create().show();
			break;
		}
		return true;
	}

	/*
	 * Refresh data from SABNzbd application in the background  
	 */
	private void onRefreshData() {    
		// We don't specify ActionView via XML like in http://goo.gl/R02Iq.
		// See http://goo.gl/PGnv9.
		if(refreshActionView == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			refreshActionView = (ImageView) inflater.inflate(R.layout.refresh_actionview, null);
		}

		if(rotateClockwise == null) {
			rotateClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
			rotateClockwise.setAnimationListener(rotateClockwiseListener);
		}

		// Reset download status
		historyDownloadCompleted = false;
		queueDownloadCompleted = false;

		refreshActionView.startAnimation(rotateClockwise);
		refreshItem.setActionView(refreshActionView);
		downloadData();
	}

	/*
	 * Set visibility of the root view...
	 */
	private void setPagerVisibility(int visibility) {
		View root = findViewById(R.id.pager);
		if(root.getVisibility() != visibility) {
			root.setVisibility(visibility);
		}
	}

	////////////////////////////////////////////////////////////////////////
	// Dialog helpers BEGIN
	////////////////////////////////////////////////////////////////////////

	private void showDialog(AlertDialog dialog, String message) {
		if(!dialog.isShowing()) {
			dialog.setMessage(message);
			dialog.show();
		}
	}

	/*
	 * A helper method to dismiss any showing
	 * dialogs.
	 */
	private void dismissDialogs() {
		if(progressDialog != null) {
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}

		if(errorDialog != null) {
			if(errorDialog.isShowing()) {
				errorDialog.dismiss();
			}
		}			
	}

	////////////////////////////////////////////////////////////////////////
	// Dialog helpers END
	////////////////////////////////////////////////////////////////////////	

	/*
	 * Animation listener
	 */
	private AnimationListener rotateClockwiseListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {}

		@Override
		public void onAnimationRepeat(Animation animation) {}

		/*
		 * A hacked way to stop the animation after it has complete
		 * it's current cycle.
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			if(queueDownloadCompleted && historyDownloadCompleted) {
				refreshActionView.clearAnimation();
				refreshItem.setActionView(null);
			}
			else {
				refreshActionView.startAnimation(rotateClockwise);
			}
		}
	};

	/*
	 * A callback for ActionMode
	 */
	private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			historyFragment.resetAdapter();
			queueFragment.resetAdapter();
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.action_mode_base, menu);
			currentVisibleFragment.inflateActionModeMenu(mode, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			switch(item.getItemId()) {
			case R.id.delete:        
				// Delete all items
				mActionMode.finish(); // Remove the ActionMode
				break;
			case R.id.select_all:
				// Check all items
				break;
			case R.id.pause:
				// Pause
				mActionMode.finish();
				break;
			case R.id.resume:
				// Resume
				mActionMode.finish();
				break;
			}
			return true;
		}
	};

	@Override
	protected void onPause() {
		if(mActionMode != null) {
			mActionMode.finish();
		}
		super.onPause();
	}

	/*
	 * A callback that gets triggered when an item has been
	 * checked or unchecked so we can properly handle
	 * changes for ActionMode.
	 */
	@Override
	public void onCheckChanged(boolean isChecked, int checkCount) {
		if(mActionMode == null) {
			mActionMode = startActionMode(actionModeCallback);
		}
		else if(checkCount == 0) {
			mActionMode.finish();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
	}

	private SabListFragment getFragmentAt(int position) {
		switch(position) {
		case 0: return queueFragment;
		case 1: return historyFragment;
		default: return null;
		}
	}

	//////////////////////////////////////////////////
	// Service functions BEGIN
	//////////////////////////////////////////////////

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mServiceMessenger = new Messenger(service);
		// textStatus.setText("Attached.");
		try {
			Message msg = Message.obtain(null, DownloadService.MSG_REGISTER_CLIENT);
			msg.replyTo = mMessenger;
			mServiceMessenger.send(msg);
		} 
		catch (RemoteException e) {
			// In this case the service has crashed before we could even do anything with it
		} 
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mServiceMessenger = null;
	}

	/**
	 * Bind this Activity to a Service
	 */
	private void bindToService() {
		bindService(new Intent(this, DownloadService.class), this, Context.BIND_AUTO_CREATE);
		mIsBoundToService = true;
	}

	/**
	 * Unregister this Activity from a Service
	 */     
	private void unBindFromService() {
		if (mIsBoundToService) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null, DownloadService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} 
				catch (RemoteException e) {
					// There is nothing special we need to do if the service has crashed.
				}
			}
			// Detach our existing connection.
			unbindService(this);
			mIsBoundToService = false;
		}
	}

	//////////////////////////////////////////////////
	// Service functions END
	//////////////////////////////////////////////////

	@Override
	protected void onDestroy() {
		/*
		 * Cancel any alarms and unregister from bound Service. 
		 */
		
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		manager.cancel(mRecurringDownload);	
		unBindFromService();
		super.onDestroy();
	}

	@Override
	public void onReceiveHistory(JSONObject result) {
		ArrayList<NzoItem> historyItems = new ArrayList<NzoItem>();
		try {
			JSONArray arrayObject = result.getJSONObject("history").getJSONArray("slots");
			for(int i = 0, max = arrayObject.length(); i < max; i++) {
				NzoItem item = new HistoryItem().buildFromJson(arrayObject.getJSONObject(i));
				historyItems.add(item);
			}
			historyFragment.updateItems(historyItems);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		finally {
			historyDownloadCompleted = true;
			onFinishConnection();
		}	  	
	}

	@Override
	public void onReceiveQueue(JSONObject result) {
		ArrayList<NzoItem> queueItems = new ArrayList<NzoItem>();
		try {
			JSONArray arrayObject = result.getJSONObject("queue").getJSONArray("slots");
			for(int i = 0, max = arrayObject.length(); i < max; i++) {
				NzoItem item = new HistoryItem().buildFromJson(arrayObject.getJSONObject(i));
				queueItems.add(item);
			}
			queueFragment.updateItems(queueItems);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		finally {
			queueDownloadCompleted = true;
			onFinishConnection();
		}		
	}

	@Override
	public void onDownloadFailure(String message) {
		onFinishConnection();
		showDialog(errorDialog, message);
	}

	/**
	 * When data connection has completed, regardless if it was
	 * successful or not, clean up the UI.
	 */
	private void onFinishConnection() {
		setPagerVisibility(View.VISIBLE);
		dismissDialogs();
	}
}
package com.gmail.at.faint545.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.os.*;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.at.faint545.R;
import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.adapters.ViewRemotePagerAdapter;
import com.gmail.at.faint545.connectors.RequestHandler.Request;
import com.gmail.at.faint545.fragments.HistoryFragment;
import com.gmail.at.faint545.fragments.QueueFragment;
import com.gmail.at.faint545.fragments.SabListFragment;
import com.gmail.at.faint545.interfaces.CheckChangedListener;
import com.gmail.at.faint545.nzo.NzoItem;
import com.gmail.at.faint545.receivers.AlarmReceiver;
import com.gmail.at.faint545.services.RequestReceiver;
import com.gmail.at.faint545.views.InputDialogBuilder;
import com.gmail.at.faint545.views.ProgressDialog;
import com.viewpagerindicator.TitlePageIndicator;
import java.util.Calendar;
import java.util.List;

public class ViewRemoteActivity extends SabFragmentActivity implements CheckChangedListener, ServiceConnection {

	public static final String EXTRA = "extra";

	private ViewRemotePagerAdapter adapter;
	private ViewPager viewPager;
	private TitlePageIndicator pageIndicator;
	private QueueFragment mQueueFragment;
	private HistoryFragment mHistoryFragment;
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

	private Messenger mMessenger = new Messenger(new IncomingHandler());
	private boolean mIsBoundToService = false; // Bound to a Service?
	private Messenger mServiceMessenger = null;

	public static final int QUEUE = 0, HISTORY = 1, STATUS = 2, ERROR = 3;

	public static final String LOGTAG = "ViewRemoteActivity";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		bindToService();
		mRemote = getIntent().getParcelableExtra(EXTRA); // Get data from Intent.		
		setContentView(R.layout.view_remote);
		setupActionBar();
		setupPager();
		scheduleRecurringDownload();
	}

	/**
	 * Kicks off the download service only once.
	 */
	private void startOneTimeDownload() {
		executeRequest(Request.toInt(Request.DOWNLOAD), null);
	}

	/**
	 * Schedule a download (via BroadcastIntent) to execute periodically. 
	 * Frequency of download is determined by the remote's settings.
	 */
	private void scheduleRecurringDownload() {
		Intent targetIntent = new Intent();
		targetIntent.putExtras(buildBaseMessage());

		if(mRemote.hasRefreshInterval()) {
			Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);

			Calendar downloadTime = Calendar.getInstance(); // When to download
			downloadTime.setTimeInMillis(downloadTime.getTimeInMillis()+mRemote.getRefreshInterval());
			mRecurringDownload = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, downloadTime.getTimeInMillis(), mRemote.getRefreshInterval(), mRecurringDownload);
		}
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
		mQueueFragment = QueueFragment.getInstance(mRemote);
		mHistoryFragment = HistoryFragment.getInstance(mRemote);
		currentVisibleFragment = mQueueFragment;
	}

	private void setupAdapter() {
		adapter = new ViewRemotePagerAdapter(this,getSupportFragmentManager());    
		adapter.addFragments(mQueueFragment,mHistoryFragment);
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
			onRefreshPressed();
			break;
		case R.id.menu__speed_limit:
			InputDialogBuilder builder = new InputDialogBuilder(this);
			builder.setTitle(R.string.title__speed);
			builder.setInputType(InputType.TYPE_CLASS_NUMBER);
			builder.setOkButton(R.string.ok, new InputDialogBuilder.OnClickListener() {        
				@Override
				public void onClick(String result, DialogInterface dialog) {
					Bundle b = new Bundle();
					b.putParcelable("remote", mRemote);
					b.putString("value",result);
					executeRequest(Request.toInt(Request.SET_SPEED_LIMIT), b);
					Toast.makeText(getApplicationContext(),R.string.setting_speed_limit,Toast.LENGTH_SHORT).show();
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
	private void onRefreshPressed() {    
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
		startOneTimeDownload();
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
			mHistoryFragment.resetAdapter();
			mQueueFragment.resetAdapter();
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.action_mode_base, menu);
			currentVisibleFragment.inflateActionModeMenu(mode, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Bundle data = new Bundle();

			switch(item.getItemId()) {
			case R.id.delete:
				// Get the ids that have been checked
				String ids = currentVisibleFragment.getCheckedIds();
				// Add additional arguments to be executed.
				data.putString("value",ids);
				data.putString("mode",getCurrentMode());
				// Send the message
				executeRequest(Request.toInt(Request.DELETE), data);
				Toast.makeText(getApplicationContext(),R.string.removing,Toast.LENGTH_SHORT).show();
				mActionMode.finish();
				break;
			case R.id.select_all:
				currentVisibleFragment.setAllChecked(true);
				break;
			case R.id.pause:
				// Get the ids that have been checked
				ids = currentVisibleFragment.getCheckedIds();
				// Add it to the message Bundle
				data.putString("value", ids);
				// Send the message
				executeRequest(Request.toInt(Request.PAUSE), data);
				Toast.makeText(getApplicationContext(),R.string.pausing,Toast.LENGTH_SHORT).show();
				mActionMode.finish();
				break;
			case R.id.resume:
				// Get the ids that have been checked
				ids = currentVisibleFragment.getCheckedIds();
				// Add it to the message Bundle
				data.putString("value", ids);
				// Send the message
				executeRequest(Request.toInt(Request.RESUME), data);
				Toast.makeText(getApplicationContext(),R.string.resuming,Toast.LENGTH_SHORT).show();
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
		case 0: return mQueueFragment;
		case 1: return mHistoryFragment;
		default: return null;
		}
	}

	//////////////////////////////////////////////////
	// Service functions BEGIN
	//////////////////////////////////////////////////

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mServiceMessenger = new Messenger(service);

		try {
			Message msg = Message.obtain();
			msg.what = RequestReceiver.REGISTER_CLIENT;
			msg.obj = mRemote;
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
		if(!mIsBoundToService) {
			bindService(new Intent(this, RequestReceiver.class), this, Context.BIND_AUTO_CREATE);
			mIsBoundToService = true;
		}
	}

	/**
	 * Unregister this Activity from a Service
	 */     
	private void unBindFromService() {
		if (mIsBoundToService) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null, RequestReceiver.UNREGISTER_CLIENT);
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

	/**
	 * Send a request message to RequestReceiver so that
	 * the request and message can be properly handled. 
	 * @param request The action to execute.
	 * @param data The data to be sent.
	 */
	private void executeRequest(int request, Bundle data) {    
		if (mIsBoundToService) {
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain();
					msg.what = request;          
					msg.replyTo = mMessenger;
					msg.setData(data);
					mServiceMessenger.send(msg);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//////////////////////////////////////////////////
	// Service functions END
	//////////////////////////////////////////////////

	@Override
	protected void onDestroy() {
		cancelRecurringAlarm();
		unBindFromService();		
		super.onDestroy();
	}

	private void cancelRecurringAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		manager.cancel(mRecurringDownload);
	}

	/**
	 * When data connection has completed, regardless if it was
	 * successful or not, clean up the UI.
	 */
	public void onFinishConnection() {
		if(!queueDownloadCompleted)
			queueDownloadCompleted = true;
		if(!historyDownloadCompleted)
			historyDownloadCompleted = true;
		setPagerVisibility(View.VISIBLE);
		dismissDialogs();
	}

	/**
	 * Builds a Bundle of common arguments (URL and authentication)
	 * to be passed to DownloadService to be executed.
	 * @return A Bundle.
	 */
	private Bundle buildBaseMessage() {
		Bundle b = new Bundle();
		b.putString("url", mRemote.getUrl());
		String username = mRemote.getUsername();
		String password = mRemote.getPassword();
		String apikey = mRemote.getAPIKey();

		if(username != null)
			b.putString("username", username);
		if(password != null)
			b.putString("password", password);
		if(apikey != null)
			b.putString("apikey", apikey);
		return b;
	}

	private String getCurrentMode() {
		switch(adapter.getItemPosition(currentVisibleFragment)) {
		case 0: // Queue
			return "queue";
		case 1: // History
			return "history";
		default:
			return null;
		}
	}

	public Messenger getMessenger() {
		return mMessenger;
	}

	public class IncomingHandler extends Handler {		
		@Override
		public void handleMessage(Message msg) {
			onFinishConnection();
			switch(msg.what) {
			case QUEUE:
				List<NzoItem> newData = (List<NzoItem>) msg.obj;
				onFinishConnection();
				mQueueFragment.updateItems(newData);
				break;
			case HISTORY:
				newData = (List<NzoItem>) msg.obj;
				onFinishConnection();
				mHistoryFragment.updateItems(newData);
				break;
			case STATUS:
			  if((Boolean) msg.obj)
			    Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT).show();
			  else
			    Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_SHORT).show();
				break;
			case ERROR:
				mQueueFragment.onConnectionDown();
				mHistoryFragment.onConnectionDown();
				break;
			}
			super.handleMessage(msg);
		}

	}
}
package com.gmail.at.faint545.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.gmail.at.faint545.services.DownloadService;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String LOGTAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOGTAG,"onReceive");
		Intent downloader = new Intent(context,DownloadService.class);
		downloader.putExtras(intent.getExtras());
		context.startService(downloader);
	}
}

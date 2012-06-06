package com.gmail.at.faint545.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.at.faint545.services.RequestReceiver;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String LOGTAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent downloader = new Intent(context,RequestReceiver.class);
		downloader.putExtras(intent.getExtras());
		context.startService(downloader);
	}
}

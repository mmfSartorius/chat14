package com.chat14;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int code = getResultCode();
		if (code != Activity.RESULT_OK) {
			Log.d("GcmBroadcastReceiver", "onReceive " + intent.getAction());
			ComponentName comp = new ComponentName(context.getPackageName(),
					GCMNotificationIntentService.class.getName());
			startWakefulService(context, (intent.setComponent(comp)));
			setResultCode(Activity.RESULT_OK);
		}
	}
}

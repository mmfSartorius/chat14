package com.chat14;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMAckIntentService extends IntentService {

	public GCMAckIntentService() {
		super("RegistrationAck");
		// TODO Auto-generated constructor stub

	}

	public static final String TAG = "GCMAckIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onHandleIntent");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				if (extras.getBoolean("c")) {

				} else {
					String msg = null;
					String messageFromBundle = extras.getString("p");
					try {
						JSONObject json = new JSONObject(messageFromBundle);
						msg = json.getString("msg");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg = e.toString();
					}
					Log.d(TAG, msg);

				}
				Log.i(TAG, extras.toString());

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

}

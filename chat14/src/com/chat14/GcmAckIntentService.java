package com.chat14;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chat14.helpers.CompressUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmAckIntentService extends IntentService {

	public GcmAckIntentService() {
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
				String msg = getMessageFromBundle(extras);
				Log.i(TAG, extras.toString());

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private String getMessageFromBundle(Bundle bundle) {
		JSONObject json;
		String messageFromBundle = bundle.getString("p");
		String msg = null;

		if (Boolean.valueOf(bundle.getString("c"))) {

			switch (Integer.parseInt(bundle.getString("t"))) {
			case 0:
				msg = "Successful registration";
				break;

			case 1:
				msg = "Successful entry";
				break;
			case 2:
				int decompressedLenght = Integer
						.parseInt(bundle.getString("s"));
				String decompressedMessage = CompressUtils
						.getDecompressedMessage(messageFromBundle,
								decompressedLenght);
				try {
					json = new JSONObject(decompressedMessage);
					msg = json.getString("msg");
				} catch (JSONException e) {
					e.printStackTrace();
					msg = e.toString();
				}
				break;
			default:
				msg = "Error";
				break;
			}

		} else {
			try {
				json = new JSONObject(messageFromBundle);
				msg = json.getString("msg");
			} catch (JSONException e) {
				e.printStackTrace();
				msg = e.toString();
			}
			Log.d(TAG, msg);

		}
		return msg;
	}

}

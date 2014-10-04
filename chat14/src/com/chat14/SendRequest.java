package com.chat14;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SendRequest extends AsyncTask<Void, String, String> {

	Bundle data;
	String id;
	GoogleCloudMessaging gcm;
	Context context;
	ProgressDialog dialog;

	SendRequest(Context context, ProgressDialog dialog, Bundle bundle,
			String id, GoogleCloudMessaging gcm) {
		this.context = context;
		this.dialog = dialog;
		data = bundle;
		this.id = id;
		this.gcm = gcm;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			gcm.send(Config.GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id, data);
			result = "Message is sent";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "Error. " + e.toString();
			e.printStackTrace();
		}

		return result;
	}

	protected void onPostExecute(String result) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		Toast.makeText(context, result, Toast.LENGTH_LONG).show();
	}

}

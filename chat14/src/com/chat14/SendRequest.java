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

	private Bundle data;
	private String id;
	private GoogleCloudMessaging gcm;

	SendRequest(Bundle bundle, String id, GoogleCloudMessaging gcm) {
		data = bundle;
		this.id = id;
		this.gcm = gcm;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
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

	}

}

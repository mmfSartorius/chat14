package com.chat14;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterInGCM {

	String regId;
	GoogleCloudMessaging gcm;
	Context context;

	RegisterInGCM(Context context, GoogleCloudMessaging gcm) {
		this.context = context;
		this.gcm = gcm;
	}

	public String registerGCM() {

		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		}
		Log.d("RegisterActivity", "regId = " + regId);
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(
				LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(Config.REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		int registeredVersion = prefs.getInt(Config.APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(
				LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Config.REG_ID, regId);
		editor.putInt(Config.APP_VERSION, appVersion);
		editor.commit();
	}
}

package com.chat14;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat14.helpers.CompressUtils;
import com.chat14.helpers.Generator;
import com.chat14.helpers.model.CompressedData;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegistrationActivity extends Activity {

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	EditText username, email, password;
	Button registration, back;

	GoogleCloudMessaging gcm;
	Context context;
	public ProgressDialog dialog;
	SendRequest sendRequest;
	String regId;
	Bundle data, ack;
	JSONObject json;
	BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);
		context = getApplicationContext();

		username = (EditText) findViewById(R.id.usernameRegistration);
		email = (EditText) findViewById(R.id.emailRegistration);
		password = (EditText) findViewById(R.id.passwordRegistration);

		gcm = GoogleCloudMessaging.getInstance(this);

		registration = (Button) findViewById(R.id.registrationFromRegistration);

		registration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				regId = registerGCM();
				if (username.getText().toString().length() != 0
						&& email.getText().toString().length() != 0
						&& password.getText().toString().length() != 0) {
					data = new Bundle();
					json = new JSONObject();

					try {
						json.put(Config.LOGIN, username.getText().toString());
						json.put(Config.EMAIL, email.getText().toString());
						json.put(Config.PASSWORD, LoginActivity
								.encryptData(password.getText().toString()));
						json.put(Config.EXTERNAL_IP, getIntent().getExtras()
								.get("ip"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d("myTag", json.toString());

					List<CompressedData> list = new ArrayList<CompressedData>();
					list = CompressUtils.getCompressedAndChunkedData(
							json.toString(), 500);
					Log.d("myTag", list.get(0).toString());
					data.putString("t", "0");
					if (list.get(0).getCompressedPayload() != null) {
						data.putString("p", list.get(0).getCompressedPayload());
					}
					if (list.get(0).getDecompressedSize() != null) {
						data.putString("s", Integer.toString(list.get(0)
								.getDecompressedSize()));
					}
					if (list.get(0).getCompressed() != null) {
						data.putString("c",
								Boolean.toString(list.get(0).getCompressed()));
					}
					if (list.get(0).getMessageId() != null) {
						data.putString("msgId", list.get(0).getMessageId());
					}
					if (list.get(0).getSequenceNumber() != null) {
						data.putString("sn", Integer.toString(list.get(0)
								.getSequenceNumber()));
					}
					if (list.get(0).getTotalNumber() != null) {
						data.putString("tn",
								Integer.toString(list.get(0).getTotalNumber()));
					}
					Log.d("myTag", data.toString());

					dialog = new ProgressDialog(RegistrationActivity.this);
					dialog.setTitle("Registration");
					dialog.setMessage("Please wait");
					dialog.show();
					createReceiver();

					sendRequest = new SendRequest(context, dialog, data,
							Generator.getInstance().getRandomUUID(), gcm);
					sendRequest.execute();
				}

			}
		});
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void createReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				ack = intent.getExtras();
				Log.d("myTag", "Registration receiver \n" + ack.toString());
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.google.android.c2dm.intent.RECEIVE");
		registerReceiver(receiver, filter);

	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	public String registerGCM() {

		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				RegistrationActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
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
				Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				RegistrationActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

}

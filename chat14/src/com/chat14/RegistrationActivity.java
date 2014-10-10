package com.chat14;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.chat14.helpers.CompressUtils;
import com.chat14.helpers.Generator;
import com.chat14.helpers.model.CompressedData;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegistrationActivity extends Activity {

	private EditText username, email, password;
	private Button registration, back;

	private Context context;
	private ProgressDialog dialog;
	private SendRequest sendRequest;
	private String regId;
	private Bundle data, ack;
	private JSONObject json;
	private BroadcastReceiver receiver;
	private GoogleCloudMessaging gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);
		context = this;

		username = (EditText) findViewById(R.id.usernameRegistration);
		email = (EditText) findViewById(R.id.emailRegistration);
		password = (EditText) findViewById(R.id.passwordRegistration);

		gcm = GoogleCloudMessaging.getInstance(context);

		registration = (Button) findViewById(R.id.registrationFromRegistration);

		registration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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

					List<CompressedData> list = CompressUtils
							.getCompressedAndChunkedData(json.toString(), 500);

					data.putString("t", "0");
					for (int i = 0; i < list.size(); i++) {
						CompressedData compressedData = list.get(i);
						Log.d("myTag", compressedData.toString());
						if (compressedData.getCompressedPayload() != null) {
							data.putString("p",
									compressedData.getCompressedPayload());
						}
						if (compressedData.getDecompressedSize() != null) {
							data.putString("s", Integer.toString(compressedData
									.getDecompressedSize()));
						}
						if (compressedData.getCompressed() != null) {
							data.putString("c", Boolean.toString(compressedData
									.getCompressed()));
						}
						if (compressedData.getMessageId() != null) {
							data.putString("msgId",
									compressedData.getMessageId());
						}
						if (compressedData.getSequenceNumber() != null) {
							data.putString("sn", Integer
									.toString(compressedData
											.getSequenceNumber()));
						}
						if (compressedData.getTotalNumber() != null) {
							data.putString("tn", Integer
									.toString(compressedData.getTotalNumber()));
						}
						Log.d("myTag", data.toString());
					}
					dialog = new ProgressDialog(context);
					dialog.setTitle("Registration");
					dialog.setMessage("Please wait");
					dialog.show();
					createReceiver();

					sendRequest = new SendRequest(data, Generator.getInstance()
							.getRandomUUID(), gcm);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	private void createReceiver() {
		createTimer();
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

	private void createTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}, 10000);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

}

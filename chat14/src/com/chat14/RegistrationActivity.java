package com.chat14;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegistrationActivity extends Activity {

	private EditText username, email, password;
	private Button registration, back;

	private Context context;
	private ProgressDialog dialog;
	private JSONObject json;
	private BroadcastReceiver receiver;
	private GoogleCloudMessaging gcm;

	public static final String TAG = "RegistrationActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				register();

			}
		});
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

	private void register() {
		if (username.getText().toString().length() >= 3
				&& username.getText().toString().length() <= 15) {
			username.setError(null);
			if (email.getText().toString().length() >= 5
					&& email.getText().toString().length() <= 50) {
				if (password.getText().toString().length() >= 5) {
					json = new JSONObject();

					try {
						json.put(Config.LOGIN, username.getText().toString());
						json.put(Config.EMAIL, email.getText().toString());
						json.put(Config.PASSWORD, UtilityMethods
								.encryptData(password.getText().toString()));
						json.put(Config.EXTERNAL_IP, getIntent().getExtras()
								.get("ip"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.d(TAG, "Registration activity json" + json.toString());

					createReceiver();
					UtilityMethods.fillAndSendBundle(json, gcm,
							Config.COMMAND_TYPE_REGISTRATION);

					dialog = new ProgressDialog(context);
					dialog.setTitle("Registration");
					dialog.setMessage("Please wait");
					dialog.show();

				} else {
					password.setError(context
							.getString(R.string.password_error));
				}
			} else {
				email.setError(context.getString(R.string.email_error));
			}
		} else {
			username.setError(context.getString(R.string.username_error));
		}
	}

	private void createReceiver() {
		createTimer();
		receiver = new WakefulBroadcastReceiver() {

			@Override
			public void onReceive(Context context, final Intent intent) {
				setResultCode(Activity.RESULT_OK);
				ComponentName comp = new ComponentName(
						context.getPackageName(),
						GcmAckIntentService.class.getName());
				startWakefulService(context, (intent.setComponent(comp)));
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.setPriority(60);
		filter.addAction("com.google.android.c2dm.intent.RECEIVE");
		filter.addAction("com.google.android.c2dm.intent.REGISTRATION");
		registerReceiver(receiver, filter,
				"com.google.android.c2dm.permission.SEND", null);

	}

	private void createTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
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

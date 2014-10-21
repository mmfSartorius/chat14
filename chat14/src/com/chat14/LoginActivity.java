package com.chat14;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chat14.database.DBProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends Activity {

	private EditText username, password;
	private Button login, registration, exit;
	private CheckBox checkBoxRemember;
	private Context context;
	private BroadcastReceiver receiver;
	private String ip;
	private Bundle ack;
	private JSONObject json;
	private ProgressDialog dialog;
	private GoogleCloudMessaging gcm;
	private DBProvider dbProvider;

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public final static String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
		context = this;

		if (checkPlayServices()) {
			ip = getCurrentIP();
			gcm = GoogleCloudMessaging.getInstance(context);
			dbProvider = new DBProvider(context);

			RegisterInGCM regGCM = new RegisterInGCM(context, gcm);
			regGCM.registerGCM();

			login = (Button) findViewById(R.id.login);
			login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					login();

				}
			});
			registration = (Button) findViewById(R.id.registration);
			registration.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							RegistrationActivity.class);
					intent.putExtra("ip", ip);
					startActivity(intent);

				}
			});
			exit = (Button) findViewById(R.id.exit);
			exit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbProvider.close();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("DefaultLocale")
	private void login() {
		if (username.getText().toString().length() >= 3
				&& username.getText().toString().length() <= 15) {
			username.setError(null);
			if (password.getText().toString().length() >= 5) {
				json = new JSONObject();
				json = new JSONObject();
				dbProvider.open(username.getText().toString().toLowerCase());

				try {
					json.put(Config.LOGIN, username.getText().toString());
					json.put(Config.PASSWORD, UtilityMethods
							.encryptData(password.getText().toString()));
					json.put(Config.EXTERNAL_IP, ip);
					json.put(Config.LAST_MESSAGE_TIME,
							dbProvider.getLastMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				createReceiver();
				UtilityMethods.fillAndSendBundle(json, gcm,
						Config.COMMAND_TYPE_LOGIN);

				dialog = new ProgressDialog(context);
				dialog.setTitle("Login");
				dialog.setMessage("Please wait");
				dialog.show();

			} else {
				password.setError(context.getString(R.string.password_error));
			}
		} else {
			username.setError(context.getString(R.string.username_error));
		}

	}

	private void createReceiver() {
		createTimer();
		receiver = new WakefulBroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				setResultCode(Activity.RESULT_OK);
				ComponentName comp = new ComponentName(
						context.getPackageName(),
						GCMAckIntentService.class.getName());
				startWakefulService(context, (intent.setComponent(comp)));
				setResultCode(Activity.RESULT_OK);

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

	public static void storeInPreferences(Context context, String key,
			String data) {
		final SharedPreferences prefs = context.getSharedPreferences(
				LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, data);
		editor.commit();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("LoginActivity", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getCurrentIP() {

		String ip = null;
		AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String ip = "";
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(
							"http://whatismyip.akamai.com/ip");
					// HttpGet httpGet = new
					// HttpGet("http://whatismyip.com.au/");
					// HttpGet httpGet = new
					// HttpGet("http://www.whatismyip.org/");
					HttpResponse response;

					response = httpClient.execute(httpGet);

					// Log.i("externalip",response.getStatusLine().toString());

					HttpEntity entity = response.getEntity();
					if (entity != null) {
						long len = entity.getContentLength();
						if (len != -1 && len < 1024) {
							String str = EntityUtils.toString(entity);
							// Log.i("externalip",str);
							ip = str;
						} else {
							ip = "Response too long or error.";
							// debug
							// ip.setText("Response too long or error: "+EntityUtils.toString(entity));
							// Log.i("externalip",EntityUtils.toString(entity));
						}
					} else {
						ip = "Null:" + response.getStatusLine().toString();
					}

				} catch (Exception e) {
					ip = null;
					e.printStackTrace();
				}
				return ip;
			}

		}.execute(null, null, null);

		try {
			ip = asyncTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		return ip;

	}
}

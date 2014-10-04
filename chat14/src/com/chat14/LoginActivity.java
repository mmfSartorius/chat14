package com.chat14;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

	EditText username, password;
	Button login, registration, exit;
	CheckBox checkBoxRemember;
	Context context = this;
	public static AtomicInteger id = new AtomicInteger();
	String ip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
		
		ip = getCurrentIP();

		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login();

			}
		});
		registration = (Button) findViewById(R.id.registration);
		registration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, RegistrationActivity.class);
				intent.putExtra("ip", ip);
				startActivity(intent);

			}
		});
		exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
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

	private void login() {

	}

	public static void storeInPreferences(Context context, String key,
			String data) {
		final SharedPreferences prefs = context.getSharedPreferences(
				RegistrationActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, data);
		editor.commit();
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static String encryptData(String data) {

		MessageDigest digest = null;
		String hash = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(data.getBytes());

			hash = bytesToHexString(digest.digest());

			Log.i("Eamorr", "result is " + hash);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return hash;
	}

	private String getCurrentIP() {

		String ip = null;
		AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
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
		};
		Log.d("myTag", ip);
		return ip;


	}
}

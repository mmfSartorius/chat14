package com.chat14;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.chat14.helpers.CompressUtils;
import com.chat14.helpers.Generator;
import com.chat14.helpers.model.CompressedData;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class UtilityMethods {

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
			e1.printStackTrace();
		}

		return hash;
	}

	public static void fillAndSendBundle(JSONObject json, GoogleCloudMessaging gcm) {
		Bundle data = new Bundle();
		List<CompressedData> list = CompressUtils.getCompressedAndChunkedData(
				json.toString(), 3000);
		for (int i = 0; i < list.size(); i++) {
			CompressedData compressedData = list.get(i);
			Log.d("myTag", compressedData.toString());
			data.putString("t", "1");
			if (compressedData.getCompressedPayload() != null) {
				data.putString("p", compressedData.getCompressedPayload());
			}
			if (compressedData.getDecompressedSize() != null) {
				data.putString("s",
						Integer.toString(compressedData.getDecompressedSize()));
			}
			if (compressedData.getCompressed() != null) {
				data.putString("c",
						Boolean.toString(compressedData.getCompressed()));
			}
			if (compressedData.getMessageId() != null) {
				data.putString("msgId", compressedData.getMessageId());
			}
			if (compressedData.getSequenceNumber() != null) {
				data.putString("sn",
						Integer.toString(compressedData.getSequenceNumber()));
			}
			if (compressedData.getTotalNumber() != null) {
				data.putString("tn",
						Integer.toString(compressedData.getTotalNumber()));
			}
			Log.d("myTag", data.toString());
			SendRequest sendRequest = new SendRequest(data, Generator
					.getInstance().getRandomUUID(), gcm);
			sendRequest.execute();
		}

	}
}

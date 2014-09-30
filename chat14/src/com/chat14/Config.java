package com.chat14;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://chat14-ashaman.rhcloud.com/regUser";

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "1084629732244";
	static final String MESSAGE = "msg";
	static final String LOGIN = "login";
	static final String PASSWORD = "passwd";
	static final String EMAIL = "email";
	static final String EXTERNAL_IP = "ip";
	

}

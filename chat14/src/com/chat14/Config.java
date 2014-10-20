package com.chat14;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://chat14-ashaman.rhcloud.com/regUser";

	public static final String REG_ID = "regId";
	public static final String APP_VERSION = "appVersion";

	static final String GOOGLE_PROJECT_ID = "1084629732244";
	static final String MESSAGE = "msg";
	static final String LOGIN = "login";
	static final String PASSWORD = "passwd";
	static final String EMAIL = "email";
	static final String EXTERNAL_IP = "ip";
	static final String LAST_MESSAGE_TIME = "lastMsgTime";

	static final String COMMAND_TYPE_REGISTRATION = "0";
	static final String COMMAND_TYPE_LOGIN = "1";

}

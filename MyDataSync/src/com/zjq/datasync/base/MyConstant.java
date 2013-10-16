package com.zjq.datasync.base;

public class MyConstant {
	public final static String POST_REGSIT_USER_KEY = "post_regist_user_key";
//	public final static String POST_REGSIT_USER_URI = "http://192.168.1.122:8080/regist";
	public final static String POST_REGSIT_USER_URI = "http://zjqbp860.duapp.com/regist";

	public final static String POST_LOGIN_USER_KEY = "post_login_user_key";
//	public final static String POST_LOGIN_USER_URI = "http://192.168.1.122:8080/login";
	public final static String POST_LOGIN_USER_URI = "http://zjqbp860.duapp.com/login";
	
	public final static String BACKUP_CONTACTS_KEY = "backup_contacts_key";
//	public final static String BACKUP_CONTACTS_URI = "http://192.168.1.122:8080/backup/contacts";
	public final static String BACKUP_CONTACTS_URI = "http://zjqbp860.duapp.com/backup/contacts";
	
	//参数KEY 恢复联系人时
	public final static String RESTORE_CONTACTS_KEY = "restore_contacts_key";
//	public final static String RESTORE_CONTACTS_URI = "http://192.168.1.122:8080/restore/contacts";
	public final static String RESTORE_CONTACTS_URI = "http://zjqbp860.duapp.com/restore/contacts";

	public final static int SERVER_RESPONSE_ERROR = 3;

	public final static String APP_BASE_DIR = "DataSync";

	public final static String BACKUP_FILE_DIR = APP_BASE_DIR + "/"
			+ "backup/TxtFile/";
	public final static String BACKUP_SECRET_FILE_DIR = APP_BASE_DIR + "/"
			+ "backup/SecretFile/";
	public final static String BACKUP_SECRET_FILE_NAME = "BACKUP";
	
	public final static String ACTIVITY_GET_LOGIN_USER_KEY = "ACTIVITY.GET.LOGIN.USER.KEY";
}

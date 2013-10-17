package com.zjq.datasync.tools;

public class StateCodeParse {
	// 数据库连接失败
	public static final int DATABASE_CONN_FAILED = 101;
	// 数据库连接成功
	public static final int DATABASE_CONN_SUCCESS = 102;
	// 服务器内部数据错误
	public static final int SERVER_DATA_ERROR = 103;
	// 注册时用户名已经存在
	public static final int REGSIT_USER_NAME_EXIST = 104;
	// 登陆时，用户名不存在
	public static final int LOGIN_USER_NAME_NOT_EXIST = 105;
	// 登陆时，密码不一致
	public static final int LOGIN_PASSWORD_NOT_EQUALS = 106;
	// 登录时，登陆成功
	public static final int LOGIN_SUCCESS = 107;

	public static String parse(int code) {
		String s = null;
		switch (code) {
		case DATABASE_CONN_FAILED:
			s = "服务器数据库打开错误";
			break;
		case DATABASE_CONN_SUCCESS:
			s = "服务器数据库打开成功";
			break;
		case SERVER_DATA_ERROR:
			s = "服务器数据错误";
			break;
		case REGSIT_USER_NAME_EXIST:
			s = "用户名已经存在";
			break;
		case LOGIN_USER_NAME_NOT_EXIST:
			s = "用户名不存在";
			break;
		case LOGIN_PASSWORD_NOT_EQUALS:
			s = "密码不正确";
			break;
		case LOGIN_SUCCESS:
			break;
		default:s = "未知错误";
		}
		return s;
	}
}

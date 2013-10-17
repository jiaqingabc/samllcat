package com.zjq.datasync.tools;

public class StateCodeParse {
	// ���ݿ�����ʧ��
	public static final int DATABASE_CONN_FAILED = 101;
	// ���ݿ����ӳɹ�
	public static final int DATABASE_CONN_SUCCESS = 102;
	// �������ڲ����ݴ���
	public static final int SERVER_DATA_ERROR = 103;
	// ע��ʱ�û����Ѿ�����
	public static final int REGSIT_USER_NAME_EXIST = 104;
	// ��½ʱ���û���������
	public static final int LOGIN_USER_NAME_NOT_EXIST = 105;
	// ��½ʱ�����벻һ��
	public static final int LOGIN_PASSWORD_NOT_EQUALS = 106;
	// ��¼ʱ����½�ɹ�
	public static final int LOGIN_SUCCESS = 107;

	public static String parse(int code) {
		String s = null;
		switch (code) {
		case DATABASE_CONN_FAILED:
			s = "���������ݿ�򿪴���";
			break;
		case DATABASE_CONN_SUCCESS:
			s = "���������ݿ�򿪳ɹ�";
			break;
		case SERVER_DATA_ERROR:
			s = "���������ݴ���";
			break;
		case REGSIT_USER_NAME_EXIST:
			s = "�û����Ѿ�����";
			break;
		case LOGIN_USER_NAME_NOT_EXIST:
			s = "�û���������";
			break;
		case LOGIN_PASSWORD_NOT_EQUALS:
			s = "���벻��ȷ";
			break;
		case LOGIN_SUCCESS:
			break;
		default:s = "δ֪����";
		}
		return s;
	}
}

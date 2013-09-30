package com.zjq.datasync.model;

public class BackupServerRespond {
	
	public static final int VERIFY_FAILED = 201;
	public static final int VERIFY_SUCCESS = 200;
	public static final int SERVER_DATA_ERROR = 103;
	
	int updateNum = 0;
	int respCode = 0;

	public BackupServerRespond() {
		// TODO Auto-generated constructor stub
	}

	public int getUpdateNum() {
		return updateNum;
	}

	public void setUpdateNum(int updateNum) {
		this.updateNum = updateNum;
	}

	public int getRespCode() {
		return respCode;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	@Override
	public String toString() {
		return "BackupServerRespond [updateNum=" + updateNum + ", respCode="
				+ respCode + "]";
	}

}

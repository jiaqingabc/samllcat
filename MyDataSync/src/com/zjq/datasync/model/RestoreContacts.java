package com.zjq.datasync.model;

import java.util.ArrayList;

public class RestoreContacts {
	public static final int SERVER_DATA_ERROR = 103;
	public static final int SUCCESS = 200;
	
	int code = 0;
	
	ArrayList<Contact>	contacts = null;
	
	public RestoreContacts() {
		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}

}

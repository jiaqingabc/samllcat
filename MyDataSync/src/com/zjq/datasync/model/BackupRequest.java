package com.zjq.datasync.model;

import java.util.LinkedList;
import java.util.List;

public class BackupRequest {
	User user = null;
	List<Contact> contacts = null;
	public BackupRequest() {
		// TODO Auto-generated constructor stub
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts2) {
		this.contacts = contacts2;
	}

}

package com.zjq.datasync.tools;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.User;

public class GsonTools {
	
	private Gson gson = null;

	public GsonTools() {
		// TODO Auto-generated constructor stub
		this.gson = new Gson();
	}

	public String getJsonStr(Object o){
		String gStr = null;
		gStr = gson.toJson(o);
		return gStr;
	}
	
	public ArrayList<Contact> getContacts(String json){
		ArrayList<Contact> list = gson.fromJson(json, new TypeToken<ArrayList<Contact>>(){}.getType());
		return list;
	}
	
	public ArrayList<User> getUsers(String json){
		ArrayList<User> list = gson.fromJson(json, new TypeToken<ArrayList<User>>(){}.getType());
		return list;
	}
}

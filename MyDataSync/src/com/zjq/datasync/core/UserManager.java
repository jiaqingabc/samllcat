package com.zjq.datasync.core;

import com.zjq.datasync.model.User;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
	final String USER_CONFIG = "_user";
	Context c = null;
	SharedPreferences sp = null;
	SharedPreferences.Editor editor = null;

	public UserManager(Context c) {
		// TODO Auto-generated constructor stub
		this.c = c;
		sp = c.getSharedPreferences(USER_CONFIG, 0);
		
		editor = sp.edit();
	}

	public User getUser(){
		User u = new User();
		u.setName(sp.getString("NAME", null));
		u.setPassword(sp.getString("PASSWORD", null));
		u.setImei(sp.getString("IMEI", null));
		u.setMail(sp.getString("MAIL", null));
		if(u.getName() == null){
			u = null;
		}
		return u;
	}
	
	public void saveUser(User u){
		editor.putString("NAME", u.getName());
		editor.putString("PASSWORD", u.getPassword());
		editor.putString("IMEI", u.getImei());
		editor.putString("MAIL", u.getMail());
		editor.commit();
	}
	
	public void deleteUser(){
		editor.remove("NAME");
		editor.remove("PASSWORD");
		editor.remove("IMEI");
		editor.remove("MAIL");
		editor.commit();
	}
}

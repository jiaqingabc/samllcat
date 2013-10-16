package com.zjq.datasync.service;

import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.UserManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BackService extends Service {
	
	public static final String SERVICE_START_BROADCAST = "com.zjq.datasync.service.SERVICE_START_BROADCAST";
	
	private User user = null;
	UserManager userManager = null;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		userManager = new UserManager(BackService.this);
		super.onCreate();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		
		
		Intent startIntent = new Intent(SERVICE_START_BROADCAST);
		sendBroadcast(startIntent);
		Log.d("test", "Service started");
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	public void setCurrentUser(User u){
		this.user = u;
		
		userManager.saveUser(this.user);
	}
	
	public void deleteCurrentUser(){
		this.user = null;
		
		userManager.deleteUser();
	}
	
	public User getCurrentUser(){
		this.user = userManager.getUser();
		return this.user;
	}
	

	public class MyBinder extends Binder{
		
		public BackService getService(){
			return BackService.this;
		}
	}
}

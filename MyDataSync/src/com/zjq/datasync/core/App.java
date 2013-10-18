package com.zjq.datasync.core;

import android.app.Application;

public class App extends Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		CrashHandler crashHandler = CrashHandler.getInstance();  
        //×¢²ácrashHandler  
        crashHandler.init(getApplicationContext()); 
		super.onCreate();
	}
}

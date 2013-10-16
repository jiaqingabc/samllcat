package com.zjq.datasync.view;

import com.zjq.datasync.R;
import com.zjq.datasync.base.BaseActivity;
import com.zjq.datasync.service.BackService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class HelloActivity extends BaseActivity {
	
	BroadcastReceiver receiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello_view);
		
		receiver = new ServiceStartReceiver();
		
		IntentFilter filter = new IntentFilter(BackService.SERVICE_START_BROADCAST);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent(HelloActivity.this, BackService.class);
		startService(serviceIntent);
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	class ServiceStartReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			Log.d("test", "Receiver is :"+action);
			if(action.equals(BackService.SERVICE_START_BROADCAST)){
				startOtherActivity(MainActivity.class,true);
			}
		}
	}
}

//package com.zjq.datasync.view;
//
//import com.zjq.datasync.R;
//import com.zjq.datasync.base.MyConstant;
//import com.zjq.datasync.model.User;
//import com.zjq.datasync.service.BackService;
//import com.zjq.datasync.service.BackService.MyBinder;
//import com.zjq.datasync.tools.UserManager;
//
//import android.os.Bundle;
//import android.os.IBinder;
//import android.app.TabActivity;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.res.Resources;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TabHost;
//import android.widget.TextView;
//
//public class MainActivity extends TabActivity {
//
//	final String TAB_SPEC_BACKUP = "tab.spec.backup";
//
//	TabHost host = null;
//
//	Resources res = null;
//
//	Intent DataBackupIntent = null;
//
//	User currentUser = null;
//
//	Button loginBtn = null;
//
//	TextView titleText = null;
//	
//	ServiceConnection connection = null;
//	
//	BackService backService = null;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.table_activity);
//
//		res = getResources();
//		
//		initData();
//		
//		initIntent();
//		
//		initView();
//	}
//	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		unbindService(connection);
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		currentUser = getCurrentUser(intent);
//
//		if (currentUser != null) {
//			loginBtn.setText("×¢Ïú");
//			loginBtn.setOnClickListener(new LogoutListener());
//			titleText.setText(currentUser.getName());
//		} 
//		super.onNewIntent(intent);
//	}
//	
//	protected User getCurrentUser(Intent intent) {
//		User user = null;
//		Bundle bundle = intent.getExtras();
//		if (bundle != null) {
//			user = (User) bundle
//					.getSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY);
//		}
//		return user;
//	}
//
//	protected void initData(){
//		
//		connection = new ServiceConn();
//		Intent serviceBind = new Intent(MainActivity.this, BackService.class);
//		bindService(serviceBind, connection, BIND_AUTO_CREATE);
//	}
//	
//	protected void initIntent() {
//		DataBackupIntent = new Intent(this, DataBackupActivity.class);
////		Bundle bundle = new Bundle();
////		bundle.putSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY,
////				currentUser);
////		DataBackupIntent.putExtras(bundle);
//	}
//
//	protected void initView() {
//		loginBtn = (Button) findViewById(R.id.title_login_btn);
//
//		titleText = (TextView) findViewById(R.id.title_text);
//
//		host = getTabHost();
//
//		host.addTab(host
//				.newTabSpec(TAB_SPEC_BACKUP)
//				.setIndicator(res.getString(R.string.table_host_item_backup),
//						res.getDrawable(R.drawable.ic_launcher))
//				.setContent(DataBackupIntent));
//
//		loginBtn.setOnClickListener(new LoginListener());
//	}
//	
//	class LoginListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			Intent intent = new Intent();
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setClass(MainActivity.this, LoginActivity.class);
//			startActivityForResult(intent, 200);
//		}
//		
//	}
//	
//	class LogoutListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			backService.deleteCurrentUser();
//			currentUser = null;
//		}
//	}
//	
//	class ServiceConn implements ServiceConnection{
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// TODO Auto-generated method stub
//			backService = ((MyBinder)service).getService();
//			currentUser = backService.getCurrentUser();
//			
//			if(currentUser != null){
//				titleText.setText(currentUser.getName());
//				loginBtn.setText("×¢Ïú");
//				loginBtn.setOnClickListener(new LogoutListener());
//			}
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//			
//		}
//	}
//}

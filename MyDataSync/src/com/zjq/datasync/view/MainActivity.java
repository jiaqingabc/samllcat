package com.zjq.datasync.view;

import com.zjq.datasync.R;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.UserManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {

	final String TAB_SPEC_BACKUP = "tab.spec.backup";

	TabHost host = null;

	Resources res = null;

	Intent DataBackupIntent;

	User currentUser = null;

	UserManager uManager = null;

	Button loginBtn = null;

	TextView titleText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_activity);

		res = getResources();

		uManager = new UserManager(MainActivity.this);

		currentUser = getCurrentUser(getIntent());
		
		if (currentUser == null) {
			currentUser = uManager.getUser();
		}
		
		initIntent();

		initView();

		if (currentUser == null) {

		} else {
			titleText.setText(currentUser.getName());
			loginBtn.setText("×¢Ïú");
			loginBtn.setOnClickListener(new LogoutListener());
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		currentUser = getCurrentUser(intent);

		if (currentUser == null) {

		} else {
			loginBtn.setText("×¢Ïú");
			loginBtn.setOnClickListener(new LogoutListener());
			titleText.setText(currentUser.getName());
			host.clearAllTabs();
			initIntent();
			
			host.addTab(host
					.newTabSpec(TAB_SPEC_BACKUP)
					.setIndicator(res.getString(R.string.table_host_item_backup),
							res.getDrawable(R.drawable.ic_launcher))
					.setContent(DataBackupIntent));
		}
		super.onNewIntent(intent);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected User getCurrentUser(Intent intent) {
		User user = null;
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			user = (User) bundle
					.getSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY);
		}
		return user;
	}

	protected void initIntent() {
		DataBackupIntent = new Intent(this, DataBackupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY,
				currentUser);
		DataBackupIntent.putExtras(bundle);
	}

	protected void initView() {
		loginBtn = (Button) findViewById(R.id.title_login_btn);

		titleText = (TextView) findViewById(R.id.title_text);

		host = getTabHost();

		host.addTab(host
				.newTabSpec(TAB_SPEC_BACKUP)
				.setIndicator(res.getString(R.string.table_host_item_backup),
						res.getDrawable(R.drawable.ic_launcher))
				.setContent(DataBackupIntent));

		loginBtn.setOnClickListener(new LoginListener());
	}
	
	class LoginListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, 200);
		}
		
	}
	
	class LogoutListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			uManager.deleteUser();
			titleText.setText("");
			currentUser = null;
			loginBtn.setText(res.getString(R.string.title_login_btn));
			host.clearAllTabs();
			initIntent();
			initView();
		}
	}
}

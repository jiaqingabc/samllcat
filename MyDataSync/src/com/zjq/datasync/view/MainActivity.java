package com.zjq.datasync.view;

import com.zjq.datasync.R;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.User;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

	final String TAB_SPEC_BACKUP = "tab.spec.backup";

	TabHost host = null;

	Resources res = null;
	
	Intent DataBackupIntent;
	
	User currentUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_activity);

		res = getResources();
		
		currentUser = getCurrentUser();
		
		initIntent();
		
		initView();
		
	}
	
	protected User getCurrentUser(){
		User user = null;
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			user = (User) bundle.getSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY);
		}
		return user;
	}
	
	protected void initIntent(){
		DataBackupIntent = new Intent(this, DataBackupActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY, currentUser);
		DataBackupIntent.putExtras(bundle);
	}

	protected void initView() {
		host = getTabHost();

		host.addTab(host
				.newTabSpec(TAB_SPEC_BACKUP)
				.setIndicator(res.getString(R.string.table_host_item_backup),
						res.getDrawable(R.drawable.ic_launcher))
				.setContent(DataBackupIntent));
	}
}

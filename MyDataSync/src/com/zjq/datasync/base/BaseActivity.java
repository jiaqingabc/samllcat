package com.zjq.datasync.base;

import java.io.Serializable;

import com.zjq.datasync.model.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
//	protected final String DATA_KEY = "SERIALIZABLE_DATA_KEY";

	protected AlertDialog createBasicTextDialog(String title, String content,
			Drawable icon, boolean cancel) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);

		if(title != null){
			builder.setTitle(title);
		}
		if(content != null){
			builder.setMessage(content);
		}
		if(icon != null){
			builder.setIcon(icon);
		}
		builder.setCancelable(cancel);
		dialog = builder.create();

		return dialog;
	}
	
	protected void startOtherActivity(Class cls){
		Intent intent = new Intent(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	
	protected void startOtherActivityInData(Class cls, Serializable data){
		Intent intent = new Intent(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY, data);
		
		intent.putExtras(bundle);
		startActivity(intent);
		
		finish();
	}
	
	protected User getCurrentUser(){
		User user = null;
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			user = (User) bundle.getSerializable(MyConstant.ACTIVITY_GET_LOGIN_USER_KEY);
		}
		return user;
	}
}

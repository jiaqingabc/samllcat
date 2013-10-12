package com.zjq.datasync.view;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.zjq.datasync.R;
import com.zjq.datasync.base.BaseActivity;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.GsonTools;
import com.zjq.datasync.tools.MyHttpClient;
import com.zjq.datasync.tools.UserManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	Button loginBtn;
	EditText userName, userPassword;
	GsonTools myGson = null;
	TextView registText;
	Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		myGson = new GsonTools();
		
		res = getResources();

		initView();
	}

	protected void initView() {
		userName = (EditText) findViewById(R.id.login_user_name);
		userPassword = (EditText) findViewById(R.id.login_user_password);
		registText = (TextView) findViewById(R.id.regist_text);
		loginBtn = (Button) findViewById(R.id.login_button);

		loginBtn.setOnClickListener(new LoginBtnOnClickListener());

		registText.setClickable(true);
		registText.setOnClickListener(new RegistTextOnClickListener());
	}
	
	protected void startConnectSer(){
		System.out.println("Start");
		loginBtn.setText(res.getString(R.string.login_button_text2));
		loginBtn.setEnabled(false);
		
		userName.setEnabled(false);
		userPassword.setEnabled(false);
		
		registText.setEnabled(false);
	}
	
	protected void connectServerError(){
		System.out.println("Error");
		loginBtn.setText(res.getString(R.string.login_button));
		loginBtn.setEnabled(true);
		
		userName.setEnabled(true);
		userPassword.setEnabled(true);
		
		registText.setEnabled(true);
	}

	protected void connectServerSuccess(){
		System.out.println("Success");
		loginBtn.setText(res.getString(R.string.login_button_text4));
		loginBtn.setEnabled(false);
		
		userName.setEnabled(false);
		userPassword.setEnabled(false);
		
		registText.setEnabled(false);
	}
	
	protected User createLoginUser() {
		User user = null;

		String name, password;

		name = userName.getText().toString();
		password = userPassword.getText().toString();

		boolean flag = true;
		if (name == null || name.length() <= 0) {
			flag = false;
			Toast.makeText(getApplicationContext(), "用户名填写错误.",
					Toast.LENGTH_SHORT).show();
		}

		if (flag && (password == null || password.length() <= 0)) {
			flag = false;
			Toast.makeText(getApplicationContext(), "请填写密码.",
					Toast.LENGTH_SHORT).show();
		}

		if (flag) {
			user = new User();
			user.setName(name);
			user.setPassword(password);
		}

		return user;
	}

	class LoginBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startConnectSer();//修改界面控件
			LoginTask task = new LoginTask();
			User user = createLoginUser();
			if (user != null) {
				task.execute(user);
			}
		}
	}

	class RegistTextOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			startOtherActivity(RegistActivity.class);
		}
	}

	class LoginTask extends AsyncTask<User, String, HttpResponse> {
		String responseStr = null;
		int stateCode = 0;

		@Override
		protected void onPostExecute(HttpResponse result) {
			// TODO Auto-generated method stub
			boolean connFlag = true;
			
			try {
				if (result == null) {
					AlertDialog dialog = createBasicTextDialog("出错了",
							"服务器连接失败\n", null, true);
					dialog.setCanceledOnTouchOutside(true);
					dialog.show();
					
					connFlag = false;
				} else {
					stateCode = result.getStatusLine().getStatusCode();
					if (stateCode != 200) {
						AlertDialog dialog = createBasicTextDialog("出错了",
								"服务器连接失败\n" + stateCode, null, true);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
						
						connFlag = false;
					}

					System.out.println("RESP:" + responseStr);
					if (responseStr.trim().length() == MyConstant.SERVER_RESPONSE_ERROR) {
						AlertDialog dialog = createBasicTextDialog("出错了", "登陆失败\n"
								+ responseStr, null, true);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
						
						connFlag = false;
					}
				}
				
				if(connFlag){
					
					connectServerSuccess();
					
					Gson gson = new Gson();
					User loginUser = gson.fromJson(responseStr, User.class);
					
					if(loginUser != null){//save user
						UserManager manager = new UserManager(LoginActivity.this);
						manager.saveUser(loginUser);
					}

					startOtherActivityInData(MainActivity.class, loginUser);
				} else {
					connectServerError();
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			super.onPreExecute();
		}

		@Override
		protected HttpResponse doInBackground(User... params) {
			// TODO Auto-generated method stub
			HttpResponse response = null;
			MyHttpClient client = new MyHttpClient();
			User loginUser = params[0];

			String json = myGson.getJsonStr(loginUser);

			try {
				response = client.doPost(
						new URI(MyConstant.POST_LOGIN_USER_URI),
						createLoginUserParams(json));
				
				if(response != null){
					responseStr = EntityUtils.toString(response.getEntity());
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return response;
		}

		private List<NameValuePair> createLoginUserParams(final String json) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(MyConstant.POST_LOGIN_USER_KEY,
					json));
			return params;
		}
	}
}

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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends BaseActivity {

	String imei = null;
	Button registBtn = null;
	EditText userName, userPassword1, userPassword2, userMail;

	User registUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_layout);

		initView();

		TelephonyManager telManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		imei = telManager.getDeviceId();
	}

	protected void initView() {
		registBtn = (Button) findViewById(R.id.regist_button);
		userName = (EditText) findViewById(R.id.regist_user_name);
		userPassword1 = (EditText) findViewById(R.id.regist_user_password1);
		userPassword2 = (EditText) findViewById(R.id.regist_user_password2);
		userMail = (EditText) findViewById(R.id.regist_user_mail);

		registBtn.setOnClickListener(new RegistBtnListener());
	}

	protected User getUser() {
		User user = null;
		String name = null;
		String imei = null;
		String mail = null;

		name = userName.getText().toString();
		imei = this.imei;
		mail = userMail.getText().toString();

		String p1, p2;
		p1 = userPassword1.getText().toString();
		p2 = userPassword2.getText().toString();

		boolean check = false;
		if (name != null && name.trim().length() > 0) {
			check = true;
		}

		if (!check) {
			Toast.makeText(getApplicationContext(), "用户名为空", Toast.LENGTH_SHORT)
					.show();
		}

		if (check && imei == null) {
			check = false;
		}

		if (check
				&& (mail == null || mail.trim().length() <= 0
						|| !mail.endsWith(".com") || !mail.contains("@"))) {
			check = false;
			Toast.makeText(getApplicationContext(), "EMAIL填写有错误",
					Toast.LENGTH_SHORT).show();
		}

		if (check && (p1 == null || p2 == null || !p1.equals(p2))) {
			check = false;
			Toast.makeText(getApplicationContext(), "密码填写不一致",
					Toast.LENGTH_SHORT).show();
		}

		if (check) {
			user = new User();
			user.setImei(imei);
			user.setName(name);
			user.setPassword(p1);
			user.setMail(mail);
		}
		return user;
	}

	/**
	 * 创建成功注册后的通知界面
	 * 
	 * @param registUser
	 */
	protected void createRegistDoneDialog(final User registUser) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegistActivity.this);
		builder.setCancelable(false);
		builder.setTitle("注册成功");
		builder.setMessage("用户名:" + registUser.getName() + "\n用户设备号:"
				+ registUser.getImei());
		builder.setPositiveButton("登陆体验",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startOtherActivityInData(MainActivity.class, registUser);
					}
				});
		builder.setNegativeButton("稍后登陆",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startOtherActivity(LoginActivity.class);
					}
				});
		dialog = builder.create();
		dialog.show();
	}

	class RegistBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			AlertDialog dialog = createBasicTextDialog("", "拼命注册中..", null,
					false);
			User user = getUser();
			if (user != null) {
				RegistTask task = new RegistTask(dialog);
//				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
				task.execute(user);
			}
		}
	}

	class RegistTask extends AsyncTask<User, String, HttpResponse> {

		private AlertDialog dialog = null;
		private User user = null;
		private String responseStr = null;

		public RegistTask(AlertDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			// TODO Auto-generated method stub
			if (result == null) {
				dialog.setMessage("服务器无响应");
				dialog.setCancelable(true);
				return;
			}
			int statusCode = result.getStatusLine().getStatusCode();

			if (statusCode != 200) {
				dialog.setMessage("连接服务器错误！\n代码：" + statusCode + "服务器返回码："
						+ responseStr);
				dialog.setCancelable(true);
			} else {
				if (responseStr.trim().length() == 3) {
					dialog.setMessage("注册失败！\n" + responseStr);
					dialog.setCancelable(true);
				} else {
					Gson gson = new Gson();
					registUser = gson.fromJson(responseStr, User.class);
					dialog.dismiss();
					createRegistDoneDialog(registUser);
				}

			}

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected HttpResponse doInBackground(User... params) {
			// TODO Auto-generated method stub
			this.user = params[0];
			HttpResponse response = null;
			GsonTools gson = new GsonTools();
			MyHttpClient client = new MyHttpClient();
			String json = gson.getJsonStr(user);

			try {
				response = client.doPost(new URI(
						MyConstant.POST_REGSIT_USER_URI),
						createUserParams(json));
				if (response != null) {
					responseStr = EntityUtils.toString(response.getEntity());
				}
			} catch (URISyntaxException e) {

				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}

		private List<NameValuePair> createUserParams(final String json) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(MyConstant.POST_REGSIT_USER_KEY,
					json));
			return params;
		}
	}
}

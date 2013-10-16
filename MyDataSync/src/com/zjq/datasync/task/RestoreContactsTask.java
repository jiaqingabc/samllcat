package com.zjq.datasync.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.RestoreContacts;
import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.ContactsManager;
import com.zjq.datasync.tools.MyHttpClient;

import android.app.AlertDialog;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;

public class RestoreContactsTask extends AsyncTask<User, String, RestoreContacts> {
	Gson gson = null;
	
	AlertDialog dialog = null;
	ContactsManager cManager = null;

	public RestoreContactsTask(AlertDialog d,ContactsManager cManager) {
		gson = new Gson();
		this.dialog = d;
		this.cManager = cManager;
	}

	@Override
	protected void onPostExecute(RestoreContacts result) {
		// TODO Auto-generated method stub

		if (result != null) {

			if (result.getCode() == RestoreContacts.SUCCESS) {
				dialog.setMessage("数据获取成功,开始恢复数据..");
				int sum = 0;
				ArrayList<Contact> list = result.getContacts();
				for (int i = 0, j = list.size(); i < j; i++) {
					try {
						Contact c1 = list.get(i);
						Contact c2 = cManager.selectContact(c1.getName());
						//排除名字和电话号码相同的联系人
						if(c2 != null && c2.getNumber().equals(c1.getNumber())){
							
						} else {
							
							cManager.insertContactTransaction(c1);
							sum++;
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (OperationApplicationException e) {
						e.printStackTrace();
					}
				}

				dialog.setMessage("恢复数据 " + sum + " 条。");

			} else {
				dialog.setMessage("数据获取失败,服务器错误..");
			}
		} else {
			dialog.setMessage("数据获取失败,服务器错误..");
		}
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog.setMessage("开始连接网络..");
		dialog.setCancelable(false);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected RestoreContacts doInBackground(User... users) {
		// TODO Auto-generated method stub
		RestoreContacts rc = null;

		HttpClient client = new MyHttpClient().getHttpClient();
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(MyConstant.RESTORE_CONTACTS_KEY,
				gson.toJson(users[0])));
		String param = URLEncodedUtils.format(params, "UTF-8");
		// baseUrl
		String baseUrl = MyConstant.RESTORE_CONTACTS_URI;
		// 将URL与参数拼接
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

		try {
			HttpResponse response = client.execute(getMethod);
			if (response.getStatusLine().getStatusCode() == 200) {
				String json = EntityUtils.toString(response.getEntity());
				if (json != null) {
					rc = gson.fromJson(json, RestoreContacts.class);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rc;
	}
}

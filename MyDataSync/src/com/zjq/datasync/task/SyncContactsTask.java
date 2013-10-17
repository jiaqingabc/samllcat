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
import com.zjq.datasync.adapter.ContactsListAdapter;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.core.ContactsManager;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.RestoreContacts;
import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.MyHttpClient;

import android.app.AlertDialog;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class SyncContactsTask extends AsyncTask<User, String, RestoreContacts> {
	Gson gson = null;

	AlertDialog dialog = null;
	ContactsManager cManager = null;
	ListView contactsListView = null;
	ImageButton syncBtn = null;
	ContactsListAdapter adapter = null;

	public SyncContactsTask(AlertDialog d, ContactsManager cManager,
			ListView listView, ImageButton syncBtn,ContactsListAdapter adapter) throws Exception {
		gson = new Gson();
		this.dialog = d;
		this.cManager = cManager;
		this.contactsListView = listView;
		this.syncBtn = syncBtn;
		this.adapter = adapter;
		
		if(adapter == null){
			throw new Exception();
		}
	}

	
	@Override
	protected void onPostExecute(RestoreContacts result) {
		// TODO Auto-generated method stub

		if (result != null) {

			if (result.getCode() == RestoreContacts.SUCCESS) {
				dialog.setMessage("数据获取成功");
				ArrayList<Contact> list = result.getContacts();
				
				if(list.size() != 0){
					adapter.setData(list);
					
					adapter.sortPinyin();
					
					contactsListView.setAdapter(adapter);
					
					syncBtn.setVisibility(View.GONE);
					
					contactsListView.setVisibility(View.VISIBLE);
					
					contactsListView.setDividerHeight(5);
					
					dialog.setMessage("同步数据 " + list.size() + " 条。");
				} else {
					dialog.setMessage("目标服务器上没有可同步信息。 ");
				}
				
				

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
		params.add(new BasicNameValuePair(MyConstant.RESTORE_CONTACTS_KEY, gson
				.toJson(users[0])));
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

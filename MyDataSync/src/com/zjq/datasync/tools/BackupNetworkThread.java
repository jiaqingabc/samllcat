package com.zjq.datasync.tools;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.BackupRequest;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.User;
import com.zjq.file.FileTools;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class BackupNetworkThread extends Thread {
	
	public static final String CODE_KEY_1 = "code.get.key.1";
	public static final String CODE_KEY_2 = "code.get.key.2";
	public static final int THREAD_START = 0;
	public static final int THREAD_END = 2;
	public static final int READ_CONTACTS_START = 3;
	public static final int READ_CONTACTS_END = 4;
	public static final int SERVER_CONN_START = 5;
	public static final int SERVER_CONN_END = 6;
	public static final int SERVER_CONN_FAILED = 7;
	public static final int SERVER_CONN_SUCCESS = 8;
	public static final int THREAD_INSET_ERROR = 9;
	public static final int THREAD_INSET_ERROR_IO = 10;
	public static final int THREAD_INSET_ERROR_URI = 10;

	Handler mHandler = null;
	User user = null;
	ContactsManager numbers = null;
	
	GsonTools myGson = null;
	MyHttpClient myClient = null;
	HttpClient client = null;

	public BackupNetworkThread(Handler h, User user, ContactsManager numbers) {
		// TODO Auto-generated constructor stub
		this.mHandler = h;
		this.user = user;
		this.numbers = numbers;
	}

	private void sendMsg(int ...code){
		Message msg = new Message();
		Bundle data = new Bundle();
		msg.setData(data);
		data.putInt(CODE_KEY_1, code[0]);
		if(code.length == 2){
			data.putInt(CODE_KEY_2, code[1]);
		}
		mHandler.sendMessage(msg);
	}
	
	private void sendThreadErrorMsg(int eCase) {
		sendMsg(THREAD_INSET_ERROR,eCase);
	}
	
	private void sendStartThreadMsg() {
		sendMsg(THREAD_START);
	}

	private void sendEndThreadMsg() {
		sendMsg(THREAD_END);
	}

//	private void sendNetWordMsg(String s) {
//		Message msg = new Message();
//		Bundle data = new Bundle();
//		msg.setData(data);
//		data.putString(CODE_KEY_1, s);
//		mHandler.sendMessage(msg);
//	}

	private void sendReadContactsMsg() {
		sendMsg(READ_CONTACTS_START);
	}

	private void sendReadContactsEndMsg() {
		sendMsg(READ_CONTACTS_END);
	}
	
	private void sendConnectServStartMsg(){
		sendMsg(SERVER_CONN_START);
	}
	
	private void sendConnectServSuccessMsg(String resp) {
		Message msg = new Message();
		Bundle data = new Bundle();
		msg.setData(data);
		data.putInt(CODE_KEY_1, SERVER_CONN_SUCCESS);
		data.putString(CODE_KEY_2, resp);
		mHandler.sendMessage(msg);
	}
	
	private void sendConnectServFailedMsg(int code){
		sendMsg(SERVER_CONN_FAILED,code);
	}

	private List<NameValuePair> createUserParams(final String json) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(MyConstant.BACKUP_CONTACTS_KEY,
				json));
		return params;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		myGson = new GsonTools();
		myClient = new MyHttpClient();
		client = myClient.getHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);

		File file = null;
		String fileName = null;
		Random random = new Random();
		fileName = "" + random.nextInt(10000);
		
		System.out.println("Backup Thread is started!");
		
		//StartThread
		sendStartThreadMsg();
		
		try {
			//ReadContacts
			sendReadContactsMsg();
			
			file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + fileName);
			while (file.exists() && file.isFile()) {
				fileName = "" + random.nextInt(10000);
				file = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + fileName);
			}

			file.createNewFile();
			
			List<Contact> contacts = numbers.getAllContacts();

			if (contacts != null && contacts.size() > 0) {
				BackupRequest br = new BackupRequest();
				
				br.setContacts(contacts);
				br.setUser(user);
				String json = myGson.getJsonStr(br);

				FileTools.wirteFile(file, json.getBytes(), false);
			}
			
			//ReadContactsEnd
			sendReadContactsEndMsg();

			
			if (file.length() > 0) {
				MultipartEntity mpEntity = new MultipartEntity(); // ÎÄ¼þ´«Êä
				ContentBody cbFile = new FileBody(file);
				mpEntity.addPart(fileName, cbFile);
				
				HttpPost post = new HttpPost();
				post.setEntity(mpEntity);
				post.addHeader("charset", HTTP.UTF_8);

//				post.setEntity(new UrlEncodedFormEntity(createUserParams(myGson.getJsonStr(user))));
				
				post.setURI(new URI(MyConstant.BACKUP_CONTACTS_URI));
				
				//ConnectServStart
				sendConnectServStartMsg();
				
				HttpResponse response = this.client.execute(post);
				
				int stateCode = 0;
				String resStr = null;
				if(response != null){
					stateCode = response.getStatusLine().getStatusCode();
					resStr = EntityUtils.toString(response.getEntity());
				}
				
				if(stateCode == 200){
					//ConnectServSuccess
					sendConnectServSuccessMsg(resStr);
					System.out.println("Upload resStr : "+resStr);
				} else {
					sendConnectServFailedMsg(stateCode);
					System.out.println("Upload stateCode : "+stateCode);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			sendThreadErrorMsg(THREAD_INSET_ERROR_IO);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			sendThreadErrorMsg(THREAD_INSET_ERROR_URI);
		} finally {
			if(file != null && file.exists()){
				file.delete();
			}
		}
		
		if(client != null){
			client.getConnectionManager().shutdown();
		}
		
		
		sendEndThreadMsg();
		super.run();
	}
}

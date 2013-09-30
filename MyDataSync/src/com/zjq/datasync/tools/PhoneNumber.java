package com.zjq.datasync.tools;

import java.util.HashMap;
import java.util.LinkedList;

import com.zjq.datasync.model.Contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class PhoneNumber {

	private Context context = null;
	private ContentResolver resolver = null;
	final String[] selectArr ={Phone.DISPLAY_NAME,Phone.NUMBER};
	final int NAME_INDEX = 0;
	final int NUMBER_INDEX = 1;
	
	public PhoneNumber(Context context) {
		this.context = context;
		this.resolver = this.context.getContentResolver();
	}
	
	public LinkedList<HashMap<String, String>> getPhoneNumber(){
		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr, null, null, null);
		
		LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String,String>>();
		
		int count = cursor.getCount();
		
		//查询所有的联系人信息
		while(cursor.moveToNext()){
			String name = null;
			String number = null;
			
			name = cursor.getString(NAME_INDEX);
			number = cursor.getString(NUMBER_INDEX);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", name);
			map.put("number", number);
			list.add(map);
		}
		
		return list;
	}
	
	public LinkedList<HashMap<String, String>> getPhoneNumber(String selectName){
		System.out.println("Select name : "+selectName);
		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr, "display_name=?", new String[]{"W"}, null);
		
		LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String,String>>();
		
		int count = cursor.getCount();
		
		//查询所有的联系人信息
		while(cursor.moveToNext()){
			String name = null;
			String number = null;
			
			name = cursor.getString(NAME_INDEX);
			number = cursor.getString(NUMBER_INDEX);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", name);
			map.put("number", number);
			list.add(map);
		}
		
		return list;
	}

	public LinkedList<Contact> getContacts(){
		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr, null, null, null);
		
		LinkedList<Contact> list = new LinkedList<Contact>();
		
		int count = cursor.getCount();
		
		//查询所有的联系人信息
		while(cursor.moveToNext()){
			String name = null;
			String number = null;
			
			name = cursor.getString(NAME_INDEX);
			number = cursor.getString(NUMBER_INDEX);
			
			Contact c = new Contact();
			c.setName(name);
			c.setNumber(number);
			list.add(c);
		}
		
		return list;
	}
	
	public void insertNumber(){
		
	}
}
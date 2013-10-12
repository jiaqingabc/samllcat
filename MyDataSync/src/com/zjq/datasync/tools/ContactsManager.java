package com.zjq.datasync.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.zjq.datasync.model.Contact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class ContactsManager {

	private Context context = null;
	private ContentResolver resolver = null;
	final String[] selectArr = { Phone.DISPLAY_NAME, Phone.NUMBER };
	final int NAME_INDEX = 0;
	final int NUMBER_INDEX = 1;

	public ContactsManager(Context context) {
		this.context = context;
		this.resolver = this.context.getContentResolver();
	}

	public Contact selectContact(String selectName) {
		Contact c = null;

		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr,
				"display_name=?", new String[] { selectName }, null);

		// 查询所有的联系人信息
		while (cursor.moveToNext()) {
			String name = null;
			String number = null;

			name = cursor.getString(NAME_INDEX);
			number = cursor.getString(NUMBER_INDEX);

			c = new Contact();
			c.setName(name);
			c.setNumber(number);
		}

		return c;
	}

	private LinkedList<Contact> getSimContacts(LinkedList<Contact> l) {
		LinkedList<Contact> list = new LinkedList<Contact>();
		if(l != null){
			list = l;
		} else {
			list = new LinkedList<Contact>();
		}
		
		Uri uri = Uri.parse("content://icc/adn");
		Cursor cursor = resolver.query(uri, null, null,
				null, null);
		
		while (cursor.moveToNext()) {
	        String name = cursor.getString(cursor.getColumnIndex(People.NAME));
	        String phoneNumber = cursor.getString(cursor
	                .getColumnIndex(People.NUMBER));
	        
	        Contact c = new Contact();
			c.setName(name);
			c.setNumber(phoneNumber);
			list.add(c);
	    }
		return list;
	}
	
	public LinkedList<Contact> getPhoneContacts(){
		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr, null,
				null, null);

		LinkedList<Contact> list = new LinkedList<Contact>();

		int count = cursor.getCount();

		// 查询所有的联系人信息
		while (cursor.moveToNext()) {
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

	public LinkedList<Contact> getAllContacts() {
		Cursor cursor = resolver.query(Phone.CONTENT_URI, selectArr, null,
				null, null);

		LinkedList<Contact> list = new LinkedList<Contact>();

		int count = cursor.getCount();

		// 查询所有的联系人信息
		while (cursor.moveToNext()) {
			String name = null;
			String number = null;

			name = cursor.getString(NAME_INDEX);
			number = cursor.getString(NUMBER_INDEX);

			Contact c = new Contact();
			c.setName(name);
			c.setNumber(number);
			list.add(c);
		}
		
		return getSimContacts(list);
	}

	public void insertNumber(Contact c) {
		String name = c.getName();
		String number = c.getNumber();
		ContentValues values = new ContentValues();

		ContentResolver cr = context.getContentResolver();

		Uri rawContactUri = cr.insert(RawContacts.CONTENT_URI, values);

		long rawContactId = ContentUris.parseId(rawContactUri);

		// 往data表入姓名数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// 内容类型
		values.put(StructuredName.GIVEN_NAME, name);
		cr.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		// 往data表入电话数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, number);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		cr.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

		// values.clear();
		// values.put(Data.RAW_CONTACT_ID, rawContactId);
		// values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		// values.put(Email.DATA, "liming@itcast.cn");
		// values.put(Email.TYPE, Email.TYPE_WORK);
		// cr.insert(android.provider.ContactsContract.Data.CONTENT_URI,
		// values);
	}

	public void insertContactTransaction(Contact c) throws RemoteException,
			OperationApplicationException {
		String name = c.getName();

		String number = c.getNumber();

		ContentResolver cr = context.getContentResolver();

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());

		ops.add(ContentProviderOperation
				.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.GIVEN_NAME, name).build());

		ops.add(ContentProviderOperation
				.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, number)
				.withValue(Phone.TYPE, Phone.TYPE_MOBILE).build());

		ContentProviderResult[] results = cr.applyBatch(
				ContactsContract.AUTHORITY, ops);
		for (ContentProviderResult result : results) {
			System.out.println(result.uri.toString());
		}
	}
}
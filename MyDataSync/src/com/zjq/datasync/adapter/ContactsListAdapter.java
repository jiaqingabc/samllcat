package com.zjq.datasync.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zjq.datasync.R;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.tools.PinyinComparator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter {

	ArrayList<Contact> data = null;
	LayoutInflater inflater = null;

	public ContactsListAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public void setData(ArrayList<Contact> data) {
		this.data = data;
	}
	
	public void sortPinyin(){
		Contact[] cs = data.toArray(new Contact[data.size()]);
		if(cs.length != 0){
			Arrays.sort(cs, new PinyinComparator());
			data.clear();
			for(int i = 0,j = cs.length;i<j;i++){
				data.add(cs[i]);
			}
		}
	}
	
	public ArrayList<Contact> getData(){
		return this.data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout view = null;
		Contact c = data.get(position);
		if (convertView == null) {
			view = (LinearLayout) inflater.inflate(
					R.layout.contacts_list_adapter_children_view, null);
		} else {
			view = (LinearLayout) convertView;
		}
		TextView nameText, numberText;
		nameText = (TextView) view.findViewById(R.id.list_contact_name);
		numberText = (TextView) view.findViewById(R.id.list_contact_number);
		
		nameText.setText(c.getName());
		numberText.setText(c.getNumber());

		return view;
	}

}

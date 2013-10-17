package com.zjq.datasync.view;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.zjq.datasync.R;
import com.zjq.datasync.adapter.ContactsListAdapter;
import com.zjq.datasync.base.BaseActivity;
import com.zjq.datasync.core.ContactsManager;
import com.zjq.datasync.core.UserManager;
import com.zjq.datasync.model.BackupServerRespond;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.User;
import com.zjq.datasync.task.OutputTask;
import com.zjq.datasync.task.RestoreContactsTask;
import com.zjq.datasync.task.SyncContactsTask;
import com.zjq.datasync.thread.BackupNetworkThread;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	final String TAB_SPEC_BACKUP = "tab.spec.backup";

	Resources res = null;

	LayoutInflater inflater = null;

	ContactsManager cManager = null;

	UserManager uManager = null;

	User currentUser = null;

	Button loginBtn = null;

	Button backupBtn, restoreBtn, outputTxtBtn;

	ImageButton syncBtn;

	EditText searchText;

	ListView contactsList = null;

	ListView searchList = null;

	TextView titleText = null;

	AlertDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);

		res = getResources();

		cManager = new ContactsManager(MainActivity.this);

		inflater = getLayoutInflater();

		initView();

		initViewListener();

		if (currentUser == null) {
			uManager = new UserManager(MainActivity.this);
			User u = uManager.getUser();
			if (u != null) {
				startOtherActivityInData(LoginActivity.class, u, false);
			}
		}
	}

	protected void initView() {
		loginBtn = (Button) findViewById(R.id.title_login_btn);

		backupBtn = (Button) findViewById(R.id.backup_btn);

		restoreBtn = (Button) findViewById(R.id.restore_btn);

		outputTxtBtn = (Button) findViewById(R.id.output_btn);

		syncBtn = (ImageButton) findViewById(R.id.sync_contacts_btn);

		searchText = (EditText) findViewById(R.id.search_edit);

		titleText = (TextView) findViewById(R.id.title_text);

		contactsList = (ListView) findViewById(R.id.contacts_list_view);

		searchList = (ListView) findViewById(R.id.search_list_view);

		dialog = createBasicTextDialog(null, null, null, false);
	}

	protected void initViewListener() {
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startOtherActivity(LoginActivity.class, false);
			}
		});

		backupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentUser != null) {
					dialog.setMessage("备份准备中.");
					dialog.show();
					final BackupNetworkThread thread = new BackupNetworkThread(
							new BackupHandler(), currentUser, cManager);
					thread.start();
				} else {
					Toast.makeText(getApplicationContext(), "请先登录.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		restoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentUser != null) {
					RestoreContactsTask task = new RestoreContactsTask(dialog,
							cManager);
					task.execute(currentUser);
				} else {
					Toast.makeText(getApplicationContext(), "请先登录.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		outputTxtBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OutputTask task = new OutputTask(dialog, cManager);
				task.execute("");
			}
		});

		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					getSearchTarget(s.toString());
				} else {
					searchList.setVisibility(View.GONE);
					contactsList.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		syncBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentUser != null) {
					ContactsListAdapter adapter = new ContactsListAdapter(
							inflater);
					SyncContactsTask task = null;
					try {
						task = new SyncContactsTask(dialog, cManager,
								contactsList, syncBtn, adapter);
						task.execute(currentUser);
					} catch (Exception e) {
						dialog.setMessage("本地数据模型错误");
						dialog.setCancelable(true);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "请先登录.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		contactsList.setOnItemClickListener(new ListItemClickListener(contactsList));
		
		searchList.setOnItemClickListener(new ListItemClickListener(searchList));
	}

	protected void getSearchTarget(String s) {
		ArrayList<Contact> target = new ArrayList<Contact>();
		ContactsListAdapter adapter = (ContactsListAdapter) contactsList
				.getAdapter();
		if (adapter != null && adapter.getData() != null) {
			searchList.setVisibility(View.VISIBLE);
			contactsList.setVisibility(View.GONE);
			ArrayList<Contact> data = adapter.getData();
			for (int i = 0, j = data.size(); i < j; i++) {
				if (data.get(i).getName().startsWith(s.toUpperCase()) || data.get(i).getName().startsWith(s.toLowerCase())) {
					target.add(data.get(i));
				}
			}
			ContactsListAdapter adapter2 = new ContactsListAdapter(inflater);
			adapter2.setData(target);
			searchList.setAdapter(adapter2);
		}
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		currentUser = getCurrentUser(intent);
		if (currentUser != null) {
			loginBtn.setText("注销");
			titleText.setText(currentUser.getName());
			loginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentUser = null;
					titleText.setText("无登录账户");
					loginBtn.setText("登录");
					contactsList.setVisibility(View.GONE);
					syncBtn.setVisibility(View.VISIBLE);
					loginBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startOtherActivity(LoginActivity.class, false);
						}
					});
				}
			});
		}
		super.onNewIntent(intent);
	}
	
	class ListItemClickListener implements OnItemClickListener{
		ListView list = null;
		public ListItemClickListener(ListView list){
			this.list = list;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			final Contact content = (Contact) list.getAdapter()
					.getItem(arg2);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			final LinearLayout view = (LinearLayout) inflater.inflate(
					R.layout.operate_contact_layout, null);

			final Button callBtn = (Button) view
					.findViewById(R.id.operate_call_btn);
			final ImageButton smsBtn = (ImageButton) view
					.findViewById(R.id.operate_sms_btn);

			callBtn.setText(content.getNumber());

			builder.setTitle(content.getName());

			builder.setCancelable(true);

			builder.setView(view);

			callBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String number = content.getNumber();
					if (number != null && number.length() > 0) {
						Intent phoneIntent = new Intent(
								"android.intent.action.CALL", Uri
										.parse("tel:" + number));
						startActivity(phoneIntent);
					}
				}
			});

			smsBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String number = content.getNumber();
					if (number != null && number.length() > 0) {
						Uri smsToUri = Uri.parse("smsto:" + number);
						Intent intent = new Intent(Intent.ACTION_SENDTO,
								smsToUri);
						intent.putExtra("sms_body", "");
						startActivity(intent);
					}

				}
			});

			builder.show();
		}
	}

	class BackupHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			int code = msg.getData().getInt(BackupNetworkThread.CODE_KEY_1);

			switch (code) {
			case BackupNetworkThread.SERVER_CONN_FAILED:
				int httpCode = msg.getData().getInt(
						BackupNetworkThread.CODE_KEY_2);
				dialog.setMessage("服务器连接失败\n" + httpCode);
				break;
			case BackupNetworkThread.THREAD_INSET_ERROR:
				int insetCode = msg.getData().getInt(
						BackupNetworkThread.CODE_KEY_2);
				dialog.setMessage("客户端内部错误\n" + insetCode);
				break;
			case BackupNetworkThread.THREAD_START:

				break;

			case BackupNetworkThread.THREAD_END:
				break;

			case BackupNetworkThread.READ_CONTACTS_START:
				dialog.setMessage("开始读取联系人信息");
				break;

			case BackupNetworkThread.READ_CONTACTS_END:
				break;

			case BackupNetworkThread.SERVER_CONN_START:
				dialog.setMessage("开始连接服务器");
				break;

			case BackupNetworkThread.SERVER_CONN_SUCCESS:
				serverConnSuccess(msg);
				break;
			case BackupNetworkThread.SERVER_CONN_END:
				break;
			}

			dialog.setCancelable(true);
		}

		private void serverConnSuccess(Message msg) {
			Gson gson = new Gson();
			BackupServerRespond bp = gson.fromJson(
					msg.getData().getString(BackupNetworkThread.CODE_KEY_2),
					BackupServerRespond.class);
			String resp = null;
			switch (bp.getRespCode()) {
			case BackupServerRespond.VERIFY_FAILED:
				resp = "服务器验证用户信息失败";
				break;
			case BackupServerRespond.SERVER_DATA_ERROR:
				resp = "服务器错误";
				break;
			case BackupServerRespond.VERIFY_SUCCESS:
				resp = "服务器验证用户信息成功\n此次备份数据  " + bp.getUpdateNum() + " 条";
				break;
			}
			dialog.setMessage("连接服务器成功\n" + resp);
		}
	}
}

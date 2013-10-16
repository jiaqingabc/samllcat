package com.zjq.datasync.view;

import com.google.gson.Gson;
import com.zjq.datasync.R;
import com.zjq.datasync.adapter.ContactsListAdapter;
import com.zjq.datasync.base.BaseActivity;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.BackupServerRespond;
import com.zjq.datasync.model.User;
import com.zjq.datasync.service.BackService;
import com.zjq.datasync.service.BackService.MyBinder;
import com.zjq.datasync.task.OutputTask;
import com.zjq.datasync.task.RestoreContactsTask;
import com.zjq.datasync.task.SyncContactsTask;
import com.zjq.datasync.tools.BackupNetworkThread;
import com.zjq.datasync.tools.ContactsManager;
import com.zjq.datasync.tools.UserManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	final String TAB_SPEC_BACKUP = "tab.spec.backup";

	Resources res = null;

	ContactsManager cManager = null;

	UserManager uManager = null;

	User currentUser = null;

	Button loginBtn = null;

	Button backupBtn, restoreBtn, outputTxtBtn;

	Button searchBtn;

	ImageButton syncBtn;

	EditText searchText;

	ListView contactsList = null;

	TextView titleText = null;

	AlertDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		res = getResources();

		cManager = new ContactsManager(MainActivity.this);

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

		searchBtn = (Button) findViewById(R.id.search_btn);

		syncBtn = (ImageButton) findViewById(R.id.sync_contacts_btn);

		searchText = (EditText) findViewById(R.id.search_edit);

		titleText = (TextView) findViewById(R.id.title_text);

		contactsList = (ListView) findViewById(R.id.contacts_list_view);

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
					dialog.setMessage("����׼����.");
					dialog.show();
					final BackupNetworkThread thread = new BackupNetworkThread(
							new BackupHandler(), currentUser, cManager);
					thread.start();
				} else {
					Toast.makeText(getApplicationContext(), "���ȵ�¼.",
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
					Toast.makeText(getApplicationContext(), "���ȵ�¼.",
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

		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		syncBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentUser != null) {
					ContactsListAdapter adapter = new ContactsListAdapter(
							getLayoutInflater());
					SyncContactsTask task = null;
					try {
						task = new SyncContactsTask(dialog, cManager,
								contactsList, syncBtn, adapter);
						task.execute(currentUser);
					} catch (Exception e) {
						dialog.setMessage("��������ģ�ʹ���");
						dialog.setCancelable(true);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "���ȵ�¼.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		currentUser = getCurrentUser(intent);
		if (currentUser != null) {
			loginBtn.setText("ע��");
			titleText.setText(currentUser.getName());
			loginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentUser = null;
					titleText.setText("�޵�¼�˻�");
					loginBtn.setText("��¼");
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
				dialog.setMessage("����������ʧ��\n" + httpCode);
				break;
			case BackupNetworkThread.THREAD_INSET_ERROR:
				int insetCode = msg.getData().getInt(
						BackupNetworkThread.CODE_KEY_2);
				dialog.setMessage("�ͻ����ڲ�����\n" + insetCode);
				break;
			case BackupNetworkThread.THREAD_START:

				break;

			case BackupNetworkThread.THREAD_END:
				break;

			case BackupNetworkThread.READ_CONTACTS_START:
				dialog.setMessage("��ʼ��ȡ��ϵ����Ϣ");
				break;

			case BackupNetworkThread.READ_CONTACTS_END:
				break;

			case BackupNetworkThread.SERVER_CONN_START:
				dialog.setMessage("��ʼ���ӷ�����");
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
				resp = "��������֤�û���Ϣʧ��";
				break;
			case BackupServerRespond.SERVER_DATA_ERROR:
				resp = "����������";
				break;
			case BackupServerRespond.VERIFY_SUCCESS:
				resp = "��������֤�û���Ϣ�ɹ�\n�˴α�������  " + bp.getUpdateNum() + " ��";
				break;
			}
			dialog.setMessage("���ӷ������ɹ�\n" + resp);
		}
	}
}

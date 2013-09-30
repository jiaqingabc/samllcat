package com.zjq.datasync.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.zjq.datasync.R;
import com.zjq.datasync.base.BaseActivity;
import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.model.BackupServerRespond;
import com.zjq.datasync.model.Contact;
import com.zjq.datasync.model.User;
import com.zjq.datasync.tools.BackupNetworkThread;
import com.zjq.datasync.tools.GsonTools;
import com.zjq.datasync.tools.PhoneNumber;
import com.zjq.file.FileTools;
import com.zjq.secret.SecretTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataBackupActivity extends BaseActivity {

	User currentUser = null;

	GridView menuView = null;

	AlertDialog dialog = null;
	String IMEI = null;
	PhoneNumber numbers = null;

	final String[] itemStrs = { "备份至网络", "备份至本地", "备份至文本", "从网络恢复", "从本地恢复" };
	final int[] itemIcons = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};

	final int BACKUP_IN_NETWORK = 0;
	final int BACKUP_IN_DES_FILE = 1;
	final int BACKUP_IN_TXT_FILE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.backup_layout);

		TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		IMEI = telManager.getDeviceId();

		numbers = new PhoneNumber(DataBackupActivity.this);

		currentUser = getCurrentUser();

		initView();
	}

	protected void initView() {
		menuView = (GridView) findViewById(R.id.backup_menu);

		dialog = createBasicTextDialog(null, null, null, false);

		menuView.setAdapter(new MenuAdapter());

		menuView.setOnItemClickListener(new MyItemsClickListener());
	}

	class MyItemsClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Toast.makeText(getApplicationContext(), "" + arg2,
					Toast.LENGTH_SHORT).show();
			switch (arg2) {
			case BACKUP_IN_NETWORK:
				dialog.setMessage("备份准备中.");
				dialog.show();
				final BackupNetworkThread thread = new BackupNetworkThread(
						new MyHandler(dialog), currentUser, numbers);
				thread.start();
				dialog.setCanceledOnTouchOutside(true);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (thread != null) {
							thread.interrupt();
						}
					}
				});
				break;
			case BACKUP_IN_DES_FILE:
				BackUpInSecretFileTask inSecretFileTask = new BackUpInSecretFileTask();
				inSecretFileTask.execute("");
				break;
			case BACKUP_IN_TXT_FILE:
				BackUpInFileTask txtFileTask = new BackUpInFileTask();
				txtFileTask.execute("");
				break;

			default:
				break;
			}
		}
	}

	class MyHandler extends Handler {

		AlertDialog mDialog = null;

		public MyHandler(AlertDialog mDialog) {
			this.mDialog = mDialog;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			int code = msg.getData().getInt(BackupNetworkThread.CODE_KEY_1);

			switch (code) {
			case BackupNetworkThread.SERVER_CONN_FAILED:
				int httpCode = msg.getData().getInt(
						BackupNetworkThread.CODE_KEY_2);
				mDialog.setMessage("服务器连接失败\n" + httpCode);
				break;
			case BackupNetworkThread.THREAD_INSET_ERROR:
				int insetCode = msg.getData().getInt(
						BackupNetworkThread.CODE_KEY_2);
				mDialog.setMessage("客户端内部错误\n" + insetCode);
				break;
			case BackupNetworkThread.THREAD_START:

				break;

			case BackupNetworkThread.THREAD_END:
				break;

			case BackupNetworkThread.READ_CONTACTS_START:
				mDialog.setMessage("开始读取联系人信息");
				break;

			case BackupNetworkThread.READ_CONTACTS_END:
				break;

			case BackupNetworkThread.SERVER_CONN_START:
				mDialog.setMessage("开始连接服务器");
				break;

			case BackupNetworkThread.SERVER_CONN_SUCCESS:
				serverConnSuccess(msg);
				break;
			case BackupNetworkThread.SERVER_CONN_END:
				break;
			}

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
			mDialog.setMessage("连接服务器成功\n" + resp);
		}
	}

	class MenuAdapter extends BaseAdapter {

		int count = 0;
		LayoutInflater inflater = null;

		public MenuAdapter() {
			this.inflater = getLayoutInflater();
			if (itemStrs.length != itemIcons.length) {
				count = 0;
			} else {
				count = itemStrs.length;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemStrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout body = null;

			String text = itemStrs[position];
			Drawable draw = getResources().getDrawable(itemIcons[position]);

			if (convertView == null) {
				body = (LinearLayout) inflater.inflate(
						R.layout.backup_grid_body, null);
			} else {
				body = (LinearLayout) convertView;
			}

			ImageView vImage = (ImageView) body
					.findViewById(R.id.grid_body_image);
			TextView vText = (TextView) body.findViewById(R.id.grid_body_text);

			vText.setText(text);
			vImage.setImageDrawable(draw);

			return body;
		}
	}

	class BackUpInSecretFileTask extends AsyncTask<String, Integer, Void> {
		File backupFile = null;
		File backupFileDir = null;

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			String fileName = MyConstant.BACKUP_SECRET_FILE_NAME;
			List<Contact> contacts = null;

			backupFileDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + MyConstant.BACKUP_SECRET_FILE_DIR);

			if (!backupFileDir.exists() || !backupFileDir.isDirectory()) {
				backupFileDir.mkdirs();
			}

			backupFile = new File(backupFileDir.getPath() + "/" + fileName);
			try {
				if (backupFile.exists() && backupFile.isFile()) {

					backupFile.delete();
				}

				backupFile.createNewFile();

				contacts = numbers.getContacts();
				if (contacts != null && contacts.size() > 0) {
					GsonTools myGson = new GsonTools();
					String json = myGson.getJsonStr(contacts);

					byte[] secretBytes = SecretTools.encrypt(json.getBytes(),
							IMEI, SecretTools.DES);
					FileTools.wirteFile(backupFile, secretBytes, false);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			dialog.setCancelable(true);
			dialog.setMessage("备份完成！\n文件位置:"
					+ backupFile.getPath().replace("/mnt/sdcard/", ""));
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog.setMessage("正在备份...");
			dialog.setCancelable(false);
			dialog.show();
			super.onPreExecute();
		}
	}

	class BackUpInFileTask extends AsyncTask<String, Integer, Void> {

		File backupFile = null;
		File backupFileDir = null;

		public BackUpInFileTask() {

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			String fileName = null;
			List<Contact> contacts = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd",
					Locale.CHINESE);

			fileName = "backup_" + format.format(new Date()) + ".txt";

			try {
				backupFileDir = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ MyConstant.BACKUP_FILE_DIR);

				backupFile = new File(backupFileDir.getPath() + "/" + fileName);

				if (!backupFileDir.exists()) {
					backupFileDir.mkdirs();
				}

				if (backupFile.exists() && backupFile.isFile()) {
					backupFile.delete();
				}

				backupFile.createNewFile();

				contacts = numbers.getContacts();

				if (contacts != null && contacts.size() > 0) {
					StringBuffer sb = new StringBuffer();
					Contact c = null;
					for (int i = 0, j = contacts.size(); i < j; i++) {
						c = contacts.get(i);
						sb.append(c.getName() + "  " + c.getNumber());
						sb.append("\r\n");
					}
					FileTools.wirteFile(backupFile, sb.toString().getBytes(),
							false);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			dialog.setCancelable(true);
			dialog.setMessage("备份完成！\n文件位置:"
					+ backupFile.getPath().replace("/mnt/sdcard/", ""));
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog.setMessage("正在备份...");
			dialog.setCancelable(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
	}
}

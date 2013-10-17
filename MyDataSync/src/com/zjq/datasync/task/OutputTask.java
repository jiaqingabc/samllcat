package com.zjq.datasync.task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.zjq.datasync.base.MyConstant;
import com.zjq.datasync.core.ContactsManager;
import com.zjq.datasync.model.Contact;
import com.zjq.file.FileTools;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Environment;

public class OutputTask extends AsyncTask<String, Integer, Void> {

	File backupFile = null;
	File backupFileDir = null;
	
	AlertDialog dialog = null;
	ContactsManager cManager = null;

	public OutputTask(AlertDialog d,ContactsManager cManager) {
		this.dialog = d;
		this.cManager = cManager;
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

			contacts = cManager.getAllContacts();

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

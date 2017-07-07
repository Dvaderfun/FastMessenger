package ru.lischenko_dev.fastmessenger.common;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import ru.lischenko_dev.fastmessenger.*;
import ru.lischenko_dev.fastmessenger.util.*;
import java.net.*;
import android.net.*;

import android.app.DownloadManager.Request.*;
import android.os.*;
import java.io.*;
import java.util.*;
import android.database.*;
import android.util.*;

public class OTAManager {

	public final String app_version = "0.9";
	public final String json_url = "http://advteam.3dn.ru/fvk.json";

	private Context context;

	private long downloadRef;
	private String download_url = "http://advteam.3dn.ru/fastmessenger/";
	private String newVersion = null;
	private String path_external = "Android/data/ru.lischenko_dev.fastmessenger/";

	private DownloadManager manager;

	public void checkOTAUpdates() {
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject root = JSONParser.parseObject(json_url, "utf-8");
					    newVersion = root.optString("version");

						((Activity) context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (!app_version.contains(newVersion))
										showUpdateDialog(newVersion);
								}
							});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
	}

	private void downloadLatestVersion(String url) {
		manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri download_link = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(download_link);
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		request.setVisibleInDownloadsUi(true);
		request.setTitle("Downloading...");
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setShowRunningNotification(true);
		request.setDescription("Fast Messenger v." + newVersion);
		request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, newVersion + ".apk");
		downloadRef = manager.enqueue(request);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			public void onReceive(Context c, Intent intent) {
				long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (downloadRef == reference) {
				//	DownloadManager.Query query = new DownloadManager.Query();
					//query.setFilterById(reference);
					//Cursor cursor = manager.query(query);
					//cursor.moveToFirst();
					//int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
					//String path = context.getString(fileNameIndex);
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Environment.getExternalStorageDirectory() + path_external + newVersion + ".apk"));
					c.startActivity(i);
					Log.d("Receiver", "OnComplete");
				}
			}

		};
		((Activity) context).registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	private void showUpdateDialog(String version) {
		final Dialog dialog = new Dialog(context, R.style.DialogFullscreen);
		View v = View.inflate(context, R.layout.ota_new_version, null);
		((Button) v.findViewById(R.id.btnDownload)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1) {
					downloadLatestVersion(download_url + newVersion + ".apk");
					dialog.dismiss();
					//c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(download_url + newVersion + ".apk")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					//((Activity) c).finishAffinity();
				}
			});
		((ImageView) v.findViewById(R.id.img_dialog_fullscreen_close)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1) {
					dialog.dismiss();
				}
			});

		((TextView) v.findViewById(R.id.tvChangelog)).setText("Version: " + newVersion);
		((TextView) v.findViewById(R.id.tvTitle)).setText("New OTA Version!");
		dialog.setContentView(v);
		dialog.setCancelable(false);
		dialog.show();
	}

	public static OTAManager get(Context c) {
		return new OTAManager(c);
	}

	public OTAManager(Context c) {
		this.context = c;
	}
}

package ru.lischenko_dev.fastmessenger.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.util.JSONParser;
import java.io.*;
import android.widget.*;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.*;

public class OTAManager
{

    public final String app_version = "0.8.5";
    public final String json_url = "http://advteam.3dn.ru/fvk.json";
	
    private Context context;

    private long downloadRef;

    private BroadcastReceiver receiver;


    private String download_url = "http://advteam.3dn.ru/fastmessenger/";
    private String newVersion = null;
	private String newChangelog = null;

    private DownloadManager manager;

    public OTAManager(Context c)
	{
        this.context = c;
    }

    public static OTAManager get(Context c)
	{
        return new OTAManager(c);
    }

    public void checkOTAUpdates()
	{
        new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						JSONObject root = JSONParser.parseObject(json_url, "utf-8");
						newVersion = root.optString("version");
						newChangelog = root.optString("changelog");
						if(newChangelog == null)
							newChangelog = "null. this bad";
						((Activity) context).runOnUiThread(new Runnable() {

								@Override
								public void run()
								{
									if (!app_version.contains(newVersion))
										showUpdateDialog(newVersion);

								}
							});
					}
					catch (Exception e)
					{
						Log.e("OTAManager check Error", e.toString());
						e.printStackTrace();
					}
				}
			}).start();
    }

    private void downloadLatestVersion(String url)
	{
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri download_link = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(download_link);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(context.getString(R.string.downloading));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setShowRunningNotification(true);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, newVersion + ".apk");
        downloadRef = manager.enqueue(request);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent)
			{
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
				{
                    new Thread(new Runnable() {
							@Override
							public void run()
							{
								File toInstall = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/files/Download/", newVersion + ".apk");
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
									Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", toInstall);
									Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
									intent.setData(apkUri);
									intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
									context.startActivity(intent);
								} else {
									Uri apkUri = Uri.fromFile(toInstall);
									Intent intent = new Intent(Intent.ACTION_VIEW);
									intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent);
								}
							}

						}).start();
                }

            }
        };
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void showUpdateDialog(final String version)
	{
        final Dialog dialog = new Dialog(context, R.style.DialogFullscreen);
        View v = View.inflate(context, R.layout.ota_new_version, null);
        (v.findViewById(R.id.btnDownload)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
					downloadLatestVersion(download_url + version + ".apk");
				}
			});
        (v.findViewById(R.id.img_dialog_fullscreen_close)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
				}
			});

		((TextView) v.findViewById(R.id.tvChangelog)).setText("Version: " + newVersion + "\nChangelog" + newChangelog);
        ((TextView) v.findViewById(R.id.tvTitle)).setText("New OTA Version!");
        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.show();
    }
}

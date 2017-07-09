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

public class OTAManager {

    public final String app_version = "0.8";
    public final String json_url = "http://advteam.3dn.ru/fvk.json";

    private Context context;

    private long downloadRef;

    private BroadcastReceiver receiver;


    private String download_url = "http://advteam.3dn.ru/fastmessenger/";
    private String newVersion = null;

    private DownloadManager manager;

    public OTAManager(Context c) {
        this.context = c;
    }

    public static OTAManager get(Context c) {
        return new OTAManager(c);
    }

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
                    Log.e("OTAManager check Error", e.toString());
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
        request.setTitle(context.getString(R.string.downloading));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setShowRunningNotification(true);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, newVersion + ".apk");
        downloadRef = manager.enqueue(request);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }

                    }).start();
                }

            }
        };
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void showUpdateDialog(final String version) {
        final Dialog dialog = new Dialog(context, R.style.DialogFullscreen);
        View v = View.inflate(context, R.layout.ota_new_version, null);
        (v.findViewById(R.id.btnDownload)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                downloadLatestVersion(download_url + version + ".apk");
                dialog.dismiss();
            }
        });
        (v.findViewById(R.id.img_dialog_fullscreen_close)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                dialog.dismiss();
            }
        });

        // ((TextView) v.findViewById(R.id.tvChangelog)).setText("Version: " + newVersion);
        //((TextView) v.findViewById(R.id.tvTitle)).setText("New OTA Version!");
        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.show();
    }
}

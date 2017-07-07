package ru.lischenko_dev.fastmessenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.KException;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class LongPollService extends Service {

    public boolean isRunning;
    private Thread updateThread;
    public static final String TAG = "VKLongPollService";
    private String key;
    private String server;
    private Long ts;
    private Object[] pollServer;

    private Account account = new Account();
    private Api api;

    public LongPollService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        account.restore(this);
        Log.d(TAG, "Created");
        api = Api.init(account);
        isRunning = true;
        updateThread = new Thread(new MessageUpdater());
        updateThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MessageUpdater implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                Log.d(TAG, "Updater Task Started...");
                if (!Utils.hasConnection(getApplicationContext())) {
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                try {
                    if (server == null) {
                        getServer();
                    }
                    String response = getResponse();
                    JSONObject root = new JSONObject(response);

                    Long tsResponse = root.optLong("ts");
                    JSONArray updates = root.getJSONArray("updates");


                    ts = tsResponse;
                    if (updates.length() != 0) {
                        process(updates);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    server = null;
                }

            }
        }

        private String getResponse() throws IOException {
            String request = "https://" + server
                    + "?act=a_check&key=" + key
                    + "&ts=" + ts +
                    "&wait=25&mode=2";
            return Api.sendRequestInternal(request, "", false);
        }


        private void messageEvent(JSONArray item) throws JSONException {
            VKMessage message = VKMessage.parse(item);
            EventBus.getDefault().postSticky(message);
        }

        private void messageClearFlags(int id, int mask) {
            if (VKMessage.isUnread(mask)) {
                EventBus.getDefault().post(id);
            }
        }

        private void getServer() throws IOException, JSONException, KException {
            pollServer = api.getLongPollServer(null, null);
            key = (String) pollServer[0];
            server = (String) pollServer[1];
            ts = (Long) pollServer[2];
        }

        private void process(JSONArray updates) throws JSONException {
            if (updates.length() == 0) {
                return;
            }

            for (int i = 0; i < updates.length(); i++) {
                JSONArray item = updates.optJSONArray(i);
                int type = item.optInt(0);

                switch (type) {
                    case 3:
                        int id = item.optInt(1);
                        int mask = item.optInt(2);
                        messageClearFlags(id, mask);
                        break;

                    case 4:
                        messageEvent(item);
                        break;

                }
            }
        }

    }
}

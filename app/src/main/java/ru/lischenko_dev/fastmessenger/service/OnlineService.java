package ru.lischenko_dev.fastmessenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.vkapi.Api;

public class OnlineService extends Service {

    private Timer timer;
    private Account account;
    private Api api;

    public OnlineService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        account = Account.get(this);
        api = Api.init(account);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                api.setOnline(null, null);
            }
        }, 0, 60 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
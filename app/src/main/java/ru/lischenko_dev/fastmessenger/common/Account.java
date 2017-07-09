package ru.lischenko_dev.fastmessenger.common;

import android.content.Context;
import android.content.SharedPreferences;

import ru.lischenko_dev.fastmessenger.util.Utils;

public class Account {

    public long user_id = 0;
    public String avatar, name, status, small_avatar, access_token = null;
    public boolean isMusicPlaying = false;

    private SharedPreferences prefs = null;
    private SharedPreferences.Editor editor = null;

    private Context context = null;

    public Account(Context context) {
        this.context = context;
        restore();
    }

    public static Account get(Context context) {
        return new Account(context);
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void save() {
        prefs = Utils.getPrefs(context);
        editor = prefs.edit();
        editor.putString("access_token", access_token);
        editor.putLong("user_id", user_id);
        editor.putString("avatar", avatar);
        editor.putString("small_avatar", small_avatar);
        editor.putString("name", name);
        editor.putString("status", status);
        editor.putBoolean("isMusicPlaying", isMusicPlaying);
        editor.apply();
    }

    public Account restore() {
        prefs = Utils.getPrefs(context);
        access_token = prefs.getString("access_token", "");
        user_id = prefs.getLong("user_id", 0);
        avatar = prefs.getString("avatar", "");
        small_avatar = prefs.getString("small_avatar", "");
        name = prefs.getString("name", "");
        status = prefs.getString("status", "");
        isMusicPlaying = prefs.getBoolean("isMusicPlaying", false);

        return null;
    }

    public void clear() {
        editor = Utils.getPrefs(context).edit();
        editor.remove("access_token");
        editor.remove("user_id");
        editor.remove("name");
        editor.remove("status");
        editor.remove("isMusicPlaying");
        editor.apply();
        save();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public long getId() {
        return user_id;
    }

    public String getToken() {
        return access_token;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSmallAvatar() {
        return small_avatar;
    }
}

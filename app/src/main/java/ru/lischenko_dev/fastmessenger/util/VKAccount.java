package ru.lischenko_dev.fastmessenger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class VKAccount {

    private Context context;

    public long user_id;
    public String avatar, name, status, small_avatar, access_token = null;

    private SharedPreferences prefs;
    private Editor editor;

    public VKAccount(Context context) {
        this.context = context;
        restore();
    }

    public static VKAccount get(Context context) {
        return new VKAccount(context);
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
        editor.apply();
    }

    public VKAccount restore() {
        prefs = Utils.getPrefs(context);
        access_token = prefs.getString("access_token", "");
        user_id = prefs.getLong("user_id", 0);
        avatar = prefs.getString("avatar", "");
        small_avatar = prefs.getString("small_avatar", "");
        name = prefs.getString("name", "");
        status = prefs.getString("status", "");
        return null;
    }

    public void clear() {
        prefs = Utils.getPrefs(context);
        editor = prefs.edit();
        editor.remove("access_token");
        editor.remove("avatar");
        editor.remove("user_id");
        editor.remove("small_avatar");
        editor.remove("status");
        editor.remove("name");
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

}

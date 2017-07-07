package ru.lischenko_dev.fastmessenger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Account
{

    public String access_token;
    public long user_id;

    public String avatar, name, status, small_avatar = null;

    private SharedPreferences prefs;
    private Editor editor;

    public Account()
	{

    }

    public void save(Context c)
	{
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        editor = prefs.edit();
        editor.putString("access_token", access_token);
        editor.putLong("user_id", user_id);
        editor.putString("avatar", avatar);
        editor.putString("small_avatar", small_avatar);
        editor.putString("name", name);
        editor.putString("status", status);

        editor.apply();
    }

    public void restore(Context c)
	{
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        access_token = prefs.getString("access_token", "");
        user_id = prefs.getLong("user_id", 0);
        avatar = prefs.getString("avatar", "");
        small_avatar = prefs.getString("small_avatar", "");
        name = prefs.getString("name", "");
        status = prefs.getString("status", "");
    }

    public void clear(Context c)
	{
        access_token = null;
        user_id = 0;
        avatar = null;
        small_avatar = null;
        status = null;
        name = null;

        save(c);
    }

    @Override
    public String toString()
	{
        return name;
    }

	public String getName()
	{
		return name;
	}

	public String getStatus()
	{
		return status;
	}

	public long getId()
	{
		return user_id;
	}

	public String getToken()
	{
		return access_token;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public String getSmallAvatar() {
		return small_avatar;
	}
}

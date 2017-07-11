package ru.lischenko_dev.fastmessenger.util;
import android.content.*;
import android.preference.*;

public class PrefsManager
{
	private static Context context;
	
	public static PrefsManager getInstance(Context c) {
		return new PrefsManager(c);
	}
	
	public PrefsManager(Context c) {
		this.context = c;
	}
	
	public static void put(String key, String value) {
		if (context == null)
			throw new NullPointerException("Context == null! I cant work (black_moon)");
		PrefsManager.getPrefs().edit().putString(key, value);
	}
	
	public static Object get(String key, String defValue) {
		if(context == null)
			throw new NullPointerException("Context == null");
		return PrefsManager.getPrefs().getString(key, defValue);
	}
	
	public static SharedPreferences getPrefs() {
		if(context == null)
			throw new NullPointerException("Context == null");
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}

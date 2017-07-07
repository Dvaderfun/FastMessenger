package ru.lischenko_dev.fastmessenger.util;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.support.design.widget.*;

public class ExceptionChecker implements Thread.UncaughtExceptionHandler
 {

    Thread.UncaughtExceptionHandler oldHandler;

	private Context c;
	
    public ExceptionChecker(Context c) {
		this.c = c;
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e("ExceptionChecker", "Something wrong! " + "Exception Thread: " + thread + " Exception: " + throwable);
		TextView tv = new TextView(c);
		tv.setText(throwable.toString());
		BottomSheetDialog d = new BottomSheetDialog(c);
		d.setContentView(tv);
		d.show();
		//Toast.makeText(c, throwable.toString(), Toast.LENGTH_LONG).show();
		//new AlertDialog.Builder(c).setTitle("Error!").setMessage(throwable.toString()).create().show();
        if(oldHandler != null) oldHandler.uncaughtException(thread, throwable);
    }
}

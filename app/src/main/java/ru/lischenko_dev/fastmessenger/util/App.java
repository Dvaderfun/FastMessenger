package ru.lischenko_dev.fastmessenger.util;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import ru.lischenko_dev.fastmessenger.service.*;
import org.acra.annotation.*;
import org.acra.*;

import ru.lischenko_dev.fastmessenger.common.*;



@ReportsCrashes(mailTo = "lischenkodev@gmail.com",
	mode = ReportingInteractionMode.DIALOG,
	resDialogText = ru.lischenko_dev.fastmessenger.R.string.crash_text)
public class App extends Application
{

    public Context context;

    public static volatile int screenWidth;
    public static volatile int screenHeight;

    @Override
    public void onCreate()
	{
        super.onCreate();
		ACRA.init(this);
        context = getApplicationContext();
        screenHeight = Utils.getDisplayHeight(this);
        screenWidth = Utils.getDisplayWidth(this);

		if (Utils.hasConnection(this))
			startService(new Intent(this, LongPollService.class));
    }
}

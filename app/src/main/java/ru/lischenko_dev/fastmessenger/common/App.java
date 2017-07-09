package ru.lischenko_dev.fastmessenger.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import ru.lischenko_dev.fastmessenger.service.LongPollService;
import ru.lischenko_dev.fastmessenger.util.Utils;


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
		if(!Build.BRAND.toLowerCase().contains("xiaomi"))
		ACRA.init(this);
        context = getApplicationContext();
        screenHeight = Utils.getDisplayHeight(this);
        screenWidth = Utils.getDisplayWidth(this);


		if (Utils.hasConnection(this))
			startService(new Intent(this, LongPollService.class));
    }
}

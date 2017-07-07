package ru.lischenko_dev.fastmessenger;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;


import ru.lischenko_dev.fastmessenger.common.*;

import ru.lischenko_dev.fastmessenger.fragment.*;
import ru.lischenko_dev.fastmessenger.service.*;
import ru.lischenko_dev.fastmessenger.util.*;

import android.support.v7.widget.Toolbar;

import ru.lischenko_dev.fastmessenger.util.ThemeManager;

import ru.lischenko_dev.fastmessenger.util.Utils;
import android.support.v7.app.*;
import android.support.annotation.*;
import android.graphics.*;
import android.graphics.drawable.*;


public class MainActivity extends AppCompatActivity 
{

    public static final int REQUEST_LOGIN = 1;

    private Account account = new Account();

    private Toolbar toolbar;
	private DrawerLayout drawerLayout;
	public NavigationView navigationView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(ThemeManager.get(this).getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		initItems();
        account.restore(this);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(0x00000000);
			toolbar.setElevation(8);
		}
        if (Utils.hasConnection(this))
			initDrawerHeader(navigationView.getHeaderView(0));
    
        if (Utils.hasConnection(this)) {
			OTAManager.get(this).checkOTAUpdates();
			initDrawerHeader(navigationView.getHeaderView(0));
        }

		if (navigationView != null)
		{
			navigationView.setCheckedItem(R.id.nav_messages);
			navigationView.inflateMenu(R.menu.activity_main_drawer);
			navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

					@Override
					public boolean onNavigationItemSelected(MenuItem p1)
					{
						switch (p1.getItemId())
						{
							case R.id.nav_messages:
								replaceFragment(new FragmentMessages());
								break;
							case R.id.nav_friends:
								replaceFragment(new FragmentFriends());
								break;
							case R.id.nav_settings:
								startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
								break;
							case R.id.nav_account_exit:
								showExitDialog();
								break;
						}

						drawerLayout.closeDrawers();
						return false;
					}
				});

		}
        replaceFragment(new FragmentMessages());

    }
	
	

    private void showExitDialog()
	{
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.warning));
        adb.setMessage(getString(R.string.exit_message));
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i)
				{
					account.clear(getApplicationContext());
					ActivityCompat.finishAffinity(MainActivity.this);
				}
			});
        adb.setNegativeButton(R.string.no, null);
        AlertDialog alert = adb.create();
        alert.show();
    }

	private void initDrawerHeader(View v)
	{
		Picasso.with(this).load(account.getAvatar()).into(((ImageView) v.findViewById(R.id.ivAva)));
		((TextView) v.findViewById(R.id.tvName)).setText(account.getName());
		((TextView) v.findViewById(R.id.tvStatus)).setText(account.getStatus());
	}

    @Override
    protected void onDestroy()
	{
        stopService(new Intent(this, LongPollService.class));
        super.onDestroy();
    }

    private void replaceFragment(Fragment fragment)
	{
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void initItems()
	{
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,  toolbar,  R.string.app_name, R.string.app_name);
		toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		}

	@Override
	public void onBackPressed()
	{
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawers();
		else
			super.onBackPressed();
	}
}

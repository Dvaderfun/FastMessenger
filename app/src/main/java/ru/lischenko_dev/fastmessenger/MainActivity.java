package ru.lischenko_dev.fastmessenger;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.service.LongPollService;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.common.*;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 1;
    public static final int REQUEST_PERMISSIONS = 2;
    public NavigationView navigationView;
    private Account account;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeManager.get(this).getCurrentTheme());
        super.onCreate(savedInstanceState);
        account = Account.get(this);
        setContentView(R.layout.activity_main);

        initItems();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0x00000000);
            getWindow().setNavigationBarColor(ThemeManager.get(this).getPrimaryColor());
            toolbar.setElevation(8);
        }

        requestPermissionsForWrite();

        if (Utils.hasConnection(this)) {
            initDrawerHeader(navigationView.getHeaderView(0));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem p1) {
                    int id = p1.getItemId();
                    if (id != R.id.nav_account_exit & id != R.id.nav_settings)
                        p1.setChecked(true);
                    switch (id) {
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

    private void requestPermissionsForWrite() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            Snackbar.make(drawerLayout, R.string.need_permissions_message, 10000).setAction(R.string.grant_permissions, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_PERMISSIONS);
                }
            }).show();
		else
        OTAManager.get(this).checkOTAUpdates();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().findFragmentByTag("chat") != null) {
            MenuItem materials = menu.add(R.string.activity_materials_title);
            MenuItem update = menu.add(R.string.update);

            update.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ((FragmentChat) getSupportFragmentManager().findFragmentByTag("chat")).update();
                    return false;
                }
            });
            materials.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startActivity(new Intent(getApplicationContext(), MaterialsActivity.class).putExtra("cid", ((FragmentChat) getSupportFragmentManager().findFragmentByTag("chat")).getCid()).putExtra("uid", ((FragmentChat) getSupportFragmentManager().findFragmentByTag("chat")).getUid()));
                    return false;
                }
            });
        } else
            return false;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return;//OTAManager.get(MainActivity.this).checkOTAUpdates();
            else
                Snackbar.make(drawerLayout, R.string.cant_work_without_permissions, 10000).setAction(R.string.grant_permissions, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_PERMISSIONS);
                    }
                }).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showExitDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.warning));
        adb.setMessage(getString(R.string.exit_message));
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                account.clear();
                finishAffinity();
            }
        });
        adb.setNegativeButton(R.string.no, null);
        AlertDialog alert = adb.create();
        alert.show();
    }

    private void initDrawerHeader(View v) {
        Picasso.with(this).load(account.getAvatar()).into(((ImageView) v.findViewById(R.id.ivAva)));
        ((TextView) v.findViewById(R.id.tvName)).setText(account.getName());
        ((TextView) v.findViewById(R.id.tvStatus)).setText(account.getStatus());
        v.findViewById(R.id.ivMusic).setVisibility(account.isMusicPlaying() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, LongPollService.class));
        super.onDestroy();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void initItems() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("chat") != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, navigationView.getMenu().getItem(0).isChecked() ? new FragmentMessages() : new FragmentFriends()).commit();
            invalidateOptionsMenu();
            toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawers();
            else
                super.onBackPressed();
        }
    }
}

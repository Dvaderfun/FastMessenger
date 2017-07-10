package ru.lischenko_dev.fastmessenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKStatus;


public class StartActivity extends AppCompatActivity {


    private Account account;

    private ImageView ivAva;
    private Button btnAuth;


    @Override
    protected void onCreate(Bundle b) {
        setTheme(ThemeManager.get(this).getCurrentTheme());
        getWindow().setBackgroundDrawable(new ColorDrawable(ThemeManager.get(this).getPrimaryColor()));
        super.onCreate(b);

        if (Utils.getPrefs(this).getBoolean("show_login", true)) {
            if (Utils.hasConnection(this)) {
                account = Account.get(this);
				account.restore();
                setContentView(R.layout.activity_start);
                (findViewById(R.id.downLLStart)).setBackgroundColor(ThemeManager.get(this).getSecondaryBackgroundColor());
                btnAuth = (Button) findViewById(R.id.btnLogin);
                ivAva = (ImageView) findViewById(R.id.ivAva);
                btnAuth.setText(account.user_id == 0 ? getString(R.string.authorize).toUpperCase() : getString(R.string.login).toUpperCase());
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(ThemeManager.get(this).getButtonColor());
                gd.setCornerRadius(75);
                btnAuth.setBackgroundDrawable(gd);
                if (account.avatar != null)
                    try {
                        Picasso.with(this).load(account.avatar).placeholder(R.drawable.camera_200).into(ivAva);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                ivAva.setVisibility(account.user_id == 0 ? View.GONE : View.VISIBLE);

                btnAuth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (btnAuth.getText().toString().equals(getString(R.string.authorize).toUpperCase())) {
                            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), MainActivity.REQUEST_LOGIN);
                        } else {
                            getMoreAccountInfo();
                        }
                    }
                });
            } else {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.warning);
                adb.setCancelable(false);
                adb.setMessage(R.string.not_connected_to_internet);
                adb.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        ActivityCompat.finishAffinity(StartActivity.this);
                    }
                });
                AlertDialog alert = adb.create();
                alert.show();
            }
        } else {
            if (Utils.hasConnection(this)) {
                account = Account.get(this);
                if (account.user_id == 0)
                    startActivityForResult(new Intent(this, LoginActivity.class), MainActivity.REQUEST_LOGIN);
                else
                    getMoreAccountInfo();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                account.access_token = data.getStringExtra("access_token");
                account.user_id = data.getLongExtra("user_id", 0);
                account.save();
                getMoreAccountInfo();
            } else
                ActivityCompat.finishAffinity(this);
        }
    }

    private void getMoreAccountInfo() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.wait);
        adb.setMessage(R.string.login_wait_message);
        adb.setCancelable(false);
        final AlertDialog alert = adb.create();
        alert.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Api api = Api.init(account);
                    ArrayList uid = new ArrayList();
                    uid.add(account.user_id);
                    VKStatus apiStatus = api.getStatus(account.user_id);
                    ArrayList<VKFullUser> apiProfile = api.getProfiles(uid, null, "photo_200, photo_50, status", null, null, null);
                    for (VKFullUser user : apiProfile) {
                        account.avatar = user.photo_200;
                        account.name = user.toString();
                        account.isMusicPlaying = apiStatus.audio != null;
                        account.small_avatar = user.photo_50;
                        account.status = apiStatus.text.length() > 0 ? apiStatus.text : "@id" + String.valueOf(account.user_id);
                    }

                    account.save();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert.dismiss();
                            finish();
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

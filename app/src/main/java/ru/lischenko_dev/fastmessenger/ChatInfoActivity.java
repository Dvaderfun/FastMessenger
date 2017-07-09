package ru.lischenko_dev.fastmessenger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.adapter.ChatInfoAdapter;
import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKChat;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;

public class ChatInfoActivity extends AppCompatActivity implements EditText.OnEditorActionListener {

    private long cid;
    private RecyclerView recyclerView;
    private Api api;
    private String title;
    private Account account;
    private EditText et;
    private ImageView ivAva;
    private VKChat chat;
    private ThemeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = ThemeManager.get(this);
        setTheme(manager.getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        account = Account.get(this);
        api = Api.init(account);
        cid = getIntent().getExtras().getLong("cid");
        title = getIntent().getExtras().getString("title");
        recyclerView = (RecyclerView) findViewById(R.id.lv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        et = (EditText) findViewById(R.id.et);
        ivAva = (ImageView) findViewById(R.id.ivAva);

        et.setText(title);
        et.setOnEditorActionListener(this);

        manager.setHrBackgroundColor(findViewById(R.id.hr));
        et.setHintTextColor(manager.getSecondaryTextColor());
        et.setTextColor(manager.getEditTextColor());
        getSupportActionBar().setTitle(title);
        new ChatInfoGetter().execute();
        getChatById();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void getChatById() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chat = api.getChat(cid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            et.setText(chat.title);
                            et.setSelection(et.getText().length());
                            getSupportActionBar().setTitle(chat.title);
                            try {
                                Picasso.with(getApplicationContext()).load(chat.photo_100).placeholder(R.drawable.community_200).into(ivAva);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showChangeTitleDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!et.getText().toString().equals(chat.title))
                        api.editChat(chat.id, et.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getChatById();
                            if (!et.getText().toString().equals(chat.title))
                                Toast.makeText(ChatInfoActivity.this, getString(R.string.title_changed), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_GO) {
            showChangeTitleDialog();
            return true;
        }
        return false;
    }

    private class ChatInfoGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final ArrayList<VKFullUser> apiChatUsers = api.getChatUsers(cid, "online, last_seen, photo_100, photo_200, invited_by");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChatInfoAdapter adapter = new ChatInfoAdapter(apiChatUsers, getApplicationContext());
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

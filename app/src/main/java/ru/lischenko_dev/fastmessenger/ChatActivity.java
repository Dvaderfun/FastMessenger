package ru.lischenko_dev.fastmessenger;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import ru.lischenko_dev.fastmessenger.adapter.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

import android.support.v7.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private Account account = new Account();
    private Api api;

    private ArrayList<ChatItems> items;
    private ChatAdapter adapter;

    private ListView lv;
    private AppCompatImageButton btnSend, btnSmile, btnAttach;
    private AppCompatEditText et;
    private Toolbar toolbar;
    private ProgressBar progress;

    private long uid, cid;
    private String title;

    private boolean isLeaved;
    private boolean isLoaded;

    private VKChat chat;

	private ThemeManager manager;


    @Override
    protected void onCreate(Bundle bb) {
		manager = ThemeManager.get(this);
		setTheme(manager.getCurrentTheme());
		getWindow().setBackgroundDrawable(new ColorDrawable(manager.getSecondaryBackgroundColor()));
        super.onCreate(bb);
        setContentView(R.layout.activity_chat);
        checkLeaved();
		(findViewById(R.id.chat_panel)).getBackground().setColorFilter(manager.getPanelColor(), PorterDuff.Mode.MULTIPLY);
        account.restore(this);
        api = new Api(account.access_token, Constants.API_ID);
        initExtraItems();
        initItems();
		et.setHintTextColor(manager.getEditTextColor());
		et.setTextColor(manager.getEditTextColor());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv.setStackFromBottom(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			toolbar.setElevation(8);

        View listViewFooter = new View(this);
        listViewFooter.setBackgroundColor(Color.TRANSPARENT);
        listViewFooter.setVisibility(View.INVISIBLE);
        listViewFooter.setEnabled(false);
        listViewFooter.setClickable(false);
        listViewFooter.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Utils.convertDpToPixel(getApplicationContext(), 61)));

        lv.addFooterView(listViewFooter);
        btnSend.setColorFilter(Color.GRAY);

        btnSmile.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					String template = Utils.getPrefs(getApplicationContext()).getString("template", "");
					if (template.length() > 0) {
						et.setText(et.getText().toString() + template);
						et.setSelection(et.getText().length());
					}
					return false;
				}
			});

        et.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					if (et.getText().length() == 0)
						btnSend.setColorFilter(Color.GRAY);
					else
						btnSend.setColorFilter(Color.parseColor("#1565c0"));
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					if (et.getText().length() == 0)
						btnSend.setColorFilter(Color.GRAY);
					else
						btnSend.setColorFilter(Color.parseColor("#1565c0"));
				}

				@Override
				public void afterTextChanged(Editable editable) {
					if (et.getText().length() == 0)
						btnSend.setColorFilter(Color.GRAY);
					else
						btnSend.setColorFilter(Color.parseColor("#1565c0"));
				}
			});

        getSupportActionBar().setTitle(title);
        isLoaded = false;
        new ChatGetter(true).execute();
    }

    @Override
    protected void onDestroy() {
		if (adapter != null)
			adapter.destroy();
        super.onDestroy();
    }

    private void openChatInfo() {
        startActivity(new Intent(this, ChatInfoActivity.class).putExtra("cid", cid).putExtra("title", chat.title));
    }

    private void checkLeaved() {
        try {
            isLeaved = api.isChatUser(account.user_id, cid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuMaterials:
                startActivity(new Intent(getApplicationContext(), MaterialsActivity.class).putExtra("uid", uid).putExtra("cid", cid));
                break;
            case R.id.menuUpdate:
                new ChatGetter(true).execute();
                break;
            case R.id.menuLeaveReturn:
                showLeaveDialog(item.getTitle().toString());
                break;
            case R.id.menuFind:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.getItem(3);
        MenuItem update = menu.getItem(2);
        update.setVisible(isLoaded);
        item.setVisible(cid > 0);
        if (isLoaded)
            btnSend.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!TextUtils.isEmpty(et.getText().toString())) {
							sendMessage();
						}
						else
							Toast.makeText(ChatActivity.this, R.string.type_message_error, Toast.LENGTH_SHORT).show();
					}
				});
        item.setTitle(isLeaved ? getString(R.string.return_to_chat) : getString(R.string.leave_from_chat));
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_history, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void showLeaveDialog(final String action) {
        Snackbar.make((findViewById(R.id.llMessageHistory)), action.equals(getString(R.string.leave_from_chat)) ? getString(R.string.leave_chat_confirm) : getString(R.string.return_chat_confirm), Snackbar.LENGTH_LONG).setAction("Да", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									if (action.equals(getString(R.string.leave_from_chat))) {
										isLeaved = true;
										api.removeUserFromChat(cid, account.user_id);
									}
									else {
										isLeaved = false;
										api.addUserToChat(cid, account.user_id);
									}
									invalidateOptionsMenu();
									runOnUiThread(new Runnable() {
											@Override
											public void run() {
												new ChatGetter(false).execute();
												Toast.makeText(ChatActivity.this, isLeaved ? getString(R.string.you_leaved_chat) : getString(R.string.returned_to_chat), Toast.LENGTH_SHORT).show();
											}
										});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
				}
			}).setActionTextColor(Color.parseColor("#1565c0")).show();
    }

    private void sendMessage() {
        new SendMessage().execute();
        et.setText(null);
    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {

		private VKMessage message = new VKMessage();
		String msg_text = et.getText().toString();
		
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            message.body = msg_text;
            message.uid = account.user_id;
            message.is_out = true;
            message.date = System.currentTimeMillis() / 1000;
            final VKFullUser user = api.getProfile(account.user_id);
            final ChatItems[] messageItem = new ChatItems[1];
            messageItem[0] = new ChatItems(message, user);
            items.add(messageItem[0]);
            adapter.notifyDataSetChanged();
            lv.setSelection(adapter.getCount());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                message.mid = api.sendMessage(uid, cid, msg_text, null, null, null, null, null, null, null, null);
                final VKFullUser user = api.getProfile(account.user_id);
                final ChatItems[] messageItem = new ChatItems[1];
                messageItem[1] = new ChatItems(message, user);
                items.add(messageItem[1]);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            lv.setSelection(adapter.getCount());

        }
    }

    private class ChatGetter extends AsyncTask<Void, Void, Void> {
		public boolean withProgress;

		public ChatGetter(boolean withProgress) {
			this.withProgress = withProgress;
		}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoaded = false;
            if (withProgress) {
                lv.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HashMap<Long, VKFullUser> mapUsers = new HashMap<>();
                items = new ArrayList<>();

                ArrayList<VKMessage> dialogs = api.getMessagesHistory(uid, cid, account.user_id, 0, 200);
                for (VKMessage msg : dialogs) {
                    mapUsers.put(msg.uid, null);
                }

                ArrayList<VKFullUser> profiles = api.getProfiles(mapUsers.keySet(), null, VKFullUser.DEFAULT_FIELDS, null, null, null);
                for (VKFullUser user : profiles) {
                    mapUsers.put(user.uid, user);
                }

                for (VKMessage msg : dialogs) {
                    VKFullUser user = mapUsers.get(msg.uid);
                    items.add(0, new ChatItems(msg, user));
                }

                if (cid > 0)
                    chat = api.getChat(cid);
                runOnUiThread(new Runnable() {

						@Override
						public void run() {
							isLoaded = true;
							invalidateOptionsMenu();
							adapter = new ChatAdapter(getApplicationContext(), items, api, cid, uid, lv);
							lv.setAdapter(adapter);
							lv.setSelection(adapter.getCount());
							if (cid > 0)
								toolbar.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											openChatInfo();
										}
									});
						}

					});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (withProgress) {
                lv.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }
    }

    private void initExtraItems() {
        uid = getIntent().getExtras().getLong("uid");
        cid = getIntent().getExtras().getLong("cid");
        title = getIntent().getExtras().getString("title");
    }


    private void initItems() {
        progress = (ProgressBar) findViewById(R.id.progress);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lv = (ListView) findViewById(R.id.lv);
        et = (AppCompatEditText) findViewById(R.id.et);
        btnSend = (AppCompatImageButton) findViewById(R.id.btnSend);
        btnSmile = (AppCompatImageButton) findViewById(R.id.btnSmile);
        btnAttach = (AppCompatImageButton) findViewById(R.id.btnAttach);
    }
}

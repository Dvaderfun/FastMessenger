package ru.lischenko_dev.fastmessenger;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import ru.lischenko_dev.fastmessenger.adapter.*;
import ru.lischenko_dev.fastmessenger.common.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

public class FragmentChat extends Fragment {

    public long uid, cid;
    private Account account;
    private Api api;
    private ArrayList<MessageHistoryItems> items;
    private MessageHistoryAdapter adapter;
    private ListView lv;
    private AppCompatImageButton btnSend, btnSmile;
    private AppCompatEditText et;
    private ProgressBar progress;
    private String title;

    private ThemeManager manager;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getActivity().invalidateOptionsMenu();
        initExtraItems();

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        manager = ThemeManager.get(getActivity());

        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(manager.getSecondaryBackgroundColor()));
        (rootView.findViewById(R.id.chat_panel)).getBackground().setColorFilter(manager.getPanelColor(), PorterDuff.Mode.MULTIPLY);
        account = Account.get(getActivity());
        api = Api.init(account);


        initItems(rootView);
		
        et.setHintTextColor(manager.getEditTextColor());
        et.setTextColor(manager.getEditTextColor());
		
        btnSend.setColorFilter(Color.GRAY);
		
		View footer = new View(getActivity());
		footer.setVisibility(View.VISIBLE);
		footer.setBackgroundColor(Color.TRANSPARENT);
		footer.setVisibility(View.INVISIBLE);
		footer.setEnabled(false);
		footer.setClickable(false);
		footer.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Utils.convertDpToPixel(getActivity(), 75)));
		
		lv.addFooterView(footer);
        
		btnSmile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String template = Utils.getPrefs(getActivity()).getString("template", "");
                if (template.length() > 0) {
                    et.setText(et.getText().toString() + template);
                    et.setSelection(et.getText().length());
                }
                return false;
            }
        });
		
		btnSmile.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1) {
					et.setText(et.getText().toString() + Constants.DARK_MOON);
					et.setSelection(et.getText().length());
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

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);

        new ChatGetter(true).execute();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        return rootView;
    }

    public Long getCid() {
        return cid;
    }

    public Long getUid() {
        return uid;
    }

    public void update() {
        new ChatGetter(true).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void sendMessage() {
        new SendMessage().execute();
        et.setText(null);
    }

    private void initExtraItems() {
        Bundle b = getArguments();
        uid = b.getLong("uid");
        cid = b.getLong("cid");
        title = b.getString("title");
    }

    private void initItems(View v) {
        progress = (ProgressBar) v.findViewById(R.id.progress);
        lv = (ListView) v.findViewById(R.id.lv);
        et = (AppCompatEditText) v.findViewById(R.id.et);
        btnSend = (AppCompatImageButton) v.findViewById(R.id.btnSend);
        btnSmile = (AppCompatImageButton) v.findViewById(R.id.btnSmile);
    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {

        String msg_text = et.getText().toString();
        private VKMessage message = new VKMessage();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            message.body = msg_text;
            message.uid = account.user_id;
            message.is_out = true;
            message.date = System.currentTimeMillis() / 1000;
            final VKFullUser user = api.getProfile(account.user_id);
            final MessageHistoryItems[] messageItem = new MessageHistoryItems[1];
            messageItem[0] = new MessageHistoryItems(message, user);
            items.add(messageItem[0]);
            adapter.notifyDataSetChanged();
            lv.smoothScrollToPosition(adapter.getCount());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                message.mid = api.sendMessage(uid, cid, msg_text, null, null, null, null, null, null, null, null);
                final VKFullUser user = api.getProfile(account.user_id);
                final MessageHistoryItems[] messageItem = new MessageHistoryItems[1];
                messageItem[1] = new MessageHistoryItems(message, user);
                items.add(messageItem[1]);
                adapter.notifyDataSetChanged();
                lv.smoothScrollToPosition(adapter.getCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            lv.smoothScrollToPosition(adapter.getCount());

        }
    }

    private class ChatGetter extends AsyncTask<Void, Void, Void> {
        boolean withProgress;

        ChatGetter(boolean withProgress) {
            this.withProgress = withProgress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    items.add(0, new MessageHistoryItems(msg, user));
                }
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter = new MessageHistoryAdapter(items, getActivity().getApplicationContext());
                        lv.setAdapter(adapter);
                        lv.setSelection(adapter.getCount());
                        if (cid > 0)
                            ((MainActivity) getActivity()).getToolbar().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(), ChatInfoActivity.class).putExtra("cid", cid).putExtra("title", title));
                                }
                            });
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Some error in chat get", e.toString());
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
}

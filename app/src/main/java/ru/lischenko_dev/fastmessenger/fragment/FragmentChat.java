package ru.lischenko_dev.fastmessenger.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

import ru.lischenko_dev.fastmessenger.MainActivity;
import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.ChatAdapter;
import ru.lischenko_dev.fastmessenger.adapter.ChatItems;
import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.util.ThemeManager;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class FragmentChat extends Fragment {

    private Account account = new Account();
    private Api api;

    private ArrayList<ChatItems> items;
    private ChatAdapter adapter;

    private ListView lv;
    private AppCompatImageButton btnSend, btnSmile;
    private AppCompatEditText et;
    private ProgressBar progress;

    private long uid, cid;
    private String title;


    private ThemeManager manager;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        manager = ThemeManager.get(getActivity());
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(manager.getSecondaryBackgroundColor()));
        (rootView.findViewById(R.id.chat_panel)).getBackground().setColorFilter(manager.getPanelColor(), PorterDuff.Mode.MULTIPLY);
        account.restore(getActivity());
        api = Api.init(account);

        initExtraItems();
        initItems(rootView);
        et.setHintTextColor(manager.getEditTextColor());
        et.setTextColor(manager.getEditTextColor());
        lv.setStackFromBottom(true);

        View listViewFooter = new View(getActivity());
        listViewFooter.setBackgroundColor(Color.TRANSPARENT);
        listViewFooter.setVisibility(View.INVISIBLE);
        listViewFooter.setEnabled(false);
        listViewFooter.setClickable(false);
        listViewFooter.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Utils.convertDpToPixel(getActivity(), 61)));

        lv.addFooterView(listViewFooter);
        btnSend.setColorFilter(Color.GRAY);

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

    @Override
    public void onDetach() {
        if (adapter != null)
            adapter.destroy();
        super.onDetach();
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

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter = new ChatAdapter(getActivity().getApplicationContext(), items, api, cid, uid, lv, getActivity());
                            lv.setAdapter(adapter);
                            lv.setSelection(adapter.getCount());
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

    private void initExtraItems() {
        Bundle b = getArguments();
        uid = b.getLong("uid");
        cid = b.getLong("cid");
        title = b.getString("title");
    }


    private void initItems(View v) {
        progress = v.findViewById(R.id.progress);
        lv = v.findViewById(R.id.lv);
        et = v.findViewById(R.id.et);
        btnSend = v.findViewById(R.id.btnSend);
        btnSmile = v.findViewById(R.id.btnSmile);
    }
}

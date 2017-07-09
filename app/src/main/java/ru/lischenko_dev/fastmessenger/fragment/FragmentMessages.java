package ru.lischenko_dev.fastmessenger.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ru.lischenko_dev.fastmessenger.FragmentChat;
import ru.lischenko_dev.fastmessenger.MainActivity;
import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.MessagesAdapter;
import ru.lischenko_dev.fastmessenger.adapter.MessagesItem;
import ru.lischenko_dev.fastmessenger.util.VKAccount;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;


public class FragmentMessages extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private ListView lv;
    private VKAccount account;
    private Api api;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progress;
    private MessagesAdapter adapter;
    private ArrayList<MessagesItem> items;

    public FragmentMessages() {
    }

    @Override
    public void onRefresh() {
        new DialogsGetter(true).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        account = VKAccount.get(getActivity().getApplicationContext());
        api = Api.init(account);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_messages));
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        lv = view.findViewById(R.id.lv);
        progress = view.findViewById(R.id.progress);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessagesItem item = (MessagesItem) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putLong("uid", item.user.uid);
                b.putLong("cid", item.message.chat_id);
                b.putString("title", item.message.chat_id > 0 ? item.message.title : item.user.toString());
                FragmentChat chat = new FragmentChat();
                chat.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.container, chat, "chat").commit();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessagesItem item = (MessagesItem) adapterView.getItemAtPosition(i);
                showDialog(item);
                return true;
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        new DialogsGetter(true).execute();
        return view;
    }

    private void showDialog(final MessagesItem item) {
        String[] items = new String[]{
                getString(R.string.clean_history)
        };
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setMessage(R.string.confirm_delete_dialog);
                        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteMessages(item);
                            }
                        });
                        adb.setNegativeButton(R.string.no, null);
                        AlertDialog a = adb.create();
                        a.show();
                        break;
                }
            }
        });
        AlertDialog a = adb.create();
        a.show();
    }


    private void deleteMessages(final MessagesItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    api.deleteMessageDialog(item.message.uid, item.message.chat_id);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            items.remove(item);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), R.string.message_history_deleted, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("error delete msg", e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDetach() {
        if (adapter != null)
            adapter.destroy();
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class DialogsGetter extends AsyncTask<Void, Void, Void> {

        public boolean withProgress;

        public DialogsGetter(boolean withProgress) {
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

                ArrayList<VKMessage> dialogs = api.getMessagesDialogs(0, 30, null, null);

                for (VKMessage msg : dialogs) {
                    mapUsers.put(msg.uid, null);
                }

                ArrayList<VKFullUser> apiProfiles = api.getProfiles(mapUsers.keySet(), null, "online, photo_50, photo_200", null, null, null);
                mapUsers.clear();
                for (VKFullUser user : apiProfiles) {
                    mapUsers.put(user.uid, user);
                }

                for (VKMessage msg : dialogs) {

                    items.add(new MessagesItem(msg, mapUsers.get(msg.uid)));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MessagesAdapter(getActivity().getApplicationContext(), items);
                        lv.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
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
}



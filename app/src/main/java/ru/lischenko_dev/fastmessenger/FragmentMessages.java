package ru.lischenko_dev.fastmessenger;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import ru.lischenko_dev.fastmessenger.adapter.*;
import ru.lischenko_dev.fastmessenger.common.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;


public class FragmentMessages extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessageAdapter.OnItemClickListener {


    private RecyclerView recyclerView;
    private Account account;
    private Api api;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progress;
    private MessageAdapter adapter;
    private ArrayList<MessageItem> items;

    public FragmentMessages() {
    }

    @Override
    public void onItemClick(View view, int position) {
        MessageItem item = items.get(position);
        Bundle b = new Bundle();
        b.putLong("uid", item.user.uid);
        b.putLong("cid", item.message.chat_id);
        b.putString("title", item.message.chat_id > 0 ? item.message.title : item.user.toString());
        FragmentChat chat = new FragmentChat();
        chat.setArguments(b);
        getFragmentManager().beginTransaction().replace(R.id.container, chat, "chat").commit();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        showDialog(items.get(position));
    }

    @Override
    public void onRefresh() {
        new DialogsGetter(true).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().invalidateOptionsMenu();
        account = Account.get(getActivity());
        api = Api.init(account);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_messages));
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.lv);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.lv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

		
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        new DialogsGetter(true).execute();
        return view;
    }

    private void showDialog(final MessageItem item) {
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


    private void deleteMessages(final MessageItem item) {
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
                recyclerView.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HashMap<Long, VKFullUser> mapUsers = new HashMap<>();
                items = new ArrayList<>();
                ArrayList<VKMessage> dialogs = api.getMessagesDialogs(0, 30, null, null);
                for (VKMessage msg : dialogs)
                    mapUsers.put(msg.uid, null);
                ArrayList<VKFullUser> apiProfiles = api.getProfiles(mapUsers.keySet(), null, "online, photo_50, photo_200", null, null, null);
                mapUsers.clear();
                for (VKFullUser user : apiProfiles)
                    mapUsers.put(user.uid, user);
                for (VKMessage msg : dialogs)
                    items.add(new MessageItem(msg, mapUsers.get(msg.uid)));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageAdapter adapter = new MessageAdapter(items, getActivity());
                        adapter.setListener(FragmentMessages.this);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
						recyclerView.scrollToPosition(adapter.getItemCount() - 2);
						recyclerView.scrollToPosition(adapter.getItemCount());
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
                recyclerView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }
    }
	
	
}



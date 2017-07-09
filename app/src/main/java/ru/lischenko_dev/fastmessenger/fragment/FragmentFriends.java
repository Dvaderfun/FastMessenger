package ru.lischenko_dev.fastmessenger.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.FragmentChat;
import ru.lischenko_dev.fastmessenger.MainActivity;
import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.FriendsAdapter;
import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.util.Constants;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKUser;

public class FragmentFriends extends Fragment {

    private ListView lv;
    private Account account = new Account();
    private Api api;
    private ArrayList<VKFullUser> apiProfiles;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        account.restore(getActivity());
        api = new Api(account.access_token, Constants.API_ID);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_friends));

        lv = view.findViewById(R.id.lv);
        progress = view.findViewById(R.id.progress);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VKFullUser user = (VKFullUser) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putLong("uid", user.uid);
                b.putString("title", user.toString());
                FragmentChat chat = new FragmentChat();
                chat.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.container, chat, "chat").commit();
            }
        });


        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.hasConnection(getActivity()))
                    new FriendsGetter().execute();
                else
                    Toast.makeText(getActivity(), getString(R.string.not_connected_to_internet), Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        new FriendsGetter().execute();
        return view;
    }

    private class FriendsGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            lv.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ArrayList<VKUser> apiFriends = api.getFriends(account.user_id, "hints", 0, null, null);
                ArrayList<Long> uids = new ArrayList<>();
                for (VKUser user : apiFriends) {
                    uids.add(Long.parseLong(String.valueOf(user.user_id)));
                }

                apiProfiles = api.getProfiles(uids, null, "photo_100, online, last_seen", null, null, null);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FriendsAdapter adapter = new FriendsAdapter(getActivity().getApplicationContext(), apiProfiles);
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
            lv.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}

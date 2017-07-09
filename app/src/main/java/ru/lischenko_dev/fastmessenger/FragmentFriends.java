package ru.lischenko_dev.fastmessenger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.adapter.FriendsAdapter;
import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.util.Constants;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKUser;

public class FragmentFriends extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FriendsAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private Account account;
    private Api api;
    private ArrayList<VKFullUser> apiProfiles;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        account = Account.get(getActivity());
        api = new Api(account.access_token, Constants.API_ID);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_friends));

        recyclerView = view.findViewById(R.id.lv);
        progress = view.findViewById(R.id.progress);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = view.findViewById(R.id.lv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        new FriendsGetter().execute();
        return view;
    }

    @Override
    public void onRefresh() {
        if (Utils.hasConnection(getActivity()))
            new FriendsGetter().execute();
    }

    @Override
    public void onItemClick(View view, int position) {
        VKFullUser user = apiProfiles.get(position);
        Bundle b = new Bundle();
        b.putLong("uid", user.uid);
        b.putLong("cid", 0);
        b.putString("title", user.toString());
        FragmentChat chat = new FragmentChat();
        chat.setArguments(b);
        getFragmentManager().beginTransaction().replace(R.id.container, chat, "chat").commit();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private class FriendsGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            recyclerView.setVisibility(View.INVISIBLE);
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
                        FriendsAdapter adapter = new FriendsAdapter(apiProfiles, getActivity());
                        adapter.setListener(FragmentFriends.this);
                        recyclerView.setAdapter(adapter);
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
            recyclerView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}

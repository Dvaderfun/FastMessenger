package ru.lischenko_dev.fastmessenger.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.MainActivity;
import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.GroupsAdapter;
import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKGroup;

public class FragmentGroups extends Fragment {


    private Account account = new Account();
    private Api api;
    private ListView lv;
    private ProgressBar progressBar;

    public FragmentGroups() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        account.restore(getActivity());
        api = Api.init(account);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.groups));
        lv = (ListView) view.findViewById(R.id.lv);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);


        new GroupsGetter().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VKGroup group = (VKGroup) adapterView.getItemAtPosition(i);
                showDialog(group);
            }
        });
        return view;
    }

    private void showDialog(final VKGroup group) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VKGroup gr = api.getGroup(group.gid);
                    final String info = "Участников: " + String.valueOf(gr.members_count) + "\nScreen Name: " + gr.screen_name + "\nСтатус: " + (gr.status.length() > 0 ? gr.status : "отсутствует");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                            adb.setTitle(group.name);
                            adb.setMessage(info);
                            adb.setPositiveButton(R.string.ok, null);
                            AlertDialog a = adb.create();
                            a.show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class GroupsGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            lv.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final ArrayList<VKGroup> apiGroups = api.getGroups(account.user_id);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GroupsAdapter adapter = new GroupsAdapter(getActivity(), apiGroups);
                                lv.setAdapter(adapter);
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
            lv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }


}

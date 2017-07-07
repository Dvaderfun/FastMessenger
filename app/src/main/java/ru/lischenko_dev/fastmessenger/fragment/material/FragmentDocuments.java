package ru.lischenko_dev.fastmessenger.fragment.material;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.DocsMaterialAdapter;
import ru.lischenko_dev.fastmessenger.adapter.DocsMaterialItems;
import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessageAttachment;

public class FragmentDocuments extends Fragment {


    private ListView lv;
    private long uid, cid;
    private ArrayList<DocsMaterialItems> items;
    private DocsMaterialAdapter adapter;
    private Api api;
    private Account account = new Account();
    private long id;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        uid = getActivity().getIntent().getExtras().getLong("uid");
        cid = getActivity().getIntent().getExtras().getLong("cid");
        View rootView = inflater.inflate(R.layout.materials_docs, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        account.restore(getActivity());
        api = Api.init(account);
        items = new ArrayList<>();
        adapter = new DocsMaterialAdapter(getActivity(), items);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DocsMaterialItems item = (DocsMaterialItems) adapterView.getItemAtPosition(i);
                showDialog(item);
            }
        });
        new getAudios().execute();
        return rootView;
    }

    private void showDialog(final DocsMaterialItems item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage("Вы хотите скачать документ '" + item.att.doc.title + "'?");
        adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.att.doc.url)));
            }
        });
        adb.setNegativeButton("Нет", null);
        AlertDialog ad = adb.create();
        ad.show();
    }

    private class getAudios extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        items = new ArrayList<>();
                        if (cid == 0)
                            id = uid;
                        else id = 2000000000 + cid;

                        ArrayList<VKMessageAttachment> apiMessagesHistory = api.getHistoryAttachments(id, "doc", 0, 200, null);
                        for (VKMessageAttachment message : apiMessagesHistory) {
                            items.add(new DocsMaterialItems(message));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new DocsMaterialAdapter(getActivity(), items);
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

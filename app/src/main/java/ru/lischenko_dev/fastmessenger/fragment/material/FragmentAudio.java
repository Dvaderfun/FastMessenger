package ru.lischenko_dev.fastmessenger.fragment.material;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.adapter.AudioMaterialAdapter;
import ru.lischenko_dev.fastmessenger.adapter.AudioMaterialItems;
import ru.lischenko_dev.fastmessenger.util.Account;
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessageAttachment;

public class FragmentAudio extends Fragment {

    private ListView lv;
    private long uid, cid;
    private AudioMaterialAdapter adapter;
    private Api api;
    private Account account = new Account();
    private long id;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uid = getActivity().getIntent().getExtras().getLong("uid");
        cid = getActivity().getIntent().getExtras().getLong("cid");
        View rootView = inflater.inflate(R.layout.materials_audio, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        account.restore(getActivity());
        api = Api.init(account);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AudioMaterialItems item = (AudioMaterialItems) adapterView.getItemAtPosition(i);
                Toast.makeText(getActivity(), "Простите, но аудиозапись '" + item.attachment.audio.title + "' недоступна для прослушивания.", Toast.LENGTH_SHORT).show();
            }
        });
        new getAudios().execute();
        return rootView;
    }

    private class getAudios extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            lv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final ArrayList<AudioMaterialItems> items = new ArrayList<>();
                        if (cid == 0)
                            id = uid;
                        else id = 2000000000 + cid;

                        ArrayList<VKMessageAttachment> apiMessagesHistory = api.getHistoryAttachments(id, "audio", 0, 200, null);
                        for (VKMessageAttachment message : apiMessagesHistory) {
                            items.add(new AudioMaterialItems(message));
                            Log.e("Links", message.audio.url);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new AudioMaterialAdapter(getActivity(), items);
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

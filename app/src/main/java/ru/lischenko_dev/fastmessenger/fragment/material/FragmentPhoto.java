package ru.lischenko_dev.fastmessenger.fragment.material;


import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.support.v4.app.Fragment;
import ru.lischenko_dev.fastmessenger.*;
import ru.lischenko_dev.fastmessenger.adapter.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;


public class FragmentPhoto extends Fragment {


    private GridView gv;
    private long cid, uid;
    private ArrayList<PhotoMaterialsItems> items;
    private PhotoMaterialsAdapter adapter;
    private Api api;
    private Account account = new Account();
    private long id;
    private ProgressBar progressBar;

    public FragmentPhoto() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uid = getActivity().getIntent().getExtras().getLong("uid");
        cid = getActivity().getIntent().getExtras().getLong("cid");
        View rootView = inflater.inflate(R.layout.materials_photo, container, false);
        gv = (GridView) rootView.findViewById(R.id.gv);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        gv.setNumColumns(3);
        gv.setVerticalSpacing(5);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        gv.setColumnWidth(metrics.widthPixels / 3);
        account.restore(getActivity());
        api = Api.init(account);
        items = new ArrayList<>();
        adapter = new PhotoMaterialsAdapter(getActivity(), items);

        new PhotoGetter().execute();

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotoMaterialsItems item = (PhotoMaterialsItems) adapterView.getItemAtPosition(i);
                showDialog(item);
            }
        });
        return rootView;
    }

    private void showDialog(final PhotoMaterialsItems item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage("Вы действительно хотите посмотреть эту фотографию?");
        adb.setPositiveButton("В браузере", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(item.attachment.photo.src_big));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("В программе", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getActivity(), PhotoViewActivity.class).putExtra("image", item.attachment.photo.src_big));
            }
        });
        adb.setNeutralButton("Нет", null);
        AlertDialog ad = adb.create();
        ad.show();
    }


    private class PhotoGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            gv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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

                        ArrayList<VKMessageAttachment> apiMessagesHistory = api.getHistoryAttachments(id, "photo", 0, 200, null);
                        for (VKMessageAttachment message : apiMessagesHistory) {
                            items.add(new PhotoMaterialsItems(message));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new PhotoMaterialsAdapter(getActivity(), items);
                                gv.setAdapter(adapter);
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
            gv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}

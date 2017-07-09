package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;

public class MaterialsAudioAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MaterialsAdapter> items;
    private LayoutInflater inflater;
    private ThemeManager manager;

    public MaterialsAudioAdapter(Context context, ArrayList<MaterialsAdapter> items) {
        this.context = context;
        this.items = items;
        this.manager = ThemeManager.get(context);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MaterialsAdapter item = (MaterialsAdapter) getItem(i);
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.materials_audio_list, viewGroup, false);
        }


        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvArtist = v.findViewById(R.id.tvArtist);

        tvTitle.setTextColor(manager.getPrimaryTextColor());
        tvArtist.setTextColor(manager.getSecondaryTextColor());

        tvTitle.setText(item.attachment.audio.title);
        tvArtist.setText(item.attachment.audio.artist);
        return v;
    }
}

package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;

public class AudioMaterialAdapter extends BaseAdapter {

    Context context;
    ArrayList<AudioMaterialItems> items;
    LayoutInflater inflater;

    public AudioMaterialAdapter(Context context, ArrayList<AudioMaterialItems> items) {
        this.context = context;
        this.items = items;
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
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.materials_audio_list, viewGroup, false);
        }
        AudioMaterialItems item = (AudioMaterialItems) getItem(i);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView tvArtist = (TextView) v.findViewById(R.id.tvArtist);

        tvTitle.setText(item.attachment.audio.title);
        tvArtist.setText(item.attachment.audio.artist);
        return v;
    }
}

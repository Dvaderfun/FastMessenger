package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKVideo;

public class VideoMaterialAdapter extends BaseAdapter {

    ArrayList<VideoMaterialItems> items;
    Context context;
    LayoutInflater inflater;

    public VideoMaterialAdapter(Context context, ArrayList<VideoMaterialItems> items) {
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            v = inflater.inflate(R.layout.materials_video_list, viewGroup, false);
        }

        VideoMaterialItems item = (VideoMaterialItems) getItem(i);
        VKVideo k = item.att.video;
        ImageView iv = (ImageView) v.findViewById(R.id.photo);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView see = (TextView) v.findViewById(R.id.tvSee);
        TextView tvDate = (TextView) v.findViewById(R.id.tvDate);

        tvTitle.setText(k.title);
        see.setText(String.valueOf(k.views));

        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(k.date * 1000));
        Picasso.with(context)
                .load(k.image_big)
				.resize(200, 200)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(iv);
        return v;
    }
}

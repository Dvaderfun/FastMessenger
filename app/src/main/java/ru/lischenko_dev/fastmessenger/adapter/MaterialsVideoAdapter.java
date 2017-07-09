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
import ru.lischenko_dev.fastmessenger.common.App;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKVideo;

public class MaterialsVideoAdapter extends BaseAdapter {

    private ArrayList<MaterialsAdapter> items;
    private Context context;
    private LayoutInflater inflater;
    private ThemeManager manager;

    public MaterialsVideoAdapter(Context context, ArrayList<MaterialsAdapter> items) {
        this.items = items;
        this.context = context;
        this.manager = ThemeManager.get(context);
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
        MaterialsAdapter item = (MaterialsAdapter) getItem(i);
        VKVideo video = item.attachment.video;
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.materials_video_list, viewGroup, false);
        }

        ImageView ivPreview = v.findViewById(R.id.photo);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvViews = v.findViewById(R.id.tvSee);
        TextView tvDate = v.findViewById(R.id.tvDate);

        tvTitle.setText(video.title);
        tvViews.setText(String.valueOf(video.views));

        tvTitle.setTextColor(manager.getPrimaryTextColor());
        tvViews.setTextColor(manager.getSecondaryTextColor());
        tvDate.setTextColor(manager.getSecondaryTextColor());

        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(video.date * 1000));
        Picasso.with(context)
                .load(video.image_big)
                .resize(App.screenWidth / 2, App.screenWidth / 2)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(ivPreview);
        return v;
    }
}

package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.App;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;


public class MaterialsPhotoAdapter extends BaseAdapter {

    private ArrayList<MaterialsAdapter> items;
    private Context context;
    private LayoutInflater inflater;
    private ThemeManager manager;

    public MaterialsPhotoAdapter(Context context, ArrayList<MaterialsAdapter> items) {
        this.context = context;
        this.items = items;
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
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.materials_photo_list, viewGroup, false);
        }

        ImageView iv = v.findViewById(R.id.photo);
        Picasso.with(context)
                .load(item.attachment.photo.src_big)
                .resize(App.screenWidth / 3, App.screenWidth / 3)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(iv);
        return v;
    }
}

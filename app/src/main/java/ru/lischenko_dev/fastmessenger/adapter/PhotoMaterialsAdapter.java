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


public class PhotoMaterialsAdapter extends BaseAdapter {

    ArrayList<PhotoMaterialsItems> items;
    Context context;
    LayoutInflater inflater;

    public PhotoMaterialsAdapter(Context context, ArrayList<PhotoMaterialsItems> items) {
        this.context = context;
        this.items = items;
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
            v = inflater.inflate(R.layout.materials_photo_list, viewGroup, false);
        }
        PhotoMaterialsItems item = (PhotoMaterialsItems) getItem(i);
        ImageView iv = (ImageView) v.findViewById(R.id.photo);
        Picasso.with(context)
                .load(item.attachment.photo.src_big)
				.resize(200, 200)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(iv);
        return v;
    }
}

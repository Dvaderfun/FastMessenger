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

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKDocument;

public class MaterialsDocAdapter extends BaseAdapter {

    private ArrayList<MaterialsAdapter> items;
    private Context context;
    private LayoutInflater inflater;
    private String size;
    private int lel;
    private ThemeManager manager;

    public MaterialsDocAdapter(Context context, ArrayList<MaterialsAdapter> items) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MaterialsAdapter item = (MaterialsAdapter) getItem(i);
        VKDocument doc = item.attachment.doc;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.materials_docs_list, viewGroup, false);
        }


        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSize = view.findViewById(R.id.tvArtist);

        tvTitle.setTextColor(manager.getPrimaryTextColor());
        ImageView ivDoc = view.findViewById(R.id.ivDoc);

        if (doc.isImage())
            Picasso.with(context).load(doc.photo_130).placeholder(new ColorDrawable(manager.isDarkTheme() ? Color.LTGRAY : Color.DKGRAY)).into(ivDoc);
        else
            ivDoc.setVisibility(View.GONE);
        tvTitle.setText(doc.title);
        tvSize.setText(String.valueOf(doc.ext));
        return view;
    }
}

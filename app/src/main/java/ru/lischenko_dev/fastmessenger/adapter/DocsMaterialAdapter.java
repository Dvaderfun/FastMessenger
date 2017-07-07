package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;

/**
 * Created by User-Guest on 02.07.16.
 * This is a fucking java class!
 */
public class DocsMaterialAdapter extends BaseAdapter {

    ArrayList<DocsMaterialItems> items;
    Context context;
    LayoutInflater inflater;
    String size;
    int lel;

    public DocsMaterialAdapter(Context context, ArrayList<DocsMaterialItems> items) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.materials_docs_list, viewGroup, false);
        }
        DocsMaterialItems item = (DocsMaterialItems) getItem(i);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvArtist = (TextView) view.findViewById(R.id.tvArtist);
        ImageView ivDoc = (ImageView) view.findViewById(R.id.ivDoc);

        if (item.att.doc.isImage())
            Picasso.with(context).load(item.att.doc.photo_130).into(ivDoc);
        else
            ivDoc.setVisibility(View.GONE);

        if ((int) item.att.doc.size >= 1000000 && (int) item.att.doc.size < 1000000000) {
            size = "MB";
            lel = 1000000;
        } else if ((int) item.att.doc.size >= 1000000000) {
            size = "GB";
            lel = 1000000000;
        } else if ((int) item.att.doc.size <= 1000000 && (int) item.att.doc.size > 999) {
            size = "KB";
            lel = 1000;
        } else if ((int) item.att.doc.size < 1000) {
            size = "Byte";
            lel = 1000;
        }
        tvTitle.setText(item.att.doc.title);
        tvArtist.setText(String.valueOf((int) item.att.doc.size / lel + " " + size));
        return view;
    }
}

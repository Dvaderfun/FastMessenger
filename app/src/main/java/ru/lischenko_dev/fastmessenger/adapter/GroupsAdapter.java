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
import ru.lischenko_dev.fastmessenger.vkapi.models.VKGroup;

import static ru.lischenko_dev.fastmessenger.R.id.ivAva;
import static ru.lischenko_dev.fastmessenger.R.id.tvName;
import static ru.lischenko_dev.fastmessenger.R.id.tvType;

public class GroupsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VKGroup> items;
    private LayoutInflater inflater;

    public GroupsAdapter(Context context, ArrayList<VKGroup> items) {
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
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_groups_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.ivAva = (ImageView) view.findViewById(ivAva);
            viewHolder.tvName = (TextView) view.findViewById(tvName);
            viewHolder.tvType = (TextView) view.findViewById(tvType);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        VKGroup group = (VKGroup) getItem(i);
        viewHolder.tvName.setText(group.name);
        String type = null;
        switch (group.type) {
            case VKGroup.Type.EVENT:
                type = context.getString(R.string.event);
                break;
            case VKGroup.Type.GROUP:
                type = context.getString(R.string.group_type);
                break;
            case VKGroup.Type.PAGE:
                type = context.getString(R.string.page_type);
                break;
        }

        switch (group.is_closed) {
            case VKGroup.Status.CLOSED:
                type = context.getString(R.string.closed_type) + " " + type;
                break;
            case VKGroup.Status.OPEN:
                type = context.getString(R.string.open_type) + " " + type;
                break;
            case VKGroup.Status.PRIVATE:
                type = context.getString(R.string.private_type) + " " + type;
                break;
        }
        viewHolder.tvType.setText(type);

        try {
            Picasso.with(context).load(group.photo_100).into(viewHolder.ivAva);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private static class ViewHolder {
        ImageView ivAva;
        TextView tvName;
        TextView tvType;
    }
}

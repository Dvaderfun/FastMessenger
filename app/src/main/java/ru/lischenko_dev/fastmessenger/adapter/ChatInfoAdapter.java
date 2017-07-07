package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
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
import ru.lischenko_dev.fastmessenger.vkapi.Api;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;



public class ChatInfoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<VKFullUser> items;
    private LayoutInflater inflater;

    private Api api;

    public ChatInfoAdapter(Context context, ArrayList<VKFullUser> items, Api api) {
        this.context = context;
        this.api = api;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_friends_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.ivAva = (ImageView) convertView.findViewById(R.id.ivAva);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.ivOnline = (ImageView) convertView.findViewById(R.id.ivOnline);
            viewHolder.tvOnline = (TextView) convertView.findViewById(R.id.tvOnline);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VKFullUser user = (VKFullUser) getItem(position);

        String type = user.online ? context.getString(R.string.online_from) : context.getString(R.string.last_seen_at);
        viewHolder.tvDate.setText(type + " " + new SimpleDateFormat("HH:mm").format(user.last_seen * 1000));
        viewHolder.tvDate.setTextColor(Color.GRAY);
        viewHolder.tvName.setText(user.toString());
        try {
           viewHolder.tvOnline.setText("Invited by " + api.getProfile(user.invited_by).toString());// + String.valueOf(user.invited_by));

            viewHolder.ivOnline.setVisibility(user.online ? View.VISIBLE : View.GONE);
            Picasso.with(context).load(user.photo_medium_rec)
                    .placeholder(R.drawable.camera_200)
                    .into(viewHolder.ivAva);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivOnline;
        ImageView ivAva;
        TextView tvName;
        TextView tvOnline;
        TextView tvDate;
    }
}
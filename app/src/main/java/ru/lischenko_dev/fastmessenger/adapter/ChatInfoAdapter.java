package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;

public class ChatInfoAdapter extends RecyclerView.Adapter<ChatInfoAdapter.ViewHolder> {

    private ArrayList<VKFullUser> items;
    private Context context;
    private ThemeManager manager;
    private OnItemClickListener listener;

    public ChatInfoAdapter(ArrayList<VKFullUser> items, Context context) {
        this.items = items;
        this.context = context;
        this.manager = ThemeManager.get(context);
    }

    public void setListener(OnItemClickListener l) {
        this.listener = l;
    }

    private void initListeners(View v, final int position) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongClick(v, position);
                }
                return true;
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public ChatInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friends_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VKFullUser user = items.get(position);
        initListeners(holder.itemView, position);
        manager.setHrBackgroundColor(holder.hr);
        holder.tvOnline.setText(user.online ? user.online_mobile ? context.getString(R.string.online_from_phone) : context.getString(R.string.online) : context.getString(R.string.last_seen_at) + " " + new SimpleDateFormat("HH:mm").format(user.last_seen * 1000));
        holder.tvOnline.setTextColor(user.online ? context.getResources().getColor(R.color.online) : manager.getSecondaryTextColor());
        holder.tvName.setText(user.toString());
        holder.tvName.setTextColor(manager.getPrimaryTextColor());
        try {
            holder.ivOnline.setVisibility(user.online ? View.VISIBLE : View.GONE);
            Picasso.with(context).load(user.photo_medium_rec).placeholder(R.drawable.camera_200).into(holder.ivAva);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOnline;
        ImageView ivAva;
        TextView tvName;
        TextView tvOnline;
        View hr;

        public ViewHolder(View v) {
            super(v);
            ivOnline = (ImageView) v.findViewById(R.id.ivOnline);
            tvOnline = (TextView) v.findViewById(R.id.tvOnline);
            hr = v.findViewById(R.id.hr);
            tvName = (TextView) v.findViewById(R.id.tvName);
            ivAva = (ImageView) v.findViewById(R.id.ivAva);

        }
    }
}

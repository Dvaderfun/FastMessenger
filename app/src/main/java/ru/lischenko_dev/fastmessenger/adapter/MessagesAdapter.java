package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.Account;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.view.CircleImageView;
import ru.lischenko_dev.fastmessenger.view.CircleView;
import ru.lischenko_dev.fastmessenger.vkapi.VKUtils;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKAttachment;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private ArrayList<MessagesItem> items;
    private Context context;
    private ThemeManager manager;
    private OnItemClickListener listener;

    public MessagesAdapter(ArrayList<MessagesItem> items, Context context) {
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
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_messages_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void destroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        initListeners(holder.itemView, position);
        MessagesItem item = items.get(position);
        manager.setHrBackgroundColor(holder.hr);
        holder.ivChat.setVisibility(item.message.isChat() ? View.VISIBLE : View.GONE);

        holder.counter.setVisibility(!item.message.is_out && !item.message.read_state ? View.VISIBLE : View.GONE);
        holder.counter.setCircleColor(0xff1565c0);
        holder.counter.setText(String.valueOf(item.message.unread));
        holder.counter.setTextColor(Color.WHITE);
        holder.counter.setTextSize(12);

        String count = String.valueOf(item.message.unread);
        if (count.length() > 3) {
            holder.counter.setText(count.substring(0, 1) + "K+");
        } else if (count.length() > 2)
            holder.counter.setTextSize(10);

        holder.tvName.setTextColor(manager.getPrimaryTextColor());
        holder.tvBody.setTextColor(manager.getSecondaryTextColor());
        holder.tvDate.setTextColor(manager.getSecondaryTextColor());

        holder.tvName.setText(item.message.isChat() ? item.message.title : item.user.toString());
        holder.tvDate.setText(new SimpleDateFormat("HH:mm").format(item.message.date * 1000));

        holder.rl.setBackgroundDrawable(item.message.read_state ? null : item.message.is_out ? context.getResources().getDrawable(R.drawable.ic_not_read_body) : null);

        if (holder.rl.getBackground() != null)
            holder.rl.getBackground().setColorFilter(manager.getUnreadColor(), PorterDuff.Mode.MULTIPLY);

        if (!item.message.isChat() && !item.message.is_out)
            holder.ivAvaSmall.setVisibility(View.GONE);
        try {
            Picasso.with(context).load(item.message.isChat() ? item.message.photo_200 : item.user.photo_200).placeholder(R.drawable.camera_200).into(holder.ivAva);
            Picasso.with(context).load(item.message.isChat() ? item.message.is_out ? Account.get(context).getSmallAvatar() : item.user.photo_50 : item.message.is_out ? Account.get(context).getSmallAvatar() : item.user.photo_50).placeholder(R.drawable.camera_200).into(holder.ivAvaSmall);
        } catch (Exception e) {
            Log.e("АААААААА ОШИИИИИБКААААА", e.toString());
        }

        try {
            holder.ivOnline.setVisibility(View.GONE);
            if (!item.message.isChat())
                holder.ivOnline.setVisibility(item.user.online ? View.VISIBLE : View.GONE);
            else
                holder.ivOnline.setVisibility(View.GONE);
            holder.ivAvaSmall.setVisibility(item.message.isChat() ? View.VISIBLE : item.message.is_out ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvBody.setText(item.message.body);

        if (item.message.body.length() == 0)
            if (!item.message.attachments.isEmpty()) {
                for (VKAttachment att : item.message.attachments) {
                    //viewHolder.tvBody.setText(VKUtils.getStringAttachment(att.type));
                    //Spannable text = new SpannableString(attachment);
                    // text.setSpan(new StyleSpan(Typeface.BOLD), 0, attachment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b>", VKUtils.getStringAttachment(att.type))));
                    holder.tvBody.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }

            }
        if (!TextUtils.isEmpty(item.message.action)) {
            String action = null;
            switch (item.message.action) {
                case VKMessage.ACTION_CHAT_CREATE:
                    action = " created chat";
                    break;

                case VKMessage.ACTION_CHAT_PHOTO_UPDATE:
                    action = " updated photo in chat";
                    break;

                case VKMessage.ACTION_CHAT_PHOTO_REMOVE:
                    action = " removed photo in chat";
                    break;

                case VKMessage.ACTION_CHAT_TITLE_UPDATE:
                    action = " changed title in chat";
                    break;

                case VKMessage.ACTION_CHAT_KICK_USER:
                    if (item.user.uid == item.message.action_mid) {
                        action = " leaved from chat";
                    } else {
                        action = " kicked user from chat";
                    }
                    break;

                case VKMessage.ACTION_CHAT_INVITE_USER:
                    if (item.user.uid == item.message.action_mid) {
                        action = " returned to chat";
                    } else {
                        action = " invited user to chat";
                    }
                    break;
            }
            Spannable text = new SpannableString(action);
            text.setSpan(new StyleSpan(Typeface.BOLD), 0, action.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvBody.setText(text);
            holder.tvBody.setTextColor(Color.parseColor("#1565c0"));
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
        TextView tvBody;
        TextView tvDate;
        CircleImageView ivAva;
        ImageView ivAvaSmall;
        TextView tvName;
        CircleView ivOnline;
        LinearLayout rl;
        ImageView ivChat;
        CircleView counter;
        View hr;

        public ViewHolder(View v) {
            super(v);
            tvBody = v.findViewById(R.id.tvBody);
            tvDate = v.findViewById(R.id.tvDate);
            ivAva = v.findViewById(R.id.ivAva);
            ivAvaSmall = v.findViewById(R.id.ivAvaSmall);
            tvName = v.findViewById(R.id.tvName);
            ivOnline = v.findViewById(R.id.ivOnline);
            rl = v.findViewById(R.id.main_container);
            ivChat = v.findViewById(R.id.ivChat);
            counter = v.findViewById(R.id.ivCount);
            hr = v.findViewById(R.id.hr);

        }
    }
}
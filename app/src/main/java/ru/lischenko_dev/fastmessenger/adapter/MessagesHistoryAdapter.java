package ru.lischenko_dev.fastmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.common.App;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.util.TextViewLinkHandler;
import ru.lischenko_dev.fastmessenger.util.Utils;
import ru.lischenko_dev.fastmessenger.vkapi.VKUtils;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKAttachment;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class MessagesHistoryAdapter extends RecyclerView.Adapter<MessagesHistoryAdapter.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 10;

    private ArrayList<ChatItems> items;
    private Context context;
    private ThemeManager manager;
    private OnItemClickListener listener;

    public MessagesHistoryAdapter(ArrayList<ChatItems> items, Context context) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(FooterViewHolder.createFooter(context));
        }

        View v = LayoutInflater.from(context).inflate(R.layout.fragment_chat_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.isFooter()) {
            return;
        }
        ChatItems item = items.get(position);
        final VKMessage message = item.msg;
        holder.mainContainer.setGravity(message.is_out ? Gravity.END : Gravity.START);
        ((LinearLayout) holder.bubble.getParent()).setGravity(message.is_out ? Gravity.END : Gravity.START);
        final VKFullUser user = item.user;
        initListeners(holder.itemView, position);
        holder.tvBody.setText(message.body);

        Drawable bg = context.getResources().getDrawable(R.drawable.ic_msg_bg);
        holder.tvBody.setGravity(message.is_out ? Gravity.START : Gravity.CENTER_VERTICAL);
        bg.setColorFilter(message.is_out ? context.getResources().getColor(R.color.colorPrimary) : ThemeManager.get(context).getInBubbleColor(), PorterDuff.Mode.MULTIPLY);
        holder.tvBody.setTextColor(message.is_out ? Color.WHITE : manager.getBubbleInTextColor());
        holder.ivAva.setVisibility(message.isChat() ? message.is_out ? View.GONE : View.VISIBLE : View.GONE);
        holder.tvBody.setLinkTextColor(message.is_out ? Color.WHITE : manager.getAccentColor());
        if (holder.ivAva.getVisibility() != View.GONE)
            try {
                Picasso.with(context).load(user.photo_50).placeholder(R.drawable.camera_200).into(holder.ivAva);
            } catch (Exception e) {
                e.printStackTrace();
            }

        holder.tvBody.setVisibility(TextUtils.isEmpty(message.body) ? View.GONE : View.VISIBLE);
        holder.tvBody.setMaxWidth(App.screenWidth - (App.screenWidth / 4));

        holder.ivAva.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View p1) {
                if (user != null)
                    Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        holder.bubble.setBackground(bg);
        holder.tvBody.setMovementMethod(new TextViewLinkHandler() {

            @Override
            public void onLinkClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        if (!message.attachments.isEmpty()) {
            for (VKAttachment att : message.attachments)
                holder.tvBody.setText(holder.tvBody.getText().length() > 0 ? holder.tvBody.getText().toString() + "\n[" + VKUtils.getStringAttachment(att.type) + "]" : "[" + VKUtils.getStringAttachment(att.type) + "]");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.size() == position) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody;
        LinearLayout mainContainer;
        LinearLayout bubble;
        ImageView ivAva;

        public ViewHolder(View v) {
            super(v);
            tvBody = v.findViewById(R.id.tvBody);
            mainContainer = v.findViewById(R.id.main_container);
            bubble = v.findViewById(R.id.bodyContainer);
            ivAva = v.findViewById(R.id.ivAva);

        }

        public boolean isFooter() {
            return false;
        }

    }

    private static class FooterViewHolder extends ViewHolder {
        View footer;

        FooterViewHolder(View v) {
            super(v);
            footer = v;
        }

        public static View createFooter(Context context) {
            View footer = new View(context);
            footer.setVisibility(View.VISIBLE);
            footer.setBackgroundColor(Color.TRANSPARENT);
            footer.setVisibility(View.INVISIBLE);
            footer.setEnabled(false);
            footer.setClickable(false);
            footer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Utils.convertDpToPixel(context, 75)));
            return footer;
        }

        @Override
        public boolean isFooter() {
            return true;
        }
    }
}
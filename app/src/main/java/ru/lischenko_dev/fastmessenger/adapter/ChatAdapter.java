package ru.lischenko_dev.fastmessenger.adapter;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.util.*;
import org.greenrobot.eventbus.*;
import ru.lischenko_dev.fastmessenger.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;
import java.util.concurrent.*;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChatItems> items;
    private LayoutInflater inflater;
    private Api api;
    private long cid;
    private long uid;
	private ThemeManager manager;
    private ListView lv;
	private VKFullUser newUser;

    public ChatAdapter(Context context, ArrayList<ChatItems> items, Api api, long cid, long uid, ListView lv) {
        this.context = context;
        this.api = api;
        this.uid = uid;
        this.cid = cid;
        this.lv = lv;
		this.manager = ThemeManager.get(context);
        this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        EventBus.getDefault().register(this);
    }

	private class UserGet extends AsyncTask <VKFullUser, VKFullUser, VKFullUser> {

		private long id;

		public UserGet(long id) {
			this.id = id;
		}

		@Override
		protected VKFullUser doInBackground(VKFullUser[] p1) {
			VKFullUser user = null;
			try {
				user = api.getProfile(id);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("ErrorGetUserFromAsync", e.toString());
			}
			return user;
		}
	}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(final VKMessage message) {
		Log.d("LongPoll", "NewMessage with text: " + message.body);
		if(!message.is_out)
		new Thread(new Runnable() { 
				@Override 
				public void run() { 
					try { 
						// если это не тот диалог, в котором мы сейчас находимся 
						if (message.uid != uid || message.chat_id != cid) { 
							return; 
						} 
						
						
						
						VKFullUser user = api.getProfile(message.uid); 
						items.add(new ChatItems(message, user));
						notifyDataSetChanged();
						((Activity) context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									lv.setSelection(getCount() + 1);
								}
						});
					} catch (Exception e) { 
						e.printStackTrace(); 
					} 
				} 
			}).start(); 
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadMessage(Integer id) {
        VKMessage message = searchMessage(id);
        if (message != null) {
            message.read_state = true;
            message.unread = 0;
            notifyDataSetChanged();
        }
    }

    public void destroy() {
		EventBus.getDefault().unregister(this);
    }

    public VKMessage searchMessage(int id) {
        for (int i = 0; i < items.size(); i++) {
            VKMessage msg = items.get(i).msg;
            if (msg.mid == id) {
                return msg;
            }
        }
        return null;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_chat_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.mainContainer = (LinearLayout) convertView.findViewById(R.id.main_container);
            viewHolder.bodyContainer = (LinearLayout) convertView.findViewById(R.id.bodyContainer);
            viewHolder.ivAva = (ImageView) convertView.findViewById(R.id.ivAva);
            convertView.setTag(viewHolder);
        }
		else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


		ChatItems item = (ChatItems) getItem(position);
        final VKMessage message = item.msg;
		final VKFullUser user = item.user;
        viewHolder.tvBody.setText(message.body);

        Drawable bg = context.getResources().getDrawable(R.drawable.ic_msg_bg);
        viewHolder.tvBody.setGravity(message.is_out ? Gravity.START : Gravity.CENTER_VERTICAL);
        bg.setColorFilter(message.is_out ? context.getResources().getColor(R.color.colorPrimary) : ThemeManager.get(context).getInBubbleColor(), PorterDuff.Mode.MULTIPLY);
        viewHolder.tvBody.setTextColor(message.is_out ? Color.WHITE : manager.getBubbleInTextColor());
        viewHolder.ivAva.setVisibility(message.isChat() ? message.is_out ? View.GONE : View.VISIBLE : View.GONE);

		if(viewHolder.ivAva.getVisibility() != View.GONE)
        try {
            Picasso.with(context).load(user.photo_50).placeholder(R.drawable.camera_200).into(viewHolder.ivAva);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.tvBody.setVisibility(TextUtils.isEmpty(message.body) ? View.GONE : View.VISIBLE);
        viewHolder.tvBody.setMaxWidth(App.screenWidth - (App.screenWidth / 4));
        viewHolder.mainContainer.setGravity(message.is_out ? Gravity.END : Gravity.START);
        viewHolder.ivAva.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					if(user != null)
					Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show();
					return false;
				}
			});
        viewHolder.bodyContainer.setBackgroundDrawable(bg);
        viewHolder.tvBody.setMovementMethod(new TextViewLinkHandler() {

				@Override
				public void onLinkClick(String url) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});

        if (!message.attachments.isEmpty()) {
            for (VKAttachment att : message.attachments)
                viewHolder.tvBody.setText(viewHolder.tvBody.getText().length() > 0 ? viewHolder.tvBody.getText().toString() + "\n[" + VKUtils.getStringAttachment(att.type) + "]" : "[" + VKUtils.getStringAttachment(att.type) + "]");
        }

        if (!TextUtils.isEmpty(message.action)) {
            switch (message.action) {
                case VKMessage.ACTION_CHAT_CREATE:
                    viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> created chat \"%s\"", user.toString(), message.action_text)));
                    break;
                case VKMessage.ACTION_CHAT_PHOTO_UPDATE:
                    viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> updated chat photo ", user.toString())));
                    break;
                case VKMessage.ACTION_CHAT_PHOTO_REMOVE:
                    viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> removed chat photo ", user.toString())));
                    break;
                case VKMessage.ACTION_CHAT_TITLE_UPDATE:
                    viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> changed chat title to <b>'%s'</b>", user.toString(), message.action_text)));
                    break;
                case VKMessage.ACTION_CHAT_KICK_USER:
                    if (user.uid == message.action_mid) {
                        viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> leaved from chat", user.toString())));
                    }
					else {
                        viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> kicked user from chat", user.toString())));
                    }
                    break;
                case VKMessage.ACTION_CHAT_INVITE_USER:
                    if (user.uid == message.action_mid) {
                        viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> returned to chat", user.toString())));
                    }
					else {
                        viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b> invited user to chat", user.toString())));
                    }
                    break;
            }
            viewHolder.tvBody.setTextColor(manager.isDarkTheme() ? 0xfffffff : 0xff212121);
            viewHolder.bodyContainer.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.mainContainer.setGravity(Gravity.CENTER);
            viewHolder.tvBody.setGravity(Gravity.CENTER);
            viewHolder.ivAva.setVisibility(View.GONE);
        }


        return convertView;

    }

    private static class ViewHolder {
        TextView tvBody;
        LinearLayout mainContainer;
        LinearLayout bodyContainer;
        ImageView ivAva;
    }

    public void clear() {
        if (items != null) {
            items.clear();
            items.trimToSize();
            items = null;
        }
    }
}

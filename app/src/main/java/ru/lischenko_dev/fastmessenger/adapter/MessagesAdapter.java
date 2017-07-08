package ru.lischenko_dev.fastmessenger.adapter;

import android.content.*;
import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.text.*;
import java.util.*;
import org.greenrobot.eventbus.*;
import ru.lischenko_dev.fastmessenger.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.view.*;
import ru.lischenko_dev.fastmessenger.vkapi.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

public class MessagesAdapter extends BaseAdapter {
    private Context context;

	private Account account = new Account();
    private ArrayList<MessagesItem> items;
    private LayoutInflater inflater;
	private ThemeManager manager;
    private Comparator<MessagesItem> comparator;

    public MessagesAdapter(Context context, ArrayList<MessagesItem> items) {
        this.context = context;
        this.items = items;
		this.manager = ThemeManager.get(context);
		this.account.restore(this.context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        comparator = new Comparator<MessagesItem>() {
            @Override
            public int compare(MessagesItem o1, MessagesItem o2) {
                long x = o1.message.date;
                long y = o2.message.date;

                return (x > y) ? -1 : ((x == y) ? 1 : 0);
            }
        };
		
	
        EventBus.getDefault().register(this);

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
            convertView = inflater.inflate(R.layout.fragment_messages_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.ivAva = (CircleImageView) convertView.findViewById(R.id.ivAva);
            viewHolder.ivAvaSmall = (ImageView) convertView.findViewById(R.id.ivAvaSmall);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.ivOnline = (CircleView) convertView.findViewById(R.id.ivOnline);
            viewHolder.rl = (LinearLayout) convertView.findViewById(R.id.main_container);
            viewHolder.ivChat = (ImageView) convertView.findViewById(R.id.ivChat);
			viewHolder.counter = (CircleView) convertView.findViewById(R.id.ivCount);
			viewHolder.hr = convertView.findViewById(R.id.hr);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessagesItem item = (MessagesItem) getItem(position);

		manager.setHrBackgroundColor(viewHolder.hr);
		viewHolder.ivChat.setVisibility(item.message.isChat() ? View.VISIBLE: View.GONE);
		
        viewHolder.counter.setVisibility(!item.message.is_out && !item.message.read_state ? View.VISIBLE : View.GONE);
        viewHolder.counter.setCircleColor(0xff1565c0);
        viewHolder.counter.setText(String.valueOf(item.message.unread));
        viewHolder.counter.setTextColor(Color.WHITE);
        viewHolder.counter.setTextSize(12);

        String count = String.valueOf(item.message.unread);
        if (count.length() > 3) {
            viewHolder.counter.setText(count.substring(0, 1) + "K+");
        } else if (count.length() > 2)
            viewHolder.counter.setTextSize(10);
		
		viewHolder.tvName.setTextColor(manager.getPrimaryTextColor());
		viewHolder.tvBody.setTextColor(manager.getSecondaryTextColor());
		viewHolder.tvDate.setTextColor(manager.getSecondaryTextColor());
			
        viewHolder.tvName.setText(item.message.isChat() ? item.message.title : item.user.toString());
        viewHolder.tvDate.setText(new SimpleDateFormat("HH:mm").format(item.message.date * 1000));

        viewHolder.rl.setBackgroundDrawable(item.message.read_state ? null : item.message.is_out ? context.getResources().getDrawable(R.drawable.ic_not_read_body) : null);
        
		if(viewHolder.rl.getBackground() != null)
		viewHolder.rl.getBackground().setColorFilter(manager.getUnreadColor(), PorterDuff.Mode.MULTIPLY);
		
        if (!item.message.isChat() && !item.message.is_out)
            viewHolder.ivAvaSmall.setVisibility(View.GONE);
        try {
            Picasso.with(context).load(item.message.isChat() ? item.message.photo_200 : item.user.photo_200).placeholder(R.drawable.camera_200).into(viewHolder.ivAva);
            Picasso.with(context).load(item.message.isChat() ? item.message.is_out ? account.small_avatar : item.user.photo_50 : item.message.is_out ? account.small_avatar : item.user.photo_50).placeholder(R.drawable.camera_200).into(viewHolder.ivAvaSmall);
        } catch (Exception e) {
            Log.e("АААААААА ОШИИИИИБКААААА", e.toString());
        }

        try {
            viewHolder.ivOnline.setVisibility(View.GONE);
            if (!item.message.isChat())
                viewHolder.ivOnline.setVisibility(item.user.online ? View.VISIBLE : View.GONE);
            else
                viewHolder.ivOnline.setVisibility(View.GONE);
            viewHolder.ivAvaSmall.setVisibility(item.message.isChat() ? View.VISIBLE : item.message.is_out ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.tvBody.setText(item.message.body);

        if (item.message.body.length() == 0)
            if (!item.message.attachments.isEmpty()) {
				for (VKAttachment att : item.message.attachments) {
					//viewHolder.tvBody.setText(VKUtils.getStringAttachment(att.type));
					//Spannable text = new SpannableString(attachment);
					// text.setSpan(new StyleSpan(Typeface.BOLD), 0, attachment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder.tvBody.setText(Html.fromHtml(String.format("<b>%s</b>", VKUtils.getStringAttachment(att.type))));
					viewHolder.tvBody.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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
            viewHolder.tvBody.setText(text);
            viewHolder.tvBody.setTextColor(Color.parseColor("#1565c0"));
        }

        return convertView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(final VKMessage msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					VKFullUser user = Api.init(account).getProfile(msg.uid);
                    int index = searchMessageIndex(msg.uid, msg.chat_id);
                    if (index >= 0) {
                        MessagesItem c = items.get(index);
                        VKMessage current = c.message;
						VKFullUser currentUser = c.user;
                       /* current.mid = msg.mid;
						if(!msg.attachments.isEmpty() && TextUtils.isEmpty(msg.body))
						for(VKAttachment att : msg.attachments) {
							current.body = VKUtils.getStringAttachment(att.type);
						} else
						current.body = msg.body;
                        current.title = msg.title;
                        current.date = msg.date;
                        current.uid = msg.uid;
						current.attachments = msg.attachments;
                        current.chat_id = msg.chat_id;
                        current.read_state = msg.read_state;
                        current.is_out = msg.is_out;
                        current.unread++;
                        if (current.is_out) {
                            current.unread = 0;
                        }*/
						//items.remove(msg.isChat() ? current : currentUser);
						//items.add(new MessagesItem(msg, user));
                       // Collections.sort(items, comparator);
                    //    notifyDataSetChanged();
                    }
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

    private VKMessage searchMessage(int id) {
        for (int i = 0; i < items.size(); i++) {
            MessagesItem item = items.get(i);
            VKMessage msg = item.message;
            if (msg.mid == id) {
                return msg;
            }
        }
        return null;
    }

    private int searchMessageIndex(long userId, long chatId) {
        for (int i = 0; i < items.size(); i++) {
            MessagesItem item = items.get(i);
            VKMessage msg = item.message;
            if (msg.chat_id == chatId && chatId > 0) {
                return i;
            }

            if (msg.uid == userId && chatId == 0) {
                return i;
            }
        }
        return -1;
    }

    private static class ViewHolder {
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
    }

    public void destroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}

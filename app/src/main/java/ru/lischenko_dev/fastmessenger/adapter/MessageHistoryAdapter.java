package ru.lischenko_dev.fastmessenger.adapter;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.makeramen.roundedimageview.*;
import com.squareup.picasso.*;
import java.util.*;
import ru.lischenko_dev.fastmessenger.*;
import ru.lischenko_dev.fastmessenger.common.*;
import ru.lischenko_dev.fastmessenger.util.*;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

import ru.lischenko_dev.fastmessenger.R;
import android.widget.ImageView.*;
import ru.lischenko_dev.fastmessenger.view.*;

public class MessageHistoryAdapter extends BaseAdapter {

	@Override
	public int getCount() {

		return items.size();
	}

	@Override
	public Object getItem(int p1) {

		return items.get(p1);
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3) {
		View v = LayoutInflater.from(context).inflate(R.layout.fragment_chat_list, p3, false);
		MessageHistoryItems item = items.get(p1);
        final VKMessage message = item.msg;
        final VKFullUser user = item.user;

		TextView tvBody = (TextView) v.findViewById(R.id.tvBody);
		LinearLayout mainContainer = (LinearLayout) v.findViewById(R.id.main_container);
		LinearLayout bubble = (LinearLayout) v.findViewById(R.id.bodyContainer);
		ImageView ivAva = (ImageView) v.findViewById(R.id.ivAva);

		mainContainer.setGravity(message.is_out ? Gravity.END : Gravity.START);

        tvBody.setText(message.body);



        Drawable bg = context.getResources().getDrawable(R.drawable.ic_msg_bg);
        tvBody.setGravity(message.is_out ? Gravity.START : Gravity.CENTER_VERTICAL);
        bg.setColorFilter(message.is_out ? context.getResources().getColor(R.color.colorPrimary) : ThemeManager.get(context).getInBubbleColor(), PorterDuff.Mode.MULTIPLY);
        tvBody.setTextColor(message.is_out ? Color.WHITE : manager.getBubbleInTextColor());
        ivAva.setVisibility(message.isChat() ? message.is_out ? View.GONE : View.VISIBLE : View.GONE);
        tvBody.setLinkTextColor(message.is_out ? Color.WHITE : manager.getAccentColor());
        if (ivAva.getVisibility() != View.GONE)
            try {
                Picasso.with(context).load(user.photo_50).placeholder(R.drawable.camera_200).into(ivAva);
            } catch (Exception e) {
                e.printStackTrace();
            }

        tvBody.setVisibility(TextUtils.isEmpty(message.body) ? View.GONE : View.VISIBLE);
        tvBody.setMaxWidth(App.screenWidth - (App.screenWidth / 4));

        ivAva.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					if (user != null)
						Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show();
					return false;
				}
			});
        bubble.setBackground(bg);
        tvBody.setMovementMethod(new TextViewLinkHandler() {

				@Override
				public void onLinkClick(String url) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});

        if (!message.attachments.isEmpty()) {
			getAttachments(item.msg, v, tvBody);
			
        }

		return v;
	}



    private ArrayList<MessageHistoryItems> items;
    private Context context;
    private ThemeManager manager;

    public MessageHistoryAdapter(ArrayList<MessageHistoryItems> items, Context context) {
        this.items = items;
        this.context = context;
        this.manager = ThemeManager.get(context);
    }

	private void getAttachments(VKMessage msg, View view, TextView tvBody1) {
		if (!msg.attachments.isEmpty()) {
			LinearLayout attach_container = (LinearLayout) view.findViewById(R.id.attach_container);
			for (final VKAttachment att :msg. attachments)
				switch (att.type) {
					case VKAttachment.TYPE_PHOTO:
						attach_container.setVisibility(View.VISIBLE);
						
						final RoundedImageView iv = new RoundedImageView(context);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(att.photo.width / 2, att.photo.height / 2);
						iv.setLayoutParams(params);
						iv.setScaleType(ScaleType.CENTER_CROP);
						iv.setCornerRadius(15);
					
						
					//	int wheight = attachments.size() > 1 ? App.screenWidth - (App.screenWidth / 2) : App.screenWidth - (App.screenWidth / 4);
							
						attach_container.setGravity(msg.is_out ? Gravity.RIGHT : Gravity.LEFT);
						attach_container.setOrientation(LinearLayout.VERTICAL);
						attach_container.addView(iv);
						
						Picasso.with(context).load(att.photo.src_big).into(iv);
						
						
						break;
					case VKAttachment.TYPE_STICKER:
						if (attach_container.getVisibility() == View.GONE)
							attach_container.setVisibility(View.VISIBLE);
						final ImageView ivq = new ImageView(context);
						ivq.setLayoutParams(new LinearLayout.LayoutParams(att.sticker.width, att.sticker.height));
						attach_container.addView(ivq);

						Picasso.with(context)
							.load(att.sticker.photo_352)
							.placeholder(new ColorDrawable(Color.GRAY))
							.into(ivq);
						break;
						/*
					case VKAttachment.TYPE_MESSAGE:
						if (attach_container.getVisibility() == View.GONE) {
							attach_container.setVisibility(View.VISIBLE);
						}
						View fwd = LayoutInflater.from(context).inflate(R.layout.forward_messages, attach_container, false);
						fwd.setPadding(4, 2, 4, 2);
						LinearLayout ll = (LinearLayout) fwd.findViewById(R.id.cont);
						Drawable ic = context.getResources().getDrawable(R.drawable.ic_msg_bg);
						ic.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
						ll.setBackground(ic);
						final TextView tvName = (TextView) fwd.findViewById(R.id.tvName);
						final TextView tvBody = (TextView) fwd.findViewById(R.id.tvBody);
						new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

										final VKFullUser user = Api.init(Account.get(context)).getProfile(att.message.uid);

                                        ((Activity) context).runOnUiThread(new Runnable() {
												@Override
												public void run() {

													tvName.setText(user.toString());
													tvBody.setText(att.message.body);
													//Picasso.with(context).load(user.photo_200).placeholder(R.drawable.camera_200).into(ivAva);

												}
											});
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
						attach_container.addView(fwd);
						break;*/
					case VKAttachment.TYPE_AUDIO:
						try {
							if (attach_container.getVisibility() == View.GONE)
								attach_container.setVisibility(View.VISIBLE);
							View v = View.inflate(context, R.layout.audio_attach, null);
							TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
							ImageView ivPlay = (ImageView) v.findViewById(R.id.ivPlay);
							ivPlay.setColorFilter(!msg.is_out ? manager.getPrimaryColor() : manager.getBubbleInTextColor());
							TextView tvArtist = (TextView) v.findViewById(R.id.tvOnline);
							attach_container.addView(v);
							RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.base_container_message);
							GradientDrawable dg = new GradientDrawable();
							dg.setCornerRadius(85);
							dg.setColor(msg.is_out ? manager.getPrimaryColor() : manager.getBubbleInTextColor());
							rl.setBackground(dg);
							tvArtist.setTextColor(msg.is_out ? Color.WHITE : Color.DKGRAY);
							tvTitle.setTextColor(msg.is_out ? Color.WHITE : Color.DKGRAY);
							tvArtist.setText(att.audio.artist);
							tvTitle.setText(att.audio.title);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case VKAttachment.TYPE_DOC:
						try {
							if (attach_container.getVisibility() == View.GONE)
								attach_container.setVisibility(View.VISIBLE);
							View v = View.inflate(context, R.layout.doc_attach, null);
							TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
							CircleView ivPlay = (CircleView) v.findViewById(R.id.ivPlay);
							ivPlay.setCircleColor(Color.parseColor("#ffffff"));
							ivPlay.setText("." + att.document.ext);
							GradientDrawable dg = new GradientDrawable();
							dg.setCornerRadius(85);
							dg.setColor(msg.is_out ? manager.getPrimaryColor() : manager.getBubbleInTextColor());
							ivPlay.setTextSize(16);
							ivPlay.setTextColor(Color.parseColor("#212121"));
							TextView tvArtist = (TextView) v.findViewById(R.id.tvOnline);
							attach_container.addView(v);
							tvTitle.setTextColor(Color.WHITE);
							tvTitle.setText(att.document.title + "");
							tvArtist.setText("." + att.document.ext.toUpperCase());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
						/*
					case VKAttachment.TYPE_VIDEO:
						attach_container.setVisibility(View.VISIBLE);
						DisplayMetrics metrics1 = new DisplayMetrics();
						((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics1);

						final ImageView iv1 = new ImageView(context);
						LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(180, 180);
						iv1.setScaleType(ImageView.ScaleType.CENTER);
						iv1.setLayoutParams(params1);
						attach_container.setGravity(Gravity.CENTER);
						attach_container.addView(iv1);
						Picasso.with(context)
							.load(att.video.image_big)
							.resize(180, 180)
							.centerCrop()
							.placeholder(Color.LTGRAY)
							.into(iv1);

						iv1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(att.video.getVideoUrl())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            });
						break;
					case VKAttachment.TYPE_PADE:
						//tvBody1.setText(item.message.body + "\n" + "Page");
						break;
					case VKAttachment.TYPE_WALL:
						//tvBody1.setText(item.message.body + "\n" + "Wall Post");
						break;
					case VKAttachment.TYPE_LINK:
						//	tvBody1.setText(item.message.body + "\n" + att.link.url);
						break;
					case VKAttachment.TYPE_GIFT:
						if (attach_container.getVisibility() == View.GONE)
							attach_container.setVisibility(View.VISIBLE);
						final ImageView giftimg = new ImageView(context);
						attach_container.addView(giftimg);

						Picasso.with(context)
							.load(att.gift.thumb_256)
							.resize(256, 256)
							.centerInside()
							.placeholder(new ColorDrawable(Color.GRAY))
							.into(giftimg);
						break;*/
				}

		}
	}
}

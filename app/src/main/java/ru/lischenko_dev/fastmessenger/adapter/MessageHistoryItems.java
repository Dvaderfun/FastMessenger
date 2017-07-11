package ru.lischenko_dev.fastmessenger.adapter;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

public class MessageHistoryItems {
	public VKMessage msg;
	public VKFullUser user;
	
	public MessageHistoryItems(VKMessage msg, VKFullUser user) {
		this.msg = msg;
		this.user = user;
	}
}

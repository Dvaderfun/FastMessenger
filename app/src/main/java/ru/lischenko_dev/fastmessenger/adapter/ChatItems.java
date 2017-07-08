package ru.lischenko_dev.fastmessenger.adapter;
import ru.lischenko_dev.fastmessenger.vkapi.models.*;

public class ChatItems {
	public VKMessage msg;
	public VKFullUser user;
	
	public ChatItems(VKMessage msg, VKFullUser user) {
		this.msg = msg;
		this.user = user;
	}
}

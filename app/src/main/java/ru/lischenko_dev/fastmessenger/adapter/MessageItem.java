package ru.lischenko_dev.fastmessenger.adapter;

import ru.lischenko_dev.fastmessenger.vkapi.models.VKFullUser;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class MessageItem {
    public VKMessage message;
    public VKFullUser user;


    public MessageItem(VKMessage message, VKFullUser user) {
        this.message = message;
        this.user = user;
    }
}

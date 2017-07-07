package ru.lischenko_dev.fastmessenger.adapter;

import ru.lischenko_dev.fastmessenger.vkapi.models.*;

public class MessagesItem {
    public VKMessage message;
    public VKFullUser user;


    public MessagesItem(VKMessage message, VKFullUser user) {
        this.message = message;
        this.user = user;
    }
}

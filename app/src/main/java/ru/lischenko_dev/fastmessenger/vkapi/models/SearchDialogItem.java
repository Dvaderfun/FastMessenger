package ru.lischenko_dev.fastmessenger.vkapi.models;

public class SearchDialogItem {

    public enum SDIType {USER, CHAT, EMAIL}

    public String str_type;
    public SDIType type;
    public String email;
    public VKFullUser user;
    public VKMessage chat;

}

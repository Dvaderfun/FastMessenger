package ru.lischenko_dev.fastmessenger.adapter;

import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessageAttachment;

public class MaterialsAdapter {
    public VKMessageAttachment attachment;

    public MaterialsAdapter(VKMessageAttachment attachment) {
        this.attachment = attachment;
    }
}

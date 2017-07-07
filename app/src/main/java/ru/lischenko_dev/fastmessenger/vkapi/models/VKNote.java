package ru.lischenko_dev.fastmessenger.vkapi.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ru.lischenko_dev.fastmessenger.vkapi.Api;


public class VKNote implements Serializable {
    private static final long serialVersionUID = 1L;
    public long nid;
    public long owner_id;
    public String title;
    public String text;
    public long date = 0;
    public long ncom = -1;
    //public long read_ncom=-1;

    public static VKNote parse(JSONObject o) throws NumberFormatException, JSONException {
        VKNote note = new VKNote();
        note.nid = o.optLong("id");

        //в новости "добавил заметку" заметка приходит по-старому - баг в API
        if (!o.has("id") && o.has("nid"))
            note.nid = o.optLong("nid");

        note.owner_id = o.getLong("owner_id");
        note.title = Api.unescape(o.getString("title"));
        note.ncom = o.optLong("comments");

        //в новости "добавил заметку" заметка приходит по-старому - баг в API
        if (!o.has("comments") && o.has("ncom"))
            note.ncom = o.optLong("ncom");

        //note.read_ncom = o.optLong("read_comments");
        note.text = o.optString("text");
        note.date = o.optLong("date");
        return note;
    }
}

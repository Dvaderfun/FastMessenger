package ru.lischenko_dev.fastmessenger.vkapi.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFeed {
    public ArrayList<NewsItem> items = new ArrayList<NewsItem>();
    public ArrayList<VKFullUser> profiles;
    public ArrayList<VKGroup> groups;
    public int new_offset;
    public String new_from;

    public static NewsFeed parse(JSONObject root, boolean is_comments) throws JSONException {
        JSONObject response1 = root.getJSONObject("response");
        JSONArray jitems = response1.optJSONArray("items");
        JSONArray jprofiles = response1.optJSONArray("profiles");
        JSONArray jgroups = response1.optJSONArray("groups");
        NewsFeed newsfeed = new NewsFeed();
        if (jitems != null) {
            newsfeed.items = new ArrayList<NewsItem>();
            for (int i = 0; i < jitems.length(); i++) {
                JSONObject jitem = (JSONObject) jitems.get(i);
                NewsItem newsitem = NewsItem.parse(jitem, is_comments);
                newsfeed.items.add(newsitem);
            }
        }

        if (jprofiles != null) {
            newsfeed.profiles = new ArrayList<VKFullUser>();
            for (int i = 0; i < jprofiles.length(); i++) {
                JSONObject jprofile = (JSONObject) jprofiles.get(i);
                VKFullUser m = VKFullUser.parseFromNews(jprofile);
                newsfeed.profiles.add(m);
            }
        }
        if (jgroups != null)
            newsfeed.groups = VKGroup.parseGroups(jgroups);
        newsfeed.new_offset = response1.optInt("new_offset");
        newsfeed.new_from = response1.optString("new_from");
        return newsfeed;
    }

    public static NewsFeed parseFromSearch(JSONObject root) throws JSONException {
        JSONObject response1 = root.getJSONObject("response");
        JSONArray jitems = response1.optJSONArray("items");
        JSONArray jprofiles = response1.optJSONArray("profiles");
        JSONArray jgroups = response1.optJSONArray("groups");
        NewsFeed newsfeed = new NewsFeed();
        if (jitems != null) {
            newsfeed.items = new ArrayList<NewsItem>();
            for (int i = 0; i < jitems.length(); i++) {
                JSONObject jitem = (JSONObject) jitems.get(i);
                NewsItem newsitem = NewsItem.parseFromSearch(jitem);
                newsfeed.items.add(newsitem);
            }
        }

        if (jprofiles != null) {
            newsfeed.profiles = new ArrayList<VKFullUser>();
            for (int i = 0; i < jprofiles.length(); i++) {
                JSONObject jprofile = (JSONObject) jprofiles.get(i);
                VKFullUser m = VKFullUser.parseFromNews(jprofile);
                newsfeed.profiles.add(m);
            }
        }
        if (jgroups != null)
            newsfeed.groups = VKGroup.parseGroups(jgroups);
        newsfeed.new_offset = response1.optInt("new_offset");
        newsfeed.new_from = response1.optString("new_from");
        return newsfeed;
    }
}
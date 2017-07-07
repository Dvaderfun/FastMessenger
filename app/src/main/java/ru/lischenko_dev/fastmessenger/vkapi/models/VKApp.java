package ru.lischenko_dev.fastmessenger.vkapi.models;
import java.util.*;
import org.json.*;

public class VKApp {
    public int id;
    public String title;
	
	
	public static ArrayList<VKApp> parseApp(JSONArray array) throws JSONException {
		ArrayList<VKApp> apps = new ArrayList<VKApp>();
        if (array == null)
            return apps;

        for (int i = 0; i < array.length(); ++i) {
            JSONObject o = (JSONObject) array.get(i);
            VKApp app = VKApp.parse(o);
            apps.add(app);
        }
        return apps;
	}
	
	public static VKApp parse(JSONObject o) throws JSONException {
		VKApp app = new VKApp();
		app.id = o.optInt("id");
		app.title = o.optString("title");
		return app;
	}
	
}



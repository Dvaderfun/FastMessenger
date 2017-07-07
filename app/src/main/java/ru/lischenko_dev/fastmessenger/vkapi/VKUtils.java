package ru.lischenko_dev.fastmessenger.vkapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lischenko_dev.fastmessenger.vkapi.models.VKAttachment;
import ru.lischenko_dev.fastmessenger.vkapi.models.VKMessage;

public class VKUtils {

    public static String extractPattern(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (!m.find())
            return null;
        return m.toMatchResult().group(1);
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[2048];
        try {
            for (int n; (n = r.read(buffer)) != -1; )
                sw.write(buffer, 0, n);
        } finally {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.toString();
    }

    public static void closeStream(Object oin) {
        if (oin != null)
            try {
                if (oin instanceof InputStream)
                    ((InputStream) oin).close();
                if (oin instanceof OutputStream)
                    ((OutputStream) oin).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String getAttachmentString(VKMessage message) {
        for (VKAttachment attachment : message.attachments) {
            switch (attachment.type) {
                case "posted_photo":
					return "Пост с фото";
                case "photo":
                    String photo = "Фотограф";
                    if (message.attachments.size() > 1)
                        switch (message.attachments.size()) {
                            case 1:
                                return photo + "ия";
                            case 2:
                            case 3:
                            case 4:
                                return photo + "ии";
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                                return photo + "ий";
                        }
						break;
                case "message":
                    String msg = "сообщен";
                    String repl = "Пересланн";
                    switch (message.attachments.size()) {
                        case 1:
                            return repl + "ое" + " " + msg + "ие";
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            return repl + "ых" + " " + msg + "ий";
                    }
					break;
                case "graffiti":
                    return "Граффити";
                case "geo":
                    return "Местоположение";
                case "link":
                    return "Ссылка";
                case "audio":
                    return "Аудио";
                case "note":
                    return "Заметка";
                case "video":
                    return "Видео";
                case "poll":
                    return "Опрос";
                case "doc":
                    String doc = "Документ";
                    switch (message.attachments.size()) {
                        case 1:
                            return doc;
                        case 2:
                        case 3:
                        case 4:
                            return doc + "a";
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            return doc + "ов";
                    }
					break;
                case "wall":
                    return "Пост";
                case "page":
                    return "Страница";
                case "gift":
                    return "Подарок";
                case "album":
                    return "Альбом";
                case "sticker":
                    return "Стикер";
            }
        }
        return null;
    }
	public static String getStringAttachment(String type) {
		switch(type) {
			case "message":
				return "Forward Message";
			case "graffiti":
				return "Graffiti";
			case "geo":
				return "Geolocation";
			case "link":
				return "Link";
			case "audio":
				return "Audio";
			case "note":
				return "Note";
			case "video":
				return "Video";
			case "poll":
				return "Poll";
			case "doc":
				return "Document";
			case "wall":
				return "Wall Post";
			case "page":
				return "Page";
			case "gift":
				return "Gift";
			case "album":
				return "Album";
			case "sticker":
				return "Sticker";
			case "photo":
				return "Photo";
            case "posted_photo":
				return "Post Photo";
		}

		return null;
	}
	

    private static String pattern_string_profile_id = "^(id)?(\\d{1,10})$";
    private static Pattern pattern_profile_id = Pattern.compile(pattern_string_profile_id);

    public static String parseProfileId(String text) {
        Matcher m = pattern_profile_id.matcher(text);
        if (!m.find())
            return null;
        return m.group(2);
    }
}

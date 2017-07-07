package ru.lischenko_dev.fastmessenger.vkapi.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ru.lischenko_dev.fastmessenger.vkapi.Api;


//Fields are optional. Should be null if not populated
public class VKFullUser implements Serializable {

    public static final String DEFAULT_FIELDS = "counters, photo_id, verified, sex, bdate, city, country, home_town, has_photo, photo_50, photo_100, photo_200_orig, photo_200, photo_400_orig, photo_max, photo_max_orig, online, lists, domain, has_mobile, contacts, site, education, universities, schools, status, last_seen, followers_count, common_count, occupation, nickname, relatives, relation, personal, connections, exports, wall_comments, activities, interests, music, movies, tv, books, games, about, quotes, can_post, can_see_all_posts, can_see_audio, can_write_private_message, can_send_friend_request, is_favorite, is_hidden_from_feed, timezone, screen_name, maiden_name, crop_photo, is_friend, friend_status, career, military, blacklisted, blacklisted_by_me. ";
    private static final long serialVersionUID = 1L;
    public long uid;
    public String first_name = "DELETED";
    public String last_name = "";
    public String nickname;
    public String full_name;
    public Integer sex = null;
    public Boolean online = null;
    public Boolean online_mobile = null;
    public String birthdate; //bdate
    public static final int MALE = 2;
    public static final int FEMALE = 1;
    public static final int NONE = 0;
    /**
     * В запрос надо добваить photo_50
     * URL квадратной фотографии 50х50
     */

    public int online_app;

    public String photo_50;
    /**
     * В запрос надо добавить photo_200
     * URL необрезанной фотографии 200х200
     */
    public String photo_big;        //photo_200_orig
    /**
     * В запрос надо добавить photo_200
     * URL квадратной фотографии 200х200, <b>у некоторых пользователей, которые загружали фотографию давно её нет</b>
     */
    public String photo_200;
    /**
     * <b>В запрос надо добавить photo_100</b>
     * URL квадратной фотографии 100х100
     */
    public String photo_medium_rec;    //photo_100 квадратная
    /**
     * В запрос надо добавить photo_max
     * URL квадратной фотографии максимального формата, <b>у некоторых пользователей, которые загружали фотографию давно её нет</b>
     */
    public String photo_max;
    /**
     * В запрос надо добавить photo_max_orig
     * URL необрезанной фотографии максимального формата
     */
    public String photo_max_orig;    //photo_max_orig обычно квадратная, может быть не у всех
    /**
     * В запрос надо добавить photo_400_orig
     * URL необрезанной фотографии формата 400х400
     */
    public String photo_400_orig;

    public Integer timezone = null;
    public String lists;
    public String domain;
    public Integer rate = null;
    public Integer university = null; //if education
    public String university_name; //if education
    public Integer faculty = null; //if education
    public String faculty_name; //if education
    public Integer graduation = null; //if education
    public Boolean has_mobile = null;
    public String home_phone;
    public String mobile_phone;
    public String status;
    public Integer relation;
    public String friends_list_ids = null;
    public long last_seen;
    public int albums_count;
    public int videos_count;
    public int audios_count;
    public int notes_count;
    public int friends_count;
    public int user_photos_count;
    public int user_videos_count;
    public int followers_count;
    public long invited_by;
    //public int subscriptions_count;
    //public int online_friends_count;
    public String phone;//for getByPhones
    public int groups_count;
    //relation_partner
    public Long relation_partner_id;
    public String relation_partner_first_name;
    public String relation_partner_last_name;

    //new connections fields
    public String twitter;
    public String facebook;
    public String facebook_name;
    public String skype;
    public String livejounal;

    //new additional fields
    public String interests;
    public String movies;
    public String tv;
    public String books;
    public String games;
    public String about;

    public City city;

    public static final VKFullUser EMPTY_USER = new VKFullUser() {

        @Override
        public String toString() {
            return "Empty User";
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VKFullUser vkUser = (VKFullUser) o;

        if (albums_count != vkUser.albums_count) return false;
        if (audios_count != vkUser.audios_count) return false;
        if (followers_count != vkUser.followers_count) return false;
        if (friends_count != vkUser.friends_count) return false;
        if (groups_count != vkUser.groups_count) return false;
        if (invited_by != vkUser.invited_by) return false;
        if (last_seen != vkUser.last_seen) return false;
        if (notes_count != vkUser.notes_count) return false;
        if (uid != vkUser.uid) return false;
        if (user_photos_count != vkUser.user_photos_count) return false;
        if (user_videos_count != vkUser.user_videos_count) return false;
        if (videos_count != vkUser.videos_count) return false;
        if (about != null ? !about.equals(vkUser.about) : vkUser.about != null) return false;
        if (birthdate != null ? !birthdate.equals(vkUser.birthdate) : vkUser.birthdate != null)
            return false;
        if (books != null ? !books.equals(vkUser.books) : vkUser.books != null) return false;
        if (city != null ? !city.equals(vkUser.city) : vkUser.city != null) return false;
        if (country != null ? !country.equals(vkUser.country) : vkUser.country != null)
            return false;
        if (domain != null ? !domain.equals(vkUser.domain) : vkUser.domain != null) return false;
        if (facebook != null ? !facebook.equals(vkUser.facebook) : vkUser.facebook != null)
            return false;
        if (facebook_name != null ? !facebook_name.equals(vkUser.facebook_name) : vkUser.facebook_name != null)
            return false;
        if (faculty != null ? !faculty.equals(vkUser.faculty) : vkUser.faculty != null)
            return false;
        if (faculty_name != null ? !faculty_name.equals(vkUser.faculty_name) : vkUser.faculty_name != null)
            return false;
        if (first_name != null ? !first_name.equals(vkUser.first_name) : vkUser.first_name != null)
            return false;
        if (friends_list_ids != null ? !friends_list_ids.equals(vkUser.friends_list_ids) : vkUser.friends_list_ids != null)
            return false;
        if (games != null ? !games.equals(vkUser.games) : vkUser.games != null) return false;
        if (graduation != null ? !graduation.equals(vkUser.graduation) : vkUser.graduation != null)
            return false;
        if (has_mobile != null ? !has_mobile.equals(vkUser.has_mobile) : vkUser.has_mobile != null)
            return false;
        if (home_phone != null ? !home_phone.equals(vkUser.home_phone) : vkUser.home_phone != null)
            return false;
        if (interests != null ? !interests.equals(vkUser.interests) : vkUser.interests != null)
            return false;
        if (last_name != null ? !last_name.equals(vkUser.last_name) : vkUser.last_name != null)
            return false;
        if (lists != null ? !lists.equals(vkUser.lists) : vkUser.lists != null) return false;
        if (livejounal != null ? !livejounal.equals(vkUser.livejounal) : vkUser.livejounal != null)
            return false;
        if (mobile_phone != null ? !mobile_phone.equals(vkUser.mobile_phone) : vkUser.mobile_phone != null)
            return false;
        if (movies != null ? !movies.equals(vkUser.movies) : vkUser.movies != null) return false;
        if (nickname != null ? !nickname.equals(vkUser.nickname) : vkUser.nickname != null)
            return false;
        if (online != null ? !online.equals(vkUser.online) : vkUser.online != null) return false;
        if (online_mobile != null ? !online_mobile.equals(vkUser.online_mobile) : vkUser.online_mobile != null)
            return false;
        if (phone != null ? !phone.equals(vkUser.phone) : vkUser.phone != null) return false;
        if (photo_50 != null ? !photo_50.equals(vkUser.photo_50) : vkUser.photo_50 != null)
            return false;
        if (photo_200 != null ? !photo_200.equals(vkUser.photo_200) : vkUser.photo_200 != null)
            return false;
        if (photo_400_orig != null ? !photo_400_orig.equals(vkUser.photo_400_orig) : vkUser.photo_400_orig != null)
            return false;
        if (photo_big != null ? !photo_big.equals(vkUser.photo_big) : vkUser.photo_big != null)
            return false;
        if (photo_max != null ? !photo_max.equals(vkUser.photo_max) : vkUser.photo_max != null)
            return false;
        if (photo_max_orig != null ? !photo_max_orig.equals(vkUser.photo_max_orig) : vkUser.photo_max_orig != null)
            return false;
        if (photo_medium_rec != null ? !photo_medium_rec.equals(vkUser.photo_medium_rec) : vkUser.photo_medium_rec != null)
            return false;
        if (rate != null ? !rate.equals(vkUser.rate) : vkUser.rate != null) return false;
        if (relation != null ? !relation.equals(vkUser.relation) : vkUser.relation != null)
            return false;
        if (relation_partner_first_name != null ? !relation_partner_first_name.equals(vkUser.relation_partner_first_name) : vkUser.relation_partner_first_name != null)
            return false;
        if (relation_partner_id != null ? !relation_partner_id.equals(vkUser.relation_partner_id) : vkUser.relation_partner_id != null)
            return false;
        if (relation_partner_last_name != null ? !relation_partner_last_name.equals(vkUser.relation_partner_last_name) : vkUser.relation_partner_last_name != null)
            return false;
        if (sex != null ? !sex.equals(vkUser.sex) : vkUser.sex != null) return false;
        if (skype != null ? !skype.equals(vkUser.skype) : vkUser.skype != null) return false;
        if (status != null ? !status.equals(vkUser.status) : vkUser.status != null) return false;
        if (timezone != null ? !timezone.equals(vkUser.timezone) : vkUser.timezone != null)
            return false;
        if (tv != null ? !tv.equals(vkUser.tv) : vkUser.tv != null) return false;
        if (twitter != null ? !twitter.equals(vkUser.twitter) : vkUser.twitter != null)
            return false;
        if (university != null ? !university.equals(vkUser.university) : vkUser.university != null)
            return false;
        if (university_name != null ? !university_name.equals(vkUser.university_name) : vkUser.university_name != null)
            return false;
        full_name = first_name + " " + last_name;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (uid ^ (uid >>> 32));
        result = 31 * result + (first_name != null ? first_name.hashCode() : 0);
        result = 31 * result + (last_name != null ? last_name.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (online != null ? online.hashCode() : 0);
        result = 31 * result + (online_mobile != null ? online_mobile.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (photo_50 != null ? photo_50.hashCode() : 0);
        result = 31 * result + (photo_big != null ? photo_big.hashCode() : 0);
        result = 31 * result + (photo_200 != null ? photo_200.hashCode() : 0);
        result = 31 * result + (photo_medium_rec != null ? photo_medium_rec.hashCode() : 0);
        result = 31 * result + (photo_max != null ? photo_max.hashCode() : 0);
        result = 31 * result + (photo_max_orig != null ? photo_max_orig.hashCode() : 0);
        result = 31 * result + (photo_400_orig != null ? photo_400_orig.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (lists != null ? lists.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (university != null ? university.hashCode() : 0);
        result = 31 * result + (university_name != null ? university_name.hashCode() : 0);
        result = 31 * result + (faculty != null ? faculty.hashCode() : 0);
        result = 31 * result + (faculty_name != null ? faculty_name.hashCode() : 0);
        result = 31 * result + (graduation != null ? graduation.hashCode() : 0);
        result = 31 * result + (has_mobile != null ? has_mobile.hashCode() : 0);
        result = 31 * result + (home_phone != null ? home_phone.hashCode() : 0);
        result = 31 * result + (mobile_phone != null ? mobile_phone.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (relation != null ? relation.hashCode() : 0);
        result = 31 * result + (friends_list_ids != null ? friends_list_ids.hashCode() : 0);
        result = 31 * result + (int) (last_seen ^ (last_seen >>> 32));
        result = 31 * result + albums_count;
        result = 31 * result + videos_count;
        result = 31 * result + audios_count;
        result = 31 * result + notes_count;
        result = 31 * result + friends_count;
        result = 31 * result + user_photos_count;
        result = 31 * result + user_videos_count;
        result = 31 * result + followers_count;
        result = 31 * result + (int) (invited_by ^ (invited_by >>> 32));
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + groups_count;
        result = 31 * result + (relation_partner_id != null ? relation_partner_id.hashCode() : 0);
        result = 31 * result + (relation_partner_first_name != null ? relation_partner_first_name.hashCode() : 0);
        result = 31 * result + (relation_partner_last_name != null ? relation_partner_last_name.hashCode() : 0);
        result = 31 * result + (twitter != null ? twitter.hashCode() : 0);
        result = 31 * result + (facebook != null ? facebook.hashCode() : 0);
        result = 31 * result + (facebook_name != null ? facebook_name.hashCode() : 0);
        result = 31 * result + (skype != null ? skype.hashCode() : 0);
        result = 31 * result + (livejounal != null ? livejounal.hashCode() : 0);
        result = 31 * result + (interests != null ? interests.hashCode() : 0);
        result = 31 * result + (movies != null ? movies.hashCode() : 0);
        result = 31 * result + (tv != null ? tv.hashCode() : 0);
        result = 31 * result + (books != null ? books.hashCode() : 0);
        result = 31 * result + (games != null ? games.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    public Country country;


    public static VKFullUser parse(JSONObject o) throws JSONException {
        VKFullUser u = new VKFullUser();
        u.uid = Long.parseLong(o.getString("id"));
        if (!o.isNull("first_name"))
            u.first_name = Api.unescape(o.getString("first_name"));
        if (!o.isNull("last_name"))
            u.last_name = Api.unescape(o.getString("last_name"));
        if (!o.isNull("nickname"))
            u.nickname = Api.unescape(o.optString("nickname"));
        if (!o.isNull("screen_name"))
            u.domain = o.optString("screen_name");
        if (!o.isNull("online"))
            u.online = o.optInt("online") == 1;
        if (!o.isNull("online_mobile"))
            u.online_mobile = o.optInt("online_mobile") == 1;
        else
            //if it's not there it means false
            u.online_mobile = false;
        if (!o.isNull("sex"))
            u.sex = o.optInt("sex");
        if (!o.isNull("bdate"))
            u.birthdate = o.optString("bdate");
        if (!o.isNull("city"))
            u.city = City.parse(o.optJSONObject("city"));
        if (o.has("country"))
            u.country = Country.parse(o.optJSONObject("country"));
        if (!o.isNull("timezone"))
            u.timezone = o.optInt("timezone");
        if (!o.isNull("photo_50"))
            u.photo_50 = o.optString("photo_50");
        if (!o.isNull("photo_100"))
            u.photo_medium_rec = o.optString("photo_100");
        if (!o.isNull("photo_200_orig"))
            u.photo_big = o.optString("photo_200_orig");
        if (!o.isNull("photo_200"))
            u.photo_200 = o.optString("photo_200");
        if (!o.isNull("photo_max"))
            u.photo_max = o.optString("photo_max");
        if (!o.isNull("photo_max_orig"))
            u.photo_max_orig = o.optString("photo_max_orig");
        if (!o.isNull("photo_400_orig"))
            u.photo_400_orig = o.optString("photo_400_orig");
        if (!o.isNull("has_mobile"))
            u.has_mobile = o.optInt("has_mobile") == 1;
        if (!o.isNull("home_phone"))
            u.home_phone = o.optString("home_phone");
        if (!o.isNull("mobile_phone"))
            u.mobile_phone = o.optString("mobile_phone");
        if (!o.isNull("rate"))
            u.rate = o.optInt("rate");
        if (o.has("faculty"))
            u.faculty = o.optInt("faculty");
        if (!o.isNull("faculty_name"))
            u.faculty_name = o.optString("faculty_name");
        if (o.has("university"))
            u.university = o.optInt("university");
        if (!o.isNull("university_name"))
            u.university_name = o.optString("university_name");
        if (o.has("graduation"))
            u.graduation = o.optInt("graduation");
        if (!o.isNull("status"))
            u.status = Api.unescape(o.optString("status"));
        if (!o.isNull("relation"))
            u.relation = o.optInt("relation");
        if (!o.isNull("lists")) {
            JSONArray array = o.optJSONArray("lists");
            if (array != null) {
                String ids = "";
                for (int i = 0; i < array.length() - 1; ++i)
                    ids += array.getString(i) + ",";
                ids += array.getString(array.length() - 1);
                u.friends_list_ids = ids;
            }
        }
        if (!o.isNull("last_seen")) {
            JSONObject object = o.optJSONObject("last_seen");
            if (object != null)
                u.last_seen = object.optLong("time");
        }
        if (!o.isNull("counters")) {
            JSONObject object = o.optJSONObject("counters");
            if (object != null) {
                u.albums_count = object.optInt("albums");
                u.videos_count = object.optInt("videos");
                u.audios_count = object.optInt("audios");
                u.notes_count = object.optInt("notes");
                u.friends_count = object.optInt("friends");
                u.user_photos_count = object.optInt("user_photos");
                u.user_videos_count = object.optInt("user_videos");
                //u.online_friends_count = object.optInt("online_friends");
                u.followers_count = object.optInt("followers");
                //u.subscriptions_count = object.optInt("subscriptions");
                u.groups_count = object.optInt("groups");
            }
        }
        if (!o.isNull("relation_partner")) {
            JSONObject object = o.optJSONObject("relation_partner");
            if (object != null) {
                u.relation_partner_id = object.optLong("id");
                u.relation_partner_first_name = object.optString("first_name");
                u.relation_partner_last_name = object.optString("last_name");
            }
        }

        if (!o.isNull("twitter"))
            u.twitter = o.optString("twitter");
        if (!o.isNull("facebook"))
            u.facebook = o.optString("facebook");
        if (!o.isNull("facebook_name"))
            u.facebook_name = o.optString("facebook_name");
        if (!o.isNull("skype"))
            u.skype = o.optString("skype");
        if (!o.isNull("livejounal"))
            u.livejounal = o.optString("livejounal");

        if (!o.isNull("interests"))
            u.interests = o.optString("interests");
        if (!o.isNull("movies"))
            u.movies = o.optString("movies");
        if (!o.isNull("tv"))
            u.tv = o.optString("tv");
        if (!o.isNull("books"))
            u.books = o.optString("books");
        if (!o.isNull("games"))
            u.games = o.optString("games");
        if (!o.isNull("about"))
            u.about = o.optString("about");

        if (!o.isNull("invited_by"))
            u.invited_by = o.optLong("invited_by");
        return u;
    }

    public static VKFullUser parseFromNews(JSONObject jprofile) throws JSONException {
        VKFullUser m = new VKFullUser();
        m.uid = jprofile.getLong("id");
        m.first_name = Api.unescape(jprofile.optString("first_name"));
        m.last_name = Api.unescape(jprofile.optString("last_name"));
        m.photo_50 = jprofile.optString("photo_50");
        m.photo_medium_rec = jprofile.optString("photo_100");
        if (jprofile.has("sex"))
            m.sex = jprofile.optInt("sex");
        if (!jprofile.isNull("online"))
            m.online = jprofile.optInt("online") == 1;
        return m;
    }

    public static VKFullUser parseFromGetByPhones(JSONObject o) throws JSONException {
        VKFullUser u = new VKFullUser();
        u.uid = o.getLong("id");
        u.first_name = Api.unescape(o.optString("first_name"));
        u.last_name = Api.unescape(o.optString("last_name"));
        u.phone = o.optString("phone");
        return u;
    }

    public static ArrayList<VKFullUser> parseUsers(JSONArray array) throws JSONException {
        return parseUsers(array, false);
    }

    public static ArrayList<VKFullUser> parseUsers(JSONArray array, boolean from_notifications) throws JSONException {
        ArrayList<VKFullUser> users = new ArrayList<VKFullUser>();
        //it may be null if no users returned
        //no users may be returned if we request users that are already removed
        if (array == null)
            return users;

        for (int i = 0; i < array.length(); ++i) {
            JSONObject o = (JSONObject) array.get(i);
            VKFullUser u;
            if (from_notifications)
                u = VKFullUser.parseFromNotifications(o);
            else
                u = VKFullUser.parse(o);
            users.add(u);
        }
        return users;
    }

    public static ArrayList<VKFullUser> parseUsersForGetByPhones(JSONArray array) throws JSONException {
        ArrayList<VKFullUser> users = new ArrayList<VKFullUser>();
        //it may be null if no users returned
        //no users may be returned if we request users that are already removed
        if (array == null)
            return users;
        int category_count = array.length();
        for (int i = 0; i < category_count; ++i) {
            if (array.get(i) == null || (!(array.get(i) instanceof JSONObject)))
                continue;
            JSONObject o = (JSONObject) array.get(i);
            VKFullUser u = VKFullUser.parseFromGetByPhones(o);
            users.add(u);
        }
        return users;
    }

    public static VKFullUser parseFromFave(JSONObject jprofile) throws JSONException {
        VKFullUser m = new VKFullUser();
        m.uid = Long.parseLong(jprofile.getString("id"));
        m.first_name = Api.unescape(jprofile.optString("first_name"));
        m.last_name = Api.unescape(jprofile.optString("last_name"));
        m.photo_medium_rec = jprofile.optString("photo_100");
        if (!jprofile.isNull("online"))
            m.online = jprofile.optInt("online") == 1;
        if (!jprofile.isNull("online_mobile"))
            m.online_mobile = jprofile.optInt("online_mobile") == 1;
        else
            //if it's not there it means false
            m.online_mobile = false;
        return m;
    }

    public static VKFullUser parseFromNotifications(JSONObject jprofile) throws JSONException {
        VKFullUser m = new VKFullUser();
        m.uid = jprofile.getLong("id");
        m.first_name = Api.unescape(jprofile.optString("first_name"));
        m.last_name = Api.unescape(jprofile.optString("last_name"));
        m.photo_medium_rec = jprofile.optString("photo_100");
        m.photo_50 = jprofile.optString("photo_50");
        return m;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name;
    }

    public static class LastActivity {
        public boolean online;
        public long last_seen;

        public static LastActivity parse(JSONObject o) {
            LastActivity u = new LastActivity();
            u.online = o.optInt("online") == 1;
            u.last_seen = o.optLong("time");
            return u;
        }
    }
}

package ua.com.qascript.android.constants;

public interface Constants {

    public static final Boolean ADMOB = true;      //true - Show AdMob banner; false - Hide AdMob banner;

    public static final String CLIENT_ID = "1";

    public static final String SENDER_ID = "16864";  //GOOGLE API Sender ID for support gcm messages

    public static final String APP_DESKTOP_SITE = "http://qascript.com.ua/";

    public static final String APP_NAME = "QA Script for Android";
    public static final String APP_VERSION = "1.6";
    public static final String APP_YEAR = "2015";
    public static final String APP_COPYRIGHT = "Demyanchuk Dmitry";

    public static final String API_DOMAIN = "http://qascript.com.ua/";

    public static final String API_VERSION = "v1";

    public static final String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms";
    public static final String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks";

    public static final String METHOD_APP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/app.search";
    public static final String METHOD_APP_CHECKUSERNAME = API_DOMAIN + "api/" + API_VERSION + "/method/app.checkUsername";
    public static final String METHOD_APP_PASSWORD_RECOVERY = API_DOMAIN + "api/" + API_VERSION + "/method/app.passwordRecovery";

    public static final String METHOD_FRIENDS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/friends.get";

    public static final String METHOD_PROFILE_GET = API_DOMAIN + "api/" + API_VERSION + "/method/profile.get";
    public static final String METHOD_PROFILE_ADDFOLLOWER = API_DOMAIN + "api/" + API_VERSION + "/method/profile.addFollower";
    public static final String METHOD_PROFILE_REPORTABUSE = API_DOMAIN + "api/" + API_VERSION + "/method/profile.reportAbuse";

    public static final String METHOD_PROFILE_UPLOADPHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/account.uploadPhoto";
    public static final String METHOD_PROFILE_UPLOADCOVER = API_DOMAIN + "api/" + API_VERSION + "/method/account.uploadCover";
    public static final String METHOD_ACCOUNT_EDIT = API_DOMAIN + "api/" + API_VERSION + "/method/account.edit";
    public static final String METHOD_ACCOUNT_ANONYMOUSQUESTIONS = API_DOMAIN + "api/" + API_VERSION + "/method/account.anonymousQuestions";
    public static final String METHOD_ACCOUNT_SETSTATE = API_DOMAIN + "api/" + API_VERSION + "/method/account.setState";
    public static final String METHOD_ACCOUNT_SETPASSWORD = API_DOMAIN + "api/" + API_VERSION + "/method/account.setPassword";

    public static final String METHOD_HASHTAGS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/hashtags.get";

    public static final String METHOD_QUESTIONS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/questions.get";
    public static final String METHOD_QUESTIONS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/questions.remove";
    public static final String METHOD_QUESTIONS_REPLY = API_DOMAIN + "api/" + API_VERSION + "/method/questions.reply";
    public static final String METHOD_QUESTIONS_ADD = API_DOMAIN + "api/" + API_VERSION + "/method/questions.add";
    public static final String METHOD_QUESTIONS_RANDOM = API_DOMAIN + "api/" + API_VERSION + "/method/questions.random";
    public static final String METHOD_QUESTIONS_UPLOADIMG = API_DOMAIN + "api/" + API_VERSION + "/method/questions.uploadImg";

    public static final String METHOD_ANSWERS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/answers.get";
    public static final String METHOD_ANSWERS_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/answers.like";
    public static final String METHOD_ANSWERS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/answers.remove";

    public static final String METHOD_STREAM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/stream.get";
    public static final String METHOD_FEED_GET = API_DOMAIN + "api/" + API_VERSION + "/method/feed.get";
    public static final String METHOD_WALL_GET = API_DOMAIN + "api/" + API_VERSION + "/method/wall.get";

    public static final String METHOD_NOTIFY_GETLIKES = API_DOMAIN + "api/" + API_VERSION + "/method/notify.getLikes";
    public static final String METHOD_NOTIFY_GETANSWERS = API_DOMAIN + "api/" + API_VERSION + "/method/notify.getAnswers";

    public static final String METHOD_AUTH_SIGNUP = API_DOMAIN + "api/" + API_VERSION + "/method/auth.signUp";
    public static final String METHOD_AUTH_SIGNIN = API_DOMAIN + "api/" + API_VERSION + "/method/auth.signIn";
    public static final String METHOD_AUTH_FACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/auth.facebook";
    public static final String METHOD_AUTH_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/auth.logOut";
    public static final String METHOD_AUTH_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/auth.authorize";

    public static final int LIST_ITEMS = 20;

    public static final int FRIENDS_FIND = 1;
    public static final int QUESTION_ANSWER = 2;
    public static final int QUESTION_SELECT_ANSWER_IMG = 3;

    public static final Integer ERROR_SUCCESS = 0;

    public static final int ACCOUNT_STATE_ENABLED = 0;
    public static final int ACCOUNT_STATE_DISABLED = 1;
    public static final int ACCOUNT_STATE_BLOCKED = 2;
    public static final int ACCOUNT_STATE_DEACTIVATED = 3;

    public static final int GCM_NOTIFY_CONFIG = 0;
    public static final int GCM_NOTIFY_SYSTEM = 1;
    public static final int GCM_NOTIFY_CUSTOM = 2;
    public static final int GCM_NOTIFY_LIKE = 3;
    public static final int GCM_NOTIFY_ANSWER = 4;
    public static final int GCM_NOTIFY_QUESTION = 5;
    public static final int GCM_NOTIFY_COMMENT = 6;
    public static final int GCM_NOTIFY_FOLLOWER = 7;

    public static final int NOTIFY_TYPE_LIKE = 0;
    public static final int NOTIFY_TYPE_ANSWER = 1;

    public static final int ERROR_UNKNOWN = 100;
    public static final int ERROR_ACCESS_TOKEN = 101;

    public static final int ERROR_LOGIN_TAKEN = 300;
    public static final int ERROR_EMAIL_TAKEN = 301;

    public static final int ERROR_ACCOUNT_ID = 400;

    public static final int QUESTION_TYPE_DEFAULT = 0;

    public static final int DISABLE_ANONYMOUS_QUESTIONS = 0;
    public static final int ENABLE_ANONYMOUS_QUESTIONS = 0;

    public static final String TAG = "TAG";

    public static final String HASHTAGS_COLOR = "#5BCFF2";
}
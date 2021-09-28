package com.thuannp.securepass.utils;

public class DefConstant {
    public static final int SPLASH_TIME_OUT = 3000;
    public static final String PREF_NAME = "com.thuannp.SecurePass";
    public static final String PREF_FIRST_RUN = "FIRST_RUN";
    public static final String PREF_DARK = "DARK_THEME";
    public static final String PREF_KEY_REQUIRED = "MASTER_PASSWORD";
    public static final String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    public static final String PREF_HASH = "HASH";
    public static final String EXTRA_PROVIDER_NAME = "com.thuannp.SecurePass.EXTRA_PROVIDER_NAME";
    public static final String EXTRA_PROVIDER = "com.thuannp.SecurePass.EXTRA_PROVIDER";
    public static final String EXTRA_ENCRYPT = "com.thuannp.SecurePass.EXTRA_ENCRYPT";
    public static final String EXTRA_EMAIL = "com.thuannp.SecurePass.EXTRA_EMAIL";
    public static final String EXTRA_IV = "com.thuannp.SecurePass.EXTRA_IV";
    public static final String EXTRA_SALT = "com.thuannp.SecurePass.EXTRA_SALT";
    public static final String EXTRA_DELETE = "DELETE";
    public static final String EXTRA_ID = "com.thuannp.SecurePass.EXTRA_ID";
    public static final String PASSWORD = "ClipData";
    public static final String NO_DATA = "NO_DATA";

    public static final int ADD_RECORD = 1;
    public static final int MODIFY_RECORD = 2;
    public static final int DELETE_RECORD = 3;

    public static final int DO_NOT_EXIST = -1;

    public class PASS_TYPE {
        public static final String TYPE_PASS_PIN = "PIN";
        public static final String TYPE_PASS_TEXT = "PASSWORD";
    }

    public static final String[] providersEmail = {
            "Gmail", "Outlook", "Amazon", "Protonmail", "Yahoo",
            "Apple", "Paypal", "Github", "Spotify", "Stackoverflow",
            "Trello", "Wordpress", "Other"
    };

    public static final String[] providersSocial = {
            "Facebook", "Instagram", "Twitter", "Medium", "Flickr",
            "Foursquare", "Reddit", "Slack", "Snapchat", "Tinder",
            "Linkedin", "Pinterest", "Tumblr", "Other"
    };
}

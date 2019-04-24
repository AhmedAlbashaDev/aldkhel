package com.aldkhel.aldkhel.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Consts {

    private static final String BASE_URL = "https://www.fahdaldobian.com/";
    public static final String API_URL = BASE_URL + "store_new/index.php?route=";
    public static final String BASE_IMAGE = "http://fahdaldobian.com/store_new/image/";
    public static final String APP_FONT = "fonts/jana.ttf";
    public static final String API_SESSION_KEY = "X-Oc-Session";
    public static final String API_KEY = "X-Oc-Merchant-Id";
    public static final String API_KEY_VALUE = "w0QZ6opIb3NYkSFB7MSkavQBT5UHOzYW";

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

}

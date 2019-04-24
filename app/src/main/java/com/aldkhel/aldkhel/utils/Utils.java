package com.aldkhel.aldkhel.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.aldkhel.aldkhel.models.User;

import org.json.JSONException;
import org.json.JSONObject;

final public class Utils {

    public static void saveUser(Context context, User user) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString("user", user.toJson().toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static User loadUser(Context context) {
        String json = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("user", "");

        try {
            return User.fromJson(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isUserLoggedIn(Context context) {
        return loadUser(context) != null;
    }

    public static String getSessionId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("session", "");
    }


    public static void saveSession(Context context, String session) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("session", session)
                .apply();
    }

}

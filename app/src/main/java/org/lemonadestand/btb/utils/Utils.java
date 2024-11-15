package org.lemonadestand.btb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.lemonadestand.btb.features.common.models.UserListModel;
import org.lemonadestand.btb.features.login.models.User;

public class Utils {

    public static final String FILENAME = "PREFERENCES_FILE";
    public static final String TOKEN = "token";
    public static final String USERID = "userID";
    public static final String UID = "unique_id";
    public static final String ORG_ID= "org_id";
    public static final String PICTURE= "picture";
    public static final String ORG_PICTURE= "org_picture";
    public static final String USER_NAME= "user_name";
    public static final String USER_OBJECT= "user_object";
    public static final String USER_INTEREST= "user_interest";
    public static final String USER_EVENT= "user_event";
    public static final String SELECTED_USER_ID_EVENT= "SELECTED_USER_ID";
    public static final String SELECTED_USER_ID_INTEREST= "SELECTED_USER_ID_INTEREST";

    public static void saveData(Context context, String key, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(key, null);
        return data;
    }

    public static User getUser(Context context) {
        try {
            String data = getData(context, USER_OBJECT);
            Gson gson = new Gson();
            return gson.fromJson(data, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new User();
    }


    public static void saveUser(Context context, User value) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(value);
            saveData(context, USER_OBJECT, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUserModelInterest(Context context, UserListModel value) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(value);
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_INTEREST, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveUserModelEvent(Context context, UserListModel value) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(value);
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_EVENT, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUserIDEvent(Context context, String value) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SELECTED_USER_ID_EVENT, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveUserIDInterest(Context context, String value) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SELECTED_USER_ID_INTEREST, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getUserIdInterest(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(SELECTED_USER_ID_INTEREST, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserIdEvent(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(SELECTED_USER_ID_EVENT, null);
        } catch (Exception e) {
            return null;
        }
    }
    public static UserListModel getInterestUser(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
            String data = sharedPreferences.getString(USER_INTEREST, null);
            Gson gson = new Gson();
            return gson.fromJson(data, UserListModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static UserListModel getResource(Context context) {
        try {
            String data = getData(context, USER_EVENT);
            Gson gson = new Gson();
            return gson.fromJson(data, UserListModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setResource(Context context, UserListModel value) {
        try {
            String data = new Gson().toJson(value);
            saveData(context, USER_EVENT, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);

    }
}

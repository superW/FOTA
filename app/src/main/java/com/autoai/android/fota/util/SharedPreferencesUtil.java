package com.autoai.android.fota.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chenyf on 2017/7/20.
 */

public class SharedPreferencesUtil {

    private static String name = "fota";

    public static void SharedPreferencesSave_Boolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //用putString的方法保存数据
        editor.putBoolean(key, value);
        //提交当前数据
        editor.apply();
    }

    public static void SharedPreferencesSave_String(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString(key, value);
        //提交当前数据
        editor.apply();
    }

    public static void SharedPreferencesSave_int(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //用putString的方法保存数据
        editor.putInt(key, value);
        //提交当前数据
        editor.apply();
    }
    public static void SharedPreferencesSave_long(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //用putString的方法保存数据
        editor.putLong(key, value);
        //提交当前数据
        editor.apply();
    }


    public static boolean SharedPreferencesSelect_Boolean(Context context, String key, boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static String SharedPreferencesSelect_String(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }

    public static int SharedPreferencesSelect_int(Context context, String key, int defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defValue);
    }
    public static long SharedPreferencesSelect_long(Context context, String key, long defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defValue);
    }


}

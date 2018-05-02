package com.acube.autoresponder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.acube.autoresponder.Config;

/**
 * Created by Anns on 12/04/18.
 */

public class SharedPreferenceUtils {
    public static void setStringData(Context context,String name,String path)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Config.MyPREFERENCES, Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(name, path);
        editor.apply();
    }
    public static String getStringData(Context context,String name)
    {
        SharedPreferences myPrefs;
        myPrefs = context.getSharedPreferences(Config.MyPREFERENCES, Context.MODE_PRIVATE);
        return myPrefs.getString(name, "");
    }
    public static void setIntData(Context context,String name,int value)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Config.MyPREFERENCES, Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }
    public static int getIntData(Context context,String name)
    {
        SharedPreferences myPrefs;
        myPrefs = context.getSharedPreferences(Config.MyPREFERENCES, Context.MODE_PRIVATE);
        return myPrefs.getInt(name, 0);
    }
}

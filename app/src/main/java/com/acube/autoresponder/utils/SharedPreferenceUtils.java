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
}

package com.wisdomin.studentcard.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by dugaolong on 19/5/17.
 */

public class PreferencesUtils {

    private static PreferencesUtils instance;

    public static SharedPreferences sharedPreferences;
    /**
     * sharedPreferences文件名称
     */
    public static final String fileName = "zhihui";//老师所带班级列表
    public static final String OLD_GPS_LA = "old_gps_la";//旧的纬度
    public static final String OLD_GPS_LO = "old_gps_lo";//旧的经度

    public static final String NEW_GPS_LA = "new_gps_la";//新的纬度
    public static final String NEW_GPS_LO = "new_gps_lo";//新的经度

    public synchronized static PreferencesUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    private PreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void setBoolean(final String key,
                           final boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(final String key,
                              final boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }


    public void setString(final String key,
                          final String value) {
        sharedPreferences.edit().putString(key, value).apply();

    }

    public String getString(String key,
                            final String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setInt(final String key,
                       final int value) {

        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(final String key,
                      final int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setLong(final String key,
                        final long value) {

        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(final String key,
                        final long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void setStringSet(final String key,
                             final Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    public Set<String> getStringSet(final String key,
                                    final Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }


}

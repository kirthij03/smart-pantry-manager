package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and writes the user's settings. SharedPreferences stores small
 * key-value pairs on the device, and they survive the app being closed.
 */
public class AppPreferences {

    public static final int SORT_NAME = 0;
    public static final int SORT_EXPIRY = 1;

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";
    private static final String KEY_SORT_ORDER = "sort_order";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Defaults to true, so expiring items are highlighted until the user turns it off. */
    public static boolean isHighlightExpiring(Context context) {
        return prefs(context).getBoolean(KEY_HIGHLIGHT_EXPIRING, true);
    }

    public static void setHighlightExpiring(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, enabled).apply();
    }

    public static int getSortOrder(Context context) {
        return prefs(context).getInt(KEY_SORT_ORDER, SORT_NAME);
    }

    public static void setSortOrder(Context context, int sortOrder) {
        prefs(context).edit().putInt(KEY_SORT_ORDER, sortOrder).apply();
    }
}
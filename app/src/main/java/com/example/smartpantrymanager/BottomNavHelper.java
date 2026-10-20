package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Wires up the bottom navigation bar so every screen shares the same behaviour.
 * Each tab opens its screen with an explicit Intent.
 */
public class BottomNavHelper {

    public static void setup(Activity activity, BottomNavigationView nav, int currentItemId) {
        // Highlight the tab for the screen we are on (before the listener exists)
        nav.setSelectedItemId(currentItemId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentItemId) {
                return true; // already on this screen
            }

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else if (id == R.id.nav_settings) {
                target = SettingsActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(activity, target);
            // Reuse the screen if it is already in the back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0); // no slide animation between tabs

            // Pantry is the home screen. Every other tab closes itself when you leave it,
            // so Back always returns to Pantry.
            if (!(activity instanceof MainActivity)) {
                activity.finish();
            }

            // false keeps this screen's own tab highlighted; the new screen highlights its own
            return false;
        });
    }
}
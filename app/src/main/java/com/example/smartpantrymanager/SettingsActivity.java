package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Settings screen. Each change is saved straight away with SharedPreferences.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SwitchMaterial switchHighlight = findViewById(R.id.switch_highlight_expiring);
        RadioGroup rgSort = findViewById(R.id.rg_sort_order);
        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        // Show the saved values first, then attach the listeners
        switchHighlight.setChecked(AppPreferences.isHighlightExpiring(this));
        rgSort.check(AppPreferences.getSortOrder(this) == AppPreferences.SORT_EXPIRY
                ? R.id.rb_sort_expiry : R.id.rb_sort_name);

        switchHighlight.setOnCheckedChangeListener((button, isChecked) ->
                AppPreferences.setHighlightExpiring(this, isChecked));

        rgSort.setOnCheckedChangeListener((group, checkedId) ->
                AppPreferences.setSortOrder(this, checkedId == R.id.rb_sort_expiry
                        ? AppPreferences.SORT_EXPIRY : AppPreferences.SORT_NAME));

        BottomNavHelper.setup(this, nav, R.id.nav_settings);
    }
}
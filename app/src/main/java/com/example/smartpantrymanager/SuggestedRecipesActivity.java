package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Suggested Recipes screen. Runs the strict-matching rule against the current
 * pantry. Recipes that pass are listed first; recipes missing exactly one
 * ingredient are listed separately as "Almost there".
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private PantryDatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        // Bottom padding is left to the bottom navigation bar, which handles it itself
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        dbHelper = new PantryDatabaseHelper(this);

        rvRecipes = findViewById(R.id.rv_recipes);
        tvEmpty = findViewById(R.id.tv_empty_recipes);
        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        adapter = new RecipeAdapter(this);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        BottomNavHelper.setup(this, nav, R.id.nav_recipes);
    }

    /** Re-runs the matching every time the screen appears, so it reflects pantry changes. */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void loadSuggestions() {
        List<Pantry> pantry = dbHelper.getAllPantryItems();
        List<Recipe> recipes = dbHelper.getAllRecipes();

        // The strict-matching rule lives in RecipeMatcher, which returns two separate lists
        RecipeMatcher.MatchResult result = RecipeMatcher.match(pantry, recipes);
        List<Recipe> suggested = result.getSuggested();
        List<RecipeMatcher.AlmostRecipe> almostThere = result.getAlmostThere();
        adapter.setData(this, suggested, almostThere);

        // Show a helpful message instead of a blank screen when there is nothing to list
        boolean nothingToShow = suggested.isEmpty() && almostThere.isEmpty();
        tvEmpty.setVisibility(nothingToShow ? View.VISIBLE : View.GONE);
        rvRecipes.setVisibility(nothingToShow ? View.GONE : View.VISIBLE);
    }

    /** Opens the detail screen for the tapped recipe, passing its id as an Intent extra. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
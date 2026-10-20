package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Recipe Detail screen: shows the ingredients and the method for one recipe.
 * The recipe id arrives as an Intent extra.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    /** Intent extra key: the id of the recipe to show. */
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    private static final long NO_ID = -1;

    private PantryDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PantryDatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tv_recipe_title);
        TextView tvIngredients = findViewById(R.id.tv_recipe_ingredients);
        TextView tvMethod = findViewById(R.id.tv_recipe_method);

        // Read the Intent extra, then load that recipe from the database
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, NO_ID);
        Recipe recipe = (recipeId == NO_ID) ? null : dbHelper.getRecipe(recipeId);
        if (recipe == null) {
            Toast.makeText(this, R.string.msg_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(recipe.getName());

        StringBuilder ingredientText = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredientText.length() > 0) {
                ingredientText.append("\n");
            }
            ingredientText.append(getString(R.string.ingredient_line,
                    capitalize(ingredient.getName()),
                    formatQuantity(ingredient.getRequiredQty()),
                    ingredient.getUnit()));
        }
        tvIngredients.setText(ingredientText.toString());
        tvMethod.setText(recipe.getInstructions());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    /** Shows 3 instead of 3.0 for whole numbers. */
    private String formatQuantity(double qty) {
        return (qty == Math.floor(qty)) ? String.valueOf((long) qty) : String.valueOf(qty);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * The built-in recipe collection. Each ingredient row is {name, quantity, unit}.
 * Names are written in the singular; the matcher also copes with plurals.
 */
public class RecipeSeeder {

    public static void seed(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs on Toast",
                "1. Whisk the eggs in a bowl.\n2. Melt the butter in a pan over medium heat.\n3. Pour in the eggs and stir gently until just set.\n4. Toast the bread and serve the eggs on top.",
                new String[][]{{"egg", "3", "units"}, {"bread", "2", "units"}, {"butter", "10", "g"}});

        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta in salted water until tender, then drain.\n2. Chop the tomatoes and garlic.\n3. Heat the oil, fry the garlic for 1 minute, then add the tomatoes.\n4. Simmer for 10 minutes, then toss with the pasta.",
                new String[][]{{"pasta", "200", "g"}, {"tomato", "4", "units"}, {"garlic", "2", "units"}, {"olive oil", "2", "tbsp"}});

        addRecipe(db, "Garlic Fried Rice",
                "1. Crush and chop the garlic.\n2. Fry the garlic in a hot pan for 30 seconds.\n3. Push it aside, scramble the eggs in the pan, then stir in the rice.\n4. Add the soy sauce and fry for 3 minutes.",
                new String[][]{{"rice", "200", "g"}, {"egg", "2", "units"}, {"garlic", "2", "units"}, {"soy sauce", "2", "tbsp"}});

        addRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs in a bowl.\n2. Melt the butter in a pan over medium heat.\n3. Pour in the eggs and cook until nearly set.\n4. Sprinkle over the cheese, fold in half and serve.",
                new String[][]{{"egg", "3", "units"}, {"cheese", "50", "g"}, {"butter", "10", "g"}});

        addRecipe(db, "Classic Pancakes",
                "1. Whisk the flour, milk and egg into a smooth batter.\n2. Melt a little butter in a frying pan.\n3. Pour in a small ladle of batter and cook for 2 minutes each side.\n4. Repeat with the remaining batter and serve warm.",
                new String[][]{{"flour", "150", "g"}, {"milk", "250", "ml"}, {"egg", "1", "units"}, {"butter", "20", "g"}});

        addRecipe(db, "Creamy Mashed Potatoes",
                "1. Peel and chop the potatoes.\n2. Boil for 15 to 20 minutes until soft, then drain.\n3. Add the butter and milk.\n4. Mash until smooth.",
                new String[][]{{"potato", "4", "units"}, {"butter", "30", "g"}, {"milk", "100", "ml"}});

        addRecipe(db, "Tomato Soup",
                "1. Chop the onion, garlic and tomatoes.\n2. Soften the onion and garlic in a pot for 5 minutes.\n3. Add the tomatoes and stock and simmer for 20 minutes.\n4. Blend until smooth.",
                new String[][]{{"tomato", "6", "units"}, {"onion", "1", "units"}, {"garlic", "2", "units"}, {"vegetable stock", "500", "ml"}});

        addRecipe(db, "Cheese Toastie",
                "1. Butter the outside of both slices of bread.\n2. Place the cheese between the slices.\n3. Fry in a pan for 2 to 3 minutes each side until golden.",
                new String[][]{{"bread", "2", "units"}, {"cheese", "60", "g"}, {"butter", "10", "g"}});

        addRecipe(db, "Banana Oat Pancakes",
                "1. Mash the bananas in a bowl.\n2. Mix in the eggs and oats.\n3. Cook spoonfuls in a lightly oiled pan for 2 minutes each side.",
                new String[][]{{"banana", "2", "units"}, {"egg", "2", "units"}, {"oat", "60", "g"}});

        addRecipe(db, "Creamy Mushroom Pasta",
                "1. Boil the pasta until tender, then drain.\n2. Slice the mushrooms and chop the garlic.\n3. Fry the mushrooms and garlic for 6 minutes.\n4. Stir in the cream, simmer for 2 minutes and toss with the pasta.",
                new String[][]{{"pasta", "200", "g"}, {"mushroom", "150", "g"}, {"cream", "100", "ml"}, {"garlic", "2", "units"}});

        addRecipe(db, "Chicken Stir Fry",
                "1. Cook the rice according to the packet.\n2. Slice the chicken and peppers.\n3. Fry the chicken for 6 minutes, then add the peppers.\n4. Add the soy sauce, cook for 3 minutes and serve over the rice.",
                new String[][]{{"chicken breast", "300", "g"}, {"bell pepper", "2", "units"}, {"soy sauce", "3", "tbsp"}, {"rice", "200", "g"}});

        addRecipe(db, "Lentil Soup",
                "1. Chop the onion and carrots.\n2. Soften them in a pot for 5 minutes.\n3. Add the lentils and stock.\n4. Simmer for 25 minutes until the lentils are soft.",
                new String[][]{{"lentil", "200", "g"}, {"onion", "1", "units"}, {"carrot", "2", "units"}, {"vegetable stock", "750", "ml"}});

        addRecipe(db, "Bean and Cheese Burrito",
                "1. Warm the beans in a pan and lightly mash them.\n2. Spoon the beans and cheese onto the tortillas.\n3. Roll them up and toast in a dry pan for 2 minutes each side.",
                new String[][]{{"tortilla", "2", "units"}, {"black bean", "200", "g"}, {"cheese", "50", "g"}});

        addRecipe(db, "Roasted Vegetables",
                "1. Heat the oven to 200 degrees Celsius.\n2. Chop the potatoes, carrots and onion into chunks.\n3. Toss with the oil on a baking tray.\n4. Roast for 35 minutes, turning once.",
                new String[][]{{"potato", "3", "units"}, {"carrot", "3", "units"}, {"onion", "1", "units"}, {"olive oil", "2", "tbsp"}});

        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta until tender, then drain.\n2. Chop the tomatoes and warm them in the oil for 5 minutes.\n3. Stir in the drained tuna.\n4. Toss with the pasta.",
                new String[][]{{"pasta", "200", "g"}, {"tuna", "1", "units"}, {"tomato", "2", "units"}, {"olive oil", "1", "tbsp"}});

        addRecipe(db, "French Toast",
                "1. Whisk the eggs and milk in a shallow dish.\n2. Dip each slice of bread so it soaks up the mixture.\n3. Fry in the butter for 2 minutes each side until golden.",
                new String[][]{{"bread", "4", "units"}, {"egg", "2", "units"}, {"milk", "100", "ml"}, {"butter", "20", "g"}});

        addRecipe(db, "Yogurt Oat Bowl",
                "1. Spoon the yogurt into a bowl.\n2. Slice the banana on top.\n3. Sprinkle over the oats and serve.",
                new String[][]{{"yogurt", "200", "g"}, {"oat", "40", "g"}, {"banana", "1", "units"}});

        addRecipe(db, "Guacamole",
                "1. Halve the avocados and scoop the flesh into a bowl.\n2. Mash with a fork.\n3. Dice the tomato and stir it in.\n4. Squeeze in the lime juice and mix.",
                new String[][]{{"avocado", "2", "units"}, {"lime", "1", "units"}, {"tomato", "1", "units"}});
    }

    /** Inserts one recipe row and then one row per ingredient. */
    private static void addRecipe(SQLiteDatabase db, String name, String instructions,
                                  String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(PantryDatabaseHelper.COL_RECIPE_NAME, name);
        recipeValues.put(PantryDatabaseHelper.COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(PantryDatabaseHelper.TABLE_RECIPE, null, recipeValues);

        for (String[] row : ingredients) {
            ContentValues values = new ContentValues();
            values.put(PantryDatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            values.put(PantryDatabaseHelper.COL_RI_INGREDIENT_NAME, row[0]);
            values.put(PantryDatabaseHelper.COL_RI_REQUIRED_QTY, Double.parseDouble(row[1]));
            values.put(PantryDatabaseHelper.COL_RI_UNIT, row[2]);
            db.insert(PantryDatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }
}
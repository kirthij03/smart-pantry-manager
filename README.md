# Smart Pantry Manager

A Java Android app that helps reduce food waste. Users track the ingredients they already have at home, and the app suggests recipes they can cook using **only** what is in their pantry. No shopping trip is needed.

**Module:** Mobile App Development 700, Richfield Graduate Institute of Technology
**Author:** [Kirthi Jagdoe] ([402307335])

## Features

- **Pantry management:** add, edit and delete ingredients (name, quantity, unit and an optional expiry date), with input validation.
- **Recipe collection:** 18 recipes are loaded into the database the first time the app runs.
- **Suggested Recipes:** lists only recipes for which every ingredient is in the pantry in at least the required quantity. If nothing matches, a helpful message is shown instead of a blank screen.
- **Recipe Detail:** shows the full ingredient list and the method for a recipe.
- **Settings:** highlight items that are expiring soon or expired, and choose the pantry sort order. Settings are saved on the device.
- **Bottom navigation** between Pantry, Recipes and Settings.

## The strict-matching rule

A recipe is suggested only if **every** ingredient it needs is in the pantry in at least the required quantity. A recipe missing even one ingredient is excluded. Matching copes with everyday messiness:

- Letter case and extra spaces are ignored.
- Singular and plural names match ("tomato" and "tomatoes").
- Units are converted within the same family, so 500 g equals 0.5 kg and 3 tsp equals 1 tbsp.
- Weights, volumes and counts are never compared with each other.
- Several pantry entries for the same ingredient are added together.

The logic lives in `RecipeMatcher.java`.

## Database choice: SQLite

The app uses **SQLite** through `SQLiteOpenHelper`, stored locally on the device.

- It matches the persistent-data approach taught in the module.
- It works fully offline, so the pantry is always available.
- It needs no server, account or hosting costs.
- Relational tables suit the recipe-to-ingredient relationship.

The three tables are `pantry_table`, `recipe_table` and `recipe_ingredients_table`, which links each recipe to its ingredients.

## Tech stack

- Java (no Kotlin)
- Android Studio
- Minimum SDK: API 23 (Android 6.0)
- SQLite, RecyclerView with custom adapters, Intents, SharedPreferences, Material Components

## Setup and run

1. Install **Android Studio** and a **JDK 17** (the one bundled with Android Studio works).
2. Clone the repository:
   `git clone https://github.com/kirthij03/smart-pantry-manager.git`
3. In Android Studio choose **File > Open** and select the cloned folder.
4. Wait for the Gradle sync to finish.
5. Create an emulator in **Device Manager** (for example a Pixel 6 with API 34), or connect a phone with USB debugging turned on.
6. Press **Run**.

## Project structure 

| File | Purpose |
|---|---|
| `MainActivity` | Pantry list screen |
| `AddEditIngredientActivity` | Add and edit form with validation |
| `SuggestedRecipesActivity` | Recipes that pass the strict-matching rule |
| `RecipeDetailActivity` | Ingredients and method for one recipe |
| `SettingsActivity` | User preferences |
| `PantryDatabaseHelper` | Database creation and CRUD methods |
| `RecipeSeeder` | The built-in recipe collection |
| `RecipeMatcher` | The strict-matching logic |
| `PantryAdapter`, `RecipeAdapter` | RecyclerView adapters |
| `AppPreferences` | Saved settings |
| `BottomNavHelper` | Shared bottom navigation |

## Out of scope

The app does not use maps, GPS or any location services.
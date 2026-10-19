package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The strict-matching rule. A recipe is only "suggested" if EVERY ingredient it
 * needs is in the pantry in at least the required quantity.
 *
 * To cope with real-world messiness it:
 *  - ignores letter case and extra spaces
 *  - treats plurals as the same word ("tomato" and "tomatoes")
 *  - converts units inside the same family (500 g equals 0.5 kg, 3 tsp equals 1 tbsp)
 *  - adds up duplicate pantry entries of the same ingredient
 *
 * This class has no Android code in it, so it is easy to explain and test.
 */
public class RecipeMatcher {

    private static final double TOLERANCE = 0.000001;

    /** A recipe that is missing exactly one ingredient (bonus "Almost There" list). */
    public static class AlmostRecipe {
        private final Recipe recipe;
        private final String missingIngredient;

        public AlmostRecipe(Recipe recipe, String missingIngredient) {
            this.recipe = recipe;
            this.missingIngredient = missingIngredient;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public String getMissingIngredient() {
            return missingIngredient;
        }
    }

    /** The two kept-separate results of a match run. */
    public static class MatchResult {
        private final List<Recipe> suggested = new ArrayList<>();
        private final List<AlmostRecipe> almostThere = new ArrayList<>();

        public List<Recipe> getSuggested() {
            return suggested;
        }

        public List<AlmostRecipe> getAlmostThere() {
            return almostThere;
        }
    }

    /** Runs the strict-matching rule for every recipe against the current pantry. */
    public static MatchResult match(List<Pantry> pantry, List<Recipe> recipes) {
        Map<String, Double> available = buildAvailable(pantry);
        MatchResult result = new MatchResult();

        for (Recipe recipe : recipes) {
            if (recipe.getIngredients().isEmpty()) {
                continue; // a recipe with no ingredients can never be suggested
            }

            List<String> unmet = new ArrayList<>();
            for (RecipeIngredient need : recipe.getIngredients()) {
                Double have = available.get(makeKey(need.getName(), need.getUnit()));
                double required = toBaseUnit(need.getRequiredQty(), need.getUnit());
                if (have == null || have + TOLERANCE < required) {
                    unmet.add(need.getName());
                }
            }

            if (unmet.isEmpty()) {
                result.suggested.add(recipe);
            } else if (unmet.size() == 1) {
                result.almostThere.add(new AlmostRecipe(recipe, unmet.get(0)));
            }
            // two or more missing ingredients: excluded from both lists
        }
        return result;
    }

    /**
     * Totals the pantry per ingredient and unit family, in base units
     * (g for weight, ml for volume, units for counts).
     */
    private static Map<String, Double> buildAvailable(List<Pantry> pantry) {
        Map<String, Double> available = new HashMap<>();
        for (Pantry item : pantry) {
            String key = makeKey(item.getIngredientName(), item.getUnit());
            double amount = toBaseUnit(item.getQuantity(), item.getUnit());
            Double existing = available.get(key);
            available.put(key, existing == null ? amount : existing + amount);
        }
        return available;
    }

    /** Combines the cleaned-up name with the unit family, for example "tomato|count". */
    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + unitFamily(unit);
    }

    /** Lower-cases, trims, collapses spaces and strips simple plural endings. */
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return singularize(s);
    }

    private static String singularize(String s) {
        if (s.length() <= 3) {
            return s;
        }
        if (s.length() > 4 && s.endsWith("ies")) {          // berries -> berry
            return s.substring(0, s.length() - 3) + "y";
        }
        if (s.endsWith("oes")) {                             // tomatoes -> tomato
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("ches") || s.endsWith("shes")
                || s.endsWith("sses") || s.endsWith("xes")) { // radishes -> radish
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("ss") || s.endsWith("us") || s.endsWith("is")) {
            return s;                                        // hummus, asparagus stay as they are
        }
        if (s.endsWith("s")) {                               // eggs -> egg
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    /** Weight, volume and count units can only be compared inside their own family. */
    private static String unitFamily(String unit) {
        String u = cleanUnit(unit);
        switch (u) {
            case "g":
            case "kg":
                return "mass";
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return "volume";
            case "unit":
            case "units":
                return "count";
            default:
                return u;
        }
    }

    /** Converts a quantity to the base unit of its family (g, ml or units). */
    private static double toBaseUnit(double qty, String unit) {
        switch (cleanUnit(unit)) {
            case "kg":
                return qty * 1000;
            case "l":
                return qty * 1000;
            case "tsp":
                return qty * 5;
            case "tbsp":
                return qty * 15;
            case "cup":
                return qty * 240;
            default:
                return qty; // g, ml and units are already base units
        }
    }

    private static String cleanUnit(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }
}
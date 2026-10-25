package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Local unit tests for the strict-matching rule. They run on the computer in
 * a fraction of a second and need no emulator, because RecipeMatcher contains
 * no Android code.
 */
public class RecipeMatcherTest {

    // ---- helpers that keep each test short ----

    private static Pantry pantryItem(String name, double qty, String unit) {
        return new Pantry(1, name, qty, unit, null);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(1, name, "Test method");
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }

    private static RecipeIngredient need(String name, double qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    private static RecipeMatcher.MatchResult run(List<Pantry> pantry, Recipe recipe) {
        return RecipeMatcher.match(pantry, Collections.singletonList(recipe));
    }

    // ---- the strict rule itself ----

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        Recipe omelette = recipe("Omelette", need("egg", 3, "units"), need("cheese", 50, "g"));
        List<Pantry> pantry = Arrays.asList(
                pantryItem("egg", 3, "units"),      // exactly the required amount
                pantryItem("cheese", 50, "g"));

        RecipeMatcher.MatchResult result = run(pantry, omelette);

        assertEquals(1, result.getSuggested().size());
        assertTrue(result.getAlmostThere().isEmpty());
    }

    @Test
    public void oneIngredientMissing_notSuggested_butListedAsAlmostThere() {
        Recipe omelette = recipe("Omelette",
                need("egg", 3, "units"), need("cheese", 50, "g"), need("butter", 10, "g"));
        List<Pantry> pantry = Arrays.asList(
                pantryItem("egg", 3, "units"),
                pantryItem("cheese", 50, "g"));

        RecipeMatcher.MatchResult result = run(pantry, omelette);

        assertTrue(result.getSuggested().isEmpty());
        assertEquals(1, result.getAlmostThere().size());
        assertEquals("butter", result.getAlmostThere().get(0).getMissingIngredient());
    }

    @Test
    public void twoIngredientsMissing_excludedFromBothLists() {
        Recipe omelette = recipe("Omelette",
                need("egg", 3, "units"), need("cheese", 50, "g"), need("butter", 10, "g"));
        List<Pantry> pantry = Collections.singletonList(pantryItem("egg", 3, "units"));

        RecipeMatcher.MatchResult result = run(pantry, omelette);

        assertTrue(result.getSuggested().isEmpty());
        assertTrue(result.getAlmostThere().isEmpty());
    }

    @Test
    public void quantityTooSmall_countsAsMissing() {
        Recipe pancakes = recipe("Pancakes", need("flour", 150, "g"));

        // 149 g is just below the 150 g required
        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("flour", 149, "g")), pancakes);

        assertTrue(result.getSuggested().isEmpty());
        assertEquals(1, result.getAlmostThere().size());
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        Recipe omelette = recipe("Omelette", need("egg", 3, "units"), need("cheese", 50, "g"));

        RecipeMatcher.MatchResult result = run(Collections.<Pantry>emptyList(), omelette);

        assertTrue(result.getSuggested().isEmpty());
        assertTrue(result.getAlmostThere().isEmpty());
    }

    @Test
    public void recipeWithNoIngredients_isNeverSuggested() {
        Recipe empty = recipe("Empty");

        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("egg", 1, "units")), empty);

        assertTrue(result.getSuggested().isEmpty());
        assertTrue(result.getAlmostThere().isEmpty());
    }

    // ---- robustness to everyday messiness ----

    @Test
    public void pluralAndSingularNamesMatch() {
        Recipe pasta = recipe("Tomato Pasta", need("tomato", 4, "units"));

        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("Tomatoes", 4, "units")), pasta);

        assertEquals(1, result.getSuggested().size());
    }

    @Test
    public void caseAndExtraSpacesAreIgnored() {
        Recipe toast = recipe("Egg Toast", need("egg", 2, "units"));

        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("  EGG  ", 2, "units")), toast);

        assertEquals(1, result.getSuggested().size());
    }

    @Test
    public void kilogramsConvertToGrams() {
        Recipe pancakes = recipe("Pancakes", need("flour", 500, "g"));

        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("flour", 0.5, "kg")), pancakes);

        assertEquals(1, result.getSuggested().size());
    }

    @Test
    public void teaspoonsConvertToTablespoons() {
        Recipe dressing = recipe("Dressing", need("olive oil", 1, "tbsp"));

        // 3 tsp is the same volume as 1 tbsp
        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("olive oil", 3, "tsp")), dressing);

        assertEquals(1, result.getSuggested().size());
    }

    @Test
    public void differentUnitFamiliesAreNeverCompared() {
        Recipe toast = recipe("Egg Toast", need("egg", 3, "units"));

        // 3 g of egg is a weight, not three eggs
        RecipeMatcher.MatchResult result = run(
                Collections.singletonList(pantryItem("egg", 3, "g")), toast);

        assertTrue(result.getSuggested().isEmpty());
    }

    @Test
    public void duplicatePantryEntriesAreAddedTogether() {
        Recipe pancakes = recipe("Pancakes", need("flour", 150, "g"));
        List<Pantry> pantry = Arrays.asList(
                pantryItem("flour", 100, "g"),
                pantryItem("flour", 100, "g"));

        RecipeMatcher.MatchResult result = run(pantry, pancakes);

        assertEquals(1, result.getSuggested().size());
    }

    // ---- name normalisation ----

    @Test
    public void normalizeName_handlesCommonPlurals() {
        assertEquals("tomato", RecipeMatcher.normalizeName("Tomatoes"));
        assertEquals("potato", RecipeMatcher.normalizeName("potatoes"));
        assertEquals("egg", RecipeMatcher.normalizeName(" eggs "));
        assertEquals("berry", RecipeMatcher.normalizeName("berries"));
    }

    @Test
    public void normalizeName_keepsWordsThatAreNotPlural() {
        assertEquals("hummus", RecipeMatcher.normalizeName("hummus"));
        assertEquals("cheese", RecipeMatcher.normalizeName("cheese"));
        assertEquals("glass", RecipeMatcher.normalizeName("glass"));
    }

    @Test
    public void normalizeName_nullBecomesEmptyText() {
        assertEquals("", RecipeMatcher.normalizeName(null));
    }
}
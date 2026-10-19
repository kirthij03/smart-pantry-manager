package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

/** A recipe with its method and the list of ingredients it needs. */
public class Recipe {

    private final long id;
    private final String name;
    private final String instructions;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }
}
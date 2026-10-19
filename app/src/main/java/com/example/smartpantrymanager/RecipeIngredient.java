package com.example.smartpantrymanager;

/** One ingredient line of a recipe, for example "tomato, 4, units". */
public class RecipeIngredient {

    private final String name;
    private final double requiredQty;
    private final String unit;

    public RecipeIngredient(String name, double requiredQty, String unit) {
        this.name = name;
        this.requiredQty = requiredQty;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getRequiredQty() {
        return requiredQty;
    }

    public String getUnit() {
        return unit;
    }
}
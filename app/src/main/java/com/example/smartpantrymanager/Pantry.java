package com.example.smartpantrymanager;

public class Pantry {

    private long id;
    private String ingredientName;
    private double quantity;
    private String unit;
    private String expirationDate; // can be null/empty

    public Pantry(long id, String ingredientName, double quantity, String unit, String expirationDate) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
    }

    public long getId() {
        return id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
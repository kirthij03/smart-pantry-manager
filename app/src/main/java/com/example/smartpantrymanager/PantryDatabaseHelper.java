package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    // Bump this number if you ever change the table structure later
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartpantry.db";

    // Table names
    public static final String TABLE_PANTRY = "pantry_table";
    public static final String TABLE_RECIPE = "recipe_table";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients_table";

    // Pantry columns
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "ingredient_name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiration_date";

    // Recipe columns
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe_ingredients columns
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_RI_REQUIRED_QTY = "required_qty";
    public static final String COL_RI_UNIT = "unit";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                COL_PANTRY_EXPIRY + " TEXT)";

        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPE + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";

        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COL_RI_REQUIRED_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPE + "(" + COL_RECIPE_ID + "))";

        db.execSQL(createPantryTable);
        db.execSQL(createRecipeTable);
        db.execSQL(createRecipeIngredientsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simplest approach for coursework: drop and recreate.
        // (In a production app you'd migrate data instead of dropping it.)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }
}
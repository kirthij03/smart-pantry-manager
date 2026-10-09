package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the SQLite database and provides CRUD methods for the pantry.
 */
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
        // (In a production app you would migrate the data instead of dropping it.)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // PANTRY CRUD
    // ------------------------------------------------------------------

    /**
     * CREATE: adds a new ingredient to the pantry.
     * @return the new row id, or -1 if the insert failed
     */
    public long insertPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name);
        values.put(COL_PANTRY_QTY, quantity);
        values.put(COL_PANTRY_UNIT, unit);
        values.put(COL_PANTRY_EXPIRY, expiryDate); // null is allowed (optional field)
        return db.insert(TABLE_PANTRY, null, values);
    }

    /**
     * READ: returns every ingredient in the pantry, sorted A to Z by name.
     */
    public List<Pantry> getAllPantryItems() {
        List<Pantry> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_PANTRY_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                items.add(cursorToPantry(cursor));
            }
        } finally {
            cursor.close(); // always close cursors to avoid memory leaks
        }
        return items;
    }

    /**
     * READ: returns a single ingredient by id, or null if it doesn't exist.
     */
    public Pantry getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursorToPantry(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * UPDATE: changes an existing ingredient.
     * @return the number of rows updated (1 on success, 0 if the id wasn't found)
     */
    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name);
        values.put(COL_PANTRY_QTY, quantity);
        values.put(COL_PANTRY_UNIT, unit);
        values.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * DELETE: removes an ingredient.
     * @return the number of rows deleted (1 on success, 0 if the id wasn't found)
     */
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /** Converts the current Cursor row into a Pantry object. */
    private Pantry cursorToPantry(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME));
        double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT));
        String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
        return new Pantry(id, name, qty, unit, expiry);
    }
}
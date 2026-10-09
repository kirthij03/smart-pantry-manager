package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * One screen used for both adding a new ingredient and editing an existing one.
 * If the launching Intent carries EXTRA_PANTRY_ID, the screen is in edit mode.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** Intent extra key: the id of the ingredient being edited. */
    public static final String EXTRA_PANTRY_ID = "extra_pantry_id";
    private static final long NO_ID = -1;

    private static final int MAX_NAME_LENGTH = 50;
    private static final double MAX_QUANTITY = 100000;

    private PantryDatabaseHelper dbHelper;

    private TextInputLayout tilName;
    private TextInputLayout tilQuantity;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiry;
    private Spinner spinnerUnit;
    private ArrayAdapter<CharSequence> unitAdapter;

    private long editingId = NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        // Keep the form clear of the status bar and navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.form_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PantryDatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tv_form_title);
        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiry = findViewById(R.id.et_expiry);
        spinnerUnit = findViewById(R.id.spinner_unit);
        Button btnClearDate = findViewById(R.id.btn_clear_date);
        Button btnSave = findViewById(R.id.btn_save);

        // Fill the unit dropdown from the string-array in strings.xml
        unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.unit_options, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // Read the Intent extra to decide between add mode and edit mode
        editingId = getIntent().getLongExtra(EXTRA_PANTRY_ID, NO_ID);
        if (editingId == NO_ID) {
            tvTitle.setText(R.string.add_ingredient);
        } else {
            tvTitle.setText(R.string.edit_ingredient);
            // Only load from the database on a fresh start. After a screen rotation
            // Android restores what the user already typed, so we must not overwrite it.
            if (savedInstanceState == null) {
                loadExistingItem();
            }
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveIngredient());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    /** Edit mode: fills the form with the saved values. */
    private void loadExistingItem() {
        Pantry item = dbHelper.getPantryItem(editingId);
        if (item == null) {
            Toast.makeText(this, R.string.msg_item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        etName.setText(item.getIngredientName());
        etQuantity.setText(formatQuantity(item.getQuantity()));
        selectUnit(item.getUnit());
        String expiry = item.getExpirationDate();
        if (expiry != null) {
            etExpiry.setText(expiry);
        }
    }

    /** Selects the matching unit in the dropdown. */
    private void selectUnit(String unit) {
        for (int i = 0; i < unitAdapter.getCount(); i++) {
            if (unitAdapter.getItem(i).toString().equals(unit)) {
                spinnerUnit.setSelection(i);
                return;
            }
        }
    }

    /** Shows 3 instead of 3.0 for whole numbers. */
    private String formatQuantity(double qty) {
        return (qty == Math.floor(qty)) ? String.valueOf((long) qty) : String.valueOf(qty);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // If a date is already set, open the picker on that date
        String existing = getText(etExpiry);
        if (!existing.isEmpty()) {
            String[] parts = existing.split("-");
            if (parts.length == 3) {
                try {
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1; // Calendar months start at 0
                    day = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {
                    // Fall back to today's date
                }
            }
        }

        new DatePickerDialog(this, (view, y, m, d) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                year, month, day).show();
    }

    /** Validates the form. Only saves to the database if every check passes. */
    private void saveIngredient() {
        tilName.setError(null);
        tilQuantity.setError(null);

        String name = getText(etName);
        String quantityText = getText(etQuantity);
        boolean valid = true;

        // Name checks
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() > MAX_NAME_LENGTH) {
            tilName.setError(getString(R.string.error_name_too_long));
            valid = false;
        }

        // Quantity checks: empty, not a number, zero or negative, too large
        double quantity = 0;
        if (quantityText.isEmpty()) {
            tilQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (Double.isNaN(quantity) || Double.isInfinite(quantity)) {
                    tilQuantity.setError(getString(R.string.error_quantity_invalid));
                    valid = false;
                } else if (quantity <= 0) {
                    tilQuantity.setError(getString(R.string.error_quantity_positive));
                    valid = false;
                } else if (quantity > MAX_QUANTITY) {
                    tilQuantity.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        if (!valid) {
            return; // stop here, the error messages are already showing
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = getText(etExpiry);
        String expiryToSave = expiry.isEmpty() ? null : expiry; // expiry is optional

        boolean success;
        if (editingId == NO_ID) {
            success = dbHelper.insertPantryItem(name, quantity, unit, expiryToSave) != -1;
        } else {
            success = dbHelper.updatePantryItem(editingId, name, quantity, unit, expiryToSave) > 0;
        }

        if (success) {
            Toast.makeText(this,
                    editingId == NO_ID ? R.string.msg_ingredient_added : R.string.msg_ingredient_updated,
                    Toast.LENGTH_SHORT).show();
            finish(); // go back to the pantry list
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** Returns the trimmed text of an input field, never null. */
    private String getText(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }
}
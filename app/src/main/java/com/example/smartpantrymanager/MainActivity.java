package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantry List screen: shows every ingredient in a RecyclerView and lets the
 * user add, edit and delete them.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryActionListener {

    private PantryDatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView rvPantry;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PantryDatabaseHelper(this);

        rvPantry = findViewById(R.id.rv_pantry_list);
        tvEmpty = findViewById(R.id.tv_empty_pantry);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_ingredient);

        // The adapter starts empty; onResume() fills it from the database
        adapter = new PantryAdapter(new ArrayList<>(), this);
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        rvPantry.setAdapter(adapter);

        // Explicit Intent: open the Add/Edit screen in add mode (no extras)
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    /**
     * Runs every time this screen becomes visible, including when the user comes
     * back from the Add/Edit screen. Reloading here keeps the list up to date.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    /** Reads the pantry from the database and shows either the list or the empty message. */
    private void loadPantry() {
        List<Pantry> items = dbHelper.getAllPantryItems();
        adapter.setItems(items);

        boolean isEmpty = items.isEmpty();
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /** Called by the adapter when the edit button on a card is tapped. */
    @Override
    public void onEdit(Pantry item) {
        // Explicit Intent carrying the ingredient id as an extra
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_PANTRY_ID, item.getId());
        startActivity(intent);
    }

    /** Called by the adapter when the delete button is tapped. Asks for confirmation first. */
    @Override
    public void onDelete(Pantry item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, item.getIngredientName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int rowsDeleted = dbHelper.deletePantryItem(item.getId());
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, R.string.msg_ingredient_deleted, Toast.LENGTH_SHORT).show();
                        loadPantry();
                    } else {
                        Toast.makeText(this, R.string.msg_delete_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
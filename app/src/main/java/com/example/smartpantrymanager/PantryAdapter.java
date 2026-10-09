package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Connects a list of Pantry objects to the RecyclerView.
 * Each Pantry becomes one card, built from item_pantry.xml.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** The Activity implements this so the adapter can report button taps. */
    public interface OnPantryActionListener {
        void onEdit(Pantry item);
        void onDelete(Pantry item);
    }

    private List<Pantry> pantryItems;
    private final OnPantryActionListener listener;

    public PantryAdapter(List<Pantry> pantryItems, OnPantryActionListener listener) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    /** Called when the list changes (after add, edit or delete). */
    public void setItems(List<Pantry> newItems) {
        this.pantryItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card layout once per ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Pantry item = pantryItems.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(item.getIngredientName());

        // Show 3 instead of 3.0 when the quantity is a whole number
        double qty = item.getQuantity();
        String qtyText = (qty == Math.floor(qty))
                ? String.valueOf((long) qty)
                : String.valueOf(qty);
        holder.tvQuantityUnit.setText(
                context.getString(R.string.quantity_unit_format, qtyText, item.getUnit()));

        // Expiry date is optional, so handle null and empty
        String expiry = item.getExpirationDate();
        if (expiry == null || expiry.trim().isEmpty()) {
            holder.tvExpiry.setText(R.string.no_expiry);
        } else {
            holder.tvExpiry.setText(context.getString(R.string.expires_format, expiry));
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    /** Holds references to the views inside one card so they aren't looked up repeatedly. */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantityUnit, tvExpiry;
        ImageButton btnEdit, btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantityUnit = itemView.findViewById(R.id.tv_quantity_unit);
            tvExpiry = itemView.findViewById(R.id.tv_expiry_date);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
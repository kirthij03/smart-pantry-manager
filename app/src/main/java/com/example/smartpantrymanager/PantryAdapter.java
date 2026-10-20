package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private static final int EXPIRING_SOON_DAYS = 3;
    private static final double MILLIS_PER_DAY = 24 * 60 * 60 * 1000.0;

    private List<Pantry> pantryItems;
    private final OnPantryActionListener listener;
    private boolean highlightExpiring = true;

    public PantryAdapter(List<Pantry> pantryItems, OnPantryActionListener listener) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    /** Turns the expiry colours on or off (controlled by the Settings screen). */
    public void setHighlightExpiring(boolean enabled) {
        this.highlightExpiring = enabled;
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

        // Cards are reused while scrolling, so the colours are reset on every bind
        int barColor = ContextCompat.getColor(context, R.color.primary_green);
        int dateColor = ContextCompat.getColor(context, R.color.text_secondary);

        // Expiry date is optional, so handle null and empty
        String expiry = item.getExpirationDate();
        if (expiry == null || expiry.trim().isEmpty()) {
            holder.tvExpiry.setText(R.string.no_expiry);
        } else {
            Long daysLeft = highlightExpiring ? daysUntil(expiry) : null;
            if (daysLeft != null && daysLeft < 0) {
                // Already expired: red
                holder.tvExpiry.setText(context.getString(R.string.expired_format, expiry));
                barColor = ContextCompat.getColor(context, R.color.expiry_warning);
                dateColor = barColor;
            } else {
                holder.tvExpiry.setText(context.getString(R.string.expires_format, expiry));
                if (daysLeft != null && daysLeft <= EXPIRING_SOON_DAYS) {
                    // Expiring within 3 days: orange
                    barColor = ContextCompat.getColor(context, R.color.accent_orange);
                    dateColor = barColor;
                }
            }
        }
        holder.viewIndicator.setBackgroundColor(barColor);
        holder.tvExpiry.setTextColor(dateColor);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    /**
     * Whole days from today until the date (yyyy-MM-dd). Negative means it has
     * already expired. Returns null if the text is not a valid date.
     */
    private static Long daysUntil(String isoDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date target = format.parse(isoDate);
            if (target == null) {
                return null;
            }
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            double diff = target.getTime() - today.getTimeInMillis();
            return Math.round(diff / MILLIS_PER_DAY);
        } catch (ParseException e) {
            return null;
        }
    }

    /** Holds references to the views inside one card so they aren't looked up repeatedly. */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantityUnit, tvExpiry;
        ImageButton btnEdit, btnDelete;
        View viewIndicator;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantityUnit = itemView.findViewById(R.id.tv_quantity_unit);
            tvExpiry = itemView.findViewById(R.id.tv_expiry_date);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            viewIndicator = itemView.findViewById(R.id.view_expiry_indicator);
        }
    }
}
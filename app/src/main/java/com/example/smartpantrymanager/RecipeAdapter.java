package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the Recipes screen list. It mixes three kinds of row in one RecyclerView:
 * section headers, recipe cards and an information message.
 * The strict matches and the "Almost There" recipes sit under separate headers.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** The Activity implements this so the adapter can report taps on a recipe card. */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECIPE = 1;
    private static final int TYPE_INFO = 2;

    /** One line of the list. */
    private static class Row {
        final int type;
        final String text;        // used by header and info rows
        final Recipe recipe;      // used by recipe rows
        final String stillNeeded; // null for a full match, the missing ingredient otherwise

        Row(int type, String text, Recipe recipe, String stillNeeded) {
            this.type = type;
            this.text = text;
            this.recipe = recipe;
            this.stillNeeded = stillNeeded;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    /**
     * Rebuilds the list from the matcher's two separate results.
     * If both are empty the list stays empty and the screen shows its own message.
     */
    public void setData(Context context, List<Recipe> suggested,
                        List<RecipeMatcher.AlmostRecipe> almostThere) {
        rows.clear();

        if (!suggested.isEmpty() || !almostThere.isEmpty()) {
            rows.add(new Row(TYPE_HEADER, context.getString(R.string.section_ready), null, null));
            if (suggested.isEmpty()) {
                rows.add(new Row(TYPE_INFO, context.getString(R.string.no_recipes_message), null, null));
            } else {
                for (Recipe recipe : suggested) {
                    rows.add(new Row(TYPE_RECIPE, null, recipe, null));
                }
            }

            if (!almostThere.isEmpty()) {
                rows.add(new Row(TYPE_HEADER, context.getString(R.string.section_almost), null, null));
                for (RecipeMatcher.AlmostRecipe almost : almostThere) {
                    rows.add(new Row(TYPE_RECIPE, null, almost.getRecipe(),
                            almost.getMissingIngredient()));
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false));
        } else if (viewType == TYPE_INFO) {
            return new InfoViewHolder(inflater.inflate(R.layout.item_info_row, parent, false));
        }
        return new RecipeViewHolder(inflater.inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(row.text);

        } else if (holder instanceof InfoViewHolder) {
            ((InfoViewHolder) holder).tvInfo.setText(row.text);

        } else if (holder instanceof RecipeViewHolder) {
            RecipeViewHolder h = (RecipeViewHolder) holder;
            Context context = h.itemView.getContext();
            h.tvName.setText(row.recipe.getName());

            // Cards are reused while scrolling, so both branches set the bar colour
            if (row.stillNeeded == null) {
                int count = row.recipe.getIngredients().size();
                h.tvSummary.setText(context.getResources()
                        .getQuantityString(R.plurals.recipe_summary, count, count));
                h.indicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.primary_green));
            } else {
                h.tvSummary.setText(context.getString(
                        R.string.recipe_needed_format, capitalize(row.stillNeeded)));
                h.indicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.accent_orange));
            }

            h.itemView.setOnClickListener(v -> listener.onRecipeClick(row.recipe));
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_section_header);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        final TextView tvInfo;

        InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tv_info);
        }
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvSummary;
        final View indicator;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_recipe_name);
            tvSummary = itemView.findViewById(R.id.tv_recipe_summary);
            indicator = itemView.findViewById(R.id.view_recipe_indicator);
        }
    }
}
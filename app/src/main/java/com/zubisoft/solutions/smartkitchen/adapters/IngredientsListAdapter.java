package com.zubisoft.solutions.smartkitchen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zubisoft.solutions.smartkitchen.R;
import com.zubisoft.solutions.smartkitchen.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientViewHolder> {

    private List<Ingredient> list = new ArrayList<>();

    public IngredientsListAdapter(List<Ingredient> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_list_item, viewGroup, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder ingredientViewHolder, int i) {

        Ingredient ingredient = list.get(i);
        if (ingredient != null) {
            ingredientViewHolder.qty.setText(ingredient.getQuantity() + " " + ingredient.getMetrics());
            ingredientViewHolder.name.setText(ingredient.getName());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView qty;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
        }
    }
}

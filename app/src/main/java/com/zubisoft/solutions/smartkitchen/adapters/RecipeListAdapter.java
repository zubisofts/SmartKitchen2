package com.zubisoft.solutions.smartkitchen.adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.zubisoft.solutions.smartkitchen.R;
import com.zubisoft.solutions.smartkitchen.model.Recipe;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeItemHolder> {

    private List<Recipe> recipes;
    private OnRecipeItemClickListener mListener;

    public RecipeListAdapter(List<Recipe> recpeData, OnRecipeItemClickListener listener) {
        this.recipes = recpeData;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecipeItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_list_item2, viewGroup, false);
        return new RecipeItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeItemHolder recipeItemHolder, int i) {

        final Recipe data = recipes.get(i);
        if (data != null) {
            recipeItemHolder.name.setText(data.getRecipeName());
//        recipeItemHolder.duaration.setText(data.getDuration()+" mins");
            recipeItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data != null)
                        mListener.onRecipeItemClicked(data, recipeItemHolder.imageView);
                }
            });
            loadRecipeImage(data, recipeItemHolder.imageView);
        }

//        recipeItemHolder.cardView.animate()..alpha(1.0f).setDuration(500).start();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(recipeItemHolder.cardView, View.ALPHA, 0.0f, 1f);
        fadeIn.setDuration(500).start();
    }

    private void loadRecipeImage(Recipe recipe, ImageView imageView) {

        Glide.with(imageView.getContext())
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
//        return recipes.size() > 0 ? recipes.size() : 12;
    }

    public interface OnRecipeItemClickListener {
        void onRecipeItemClicked(Recipe recipe, ImageView recipeItemHolder);
    }

    public class RecipeItemHolder extends RecyclerView.ViewHolder {

        //        private TextView likes;
//        private TextView duaration;
        public ImageView imageView;
        private MaterialCardView cardView;
        private TextView name;

        public RecipeItemHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
//            likes=itemView.findViewById(R.id.likes_count);
//            duaration=itemView.findViewById(R.id.duration);
            imageView = itemView.findViewById(R.id.recipe_image);
            cardView = itemView.findViewById(R.id.card);

        }
    }

}

package com.zubisoft.solutions.smartkitchen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcgdv.asia.lib.dots.DotsProgressIndicator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zubisoft.solutions.smartkitchen.adapters.RecipeItemAdapter;
import com.zubisoft.solutions.smartkitchen.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment implements RecipeItemAdapter.OnRecipeItemClickListener {

    private static final String RECIPE_ID = "recipe_id";
    private RecyclerView recipeList;
    private FirebaseFirestore db;
    private TextView txtNoData;
    private DotsProgressIndicator loader;

    public RecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        txtNoData = view.findViewById(R.id.empty_data);
        loader = view.findViewById(R.id.loder);

        txtNoData.setVisibility(View.GONE);

        recipeList = view.findViewById(R.id.recipe_list);
        recipeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        showRecipes();

        return view;
    }

    private void showRecipes() {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loader.setVisibility(View.GONE);
                            List<Recipe> data = new ArrayList<>();
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                Recipe recipe = snapshot.toObject(Recipe.class);
                                recipe.setId(snapshot.getId());
                                if (recipe != null)
//                                    Toast.makeText(getActivity(), recipeData.getDescription(), Toast.LENGTH_SHORT).show();
                                    data.add(recipe);
                            }

                            RecipeItemAdapter adapter = new RecipeItemAdapter(data, RecipesFragment.this);
                            recipeList.setAdapter(adapter);

                        } else {
                            loader.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onRecipeItemClicked(Recipe recipe, ImageView imageView) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, recipe.getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair[] pairs = {new Pair(imageView, "recipe_image")};

            ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
            startActivity(intent, transition.toBundle());
        } else {

            startActivity(new Intent(getActivity(), RecipeDetailActivity.class));
        }

        //startActivity(intent);
    }

}

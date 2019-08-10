package com.zubisoft.solutions.smartkitchen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zubisoft.solutions.smartkitchen.adapters.IngredientsListAdapter;
import com.zubisoft.solutions.smartkitchen.adapters.StepsListItemAdapter;
import com.zubisoft.solutions.smartkitchen.model.CookStep;
import com.zubisoft.solutions.smartkitchen.model.Ingredient;
import com.zubisoft.solutions.smartkitchen.model.Recipe;
import com.zubisoft.solutions.smartkitchen.model.RecipeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private static String RECIPE_ID = "recipe_id";
    private FirebaseFirestore db;
    private List<CookStep> stepList = new ArrayList<>();
    private TextView txtDesc;
    private TextView txtDuration;
    private TextToSpeech textToSpeech;
    private Recipe data;
    private Toolbar toolbar;
    private Menu menu;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView recipeImageView;
    private List<CookStep> cookSteps = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private RecipeData recipeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBarLight();
        setContentView(R.layout.activity_recipe_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipeImageView = findViewById(R.id.image);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(0.8f);
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtDesc.getText().toString();
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i < -272) {
                    resolveMenu(true);
                } else {
                    resolveMenu(false);
                }
            }

        });

        // Start tutor button
        RelativeLayout btnStart = findViewById(R.id.btn_start_tutor);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TutorStepsActivity.class);
                if (recipeData != null && !recipeData.getSteps().isEmpty()) {
                    intent.putExtra("recipe_data", recipeData);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }

            }
        });

//         Watch video button
        RelativeLayout btnWatchVideo = findViewById(R.id.btn_watch_video);
        btnWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        db = FirebaseFirestore.getInstance();
        setData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        this.menu = menu;
        return true;
    }

    private void setIngredients(String id) {
        ingredients = new ArrayList<>();
        final RecyclerView recyclerView = this.findViewById(R.id.ingredients_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        db.collection("recipes")
                .document(id)
                .collection("ingredients")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            Ingredient ingredient = snapshot.toObject(Ingredient.class);
//                            ingredient.setId(snapshot.getId());
                            if (ingredient != null)
//                                    Toast.makeText(getActivity(), recipeData.getDescription(), Toast.LENGTH_SHORT).show();
                                ingredients.add(ingredient);
                        }
                        IngredientsListAdapter adapter = new IngredientsListAdapter(ingredients);
                        recyclerView.setAdapter(adapter);

                        recipeData.setIngredients(ingredients);
                    }
                });
    }


    private void setSteps(String id) {
        cookSteps = new ArrayList<>();
        final RecyclerView recyclerView = this.findViewById(R.id.steps_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        db.collection("recipes")
                .document(id)
                .collection("steps")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            CookStep cookStep = snapshot.toObject(CookStep.class);
//                            ingredient.setId(snapshot.getId());
                            if (cookStep != null)
//                                    Toast.makeText(getActivity(), recipeData.getDescription(), Toast.LENGTH_SHORT).show();
                                cookSteps.add(cookStep);
                        }
                        StepsListItemAdapter adapter = new StepsListItemAdapter(cookSteps);
                        recyclerView.setAdapter(adapter);

                        recipeData.setSteps(cookSteps);

                    }
                });
    }

    private void setData() {
        final String key = getIntent().getStringExtra(RECIPE_ID);
        txtDesc = findViewById(R.id.txtDescription);
        txtDuration = findViewById(R.id.duration);

        recipeData = new RecipeData();

        db.collection("recipes")
                .document(key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            data = snapshot.toObject(Recipe.class);
                            setIngredients(key);
                            setSteps(key);
                            txtDesc.setText(data.getDescription());
                            txtDuration.setText(data.getDuration() + " mins");
                            collapsingToolbarLayout.setTitle(data.getRecipeName());

                            recipeData.setRecipe(data);

                            if (data.getImageUrl() != null) {
                                Glide.with(getApplicationContext())
                                        .load(data.getImageUrl())
                                        .placeholder(R.drawable.image_placeholder)
                                        .crossFade()
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(recipeImageView);

                            }
                        }
                    }
                });
    }

    private void setStatusBarLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    private void resolveMenu(boolean light) {

        if (menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_share);
            menuItem.setIcon(!light ? R.drawable.ic_share_white : R.drawable.ic_share);
        }

        toolbar.setNavigationIcon(!light ? R.drawable.ic_arrow_white : R.drawable.ic_arrow_back_black);
        toolbar.setOverflowIcon(!light ? getResources().getDrawable(R.drawable.ic_menu_overflow_white) : getResources().getDrawable(R.drawable.ic_menu_overflow_black));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);

    }
}

package com.zubisoft.solutions.smartkitchen.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;
import com.zubisoft.solutions.smartkitchen.IngredientsFragment;
import com.zubisoft.solutions.smartkitchen.RecipeFragment;
import com.zubisoft.solutions.smartkitchen.StepsFragment;

import java.util.ArrayList;

public class AddRecipeStepAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "position";
    ArrayList<Step> fragments = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();

    public AddRecipeStepAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {

        switch (position) {
            case 0:
                final RecipeFragment step1 = (RecipeFragment) fragments.get(position);
                Bundle b = new Bundle();
                b.putInt(CURRENT_STEP_POSITION_KEY, position);
                step1.setArguments(b);
                return step1;
            case 1:
                final IngredientsFragment step2 = (IngredientsFragment) fragments.get(position);
                Bundle b2 = new Bundle();
                b2.putInt(CURRENT_STEP_POSITION_KEY, position);
                step2.setArguments(b2);
                return step2;
            case 2:
                final StepsFragment step3 = (StepsFragment) fragments.get(position);
                Bundle b3 = new Bundle();
                b3.putInt(CURRENT_STEP_POSITION_KEY, position);
                step3.setArguments(b3);
                return step3;
        }

        return null;


    }

    @Override
    public int getCount() {
        return titles.size();
    }

    public void addStep(Step step, String title) {
        fragments.add(step);
        titles.add(title);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(int position) {
        return new StepViewModel.Builder(context)
                .setTitle(titles.get(position)) //can be a CharSequence instead
                .setSubtitle("Please fill out every field below")
                .setBackButtonLabel("PREVIOUS")
                .create();
    }
}

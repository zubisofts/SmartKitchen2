package com.zubisoft.solutions.smartkitchen.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;
import com.zubisoft.solutions.smartkitchen.TutorStepFragment;
import com.zubisoft.solutions.smartkitchen.model.CookStep;

import java.util.List;

public class TutorStepAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "position";
    private List<CookStep> steps;

    public TutorStepAdapter(@NonNull FragmentManager fm, @NonNull Context context, List<CookStep> steps) {
        super(fm, context);

        this.steps = steps;
    }

    @Override
    public Step createStep(int position) {
        final TutorStepFragment step = new TutorStepFragment();
        Bundle b = new Bundle();
        b.putSerializable("step", steps.get(position));
        b.putInt(CURRENT_STEP_POSITION_KEY, position);
        step.setArguments(b);
        return step;
    }

    @Override
    public int getCount() {
        return steps.size();
    }


    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        return new StepViewModel.Builder(context)
                .setTitle("Step " + steps.get(position).getStepNo()) //can be a CharSequence instead
                .create();
    }
}

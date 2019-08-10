package com.zubisoft.solutions.smartkitchen;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zubisoft.solutions.smartkitchen.model.Ingredient;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientsFragment extends BaseStepFragment implements BlockingStep {


    private TextInputEditText name;
    private TextInputEditText qty;
    private Spinner metrics;

    private List<Ingredient> ingredients = new ArrayList<Ingredient>();

    public IngredientsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        name = view.findViewById(R.id.name);
        qty = view.findViewById(R.id.qty);
        metrics = view.findViewById(R.id.metrics);

        Button btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(qty.getText().toString()) || TextUtils.isEmpty(metrics.getSelectedItem().toString())) {
                    Toast.makeText(getActivity(), "All inputs must be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Ingredient ingredient = new Ingredient(name.getText().toString(), Integer.valueOf(qty.getText().toString()), metrics.getSelectedItem().toString());
                ingredients.add(ingredient);
            }
        });

        return view;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        //HashMap<String, String> mapData=new HashMap<>();
        //mapData.put("name", name.getText().toString());
        //mapData.put("quantity", qty.getText().toString());
        //mapData.put("metrics", metrics.getSelectedItem().toString());

        if (ingredients.isEmpty()) {
            Toast.makeText(getActivity(), "You must add at least one ingredient!", Toast.LENGTH_SHORT).show();
            return;
        }
        stepClickListener.onNextClicked(ingredients);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }


}

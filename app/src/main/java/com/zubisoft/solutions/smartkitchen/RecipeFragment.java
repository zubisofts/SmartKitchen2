package com.zubisoft.solutions.smartkitchen;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zubisoft.solutions.smartkitchen.model.Recipe;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends BaseStepFragment implements BlockingStep {


    public TextView txtName;
    public TextView txtTime;
    public Spinner spinCat;
    private NewRecipeActivity activity;
    private TextView txtDesc;

    private Button btnPickImage;
    private ImageSourceListener imagePickSourceListener;

    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        txtName = view.findViewById(R.id.recipe_name);
        txtTime = view.findViewById(R.id.time);
        spinCat = view.findViewById(R.id.category);
        txtDesc = view.findViewById(R.id.txtDescription);
        btnPickImage = view.findViewById(R.id.btn_image);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageSource();
            }
        });

        return view;
    }


    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

       /* HashMap<String, String> mapData=new HashMap<>();
        mapData.put("name", txtName.getText().toString());
        mapData.put("time", txtTime.getText().toString());
        mapData.put("category", spinCat.getSelectedItem().toString());
       */

        if (TextUtils.isEmpty(txtName.getText().toString()) ||
                TextUtils.isEmpty(txtTime.getText().toString()) ||
                spinCat.getSelectedItemPosition() == 0 ||
                TextUtils.isEmpty(txtDesc.getText().toString())) {
            Toast.makeText(getActivity(), "All inputs must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }
        String imageUrl = "";
        imageUrl = ((NewRecipeActivity) getActivity()).getImageResultUri() != null ? ((NewRecipeActivity) getActivity()).getImageResultUri().getPath() : "";
        Recipe recipe = new Recipe(txtName.getText().toString(), spinCat.getSelectedItem().toString(), txtDesc.getText().toString(), Integer.valueOf(txtTime.getText().toString()), imageUrl);
        stepClickListener.onNextClicked(recipe);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
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

    private void pickImageSource() {
        imagePickSourceListener.onPickImageSource();
    }

    @Override
    public void onAttach(Context context) {
        imagePickSourceListener = (ImageSourceListener) context;
        super.onAttach(context);
    }

    public interface ImageSourceListener {
        void onPickImageSource();
    }
}

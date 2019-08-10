package com.zubisoft.solutions.smartkitchen;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zubisoft.solutions.smartkitchen.model.CookStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepsFragment extends BaseStepFragment implements BlockingStep {


    private FloatingActionButton btnAddStep;
    private TextInputEditText txtStep;
    private List<CookStep> steps = new ArrayList<>();
    private int count = 0;
    private TextInputEditText txtDuration;
    private FloatingActionButton btnAddImage;
    private OnAddImageListener onAddImageListener;


    public StepsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        btnAddStep = view.findViewById(R.id.btn_add_step);
        btnAddImage = view.findViewById(R.id.btn_add_image);
        txtStep = view.findViewById(R.id.step);
        txtDuration = view.findViewById(R.id.duration);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddImageListener.onAddImage();
            }
        });

        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> mapData = new HashMap<>();
                mapData.put("step_description", txtStep.getText().toString());
                mapData.put("step_number", (count++) + "");
                String imageUrl = "";
                imageUrl = ((NewRecipeActivity) getActivity()).getImageResultUri() != null ? ((NewRecipeActivity) getActivity()).getImageResultUri().getPath() : "";
                mapData.put("image", imageUrl);
//                Toast.makeText(getActivity(), imageUrl, Toast.LENGTH_SHORT).show();

                if (TextUtils.isEmpty(txtStep.getText().toString())) {
                    count--;
                    Toast.makeText(getActivity(), "Input step description!", Toast.LENGTH_SHORT).show();
                } else {
                    CookStep step = new CookStep(count, Integer.valueOf(txtDuration.getText().toString()), txtStep.getText().toString(), mapData.get("image"));
                    steps.add(step);
                    txtDuration.setText("");
                    txtStep.setText("");
                }

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


    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {


        if (steps.isEmpty()) {
            Toast.makeText(getActivity(), "You must add at least one step!", Toast.LENGTH_SHORT).show();
            return;
        }

        stepClickListener.onNextClicked(steps);
//        Toast.makeText(getActivity(), "Completed"+data.toString(), Toast.LENGTH_SHORT).show();
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Override
    public void onAttach(Context context) {
        onAddImageListener = (OnAddImageListener) context;
        super.onAttach(context);
    }

    public interface OnAddImageListener {
        void onAddImage();
    }
}

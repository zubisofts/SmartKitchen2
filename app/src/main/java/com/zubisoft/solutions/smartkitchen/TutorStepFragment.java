package com.zubisoft.solutions.smartkitchen;


import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zubisoft.solutions.smartkitchen.model.CookStep;


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorStepFragment extends Fragment implements BlockingStep {


    private TextToSpeech textToSpeech;
    private TextView desc;
    private VoiceListener listener;
    private int duartion;

    public TutorStepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutor_step, container, false);

        Bundle bundle = getArguments();
        CookStep step = (CookStep) bundle.getSerializable("step");

        desc = view.findViewById(R.id.description);
        desc.setText(step.getDescription());

        ImageView stepImage = view.findViewById(R.id.image);

        loadImage(stepImage, step.getImageUrl());

        return view;
    }

    private void loadImage(ImageView stepImage, String imageUrl) {
        Glide.with(getActivity())
                .load(imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(stepImage);
    }


    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
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

        listener.onSpeak(desc.getText().toString());
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onAttach(Context context) {

        listener = (VoiceListener) context;

        super.onAttach(context);
    }

    public interface VoiceListener {
        void onSpeak(String text);
    }
}

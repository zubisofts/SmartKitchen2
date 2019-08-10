package com.zubisoft.solutions.smartkitchen;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.zubisoft.solutions.smartkitchen.adapters.StepsClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseStepFragment extends Fragment {

    protected StepsClickListener stepClickListener;

    public BaseStepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        stepClickListener = (StepsClickListener) context;
        super.onAttach(context);
    }
}

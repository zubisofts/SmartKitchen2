package com.zubisoft.solutions.smartkitchen.adapters;

public interface StepsClickListener {
    void onNextClicked(Object mapData);

    void onPreviousPressed(Object mapData);

    void onAddSteps(Object map);
}

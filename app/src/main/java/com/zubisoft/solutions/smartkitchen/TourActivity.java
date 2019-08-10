package com.zubisoft.solutions.smartkitchen;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;

import com.zubisoft.solutions.smartkitchen.adapters.TourViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TourActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RelativeLayout btnNext, btnBack;
    private TextView nextText, backText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );

        setContentView(R.layout.activity_tour);

        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);
        nextText = findViewById(R.id.btn_text_next);
        backText = findViewById(R.id.btn_text_back);

        viewPager = findViewById(R.id.view_pager);
        TourViewPagerAdapter adapter = new TourViewPagerAdapter(this, getSteps());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    fadeView(btnBack, View.GONE, 300);
                } else if ((i < viewPager.getAdapter().getCount() - 1) && i > 0) {
                    nextText.setText("Next");
                    backText.setText("Back");
                    fadeView(btnBack, View.VISIBLE, 300);
                } else {
                    fadeView(btnBack, View.VISIBLE, 300);
                    nextText.setText("Get Started");
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                } else if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    startActivity(new Intent(TourActivity.this, RegisterActivity.class));
                    finish();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() - 1 >= 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                }
            }
        });

    }

    private List<TourViewPagerAdapter.Step> getSteps() {
        List<TourViewPagerAdapter.Step> steps = new ArrayList<>();
        steps.add(new TourViewPagerAdapter.Step("Quick & Easy Cooking", "Search for your favourite recipe and get your recipe prepared without extra hassle.", R.drawable.img4));
        steps.add(new TourViewPagerAdapter.Step("Guided Experience", "Walk through various steps to prepare your recipe guided by your smartphone.", R.drawable.img5));
        steps.add(new TourViewPagerAdapter.Step("Snap & Share With Family & Friends", "Cook your favourite, snap it and share with friends and family...", R.drawable.img3));


        return steps;
    }

    public void fadeView(final View view, final int visibility, int duration) {
        ObjectAnimator fade = ObjectAnimator.ofFloat(view, View.ALPHA, visibility == View.VISIBLE ? 1 : 0)
                .setDuration(duration);
        fade.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(visibility);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fade.start();
    }

    public Bundle makeTransition() {
        RelativeLayout button = findViewById(R.id.btn_next);

        Pair<View, String>[] pairs = new Pair[]{
                new Pair<View, String>(button, "get_started")
        };

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TourActivity.this, pairs);

        return options.toBundle();
    }
}

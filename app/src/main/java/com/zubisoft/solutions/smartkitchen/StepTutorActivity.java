package com.zubisoft.solutions.smartkitchen;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.zubisoft.solutions.smartkitchen.model.RecipeData;

public class StepTutorActivity extends AppCompatActivity {

    private int currentPage = 0;
    private boolean isDragged;
    private boolean isSettled;
    private TextView[] dots;
    private OnPageChangeListener pageChangeListener;
    private ViewPager viewpager;
    private RecipeData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tutor);

        pageChangeListener = new OnPageChangeListener();
        setupViewPager();

    }

    private void setupViewPager() {
        viewpager = findViewById(R.id.view_pager);

        data = (RecipeData) getIntent().getSerializableExtra("recipe_data");
//        adapter=new StepTutorViewPagerAdapter(this, data.getSteps());
//        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(pageChangeListener);
        //changeStatusBarColor();

//        addDots(0);
    }

    public void addDots(int pos) {

        LinearLayout dots_layout = findViewById(R.id.dots_layout);

        dots = new TextView[data.getSteps().size()];
        dots_layout.removeAllViews();
        for (int i = 0; i < data.getSteps().size(); i++) {
            dots[i] = new TextView(getApplicationContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(45);
//            dots[i].setTextColor(v.getResources().getColor(R.color.colorAccent));
            dots_layout.addView(dots[i]);
        }

        dots[pos].setTextColor(Color.YELLOW);
    }

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

//            addDots(position);
            currentPage = position;

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    isDragged = true;
//                    Toast.makeText(getApplicationContext(), "Scroll state dragging", Toast.LENGTH_SHORT).show();
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
//                        Toast.makeText(AppIntro1.this, "Scroll state settling", Toast.LENGTH_SHORT).show();
                    isSettled = true;
                    isDragged = false;
//                        slideIntro();
                    break;
                default:
                    isDragged = false;
                    isSettled = false;
                    break;
            }
        }

    }


}

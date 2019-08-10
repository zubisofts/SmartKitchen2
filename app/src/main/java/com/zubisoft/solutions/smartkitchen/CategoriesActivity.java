package com.zubisoft.solutions.smartkitchen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private ListView menuList;
    private boolean isMenuReveald;
    private TextView cat_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        menuList = findViewById(R.id.cat_list);
        menuList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_list_item, R.id.text_view, getCats()));
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleMenu();
                cat_title.setText(((TextView) view.findViewById(R.id.text_view)).getText().toString());
                switchFragment(((TextView) view.findViewById(R.id.text_view)).getText().toString());
            }
        });

        cat_title = findViewById(R.id.cat_title);
        ImageView btnToggleMenu = findViewById(R.id.menu_toggle);
        btnToggleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });


    }

    private void switchFragment(String text) {

        Bundle args = new Bundle();
        args.putString("category", text);
        Fragment cat_frag = new CategoryFragment();
        cat_frag.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_fragment_container, cat_frag)
                .commit();

    }

    private List<String> getCats() {

        return Arrays.asList("Yoruba Collection", "Hausa Collection", "Igbo Collection");

    }

    private void toggleMenu() {
        if (isMenuReveald) {
            closeMenuWithSlideUpAnim();
//            scrim_view.setVisibility(View.GONE);
            isMenuReveald = !isMenuReveald;
        } else {
            openMenuWithSlideDownAnim();
//            scrim_view.setVisibility(View.VISIBLE);
            isMenuReveald = !isMenuReveald;
        }
    }

    private void openMenuWithSlideDownAnim() {
        ObjectAnimator slide_animator = ObjectAnimator.ofFloat(findViewById(R.id.cat_layout), View.TRANSLATION_Y, 0, findViewById(R.id.cat_list).getHeight());
        slide_animator.setInterpolator(new AccelerateDecelerateInterpolator());
        //        slide_animator.setInterpolator(new AccelerateDecelerateInterpolator());

        //        slide_animator.setDuration(200);
//        slide_animator.start();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(findViewById(R.id.cat_list), View.ALPHA, 0.0f, 1f);
//        fadeOut.setDuration(200);
//        fadeOut.start();

        ObjectAnimator fadeOutScrim = ObjectAnimator.ofFloat(findViewById(R.id.cat_list), View.ALPHA, 0.0f, 1f);

//        Rect rect=new Rect();
//        top_sheet.getGlobalVisibleRect(rect);
//
//        ObjectAnimator slide_right=ObjectAnimator.ofFloat(top_sheet, View.TRANSLATION_X, top_sheet.getWidth()-rect.width(),rect.left-top_sheet.getWidth());

//        slide_right.setDuration(500);
//        slide_right.start();

        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(slide_animator, fadeOut, fadeOutScrim/*, slide_right*/);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
//                scrim_view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        set.start();


    }

    private void closeMenuWithSlideUpAnim() {
        ObjectAnimator slide_animator = ObjectAnimator.ofFloat(findViewById(R.id.cat_layout), View.TRANSLATION_Y, findViewById(R.id.cat_list).getHeight(), 0);


        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(findViewById(R.id.cat_list), View.ALPHA, 1f, 0.0f);
        ObjectAnimator fadeInScrim = ObjectAnimator.ofFloat(findViewById(R.id.cat_list), View.ALPHA, 1f, 0.0f);


        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(slide_animator, fadeIn, fadeInScrim /*slide_left*/);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
//                scrim_view.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        set.start();

//        middle_sheet.setBackgroundColor(getResources().getColor(R.color.shrine_scrim_color));
    }
}

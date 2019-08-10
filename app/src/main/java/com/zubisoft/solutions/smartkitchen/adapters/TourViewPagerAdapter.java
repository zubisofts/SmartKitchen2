package com.zubisoft.solutions.smartkitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zubisoft.solutions.smartkitchen.R;

import java.util.List;

public class TourViewPagerAdapter extends PagerAdapter {

    Context context;
    List<Step> steps;

    private LayoutInflater inflater;
    private TextView[] dots;

    public TourViewPagerAdapter(Context c, List<Step> steps) {
        this.context = c;
        this.steps = steps;
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.tour_step_layout_item, container, false);
        ImageView img = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.title);
        TextView desc = view.findViewById(R.id.description);

        Step data = steps.get(position);

        img.setImageResource(data.imageResource);
        title.setText(data.title);
        desc.setText(data.description);
//        view.setBackgroundResource(data.color);

//        ConstraintLayout layout=(ConstraintLayout) view.findViewById(R.id.item_bg);
//        layout.setBackgroundResource(data.getColor());

        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        FrameLayout view = (FrameLayout) object;
        container.removeView(view);
    }

    public static class Step {
        String title;
        String description;
        int imageResource;

        public Step(String title, String description, int imageResource) {
            this.title = title;
            this.description = description;
            this.imageResource = imageResource;
        }
    }

}

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
import com.zubisoft.solutions.smartkitchen.model.Gallery;

import java.util.ArrayList;

public class GalleryViewPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<Gallery> items;
    private LayoutInflater inflater;
    private TextView[] dots;

    public GalleryViewPagerAdapter(Context c, ArrayList<Gallery> items) {
        this.context = c;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.gallery_layout_item, container, false);
        ImageView img = view.findViewById(R.id.imageView);
//        TextView title=(TextView) view.findViewById(R.id.title);
//        TextView subtitle=(TextView) view.findViewById(R.id.subtitle);

        Gallery data = items.get(position);

        img.setImageResource(data.getDrawable());
//        title.setText(data.getTitle());
//        subtitle.setText(data.getDescription());

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

}

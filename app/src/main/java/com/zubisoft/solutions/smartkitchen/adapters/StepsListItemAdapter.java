package com.zubisoft.solutions.smartkitchen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zubisoft.solutions.smartkitchen.R;
import com.zubisoft.solutions.smartkitchen.model.CookStep;

import java.util.List;

public class StepsListItemAdapter extends RecyclerView.Adapter<StepsListItemAdapter.StepItemViewHolder> {

    private List<CookStep> list;

    public StepsListItemAdapter(List<CookStep> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StepItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_list_item, viewGroup, false);
        return new StepItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepItemViewHolder stepItemViewHolder, int i) {

        CookStep cookStep = list.get(i);
        if (cookStep != null) {
            stepItemViewHolder.step_no.setText("Step " + cookStep.getStepNo());
            stepItemViewHolder.desc.setText(cookStep.getDescription());
            loadImage(stepItemViewHolder, cookStep);
//            stepItemViewHolder.image.setImageResource(R.drawable.image_placeholder);
        }

    }

    private void loadImage(StepItemViewHolder stepItemViewHolder, CookStep cookStep) {
        Glide.with(stepItemViewHolder.itemView.getContext())
                .load(cookStep.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(stepItemViewHolder.image);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class StepItemViewHolder extends RecyclerView.ViewHolder {

        private TextView desc;
        private TextView step_no;
        private ImageView image;

        public StepItemViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.desc);
            step_no = itemView.findViewById(R.id.step);
            image = itemView.findViewById(R.id.step_image);
        }
    }
}

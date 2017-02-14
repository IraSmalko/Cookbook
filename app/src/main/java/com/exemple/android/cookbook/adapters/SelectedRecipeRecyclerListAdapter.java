package com.exemple.android.cookbook.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.SelectedRecipe;

import java.util.ArrayList;
import java.util.List;

public class SelectedRecipeRecyclerListAdapter extends RecyclerView.Adapter<SelectedRecipeRecyclerListAdapter.CustomViewHolder> {

    private Context context;
    private List<SelectedRecipe> items;
    private OnItemClickListenerSelectedRecipe onItemClickListener;

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        public ImageView imageView;

        public CustomViewHolder(View v) {
            super(v);
            this.textView = (TextView) v.findViewById(R.id.infoText);
            this.imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    public SelectedRecipeRecyclerListAdapter(Context context, List<SelectedRecipe> items) {
        this.context = context;
        this.items = items;
    }

    public OnItemClickListenerSelectedRecipe getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListenerSelectedRecipe onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SelectedRecipeRecyclerListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe, parent, false);

        return new SelectedRecipeRecyclerListAdapter.CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SelectedRecipeRecyclerListAdapter.CustomViewHolder holder, int position) {
        final SelectedRecipe item = items.get(position);

        holder.textView.setText(item.getName());
        Glide.with(context).load(item.getPhotoUrl()).into(holder.imageView);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item);
            }
        };
        holder.textView.setOnClickListener(listener);
        holder.imageView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public void setFilter(ArrayList<SelectedRecipe> newList) {
        items = new ArrayList<>();
        items.addAll(newList);
        notifyDataSetChanged();
    }
}

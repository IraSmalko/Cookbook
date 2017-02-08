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
import com.exemple.android.cookbook.entity.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerListAdapter extends RecyclerView.Adapter<RecipeRecyclerListAdapter.CustomViewHolder> {

    private Context mContext;
    private List<Recipe> items;
    private OnItemClickListenerRecipes onItemClickListener;

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextView;
        public ImageView mImageView;

        public CustomViewHolder(View v) {
            super(v);
            this.mTextView = (TextView) v.findViewById(R.id.info_text);
            this.mImageView = (ImageView) v.findViewById(R.id.imageView1);
        }
    }

    public RecipeRecyclerListAdapter(Context mContext, List<Recipe> items) {
        this.mContext = mContext;
        this.items = items;
    }

    public OnItemClickListenerRecipes getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListenerRecipes onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecipeRecyclerListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe, parent, false);

        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeRecyclerListAdapter.CustomViewHolder holder, int position) {
        final Recipe item = items.get(position);

        holder.mTextView.setText(item.getName());
        Glide.with(mContext).load(item.getPhotoUrl()).into(holder.mImageView);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item);
            }
        };
        holder.mTextView.setOnClickListener(listener);
        holder.mImageView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public void setFilter(ArrayList<Recipe> newList) {
        items = new ArrayList<>();
        items.addAll(newList);
        notifyDataSetChanged();
    }
}

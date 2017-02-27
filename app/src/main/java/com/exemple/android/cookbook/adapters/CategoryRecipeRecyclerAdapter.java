package com.exemple.android.cookbook.adapters;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.CategoryRecipes;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecipeRecyclerAdapter extends RecyclerView.Adapter<CategoryRecipeRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CategoryRecipes> mItems = new ArrayList<>();
    private final ItemClickListener mClickListener;

    public CategoryRecipeRecyclerAdapter(Context context, List<CategoryRecipes> items, ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
    }

    public void updateAdapter(@Nullable List<CategoryRecipes> categoryRecipes) {
        mItems.clear();
        if(categoryRecipes != null) {
            mItems.addAll(categoryRecipes);
        }
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        return CustomViewHolder.create(mInflater, parent, mClickListener);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        CategoryRecipes item = mItems.get(position);
        holder.bind(mContext, item);
    }

    @Override
    public int getItemCount() {
        return (null != mItems ? mItems.size() : 0);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private ImageView imageView;
        private final ItemClickListener clickListener;
        private CategoryRecipes item;

        static CustomViewHolder create(LayoutInflater inflater, ViewGroup parent, ItemClickListener clickListener) {
            return new CustomViewHolder(inflater.inflate(R.layout.card_recipe, parent, false), clickListener);
        }

        public CustomViewHolder(View v, ItemClickListener clickListener) {
            super(v);
            this.clickListener = clickListener;
            this.textView = (TextView) v.findViewById(R.id.infoText);
            this.imageView = (ImageView) v.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        void bind(Context context, CategoryRecipes item) {
            this.item = item;
            textView.setText(item.getName());
            Glide.with(context).load(item.getPhotoUrl()).into(imageView);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(CategoryRecipes item);
    }
}

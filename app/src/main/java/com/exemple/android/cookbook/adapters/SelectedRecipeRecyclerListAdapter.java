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
import com.exemple.android.cookbook.entity.SelectedRecipe;

import java.util.ArrayList;
import java.util.List;

public class SelectedRecipeRecyclerListAdapter extends RecyclerView.Adapter<SelectedRecipeRecyclerListAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SelectedRecipe> mItems = new ArrayList<>();
    private final SelectedRecipeRecyclerListAdapter.ItemClickListener mClickListener;

    public SelectedRecipeRecyclerListAdapter(Context context, List<SelectedRecipe> items,
                                             SelectedRecipeRecyclerListAdapter.ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
    }

    public void updateAdapter(@Nullable List<SelectedRecipe> selectedRecipe) {
        mItems.clear();
        if (selectedRecipe != null) {
            mItems.addAll(selectedRecipe);
        }
        notifyDataSetChanged();
    }

    @Override
    public SelectedRecipeRecyclerListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        return SelectedRecipeRecyclerListAdapter.CustomViewHolder.create(mInflater, parent, mClickListener);
    }

    @Override
    public void onBindViewHolder(SelectedRecipeRecyclerListAdapter.CustomViewHolder holder, int position) {
        SelectedRecipe item = mItems.get(position);
        holder.bind(mContext, item);
    }

    @Override
    public int getItemCount() {
        return (null != mItems ? mItems.size() : 0);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private ImageView imageView;
        private final SelectedRecipeRecyclerListAdapter.ItemClickListener clickListener;
        private SelectedRecipe item;

        static SelectedRecipeRecyclerListAdapter
                .CustomViewHolder create(LayoutInflater inflater,
                                         ViewGroup parent, SelectedRecipeRecyclerListAdapter.ItemClickListener clickListener) {
            return new SelectedRecipeRecyclerListAdapter
                    .CustomViewHolder(inflater.inflate(R.layout.card_recipe, parent, false), clickListener);
        }

        public CustomViewHolder(View v, SelectedRecipeRecyclerListAdapter.ItemClickListener clickListener) {
            super(v);
            this.clickListener = clickListener;
            this.textView = (TextView) v.findViewById(R.id.infoText);
            this.imageView = (ImageView) v.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        void bind(Context context, SelectedRecipe item) {
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
        void onItemClick(SelectedRecipe item);
    }
}

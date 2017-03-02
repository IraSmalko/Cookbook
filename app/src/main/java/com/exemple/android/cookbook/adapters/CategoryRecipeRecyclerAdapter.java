package com.exemple.android.cookbook.adapters;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.CategoryRecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryRecipeRecyclerAdapter extends RecyclerView.Adapter<CategoryRecipeRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CategoryRecipes> mItems = new ArrayList<>();
    private List<CategoryRecipes> mItemsPendingRemoval;
    private CategoryRecipes mItem;
    private CategoryRecipeRecyclerAdapter.ItemClickListener mClickListener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<CategoryRecipes, Runnable> pendingRunnables = new HashMap<>();

    public CategoryRecipeRecyclerAdapter(Context context, List<CategoryRecipes> items,
                                         CategoryRecipeRecyclerAdapter.ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
        mItemsPendingRemoval = new ArrayList<>();
    }

    public void updateAdapter(@Nullable List<CategoryRecipes> categoryRecipes) {
        mItems.clear();
        if (categoryRecipes != null) {
            mItems.addAll(categoryRecipes);
        }
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(mContext);
        }
        return CustomViewHolder.create(mInflater, parent);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        mItem = mItems.get(position);
        if (mItemsPendingRemoval.contains(mItem)) {
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.textView.setText(mItem.getName());
            Glide.with(mContext).load(mItem.getPhotoUrl()).into(holder.imageView);
        }
        holder.undo.setOnClickListener(listener);
        holder.regularLayout.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card:
                    if (mClickListener != null) {
                        mClickListener.onItemClick(mItem);
                    }
                    break;
                case R.id.undo:
                    undoOpt(mItem);
                    break;
            }

        }
    };

    private void undoOpt(CategoryRecipes item) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
        pendingRunnables.remove(item);
        if (pendingRemovalRunnable != null)
            mHandler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(item);
        // this will rebind the row in "normal" state
        notifyItemChanged(mItems.indexOf(mItem));
    }

    public void pendingRemoval(int position) {

        final CategoryRecipes data = mItems.get(position);
        if (!mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.add(data);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mItems.indexOf(data));
                }
            };
            mHandler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }

    private void remove(int position) {
        CategoryRecipes data = mItems.get(position);
        if (mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.remove(data);
        }
        if (mItems.contains(data)) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        CategoryRecipes data = mItems.get(position);
        return mItemsPendingRemoval.contains(data);
    }

    @Override
    public int getItemCount() {
        return (null != mItems ? mItems.size() : 0);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        private CardView regularLayout;
        private LinearLayout swipeLayout;
        private TextView undo;
        private TextView textView;
        private ImageView imageView;

        static CustomViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new CustomViewHolder(inflater.inflate(R.layout.row_item, parent, false));
        }

        CustomViewHolder(View v) {
            super(v);
            this.regularLayout = (CardView) v.findViewById(R.id.card);
            this.textView = (TextView) v.findViewById(R.id.infoText);
            this.imageView = (ImageView) v.findViewById(R.id.imageView);
            this.swipeLayout = (LinearLayout) v.findViewById(R.id.swipeLayout);
            this.undo = (TextView) v.findViewById(R.id.undo);
        }
    }

    public interface ItemClickListener {
        void onItemClick(CategoryRecipes item);
    }
}

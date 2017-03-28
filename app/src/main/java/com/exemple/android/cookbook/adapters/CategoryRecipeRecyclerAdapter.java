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
import com.exemple.android.cookbook.entity.firebase.RecipesCategory;
import com.exemple.android.cookbook.helpers.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryRecipeRecyclerAdapter extends RecyclerView.Adapter<CategoryRecipeRecyclerAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private RecipesCategory mItem;
    private List<RecipesCategory> mItems = new ArrayList<>();
    private List<RecipesCategory> mItemsPendingRemoval;
    private CategoryRecipeRecyclerAdapter.ItemClickListener mClickListener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<RecipesCategory, Runnable> pendingRunnables = new HashMap<>();

    public CategoryRecipeRecyclerAdapter(Context context, List<RecipesCategory> items,
                                         CategoryRecipeRecyclerAdapter.ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
        mItemsPendingRemoval = new ArrayList<>();
    }

    public void updateAdapter(@Nullable List<RecipesCategory> recipeCategories) {
        mItems.clear();
        if (recipeCategories != null) {
            mItems.addAll(recipeCategories);
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
        final RecipesCategory item = mItems.get(position);
        mItem = item;
        if (mItemsPendingRemoval.contains(item)) {
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.textView.setText(item.getName());
            Glide.with(mContext).load(item.getPhotoUrl()).into(holder.imageView);
        }
        holder.undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoOpt(item);
            }
        });
        holder.regularLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(item);
                }
            }
        });
    }

    private void undoOpt(RecipesCategory item) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
        pendingRunnables.remove(item);
        if (pendingRemovalRunnable != null)
            mHandler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(item);
        // this will rebind the row in "normal" state
        notifyItemChanged(mItems.indexOf(mItem));
    }

    public void pendingRemoval(int position) {
        final RecipesCategory data = mItems.get(position);
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
        RecipesCategory data = mItems.get(position);
        if (mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.remove(data);
        }
        if (mItems.contains(data)) {
            mItems.remove(position);
            notifyItemRemoved(position);
            new FirebaseHelper().removeCategory(mContext, data.getName());
        }
    }

    public boolean isPendingRemoval(int position, List<RecipesCategory> recipeCategories) {
        RecipesCategory data = mItems.get(position);
        return recipeCategories.contains(data) || mItemsPendingRemoval.contains(data);
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
        void onItemClick(RecipesCategory item);
    }
}

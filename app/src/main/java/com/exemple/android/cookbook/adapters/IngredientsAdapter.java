package com.exemple.android.cookbook.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private Ingredient mItem;
    private List<Ingredient> mItems = new ArrayList<>();
    private List<Ingredient> mItemsPendingRemoval;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<Ingredient, Runnable> mPendingRunnables = new HashMap<>();

    private boolean swipeRemoveNeeded = false;

    public IngredientsAdapter(Context context, List<Ingredient> items) {
        updateAdapter(items);
        mContext = context;
        mItemsPendingRemoval = new ArrayList<>();
        swipeRemoveNeeded = false;
    }

    public IngredientsAdapter(Context context, List<Ingredient> items, boolean isSwipeNeeded) {
        updateAdapter(items);
        mContext = context;
        mItemsPendingRemoval = new ArrayList<>();
        swipeRemoveNeeded = isSwipeNeeded;
    }

    public void updateAdapter(@Nullable List<Ingredient> ingredient) {
        mItems.clear();
        if (ingredient != null) {
            mItems.addAll(ingredient);
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
        final Ingredient item = mItems.get(position);
        mItem = item;

        if (swipeRemoveNeeded) {
            if (mItemsPendingRemoval.contains(item)) {
                holder.ingredientLayout.setVisibility(View.GONE);
                holder.swipeLayout.setVisibility(View.VISIBLE);
                holder.undo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoOpt(item);
                    }
                });
            } else {
                /** {show regular layout} and {hide swipe layout} */
                holder.ingredientLayout.setVisibility(View.VISIBLE);
                holder.swipeLayout.setVisibility(View.GONE);
                holder.nameIngredients.setText(item.getName());
                holder.quantity.setText(String.valueOf(item.getQuantity()));
                holder.unit.setText(item.getUnit());
            }
        } else {
            holder.ingredientLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.nameIngredients.setText(item.getName());
            holder.quantity.setText(String.valueOf(item.getQuantity()));
            holder.unit.setText(item.getUnit());
        }
    }

    private void undoOpt(Ingredient item) {
        Runnable pendingRemovalRunnable = mPendingRunnables.get(item);
        mPendingRunnables.remove(item);
        if (pendingRemovalRunnable != null)
            mHandler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(item);
        // this will rebind the row in "normal" state
        notifyItemChanged(mItems.indexOf(mItem));
    }

    public void pendingRemoval(int position) {
        final Ingredient data = mItems.get(position);
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
            mPendingRunnables.put(data, pendingRemovalRunnable);
        }

    }

    private void remove(int position) {
        Ingredient data = mItems.get(position);
        if (mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.remove(data);
        }
        if (mItems.contains(data)) {
            mItems.remove(position);
            notifyItemRemoved(position);
//            new DataSourceSQLite(mContext).removeIngredient(mIdRecipe, data);
        }
    }

    public boolean isPendingRemoval(int position) {
        Ingredient data = mItems.get(position);
        return mItemsPendingRemoval.contains(data);
    }

    @Override
    public int getItemCount() {

        return (null != mItems ? mItems.size() : 0);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView quantity, unit, nameIngredients, undo;
        private LinearLayout swipeLayout, ingredientLayout;

        static CustomViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new CustomViewHolder(inflater.inflate(R.layout.ingredient_with_swipe_remove, parent, false));
        }

        CustomViewHolder(View v) {
            super(v);
            this.quantity = (TextView) v.findViewById(R.id.quantity);
            this.unit = (TextView) v.findViewById(R.id.unit);
            this.nameIngredients = (TextView) v.findViewById(R.id.nameIngredients);
            this.swipeLayout = (LinearLayout) v.findViewById(R.id.swipeLayout);
            this.ingredientLayout = (LinearLayout) v.findViewById(R.id.item_ingredient);
            this.undo = (TextView) v.findViewById(R.id.undo);
        }
    }

    public List<Ingredient> getItems(){
        return mItems;
    }
}

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
import com.exemple.android.cookbook.helpers.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private Ingredient mItem;
    private List<Ingredient> mItems = new ArrayList<>();
    private List<Ingredient> mItemsPendingRemoval;
    private ItemClickListener mClickListener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<Ingredient, Runnable> pendingRunnables = new HashMap<>();

    public IngredientsAdapter(Context context, List<Ingredient> items, ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
        mItemsPendingRemoval = new ArrayList<>();
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

        holder.nameIngredients.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.unit.setText(item.getUnit());
        holder.nameIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(item);
            }
        });
    }

    private void undoOpt(Ingredient item) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
        pendingRunnables.remove(item);
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
            pendingRunnables.put(data, pendingRemovalRunnable);
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
            new FirebaseHelper().removeCategory(mContext, data.getName());
        }
    }

    public boolean isPendingRemoval(int position, List<Ingredient> ingredients) {
        Ingredient data = mItems.get(position);
        return ingredients.contains(data) || mItemsPendingRemoval.contains(data);
    }

    @Override
    public int getItemCount() {

        return (null != mItems ? mItems.size() : 0);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView quantity, unit, nameIngredients;

        static CustomViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new CustomViewHolder(inflater.inflate(R.layout.item_ingredient, parent, false));
        }

        CustomViewHolder(View v) {
            super(v);
            this.quantity = (TextView) v.findViewById(R.id.quantity);
            this.unit = (TextView) v.findViewById(R.id.unit);
            this.nameIngredients = (TextView) v.findViewById(R.id.nameIngredients);
        }
    }

    public interface ItemClickListener {
        void onItemClick(Ingredient item);
    }
}

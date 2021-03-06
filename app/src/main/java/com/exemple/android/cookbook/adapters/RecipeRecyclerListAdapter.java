package com.exemple.android.cookbook.adapters;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.helpers.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeRecyclerListAdapter extends RecyclerView.Adapter<RecipeRecyclerListAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Recipe> mItems = new ArrayList<>();
    private List<Recipe> mItemsPendingRemoval;
    private Recipe mItem;
    private String mNameRecipesList;
    private RecipeRecyclerListAdapter.ItemClickListener mClickListener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<Recipe, Runnable> mPendingRunnables = new HashMap<>();

    public RecipeRecyclerListAdapter(Context context, List<Recipe> items, String nameRecipesList,
                                     RecipeRecyclerListAdapter.ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
        mNameRecipesList = nameRecipesList;
        mItemsPendingRemoval = new ArrayList<>();
    }

    public void updateAdapter(@Nullable List<Recipe> recipe) {
        mItems.clear();
        if (recipe != null) {
            mItems.addAll(recipe);
        }
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        return CustomViewHolder.create(mInflater, parent);
    }

    @Override
    public void onBindViewHolder(RecipeRecyclerListAdapter.CustomViewHolder holder, int position) {
        final Recipe item = mItems.get(position);
        mItem = item;
        if (mItemsPendingRemoval.contains(item)) {
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.textView.setText(item.getName());
            Glide.with(mContext).load(item.getPhotoUrl()).placeholder(ContextCompat
                    .getDrawable(mContext, R.drawable.background_land)).centerCrop().into(holder.imageView);
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

    private void undoOpt(Recipe item) {
        Runnable pendingRemovalRunnable = mPendingRunnables.get(item);
        mPendingRunnables.remove(item);
        if (pendingRemovalRunnable != null)
            mHandler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(item);
        // this will rebind the row in "normal" state
        notifyItemChanged(mItems.indexOf(mItem));
    }

    public void pendingRemoval(int position) {

        final Recipe data = mItems.get(position);
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
        Recipe data = mItems.get(position);
        if (mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.remove(data);
        }
        if (mItems.contains(data)) {
            mItems.remove(position);
            notifyItemRemoved(position);
            new FirebaseHelper().removeRecipe(mContext, data.getName(), mNameRecipesList);
        }
    }

    public boolean isPendingRemoval(int position, List<Recipe> recipeList) {
        Recipe data = mItems.get(position);
        return recipeList.contains(data) || mItemsPendingRemoval.contains(data);
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
        void onItemClick(Recipe item);
    }
}

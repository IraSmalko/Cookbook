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
import com.exemple.android.cookbook.entity.realm.RealmRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Sakurov on 18.03.2017.
 */

public class SelectedRecipeListRealmAdapter extends RecyclerView.Adapter<SelectedRecipeListRealmAdapter.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private RealmRecipe mItem;
    private List<RealmRecipe> mItems = new ArrayList<>();
    private List<RealmRecipe> mItemsPendingRemoval;
    private SelectedRecipeListRealmAdapter.ItemClickListener mClickListener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    private HashMap<RealmRecipe, Runnable> pendingRunnables = new HashMap<>();

    private Realm mRealm = Realm.getDefaultInstance();

    public SelectedRecipeListRealmAdapter(Context context,
                                          List<RealmRecipe> items,
                                          SelectedRecipeListRealmAdapter.ItemClickListener clickListener) {
        updateAdapter(items);
        mContext = context;
        mClickListener = clickListener;
        mItemsPendingRemoval = new ArrayList<>();
    }

    public void updateAdapter(@Nullable List<RealmRecipe> selectedRecipe) {
        mItems.clear();
        if (selectedRecipe != null) {
            mItems.addAll(selectedRecipe);
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
    public void onBindViewHolder(SelectedRecipeListRealmAdapter.CustomViewHolder holder, int position) {
        final RealmRecipe item = mItems.get(position);
        mItem = item;
        if (mItemsPendingRemoval.contains(item)) {
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.textView.setText(item.getRecipeName());
            Glide.with(mContext).load(item.getRecipePhotoUrl()).into(holder.imageView);
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

    private void undoOpt(RealmRecipe item) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
        pendingRunnables.remove(item);
        if (pendingRemovalRunnable != null)
            mHandler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(item);
        // this will rebind the row in "normal" state
        notifyItemChanged(mItems.indexOf(mItem));
    }

    public void pendingRemoval(int position) {

        final RealmRecipe data = mItems.get(position);
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
        RealmRecipe data = mItems.get(position);
        if (mItemsPendingRemoval.contains(data)) {
            mItemsPendingRemoval.remove(data);
        }
        if (mItems.contains(data)) {
            mItems.remove(position);
            notifyItemRemoved(position);
            mRealm.beginTransaction();
            mRealm.where(RealmRecipe.class).equalTo("recipeName", data.getRecipeName()).findAll().removeLast();
            mRealm.commitTransaction();
        }
    }

    public boolean isPendingRemoval(int position) {
        RealmRecipe data = mItems.get(position);
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

        static SelectedRecipeListRealmAdapter.CustomViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new SelectedRecipeListRealmAdapter.CustomViewHolder(inflater.inflate(R.layout.row_item, parent, false));
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
        void onItemClick(RealmRecipe item);
    }
}

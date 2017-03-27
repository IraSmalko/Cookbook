package com.exemple.android.cookbook.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.firebase.FirebaseIngredient;
import com.exemple.android.cookbook.entity.realm.RealmIngredient;

import java.util.List;

/**
 * Created by Sakurov on 13.03.2017.
 */

public class ShoppingAdapters {

    private static final Long ANIMATION_DELAY = 700L;

    private List<FirebaseIngredient> mShopDataset;
    private List<FirebaseIngredient> mBasketDataset;

    public ShoppingAdapters(List<FirebaseIngredient> shopDataset, List<FirebaseIngredient> basketDataset) {
        mShopDataset = shopDataset;
        mBasketDataset = basketDataset;
    }

    public class ShopRecyclerAdapter extends RecyclerView.Adapter<ShopRecyclerAdapter.ViewHolder> {

        BasketRecyclerAdapter mAdapter;

        public ShopRecyclerAdapter() {

        }

        public ShopRecyclerAdapter(BasketRecyclerAdapter adapter) {
            mAdapter = adapter;
        }

        public void setmAdapter(BasketRecyclerAdapter adapter) {
            mAdapter = adapter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            public TextView ingredientNameTextView;
            public TextView ingredientQuantityTextView;
            public TextView ingredientUnitTextView;
            public CheckBox shopCheckBox;

            public ViewHolder(View v) {

                super(v);
                ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_name);
                ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
                ingredientUnitTextView = (TextView) itemView.findViewById(R.id.ingredient_unit);
                shopCheckBox = (CheckBox) itemView.findViewById(R.id.shop_checkbox);
                shopCheckBox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                shopCheckBox.postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getAdapterPosition() != -1) {
                            mBasketDataset.add(mShopDataset.get(getAdapterPosition()));
                            mShopDataset.remove(getAdapterPosition());
                            shopCheckBox.setChecked(false);
                            notifyDataSetChanged();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, ANIMATION_DELAY);
            }
        }

        @Override
        public ShopRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping, parent, false);

            // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.ingredientNameTextView.setText(mShopDataset.get(position).getName());
            holder.ingredientQuantityTextView.setText(String.valueOf(mShopDataset.get(position).getQuantity()));
            holder.ingredientUnitTextView.setText(mShopDataset.get(position).getUnit());
        }

        @Override
        public int getItemCount() {
            return mShopDataset.size();
        }
    }

    public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

        ShopRecyclerAdapter mAdapter;

        public BasketRecyclerAdapter(ShopRecyclerAdapter adapter) {
            mAdapter = adapter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            public TextView ingredientNameTextView;
            public TextView ingredientQuantityTextView;
            public TextView ingredientUnitTextView;
            public CheckBox basketCheckBox;

            public ViewHolder(View v) {

                super(v);
                ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_name);
                ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
                ingredientUnitTextView = (TextView) itemView.findViewById(R.id.ingredient_unit);
                basketCheckBox = (CheckBox) itemView.findViewById(R.id.shop_checkbox);
                basketCheckBox.setChecked(true);
                basketCheckBox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                basketCheckBox.postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBasketDataset.size() >= getAdapterPosition()) {
                            mShopDataset.add(mBasketDataset.get(getAdapterPosition()));
                            mBasketDataset.remove(getAdapterPosition());
                            basketCheckBox.setChecked(true);
                            notifyDataSetChanged();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, ANIMATION_DELAY);
            }
        }

        @Override
        public BasketRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping, parent, false);

            // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.ingredientNameTextView.setText(mBasketDataset.get(position).getName());
            holder.ingredientQuantityTextView.setText(String.valueOf(mBasketDataset.get(position).getQuantity()));
            holder.ingredientUnitTextView.setText(mBasketDataset.get(position).getUnit());
        }

        @Override
        public int getItemCount() {
            return mBasketDataset.size();
        }
    }

    public void setShoppingAdaptersToRecyclers(RecyclerView shopRecycler, RecyclerView basketRecycler) {
        ShopRecyclerAdapter shopRecyclerAdapter = new ShopRecyclerAdapter();
        BasketRecyclerAdapter basketRecyclerAdapter = new BasketRecyclerAdapter(shopRecyclerAdapter);
        shopRecyclerAdapter.setmAdapter(basketRecyclerAdapter);
        shopRecycler.setAdapter(shopRecyclerAdapter);
        basketRecycler.setAdapter(basketRecyclerAdapter);
    }

}

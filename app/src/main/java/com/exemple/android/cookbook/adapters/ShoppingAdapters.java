package com.exemple.android.cookbook.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Ingredient;

import java.util.List;

public class ShoppingAdapters {

    private static final Long ANIMATION_DELAY = 600L;

    private List<Ingredient> mShopDataset;
    private List<Ingredient> mBasketDataset;

    private Context mContext;

    public ShoppingAdapters(Context context, List<Ingredient> shopDataset, List<Ingredient> basketDataset) {
        mContext = context;
        mShopDataset = shopDataset;
        mBasketDataset = basketDataset;
    }

    private class ShopRecyclerAdapter extends RecyclerView.Adapter<ShopRecyclerAdapter.ViewHolder> {

        BasketRecyclerAdapter mAdapter;

        private ShopRecyclerAdapter() {

        }

        private void setmAdapter(BasketRecyclerAdapter adapter) {
            mAdapter = adapter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            private TextView ingredientNameTextView;
            private TextView ingredientQuantityTextView;
            private TextView ingredientUnitTextView;
            private CheckBox shopCheckBox;

            private ViewHolder(View v) {

                super(v);
                ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_name);
                ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
                ingredientUnitTextView = (TextView) itemView.findViewById(R.id.ingredient_unit);
                shopCheckBox = (CheckBox) itemView.findViewById(R.id.shop_checkbox);
                shopCheckBox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                shopCheckBox.postOnAnimationDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    shopCheckBox.setChecked(false);
                                    mBasketDataset.add(mShopDataset.get(getAdapterPosition()));
                                    mAdapter.notifyDataSetChanged();
                                    mShopDataset.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Toast.makeText(mContext, "Не так швидко, будь ласка!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        ANIMATION_DELAY);
            }
        }

        @Override
        public ShopRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping, parent, false);

            // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

            return new ViewHolder(v);
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

    private class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

        ShopRecyclerAdapter mAdapter;

        private BasketRecyclerAdapter(ShopRecyclerAdapter adapter) {
            mAdapter = adapter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            private TextView ingredientNameTextView;
            private TextView ingredientQuantityTextView;
            private TextView ingredientUnitTextView;
            private CheckBox basketCheckBox;

            private ViewHolder(View v) {

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
                        try {
                            mShopDataset.add(mBasketDataset.get(getAdapterPosition()));
                            mBasketDataset.remove(getAdapterPosition());
                            basketCheckBox.setChecked(true);
                            notifyDataSetChanged();
                            mAdapter.notifyDataSetChanged();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(mContext, "Не так швидко, будь ласка!", Toast.LENGTH_SHORT).show();
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
            vh.basketCheckBox.setChecked(true);
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

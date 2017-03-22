package com.exemple.android.cookbook.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.realm.RealmIngredient;

import io.realm.RealmList;

/**
 * Created by Sakurov on 21.03.2017.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    RealmList<RealmIngredient> realmIngredients;

    public IngredientsAdapter() {

    }

    public IngredientsAdapter(RealmList<RealmIngredient> ingredients) {
        realmIngredients = ingredients;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientNameTextView;
        public TextView ingredientQuantityTextView;
        public TextView ingredientUnitTextView;

        public ViewHolder(View v) {
            super(v);
            ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_name);
            ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
            ingredientUnitTextView = (TextView) itemView.findViewById(R.id.ingredient_unit);
        }
    }

    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingridient, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ingredientNameTextView.setText(realmIngredients.get(position).getName());
        holder.ingredientQuantityTextView.setText(String.valueOf(realmIngredients.get(position).getQuantity()));
        holder.ingredientUnitTextView.setText(realmIngredients.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return realmIngredients.size();
    }
}

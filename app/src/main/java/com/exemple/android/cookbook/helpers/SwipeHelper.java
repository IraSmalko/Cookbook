package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.adapters.SelectedRecipeListRealmAdapter;
import com.exemple.android.cookbook.entity.firebase.RecipesCategory;
import com.exemple.android.cookbook.entity.firebase.Recipe;
import com.exemple.android.cookbook.supporting.SwipeUtil;

import java.util.ArrayList;
import java.util.List;

public class SwipeHelper {

    private List<RecipesCategory> mRecipesCategoryList = new ArrayList<>();
    private List<Recipe> mRecipesList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Context mContext;

    public SwipeHelper(RecyclerView recyclerView, Context context) {
        mRecyclerView = recyclerView;
        mContext = context;
    }

    private SwipeUtil setSwipeForCategory(List<RecipesCategory> recipeCategories) {
        mRecipesCategoryList = recipeCategories;
        return new SwipeUtil(0, ItemTouchHelper.LEFT, mContext) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                CategoryRecipeRecyclerAdapter adapter = (CategoryRecipeRecyclerAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                CategoryRecipeRecyclerAdapter adapter = (CategoryRecipeRecyclerAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position, mRecipesCategoryList)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
    }

    private SwipeUtil setSwipeRecipe(List<Recipe> recipeList) {
        mRecipesList = recipeList;
        return new SwipeUtil(0, ItemTouchHelper.LEFT, mContext) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                RecipeRecyclerListAdapter adapter = (RecipeRecyclerListAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                RecipeRecyclerListAdapter adapter = (RecipeRecyclerListAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position, mRecipesList)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
    }

    private SwipeUtil setSwipeSelectedRecipe() {

        return new SwipeUtil(0, ItemTouchHelper.LEFT, mContext) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
//                SelectedRecipeRecyclerListAdapter adapter = (SelectedRecipeRecyclerListAdapter) mRecyclerView.getAdapter();
                SelectedRecipeListRealmAdapter adapter = (SelectedRecipeListRealmAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
//                SelectedRecipeRecyclerListAdapter adapter = (SelectedRecipeRecyclerListAdapter) mRecyclerView.getAdapter();
                SelectedRecipeListRealmAdapter adapter = (SelectedRecipeListRealmAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
    }

    public void attachSwipeSelectedRecipe() {
        SwipeUtil swipeHelper = new SwipeHelper(mRecyclerView, mContext).setSwipeSelectedRecipe();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        swipeHelper.setmLeftSwipeLable(mContext.getResources().getString(R.string.extraction));
        swipeHelper.setmLeftcolorCode(ContextCompat.getColor(mContext, R.color.starFullySelected));
    }

    public void attachSwipeRecipe(List<Recipe> recipeList) {
        SwipeUtil swipeHelper = new SwipeHelper(mRecyclerView, mContext).setSwipeRecipe(recipeList);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        swipeHelper.setmLeftSwipeLable(mContext.getResources().getString(R.string.extraction));
        swipeHelper.setmLeftcolorCode(ContextCompat.getColor(mContext, R.color.starFullySelected));
    }

    public void attachSwipeCategory(List<RecipesCategory> recipeCategories) {
        SwipeUtil swipeHelper = new SwipeHelper(mRecyclerView, mContext).setSwipeForCategory(recipeCategories);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        swipeHelper.setmLeftSwipeLable(mContext.getResources().getString(R.string.extraction));
        swipeHelper.setmLeftcolorCode(ContextCompat.getColor(mContext, R.color.starFullySelected));
    }
}



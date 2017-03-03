package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.adapters.SelectedRecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.supporting.SwipeUtil;

import java.util.ArrayList;
import java.util.List;

public class SwipeHelper {

    private List<CategoryRecipes> mCategoryRecipesList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Context mContext;

    public SwipeHelper(RecyclerView recyclerView, Context context) {
        mRecyclerView = recyclerView;
        mContext = context;
    }

    private SwipeUtil setSwipeForCategory(List<CategoryRecipes> categoryRecipes) {
        mCategoryRecipesList = categoryRecipes;
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
                if (adapter.isPendingRemoval(position, mCategoryRecipesList)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
    }

    private SwipeUtil setSwipeRecipe() {

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
                if (adapter.isPendingRemoval(position)) {
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
                SelectedRecipeRecyclerListAdapter adapter = (SelectedRecipeRecyclerListAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                SelectedRecipeRecyclerListAdapter adapter = (SelectedRecipeRecyclerListAdapter) mRecyclerView.getAdapter();
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

    public void attachSwipeRecipe() {
        SwipeUtil swipeHelper = new SwipeHelper(mRecyclerView, mContext).setSwipeRecipe();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        swipeHelper.setmLeftSwipeLable(mContext.getResources().getString(R.string.extraction));
        swipeHelper.setmLeftcolorCode(ContextCompat.getColor(mContext, R.color.starFullySelected));
    }

    public void attachSwipeCategory(List<CategoryRecipes> categoryRecipes) {
        SwipeUtil swipeHelper = new SwipeHelper(mRecyclerView, mContext).setSwipeForCategory(categoryRecipes);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        swipeHelper.setmLeftSwipeLable(mContext.getResources().getString(R.string.extraction));
        swipeHelper.setmLeftcolorCode(ContextCompat.getColor(mContext, R.color.starFullySelected));
    }
}



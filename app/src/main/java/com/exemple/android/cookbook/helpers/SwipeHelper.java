package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.exemple.android.cookbook.SwipeUtil;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;

public class SwipeHelper {

    private RecyclerView mRecyclerView;
    private Context mContext;

    public SwipeHelper (RecyclerView recyclerView, Context context){
        mRecyclerView = recyclerView;
        this.mContext = context;
    }

    public SwipeUtil setSwipeForRecyclerView() {

        return  new SwipeUtil(0, ItemTouchHelper.LEFT, mContext) {
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
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
    }
}



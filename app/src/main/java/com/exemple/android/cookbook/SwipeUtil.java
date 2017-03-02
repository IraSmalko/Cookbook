package com.exemple.android.cookbook;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public abstract class SwipeUtil extends ItemTouchHelper.SimpleCallback {

    private Drawable mBackground;
    private Drawable mDeleteIcon;

    private int mXMarkMargin;

    private boolean mInitiated;
    private Context mContext;

    private int mLeftcolorCode;
    private String mLeftSwipeLable;

    public SwipeUtil(int dragDirs, int swipeDirs, Context context) {
        super(dragDirs, swipeDirs);
        this.mContext = context;
    }

    private void init() {
        mBackground = new ColorDrawable();
        mXMarkMargin = (int) mContext.getResources().getDimension(R.dimen.ic_clear_margin);
        mDeleteIcon = ContextCompat.getDrawable(mContext, android.R.drawable.ic_menu_delete);
        mDeleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mInitiated = true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        if (!mInitiated) {
            init();
        }

        int itemHeight = itemView.getBottom() - itemView.getTop();

        //Setting Swipe Background
        ((ColorDrawable) mBackground).setColor(getmLeftcolorCode());
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int intrinsicWidth = mDeleteIcon.getIntrinsicWidth();
        int intrinsicHeight = mDeleteIcon.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - mXMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - mXMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;


        //Setting Swipe Icon
        mDeleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
        mDeleteIcon.draw(c);

        //Setting Swipe Text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(22);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(getmLeftSwipeLable(), xMarkLeft + 1, xMarkTop + 10, paint);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public String getmLeftSwipeLable() {
        return mLeftSwipeLable;
    }

    public void setmLeftSwipeLable(String mLeftSwipeLable) {
        this.mLeftSwipeLable = mLeftSwipeLable;
    }

    public int getmLeftcolorCode() {
        return mLeftcolorCode;
    }

    public void setmLeftcolorCode(int mLeftcolorCode) {
        this.mLeftcolorCode = mLeftcolorCode;
    }
}

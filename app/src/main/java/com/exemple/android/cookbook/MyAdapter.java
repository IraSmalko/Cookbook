package com.exemple.android.cookbook;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {

    private Context mContext;
    private List<CategoryRecipes> items;
    private OnItemClickListener onItemClickListener;

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextView;
        public ImageView mImageView;

        public CustomViewHolder(View v) {
            super(v);
            this.mTextView = (TextView) v.findViewById(R.id.info_text);
            this.mImageView = (ImageView) v.findViewById(R.id.imageView1);
        }
    }

    public MyAdapter(Context mContext, List<CategoryRecipes> items) {
        this.mContext = mContext;
        this.items = items;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe, parent, false);

        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final CategoryRecipes item = items.get(position);

        holder.mTextView.setText(item.getName());
        Glide.with(mContext).load(item.getPhotoUrl()).into(holder.mImageView);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item);
            }
        };
        holder.mTextView.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }
}

package com.codepath.nytsearch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytsearch.R;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.models.ArticleImage;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public @BindView(R.id.tvTitle) TextView tvTitle;
        public @BindView(R.id.ivArticleImage) ImageView ivArticleImage;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });


        }

    }

    // Store a member variable for the contacts
    private List<Article> mArticles;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom article view layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model

        // find the text view
        TextView tvTitle = holder.tvTitle;
        tvTitle.setText(article.getHeadline().getPrintHeadline());

        // find the image view
        ImageView imageView = holder.ivArticleImage;

        // clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        // populate the thumbnail image
        // remote download the image in the background
        List<ArticleImage> multimedia = article.getMultimedia();

        if (multimedia.isEmpty()) {
            imageView.setImageResource(R.drawable.article_image_placeholder);
        } else {
            Glide
                    .with(getContext())
                    .load(multimedia.get(0).getUrl())
                    .centerCrop()
                    .placeholder(R.drawable.article_image_placeholder)
                    .crossFade()
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
    
    public Article getItem(int position) {
        return mArticles.get(position);
    }

    public void clear() {
        mArticles.clear();
        notifyDataSetChanged();
    }

}

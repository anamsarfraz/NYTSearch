package com.codepath.nytsearch.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.codepath.nytsearch.models.Headline;
import com.codepath.nytsearch.util.ColorGenerator;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // view types
    private final int FULL = 0, PARTIAL = 1;

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

    public static class ViewHolderFull extends RecyclerView.ViewHolder {
        public @BindView(R.id.cvFull) CardView cvArticle;
        public @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
        public @BindView(R.id.tvTitle) TextView tvTitle;
        public @BindView(R.id.tvNewsDesk) TextView tvNewsDesk;
        public @BindView(R.id.tvSnippet) TextView tvSnippet;

        public ViewHolderFull(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(v -> {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                });
            }
    }

    public static class ViewHolderPartial extends RecyclerView.ViewHolder {
        public @BindView(R.id.cvPartial) CardView cvpArticle;
        public @BindView(R.id.tvpTitle) TextView tvpTitle;
        public @BindView(R.id.tvpNewsDesk) TextView tvpNewsDesk;
        public @BindView(R.id.tvpSnippet) TextView tvpSnippet;

        public ViewHolderPartial(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    // Store a member variable for the articles
    private List<Article> mArticles;
    // Store the context for easy access
    private Context mContext;

    // Pass in the article array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FULL:
                View viewFull = inflater.inflate(R.layout.item_article_full, parent, false);
                viewHolder = new ViewHolderFull(viewFull);
                break;
            default:
                View viewPartial = inflater.inflate(R.layout.item_article_partial, parent, false);
                viewHolder = new ViewHolderPartial(viewPartial);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        Headline headLine = article.getHeadline();
        String headline = headLine.getPrintHeadline();
        if (headline == null || headline.isEmpty()) {
            headline = headLine.getMain();
        }

        if (headline == null || headline.isEmpty()) {
            headline = headLine.getName();
        }

        String newsDesk = article.getNewsDesk();
        if (newsDesk == null || newsDesk.isEmpty() || newsDesk.equals("None")) {
            newsDesk = article.getDocumentType();
        }

        // Set item views based on your views and data model

        switch (holder.getItemViewType()) {
            case FULL:
                ViewHolderFull viewFull = (ViewHolderFull) holder;
                // set the text view


                viewFull.tvTitle.setText(headline);
                viewFull.tvNewsDesk.setText(newsDesk);
                viewFull.tvNewsDesk.setBackgroundColor(ColorGenerator.getColor(newsDesk));
                viewFull.tvSnippet.setText(article.getSnippet());

                // find the image view
                ImageView imageView = viewFull.ivArticleImage;

                // clear out recycled image from convertView from last time
                imageView.setImageResource(0);

                // populate the thumbnail image
                // remote download the image in the background
                List<ArticleImage> multimedia = article.getMultimedia();

                Glide
                        .with(getContext())
                        .load(multimedia.get(0).getUrl())
                        .centerCrop()
                        .placeholder(R.drawable.article_image_placeholder)
                        .crossFade()
                        .into(imageView);
                break;
            default:
                ViewHolderPartial viewPartial = (ViewHolderPartial) holder;
                viewPartial.tvpTitle.setText(headline);
                viewPartial.tvpNewsDesk.setText(newsDesk);
                viewPartial.tvpNewsDesk.setBackgroundColor(ColorGenerator.getColor(newsDesk));
                viewPartial.tvpSnippet.setText(article.getSnippet());
                break;
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
        int currSize = mArticles.size();
        for (int i=0; i < currSize; i++) {
            mArticles.remove(0);
            notifyItemRemoved(0);
        }

    }

    public void addAll(List<Article> newArticles) {
        int position = mArticles.size();
        for (int i=0; i < newArticles.size(); i++) {
            mArticles.add(newArticles.get(i));
            notifyItemInserted(position);
            position++;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mArticles.get(position).getMultimedia().isEmpty() ? PARTIAL : FULL;
    }

}

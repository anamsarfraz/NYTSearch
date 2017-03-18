package com.codepath.nytsearch.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytsearch.R;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.models.ArticleImage;


import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the data item for the  current position
        Article article = getItem(position);

        // check to see if existing view is being recycled
        // not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        // find the image view
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivArticleImage);

        // clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline().getPrintHeadline());

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

        return convertView;


    }
}

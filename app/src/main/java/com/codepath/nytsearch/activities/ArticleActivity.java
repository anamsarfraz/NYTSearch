package com.codepath.nytsearch.activities;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.databinding.ActivityArticleBinding;
import com.codepath.nytsearch.models.Article;

import org.parceler.Parcels;

public class ArticleActivity extends AppCompatActivity {

    private ActivityArticleBinding binding;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        setSupportActionBar(binding.tbArticle);

        article = Parcels.unwrap(getIntent().getParcelableExtra("article"));

        Log.d("ArticleActivity", article.getWebUrl());

        binding.wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                }
                return true;
            }
        });
        binding.wvArticle.loadUrl(article.getWebUrl());
    }

}

package com.codepath.nytsearch.models;

import com.codepath.nytsearch.models.Article;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usarfraz on 3/16/17.
 */

public class Articles {


    @SerializedName("docs")
    List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
    public Articles() {
        articles = new ArrayList<>();
    }
}

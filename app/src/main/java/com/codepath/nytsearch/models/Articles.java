package com.codepath.nytsearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

package com.codepath.nytsearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Articles {



    ResponseMeta meta;

    @SerializedName("docs")
    List<Article> articles;

    public Articles() {
        articles = new ArrayList<>();
    }
    public List<Article> getArticles() {
        return articles;
    }
    public ResponseMeta getMeta() {
        return meta;
    }

}

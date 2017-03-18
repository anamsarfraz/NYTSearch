package com.codepath.nytsearch.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ArticleResponse {


    Articles response;

    public ArticleResponse() {
        response = new Articles();
    }

    public Articles getResponse() {
        return response;
    }

    public static ArticleResponse parseJSON(String searchResponse) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        ArticleResponse articleResponse = gson.fromJson(searchResponse, ArticleResponse.class);
        return articleResponse;
    }
}

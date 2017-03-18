package com.codepath.nytsearch.network;

import com.codepath.nytsearch.models.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by usarfraz on 3/16/17.
 */

public interface NYTSearchService {
    @GET("/svc/search/v2/articlesearch.json")
    public Call<ArticleResponse> getArticles(@Query("api-key") String apiKey);
}

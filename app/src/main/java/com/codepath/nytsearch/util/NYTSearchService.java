package com.codepath.nytsearch.util;

import com.codepath.nytsearch.models.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by usarfraz on 3/16/17.
 */

public interface NYTSearchService {
    @GET("/svc/search/v2/articlesearch.json")
    public Call<ArticleResponse> getArticles(
            @Query(Constants.API_KEY_STR) String apiKey,
            @Query(Constants.SEARCH_QUERY_STR) String searchQuery,
            @Query(Constants.FILTERED_QUERY_STR) String filteredQuery,
            @Query(Constants.SORT_STR) String sortOrder,
            @Query(Constants.BEGIN_DATE_STR) String beginDate
            );
}

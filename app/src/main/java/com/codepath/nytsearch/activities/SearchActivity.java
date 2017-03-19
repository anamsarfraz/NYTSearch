package com.codepath.nytsearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.adapters.ArticleAdapter;
import com.codepath.nytsearch.fragments.SettingsFragment;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.models.ArticleResponse;
import com.codepath.nytsearch.util.Constants;
import com.codepath.nytsearch.util.EndlessRecyclerViewScrollListener;
import com.codepath.nytsearch.util.NYTSearchService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchActivity extends AppCompatActivity implements SettingsFragment.OnFilterSettingsChangedListener {
    @BindView(R.id.etQuery)EditText etQuery;
    @BindView(R.id.btnSearch)Button btnSeach;
    @BindView(R.id.rvArticles)RecyclerView rvArticles;
    @BindView(R.id.toolbar) Toolbar toolbar;

    ArrayList<Article> articles;
    ArticleAdapter articleAdapter;
    SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    String searchQuery;
    String filteredQuery;
    String sortOrder;
    String beginDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> fetchArticlesAsync(0));

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        searchQuery = null;
        filteredQuery = null;
        sortOrder = null;
        beginDate = null;

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        rvArticles.setAdapter(articleAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);


       articleAdapter.setOnItemClickListener((view, position) -> {
            // create an intent to display the article
            Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
            // get the article to display
            Log.d("SearchActivity", "Coming here");
            Article article = articleAdapter.getItem(position);
            // pass in that article into intent
            intent.putExtra("article", Parcels.wrap(article));
            // launch the activity
            startActivity(intent);
        });

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchArticlesAsync(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_filter) {
            FragmentManager fm = getSupportFragmentManager();
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
            settingsFragment.show(fm, "fragment_settings");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        searchQuery = etQuery.getText().toString();
        if (searchQuery.isEmpty()) {
            searchQuery = null;
        }

        fetchArticlesAsync(0);

    }

    private void fetchArticlesAsync(int page) {


        OkHttpClient client = new OkHttpClient();


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.nytimes.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NYTSearchService service = retrofit.create(NYTSearchService.class);


        Log.d("Search Activity", "Checkpoint");
        Call<ArticleResponse> call = service.getArticles(
                "7207142e827449f7af7b4525fd35c111",
                searchQuery,
                filteredQuery,
                sortOrder,
                beginDate,
                page);
        call.enqueue(new Callback<ArticleResponse>() {

            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                Log.d("ServiceActvity", String.valueOf(response.isSuccessful()));
                ArticleResponse articleResponse = response.body();

                if (swipeContainer.isRefreshing()) {
                    articleAdapter.clear();
                }
                // record current size of the list
                int curSize = articleAdapter.getItemCount();
                List<Article> newArticles = articleResponse.getResponse().getArticles();
                articles.addAll(newArticles);
                articleAdapter.notifyItemRangeInserted(curSize, newArticles.size());

                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }
                Log.d("ServiceAcivity", articles.get(0).getLeadParagraph());
                //Log.d("ServiceActivity", articles.get(0).getHeadline().getPrintHeadline());

            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Log.d("ServiceActivity", "Something went wrong"+t.getLocalizedMessage());


            }

        });
    }
    @Override
    public void onFilterSettingsChanged() {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        filteredQuery = mSettings.getString(Constants.FILTERED_QUERY_STR, null);
        sortOrder = mSettings.getString(Constants.SORT_STR, null);
        beginDate = mSettings.getString(Constants.BEGIN_DATE_STR, null);

    }
}

package com.codepath.nytsearch.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.adapters.ArticleAdapter;
import com.codepath.nytsearch.databinding.ActivitySearchBinding;
import com.codepath.nytsearch.fragments.SettingsFragment;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.models.ArticleResponse;
import com.codepath.nytsearch.models.Articles;
import com.codepath.nytsearch.util.Connectivity;
import com.codepath.nytsearch.util.Constants;
import com.codepath.nytsearch.util.EndlessRecyclerViewScrollListener;
import com.codepath.nytsearch.util.NYTSearchService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codepath.nytsearch.R.string.search;


public class SearchActivity extends AppCompatActivity implements SettingsFragment.OnFilterSettingsChangedListener {

    private ActivitySearchBinding binding;
    ArrayList<Article> articles;
    ArticleAdapter articleAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    SharedPreferences mSettings;

    String searchQuery;
    String filteredQuery;
    String sortOrder;
    String beginDate;

    int totalHits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setSupportActionBar(binding.tbSearch);
        // Lookup the swipe container view
        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(this::beginNewSearch);

        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        getFilters();
        totalHits = -1;

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        //binding.rvArticles.setAdapter(articleAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvArticles.setLayoutManager(gridLayoutManager);
        binding.rvArticles.setItemAnimator(new LandingAnimator());
        binding.rvArticles.getItemAnimator().setAddDuration(500);
        binding.rvArticles.getItemAnimator().setRemoveDuration(500);
        binding.rvArticles.getItemAnimator().setMoveDuration(500);
        binding.rvArticles.getItemAnimator().setChangeDuration(500);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(articleAdapter);
        alphaAdapter.setFirstOnly(false);
        binding.rvArticles.setAdapter(alphaAdapter);

       articleAdapter.setOnItemClickListener((view, position) -> {
           CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
           // set toolbar color
           builder.setToolbarColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
           builder.addDefaultShareMenuItem();
           Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_share);

           String url = articleAdapter.getItem(position).getWebUrl();
           Intent intent = new Intent(Intent.ACTION_SEND);
           intent.setType("text/plain");
           intent.putExtra(Intent.EXTRA_TEXT, url);

           int requestCode = 100;

           PendingIntent pendingIntent = PendingIntent.getActivity(this,
                   requestCode,
                   intent,
                   PendingIntent.FLAG_UPDATE_CURRENT);

           // Map the bitmap, text, and pending intent to this icon
           // Set tint to be true so it matches the toolbar color
           builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

           CustomTabsIntent customTabsIntent = builder.build();

           customTabsIntent.launchUrl(this, Uri.parse(url));


       });

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // NewYork Times API has a page limit of 120
                if (totalHits > totalItemsCount && page <= 120) {
                    Log.d("SearchActivity", String.format("Total hits: %d, total Item Count: %d", totalHits, totalItemsCount));
                    fetchArticlesAsync(page);
                } else {
                    Log.d("SearchActivity", String.format("Cant load more. Total hits: %d, total Item Count: %d", totalHits, totalItemsCount));
                }

            }
        };
        // Adds the scroll listener to RecyclerView
        binding.rvArticles.addOnScrollListener(scrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences.Editor editor = mSettings.edit();
                if (query.isEmpty()) {
                    searchQuery = null;
                    editor.remove(Constants.SEARCH_QUERY_STR);
                } else {
                    searchQuery = query;
                    editor.putString(Constants.SEARCH_QUERY_STR, searchQuery);
                }

                beginNewSearch();

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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


        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed

        final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d("Search Activity", "Retrying Network Request");
            }
        };
        Log.d("Search Activity", String.format("Checkpoint. Page number: %d", page));
        Call<ArticleResponse> call = service.getArticles(
                "7207142e827449f7af7b4525fd35c111",
                searchQuery,
                filteredQuery,
                sortOrder,
                beginDate,
                page);
        Callback callback = new Callback<ArticleResponse>() {

            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {

                if (!response.isSuccessful()) {
                    if (response.code() == 429) {
                        Log.d("Search Activity", "Retrying Network Request");
                        handler.postDelayed(runnableCode, 3000);
                        call.clone().enqueue(this);

                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to get articles. Check connection and try again.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                handler.removeCallbacks(runnableCode);
                Articles articleResponse = response.body().getResponse();

                if (binding.swipeContainer.isRefreshing()) {
                    articleAdapter.clear();
                }
                // record current size of the list
                List<Article> newArticles = articleResponse.getArticles();
                totalHits = articleResponse.getMeta().getHits();
                articleAdapter.addAll(newArticles);


                hideRefreshControl();
                Log.d("ServiceAcivity", articles.get(0).getLeadParagraph());
                //Log.d("ServiceActivity", articles.get(0).getHeadline().getPrintHeadline());

            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                beginNewSearch();



            }

        };

        call.enqueue(callback);



    }
    @Override
    public void onFilterSettingsChanged() {
        getFilters();


    }

    private void getFilters() {
        searchQuery = mSettings.getString(Constants.SEARCH_QUERY_STR, null);
        filteredQuery = mSettings.getString(Constants.FILTERED_QUERY_STR, null);
        sortOrder = mSettings.getString(Constants.SORT_STR, null);
        beginDate = mSettings.getString(Constants.BEGIN_DATE_STR, null);
    }

    private void beginNewSearch() {
        articleAdapter.clear();
        scrollListener.resetState();
        hideRefreshControl();
        if (Connectivity.isConnected(this)) {
            fetchArticlesAsync(0);
        } else {
            Toast.makeText(this, "Unable to access internet. Network Error?", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideRefreshControl() {
        if (binding.swipeContainer.isRefreshing()) {
            binding.swipeContainer.setRefreshing(false);
        }
    }


}

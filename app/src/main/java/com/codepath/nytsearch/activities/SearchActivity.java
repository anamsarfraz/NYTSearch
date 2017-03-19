package com.codepath.nytsearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.adapters.ArticleArrayAdapter;
import com.codepath.nytsearch.fragments.SettingsFragment;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.models.ArticleResponse;
import com.codepath.nytsearch.util.Constants;
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
    @BindView(R.id.gvResults)GridView gvResults;
    @BindView(R.id.toolbar) Toolbar toolbar;

    List<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;


    String filteredQuery;
    String sortOrder;
    String beginDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        filteredQuery = null;
        sortOrder = null;
        beginDate = null;

        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleArrayAdapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Log.d("SearchActivity", "Coming here");
                Article article = articleArrayAdapter.getItem(position);
                // pass in that article into intent
                intent.putExtra("article", Parcels.wrap(article));
                // launch the activity
                startActivity(intent);
            }
        });
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
        String searchQuery = etQuery.getText().toString();
        if (searchQuery.isEmpty()) {
            searchQuery = null;
        }



        OkHttpClient client = new OkHttpClient();
        /*client.interceptors().add(new Interceptor() {
            @Override
            public Docs intercept(Chain chain) throws IOException {
                HttpUrl httpUrl = chain.request().url().newBuilder().addQueryParameter("", "").build();

                chain.request().newBuilder().url(httpUrl);
                return chain.proceed(chain.request());
            }
        })*/

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
                beginDate);
        call.enqueue(new Callback<ArticleResponse>() {

            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                Log.d("ServiceActvity", String.valueOf(response.isSuccessful()));
                ArticleResponse articleResponse = response.body();

                articleArrayAdapter.addAll(articleResponse.getResponse().getArticles());
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

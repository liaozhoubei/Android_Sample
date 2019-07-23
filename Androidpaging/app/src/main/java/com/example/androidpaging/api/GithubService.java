package com.example.androidpaging.api;

import android.util.Log;

import com.example.androidpaging.model.Repo;

import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GithubService {
    private final String TAG = "GithubService";
    private final String IN_QUALIFIER = "in:name,description";

    public void searchRepos(GithubService.GithubAPI service, String query, int page, int itemsPerPage, final RequestResult requestResult) {
        Log.d(TAG, "query: $query+" + query + ", page: $page" + page + ", itemsPerPage: $itemsPerPage" + itemsPerPage);

        String apiQuery = query + IN_QUALIFIER;
        service.searchRepos(apiQuery, page, itemsPerPage).enqueue(new Callback<RepoSearchResponse>() {
            @Override
            public void onResponse(Call<RepoSearchResponse> call, Response<RepoSearchResponse> response) {
                Log.d(TAG, "got a response $response");
                if (response.isSuccessful()) {
                    RepoSearchResponse body = response.body();
                    List<Repo> repos = body.items;
                    if (repos == null) {
                        repos = Collections.emptyList();
                    }
//                    val repos = response.body()?.items ?: emptyList()
                    requestResult.onSucess(repos);
                } else {
                    requestResult.onError("Unknown error");
                }
            }

            @Override
            public void onFailure(Call<RepoSearchResponse> call, Throwable t) {
                Log.d(TAG, "fail to get data");
                requestResult.onError(t.getMessage());
            }
        });
    }


    private final String BASE_URL = "https://api.github.com/";

    public GithubAPI create() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(Level.BASIC);
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logger)
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubAPI.class);
    }

    public interface GithubAPI {
        /**
         * Get repos ordered by stars.
         */
        @GET("search/repositories?sort=stars")
        public Call<RepoSearchResponse> searchRepos(
                @Query("q") String query,
                @Query("page") int page,
                @Query("per_page") int itemsPerPage
        );

    }

}


package com.example.androidpaging.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import android.util.Log;

import com.example.androidpaging.api.GithubService;
import com.example.androidpaging.db.GithubLocalCache;
import com.example.androidpaging.model.Repo;
import com.example.androidpaging.model.RepoSearchResult;

public class GithubRepository {
    private GithubService service;
    private GithubLocalCache cache;
    private final GithubService.GithubAPI githubAPI;
    private static int DATABASE_PAGE_SIZE = 20;

    public GithubRepository(GithubService service, GithubLocalCache cache) {
        this.service = service;
        githubAPI = service.create();
        this.cache = cache;
    }



    // 当需要查询的时候
    public RepoSearchResult search(String query) {

            Log.d("GithubRepository", "New query: $query");
            DataSource.Factory<Integer, Repo> dataSourceFactory = cache.reposByName(query);
            RepoBoundaryCallback boundaryCallback = new RepoBoundaryCallback(query, service, cache);
            MutableLiveData<String> networkErrors = boundaryCallback.getNetworkErrors();


            LiveData data = new LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                    .setBoundaryCallback(boundaryCallback)
                    .build();
            return new RepoSearchResult(data, networkErrors);

    }


}

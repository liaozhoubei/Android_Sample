package com.example.androidpaging.db;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import android.util.Log;

import com.example.androidpaging.model.Repo;

import java.util.List;
import java.util.concurrent.Executor;

public class GithubLocalCache {
    private RepoDao repoDao;
    private Executor ioExecutor;

    public GithubLocalCache(RepoDao repoDao, Executor ioExecutor) {
        this.repoDao = repoDao;
        this.ioExecutor = ioExecutor;
    }

    public void insert(final List<Repo> repos) {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("GithubLocalCache", "inserting " + repos.size() + "repos");
                repoDao.insert(repos);
            }
        });
    }

    public DataSource.Factory<Integer, Repo> reposByName(String name) {
        String query = "%" + name.trim() + "%";
        DataSource.Factory<Integer, Repo> listLiveData = repoDao.reposByName(query);


        return listLiveData;
    }
}

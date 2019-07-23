package com.example.androidpaging;

import androidx.lifecycle.ViewModelProvider;
import android.content.Context;

import com.example.androidpaging.api.GithubService;
import com.example.androidpaging.data.GithubRepository;
import com.example.androidpaging.db.GithubLocalCache;
import com.example.androidpaging.db.RepoDatabase;
import com.example.androidpaging.ui.ViewModelFactory;

import java.util.concurrent.Executors;

public class Injection {
    private static GithubLocalCache provideCache(Context context) {
        RepoDatabase instance = RepoDatabase.getInstance(context);
        return new GithubLocalCache(instance.repoDao(), Executors.newSingleThreadExecutor());
    }

    private static GithubRepository provideGithubRepository(Context context) {
        GithubService githubService = new GithubService();
        return new GithubRepository(githubService, provideCache(context));
    }

    public static ViewModelProvider.Factory provideViewModelFactory(Context context) {
        return new ViewModelFactory(provideGithubRepository(context));
    }

}

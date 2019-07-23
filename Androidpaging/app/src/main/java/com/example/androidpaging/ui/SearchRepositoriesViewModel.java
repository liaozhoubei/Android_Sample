package com.example.androidpaging.ui;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import android.util.Log;

import com.example.androidpaging.data.GithubRepository;
import com.example.androidpaging.model.Repo;
import com.example.androidpaging.model.RepoSearchResult;

public class SearchRepositoriesViewModel extends ViewModel {
    public static final String TAG ="SearchReposViewModel";
    private GithubRepository repository;
    static final int VISIBLE_THRESHOLD = 5;

    public SearchRepositoriesViewModel(GithubRepository repository) {
        this.repository = repository;
    }

    private MutableLiveData<String> queryLiveData = new MutableLiveData<String>();
    private LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData, new Function<String, RepoSearchResult>() {
        @Override
        public RepoSearchResult apply(String input) {
            Log.e(TAG, "repoResult: " + "Transformations.map" );
            RepoSearchResult search = repository.search(input);
            return search;
        }
    });

    public LiveData<PagedList<Repo>> repos = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<PagedList<Repo>>>() {
        @Override
        public LiveData<PagedList<Repo>> apply(RepoSearchResult input) {
            Log.e(TAG, "repos: " + "Transformations.switchMap" );
            return input.data;
        }
    });

    public LiveData<String> networkErrors = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<String>>() {
        @Override
        public LiveData<String> apply(RepoSearchResult input) {
            return input.networkErrors;
        }
    });

    public void searchRepo(String queryString) {
        queryLiveData.postValue(queryString);
    }

//    /**
//     * 当滑动到最底部的时候就请求网络
//     * @param visibleItemCount
//     * @param lastVisibleItemPosition
//     * @param totalItemCount
//     */
//    public void listScrolled(int visibleItemCount, int lastVisibleItemPosition, int totalItemCount) {
//        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
//            String immutableQuery = lastQueryValue();
//            if (immutableQuery != null) {
//                repository.requestMore(immutableQuery);
//            }
//        }
//    }

    public String lastQueryValue() {
        return queryLiveData.getValue();
    }
}

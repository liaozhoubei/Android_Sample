package com.example.androidpaging.data;

import androidx.lifecycle.MutableLiveData;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import android.util.Log;

import com.example.androidpaging.api.GithubService;
import com.example.androidpaging.api.RequestResult;
import com.example.androidpaging.db.GithubLocalCache;
import com.example.androidpaging.model.Repo;

import java.util.List;

public class RepoBoundaryCallback extends PagedList.BoundaryCallback<Repo> {
    private String TAG = "RepoBoundaryCallback";
    private String query;
    private GithubService service;
    private GithubLocalCache cache;
    private static int NETWORK_PAGE_SIZE = 50;
    private int lastRequestedPage = 1;
    private MutableLiveData<String> _networkErrors = new MutableLiveData<>();
    private boolean isRequestInProgress = false;
    private final GithubService.GithubAPI githubAPI;

    public RepoBoundaryCallback(String query, GithubService service, GithubLocalCache cache) {
        this.query = query;
        this.service = service;
        this.cache = cache;
        githubAPI = service.create();
    }

    public MutableLiveData<String> getNetworkErrors() {
        return _networkErrors;
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        // PagedList的数据源的初始加载返回零项时调用
        Log.e(TAG, "onZeroItemsLoaded: 再次请求网络");
        requestAndSaveData(query);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Repo itemAtEnd) {
//        当数据源在列表末尾用尽数据时，onItemAtEndLoaded(Object)会调用，
//        并且您可以启动异步网络加载，将结果直接写入数据库。
//        由于正在观察数据库，因此绑定到该UI的UI LiveData<PagedList>将
//        自动更新以考虑新项目。
        Log.e(TAG, "onItemAtEndLoaded: 再次请求网络");
        requestAndSaveData(query);
    }

    int i = 0;

    private void requestAndSaveData(String query) {
//        if (i == 0) {
            i++;
            if (isRequestInProgress)
                return;

            isRequestInProgress = true;
            service.searchRepos(githubAPI, query, lastRequestedPage, NETWORK_PAGE_SIZE, new RequestResult() {
                @Override
                public void onError(String error) {
                    _networkErrors.postValue(error);
                    isRequestInProgress = false;
                }

                @Override
                public void onSucess(List<Repo> repos) {
                    cache.insert(repos);
                    lastRequestedPage++;
                    isRequestInProgress = false;
                }
            });
//        }

    }


}

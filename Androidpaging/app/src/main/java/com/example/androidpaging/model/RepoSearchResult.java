package com.example.androidpaging.model;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;


public class RepoSearchResult {
    public LiveData<PagedList<Repo>> data;
    public LiveData<String> networkErrors;

    public RepoSearchResult(LiveData<PagedList<Repo>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<Repo>> getData() {
        return data;
    }

    public void setData(LiveData<PagedList<Repo>> data) {
        this.data = data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    public void setNetworkErrors(LiveData<String> networkErrors) {
        this.networkErrors = networkErrors;
    }
}

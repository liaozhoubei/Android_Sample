package com.example.androidpaging.api;

import com.example.androidpaging.model.Repo;

import java.util.List;

public interface RequestResult {
    public void onError(String error);
    public void onSucess( List<Repo> repos);
}

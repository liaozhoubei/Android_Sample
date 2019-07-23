package com.example.androidpaging.api;

import com.example.androidpaging.model.Repo;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class RepoSearchResponse {
    @SerializedName("total_count") int total  = 0;
    @SerializedName("items") public List<Repo> items = Collections.emptyList();
    public int nextPage = 0;
}

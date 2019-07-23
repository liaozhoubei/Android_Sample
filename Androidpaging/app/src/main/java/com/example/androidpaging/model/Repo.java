package com.example.androidpaging.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "repos", indices = {})
public class Repo {
    @PrimaryKey
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("full_name")
    public String fullName;
    @SerializedName("description")
    public String description;
    @SerializedName("html_url")
    public String url;
    @SerializedName("stargazers_count")
    public int stars;
    @SerializedName("forks_count")
    public int forks;
    @SerializedName("language")
    public String language;

}

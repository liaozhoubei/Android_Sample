package com.example.androidpaging.db;

import androidx.lifecycle.LiveData;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.androidpaging.model.Repo;

import java.util.List;

@Dao
public interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> insert(List<Repo> posts);

    @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " +
            ":queryString) ORDER BY stars DESC, name ASC")
    public  DataSource.Factory<Integer,Repo> reposByName(String queryString);

    @Query("SELECT * FROM repos")
    public DataSource.Factory<Integer,Repo> reposByName();
}

package com.example.androidpaging.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.androidpaging.model.Repo;

@Database(
        entities = {Repo.class},
        version = 1,
        exportSchema = false
)
public abstract class RepoDatabase extends RoomDatabase {
    public abstract RepoDao repoDao();

    private static RepoDatabase instance;

    public static RepoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RepoDatabase.class, "Github.db")
                    .build();

        }
        return instance;
    }
}

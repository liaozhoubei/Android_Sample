package com.example.androidpaging.ui;


import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidpaging.R;
import com.example.androidpaging.model.Repo;

public class ReposAdapter extends PagedListAdapter<Repo, RecyclerView.ViewHolder> {

    public ReposAdapter() {
        super(REPO_COMPARATOR);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.repo_view_item, viewGroup, false);
        RepoViewHolder viewHolder = new RepoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Repo item = getItem(i);
        if (item != null) {
            ((RepoViewHolder) viewHolder).bind(item);
        }
    }

    public static DiffUtil.ItemCallback<Repo> REPO_COMPARATOR = new DiffUtil.ItemCallback<Repo>() {

        @Override
        public boolean areItemsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return oldItem.fullName.equals(newItem.fullName);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return (oldItem.id == newItem.id && oldItem.fullName.equals(newItem.fullName));
        }
    };

}

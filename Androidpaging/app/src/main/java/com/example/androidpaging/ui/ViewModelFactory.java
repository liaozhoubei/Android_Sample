package com.example.androidpaging.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.androidpaging.data.GithubRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private GithubRepository repository;
    ;

    public ViewModelFactory(GithubRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchRepositoriesViewModel.class)) {
            SearchRepositoriesViewModel viewModel = new SearchRepositoriesViewModel(repository);
            return (T) viewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}

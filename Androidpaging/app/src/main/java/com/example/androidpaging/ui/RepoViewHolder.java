package com.example.androidpaging.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.androidpaging.R;
import com.example.androidpaging.model.Repo;

public class RepoViewHolder extends RecyclerView.ViewHolder {

    private Repo repo = null;
    private View view;
    private final TextView name;
    private final TextView description;
    private final TextView stars;
    private final TextView language;
    private final TextView forks;


    public RepoViewHolder(@NonNull final View view) {
        super(view);
        this.view = view;
        name = view.findViewById(R.id.repo_name);
        description = view.findViewById(R.id.repo_description);
        stars = view.findViewById(R.id.repo_stars);
        language = view.findViewById(R.id.repo_language);
        forks = view.findViewById(R.id.repo_forks);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repo != null && TextUtils.isEmpty(repo.url)){
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(repo.url)));
                }
            }
        });
    }

    public void bind(Repo repo){
        if (repo == null){
            Resources resources = itemView.getResources();
            name.setText("Loading");
            description.setVisibility(View.GONE);
            language.setVisibility(View.GONE);
            stars.setText("?");
            forks.setText("?");
        }else {
            showRepoData(repo);
        }
    }

    private void showRepoData(Repo repo) {
        this.repo = repo;
        name.setText(repo.fullName);
        int descriptionVisibility = View.GONE;

        if (repo.description != null) {
            description.setText(repo.description);
            descriptionVisibility = View.VISIBLE;
        }
        description.setVisibility(descriptionVisibility);
        stars.setText(repo.stars + "");
        forks.setText(repo.forks +"");

        int languageVisibility = View.GONE;
        if (TextUtils.isEmpty(repo.language)) {
            Resources resources = this.itemView.getContext().getResources();
            language.setText(resources.getString(R.string.language, repo.language));
            languageVisibility = View.VISIBLE;
        }
        language.setVisibility(languageVisibility);

    }
}

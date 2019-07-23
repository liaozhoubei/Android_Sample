package com.example.androidpaging;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidpaging.model.Repo;
import com.example.androidpaging.ui.ReposAdapter;
import com.example.androidpaging.ui.SearchRepositoriesViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String LAST_SEARCH_QUERY = "last_search_query";
    private static String DEFAULT_QUERY = "Android";
    private SearchRepositoriesViewModel viewModel;


    private ReposAdapter adapter = new ReposAdapter();
    private RecyclerView list;
    private EditText search_repo;
    private TextView emptyList;
    private Button btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.list);
        initView();
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(SearchRepositoriesViewModel.class);

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
//        setupScrollListener();
        initAdapter();
        String query = "";
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY);
        }
        if (TextUtils.isEmpty(query)) {
            query = DEFAULT_QUERY;
        }
        // 一开始就默认使用 android 进行关键词查询
        // 通过livedata 传值
        viewModel.searchRepo(query);
        initSearch(query);
//        String addressLog = DebugDB.getAddressLog();
//        Log.e("MainActivity", "onCreate: " + addressLog );


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue());
    }

    public void initAdapter() {
        list.setAdapter(adapter);
        // 注册观察者
        viewModel.repos.observe(this, new Observer<PagedList<Repo>>() {
            @Override
            public void onChanged(@Nullable PagedList<Repo> repos) {
                Log.e(TAG, "PagedList 数据更改了");
                showEmptyList(repos.size() == 0);
                adapter.submitList(repos);
            }
        });
        viewModel.networkErrors.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show();
            }
        });
    }

    int i, j;

    public void initSearch(String query) {
        search_repo.setText(query);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRepoListFromInput();
            }
        });
//        search_repo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_GO) {
//                    i ++;
//                    Log.e(TAG, "onEditorAction: " + i );
//                    updateRepoListFromInput();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//        search_repo.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    j++;
//                    Log.e(TAG, "onEditorAction: " + j );
//                    updateRepoListFromInput();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
    }

    /**
     * 从EditText 中获取输入信息后请求网络
     */
    public void updateRepoListFromInput() {
        String trim = search_repo.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            list.scrollToPosition(0);
            viewModel.searchRepo(trim);
            adapter.submitList(null);
        }
    }

    public void showEmptyList(boolean show) {
        if (show) {
            emptyList.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

//    public void setupScrollListener() {
//        final LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
//        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int totalItemCount = layoutManager.getItemCount();
//                int visibleItemCount = layoutManager.getChildCount();
//                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//
//                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount);
//            }
//        });
//    }

    private void initView() {
//        mInputLayout = (TextInputLayout) findViewById(R.id.input_layout);
        search_repo = (EditText) findViewById(R.id.search_repo);
        emptyList = (TextView) findViewById(R.id.emptyList);
        btn_search = findViewById(R.id.btn_search);

    }
}

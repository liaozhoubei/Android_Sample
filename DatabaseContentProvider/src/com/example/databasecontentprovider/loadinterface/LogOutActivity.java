package com.example.databasecontentprovider.loadinterface;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.databasecontentprovider.R;

public class LogOutActivity extends Activity{
    private SQLiteDatabase mSqLiteDatabase;
    ContentValues mContentValues = new ContentValues();
    private LoginActivity mLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = databaseHelper.getWritableDatabase();
        // 获取登录名
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String userName = bundle.getString("loginUserName");
        Button signOutButton = (Button) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新数据库登陆状态
                mContentValues.put(DatabaseHelper.LOGIN_STATE, "false");
                String whereClauseString = "username=?";
                String[] whereArgsString = {userName};
                mSqLiteDatabase.update(DatabaseHelper.USER_TABLE_NAME, mContentValues, whereClauseString, whereArgsString);
                Toast.makeText(LogOutActivity.this, "账号已登出", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LogOutActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}

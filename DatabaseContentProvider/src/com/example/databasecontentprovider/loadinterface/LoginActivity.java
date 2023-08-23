package com.example.databasecontentprovider.loadinterface;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.databasecontentprovider.MainActivity;
import com.example.databasecontentprovider.R;

import java.util.Calendar;

/**
 * Created by Bei on 2016/2/24.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private Button mSignUpButton;
    private Button mSignInButton;
    private EditText mUserNameEditorText;
    private EditText mPasswordEditorText;
    private SQLiteDatabase mSqLiteDatabase;
    private String STATE_SIGNIN = "true";
    private String STATE_SIGNOUT = "false";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    ContentValues mContentValues = new ContentValues();
    private boolean isSignUp;
    public boolean isSignIn;
    String mUserName;
    String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = databaseHelper.getWritableDatabase();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            // 将账号设置到文本框中
            mUserName = pref.getString("mUserName", "");
            mPassword = pref.getString("mPassword", "");
            mUserNameEditorText.setText(mUserName);
            mPasswordEditorText.setText(mPassword);
            rememberPass.setChecked(true);
        }
    }

    private void findView() {
        mUserNameEditorText = (EditText) findViewById(R.id.username_editortext);
        mPasswordEditorText = (EditText) findViewById(R.id.password_editortext);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignUpButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up_button) {
            mUserName = mUserNameEditorText.getText().toString();
            mPassword = mPasswordEditorText.getText().toString();
            // 查询账号是否注册过
            query();
            if (!isSignUp) {
                addUser();
            }
        } else if (v.getId() == R.id.sign_in_button) {
            singIn();
        }

    }
// 添加用户
    private void addUser() {
        mContentValues.put(DatabaseHelper.USERNAME, mUserName);
        mContentValues.put(DatabaseHelper.PASSWORD, mPassword);
        mContentValues.put(DatabaseHelper.LOGIN_STATE, STATE_SIGNOUT);
        long rowNumber = mSqLiteDatabase.insert(DatabaseHelper.USER_TABLE_NAME, null, mContentValues);
        if(rowNumber != -1){
            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
        }
    }
// 查询用户名是否已被注册
    private void query() {
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.USER_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME));
                if (username.equals(mUserName)) {
                    Toast.makeText(LoginActivity.this, "账号已被注册", Toast.LENGTH_SHORT).show();
                    isSignUp = true;
                    break;
                } else {
                    isSignUp = false;
                }
            } while (cursor.moveToNext());
        }
    }
// 核对账号信息并登录
    private void singIn() {
        mUserName = mUserNameEditorText.getText().toString();
        mPassword = mPasswordEditorText.getText().toString();
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.USER_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PASSWORD));

                if (username.equals(mUserName) && password.equals(mPassword)) {
                    isSignIn = true;
                    // 获取用户最后登录时间记录
                    login_history();
                    updataState(STATE_SIGNIN);
                    Toast.makeText(LoginActivity.this, "账号已登录", Toast.LENGTH_SHORT).show();
                    editor = pref.edit();
                    // 检查复选框是否被选中
                    if (rememberPass.isChecked()) {
                        // 使用sharedPreferences暂时存储账号/密码
                        editor.putBoolean("remember_password", true);
                        editor.putString("mUserName", mUserName);
                        editor.putString("mPassword", mPassword);
                    } else {
                        editor.clear();
                    }
                    editor.apply();
                    // 向主界面传递登录用户并跳转到主界面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginUserName", username);
                    LoginActivity.this.startActivity(intent);
                    break;
                }
                cursor.moveToNext();
            }
        }
        if (!isSignIn) {
            Toast.makeText(LoginActivity.this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
        }
    }
//  更新登录状态
    private void updataState(String state) {
        mContentValues.put(DatabaseHelper.LOGIN_STATE, state);
        mContentValues.put(DatabaseHelper.LOGIN_TIME, login_history());
        String whereClauseString = "username=?";
        String[] whereArgsString = {mUserName};
        mSqLiteDatabase.update(DatabaseHelper.USER_TABLE_NAME, mContentValues, whereClauseString, whereArgsString);
    }

    // 获取系统当前时间
    private String login_history() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String loginTime =  year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分";
        return loginTime;
    }

}

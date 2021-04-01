package com.example.example.multilanguage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.example.R;

import java.util.Locale;

public class MultiLanguageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_language);
    }

    public void select(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择您的语言");
        final String items[] = {"中国", "美国"};
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String item = items[which];
                if (item.equals("中国")) {
                    switchLanguage("zh_simple");
                } else if (item.equals("美国")) {
                    switchLanguage("en");
                }
                dialog.dismiss();
            }
        });

        builder.show();

    }

    public void second(View v) {
        startActivity(new Intent(MultiLanguageActivity.this, LanguageActivity.class));
    }


    /**
     * 切换语言
     *
     * @param language
     */

    private void switchLanguage(String language) {

        //设置应用语言类型

        Resources resources = getResources();

        Configuration config = resources.getConfiguration();

        DisplayMetrics dm = resources.getDisplayMetrics();

        if (language.equals("zh_simple")) {

            config.locale = Locale.SIMPLIFIED_CHINESE;

        } else if (language.equals("en")) {

            config.locale = Locale.ENGLISH;

        } else {

            config.locale = Locale.getDefault();

        }

        resources.updateConfiguration(config, dm);

        //保存设置语言的类型

        SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.commit();

        //更新语言后，destroy当前页面，重新绘制

        finish();

        Intent it = new Intent(MultiLanguageActivity.this, MultiLanguageActivity.class);

        //清空任务栈确保当前打开activit为前台任务栈栈顶

        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(it);

    }
}

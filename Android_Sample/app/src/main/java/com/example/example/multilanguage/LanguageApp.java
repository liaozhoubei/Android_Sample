package com.example.example.multilanguage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

/**
 * 动态更换国际语言时的Application的配置方法
 */
public class LanguageApp extends Application {
    @Override

    public void onCreate() {

        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        String lan = sharedPreferences.getString("language", "zh_simple");
        System.out.println("======之前选择的语言：  "+lan);
        Log.d("App", "onCreate: " + lan);

        getLanguage(lan);

    }

    private void getLanguage(String lan){

        Resources resources = getResources();

        Configuration config = resources.getConfiguration();

        DisplayMetrics dm = resources.getDisplayMetrics();

        if (lan.equals("zh_simple")) {

            config.locale = Locale.SIMPLIFIED_CHINESE;

        }else if(lan.equals("zh_tw")){

            config.locale = Locale.TRADITIONAL_CHINESE;

        } else if(lan.equals("en")){

            config.locale = Locale.ENGLISH;

        }else{

            config.locale = Locale.getDefault();

        }

        resources.updateConfiguration(config, dm);

    }
}

package com.example.example.activity;

import android.os.Bundle;

import com.example.example.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webview);
        // 启用javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        // 从assets目录下面的加载html
        webView.loadUrl("file:///android_asset/web.html");
        //  name:android在网页里面可以用 window.name.方法名 调用java方法
        webView.addJavascriptInterface(WebViewActivity.this, "java");

        //Button1按钮 无参调用HTML js方法
        findViewById(R.id.button).setOnClickListener(onClickListener);
        //Button2按钮 有参调用HTML js方法
        findViewById(R.id.button2).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button:
                    // 无参数调用 JS的方法
                    webView.loadUrl("javascript:javaToJS()");
                    break;
                case R.id.button2:
                    // 传递参数调用JS的方法，点击后调用document.location=arg切换网页
                    webView.loadUrl("javascript:javaToJsWith(" + "'https://baike.so.com/doc/456230-483111.html'" + ")");
                    break;
                default:
            }
        }
    };

    //由于安全原因 targetSdkVersion>=17需要加 @JavascriptInterface
    //JS调用java，且无参
    @JavascriptInterface
    public void noParameterFunction() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = (Button) findViewById(R.id.button2);
                button.setText("JS->android 无参成功");
            }
        });
    }

    //JS调用java，且传参
    @JavascriptInterface
    public void parameterFunction(final String parameter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = (Button) findViewById(R.id.button2);
                button.setText(parameter);
            }
        });
    }
}

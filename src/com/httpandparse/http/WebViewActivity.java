package com.httpandparse.http;

import com.httpandparse.R;
import com.httpandparse.R.id;
import com.httpandparse.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 使用WebView上网
 * @author ASUS-H61M
 *
 */
public class WebViewActivity extends Activity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		webView = (WebView) findViewById(R.id.webView1);
		initWebView();

	}

	// 使用WebView打开网页
	public void initWebView() {
		webView.loadUrl("http://www.baidu.com");
		WebSettings settings = webView.getSettings();
		settings.setBuiltInZoomControls(true); // 可以双击缩放
		settings.setJavaScriptEnabled(true); // 可以使用js
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				System.out.println("正在加载网页");
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				System.out.println("网页加载完成");
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.goBack();
		webView.goForward();

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// 获取当前网页加载进度，可用于更新进度条
				System.out.println("网页加载进度" + newProgress);
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				// 获得网页标题
				super.onReceivedTitle(view, title);
			}
		});
	}

}

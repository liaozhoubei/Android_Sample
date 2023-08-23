package com.httpandparse.http;

import com.httpandparse.R;
import com.httpandparse.R.id;
import com.httpandparse.R.layout;
import com.httpandparse.thread.HttpThread;
import com.httpandparse.thread.HttpThread.NetListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
/**
 * 下载文件的Activity
 * @author ASUS-H61M
 *
 */
public class NetFileActivity extends Activity {
	private ImageView imageView;
	private String pictureUrl = "http://img.pconline.com.cn/images/upload/upc/tx/photoblog/1503/17/c2/3974346_1426551981202_mthumb.jpg";
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_netfile);
		imageView = (ImageView) findViewById(R.id.imageView1);
		final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
		new HttpThread(pictureUrl, handler, new NetListener() {
			// 通过回调的方式显示图片
			@Override
			public void showView(String absolutePath) {
				progress.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
				imageView.setImageBitmap(bitmap);
			}

	
		}).start();
	}
}
